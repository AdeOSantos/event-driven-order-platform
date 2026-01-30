package com.adalbertosantos.inventory.application;

import com.adalbertosantos.events.inventory.InventoryRejectedEvent;
import com.adalbertosantos.events.inventory.InventoryReservedEvent;
import com.adalbertosantos.events.payment.PaymentSucceededEvent;
import com.adalbertosantos.inventory.domain.InventoryItem;
import com.adalbertosantos.inventory.domain.InventoryRepository;
import com.adalbertosantos.inventory.infrastructure.messaging.InventoryEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InventoryReservationService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryReservationService.class);

    private final InventoryRepository inventoryRepository;
    private final InventoryEventProducer eventProducer;

    public InventoryReservationService(InventoryRepository inventoryRepository,
                                      InventoryEventProducer eventProducer) {
        this.inventoryRepository = inventoryRepository;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public void reserveInventory(PaymentSucceededEvent event) {
        logger.info("Attempting to reserve inventory for order: {}", event.getOrderId());

        try {
            List<InventoryReservedEvent.ReservedItem> reservedItems = new ArrayList<>();
            
            // In a real scenario, we would get the order items from the event
            // For simplicity, we're simulating a reservation
            UUID sampleProductId = UUID.randomUUID();
            int requestedQuantity = 1;

            InventoryItem item = inventoryRepository.findByIdWithLock(sampleProductId)
                    .orElseGet(() -> createDefaultInventoryItem(sampleProductId));

            if (item.hasAvailableQuantity(requestedQuantity)) {
                item.reserve(requestedQuantity);
                inventoryRepository.save(item);

                reservedItems.add(new InventoryReservedEvent.ReservedItem(
                    item.getProductId(),
                    requestedQuantity
                ));

                InventoryReservedEvent reservedEvent = new InventoryReservedEvent(
                    event.getOrderId(),
                    UUID.randomUUID(),
                    reservedItems
                );
                eventProducer.sendInventoryReservedEvent(reservedEvent);
                
                logger.info("Successfully reserved inventory for order: {}", event.getOrderId());
            } else {
                String reason = String.format("Insufficient inventory for product %s. Available: %d, Requested: %d",
                    item.getProductId(), item.getAvailableQuantity(), requestedQuantity);
                
                InventoryRejectedEvent rejectedEvent = new InventoryRejectedEvent(
                    event.getOrderId(),
                    reason
                );
                eventProducer.sendInventoryRejectedEvent(rejectedEvent);
                
                logger.warn("Inventory reservation rejected for order: {} - {}", event.getOrderId(), reason);
            }
        } catch (Exception e) {
            logger.error("Failed to reserve inventory for order: {}", event.getOrderId(), e);
            
            InventoryRejectedEvent rejectedEvent = new InventoryRejectedEvent(
                event.getOrderId(),
                "Inventory reservation failed: " + e.getMessage()
            );
            eventProducer.sendInventoryRejectedEvent(rejectedEvent);
        }
    }

    @Transactional
    public void releaseInventory(UUID orderId, UUID productId, int quantity) {
        logger.info("Releasing inventory for order: {}, product: {}, quantity: {}", orderId, productId, quantity);
        
        inventoryRepository.findByIdWithLock(productId)
                .ifPresent(item -> {
                    item.release(quantity);
                    inventoryRepository.save(item);
                    logger.info("Released inventory for order: {}", orderId);
                });
    }

    private InventoryItem createDefaultInventoryItem(UUID productId) {
        InventoryItem item = new InventoryItem();
        item.setProductId(productId);
        item.setProductName("Sample Product");
        item.setAvailableQuantity(100);
        item.setReservedQuantity(0);
        return inventoryRepository.save(item);
    }
}

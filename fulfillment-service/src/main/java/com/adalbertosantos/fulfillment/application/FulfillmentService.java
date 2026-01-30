package com.adalbertosantos.fulfillment.application;

import com.adalbertosantos.events.inventory.InventoryReservedEvent;
import com.adalbertosantos.events.order.OrderFulfilledEvent;
import com.adalbertosantos.fulfillment.domain.Fulfillment;
import com.adalbertosantos.fulfillment.domain.FulfillmentRepository;
import com.adalbertosantos.fulfillment.infrastructure.messaging.FulfillmentEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FulfillmentService {

    private static final Logger logger = LoggerFactory.getLogger(FulfillmentService.class);

    private final FulfillmentRepository fulfillmentRepository;
    private final FulfillmentEventProducer eventProducer;

    public FulfillmentService(FulfillmentRepository fulfillmentRepository,
                            FulfillmentEventProducer eventProducer) {
        this.fulfillmentRepository = fulfillmentRepository;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public void fulfillOrder(InventoryReservedEvent event) {
        logger.info("Processing fulfillment for order: {}", event.getOrderId());

        try {
            Fulfillment fulfillment = new Fulfillment();
            fulfillment.setFulfillmentId(UUID.randomUUID());
            fulfillment.setOrderId(event.getOrderId());
            fulfillment.setReservationId(event.getReservationId());
            fulfillment.setStatus(Fulfillment.FulfillmentStatus.PROCESSING);

            fulfillment = fulfillmentRepository.save(fulfillment);

            // Simulate fulfillment processing (warehouse picking, packing, shipping)
            String trackingNumber = generateTrackingNumber();
            
            fulfillment.setStatus(Fulfillment.FulfillmentStatus.SHIPPED);
            fulfillment.setTrackingNumber(trackingNumber);
            fulfillmentRepository.save(fulfillment);

            OrderFulfilledEvent fulfilledEvent = new OrderFulfilledEvent(
                event.getOrderId(),
                trackingNumber
            );
            eventProducer.sendOrderFulfilledEvent(fulfilledEvent);

            logger.info("Order fulfilled successfully: {} with tracking number: {}", 
                event.getOrderId(), trackingNumber);

        } catch (Exception e) {
            logger.error("Failed to fulfill order: {}", event.getOrderId(), e);
            
            Fulfillment failedFulfillment = new Fulfillment();
            failedFulfillment.setFulfillmentId(UUID.randomUUID());
            failedFulfillment.setOrderId(event.getOrderId());
            failedFulfillment.setReservationId(event.getReservationId());
            failedFulfillment.setStatus(Fulfillment.FulfillmentStatus.FAILED);
            failedFulfillment.setFailureReason(e.getMessage());
            fulfillmentRepository.save(failedFulfillment);
            
            throw new RuntimeException("Fulfillment failed for order: " + event.getOrderId(), e);
        }
    }

    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

package com.example.inventory.consumer;

import com.example.events.payment.PaymentSucceededEvent;
import com.example.inventory.application.InventoryReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PaymentSucceededConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentSucceededConsumer.class);

    private final InventoryReservationService inventoryReservationService;
    private final ObjectMapper objectMapper;

    public PaymentSucceededConsumer(InventoryReservationService inventoryReservationService,
                                   ObjectMapper objectMapper) {
        this.inventoryReservationService = inventoryReservationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = "payment.succeeded",
        groupId = "inventory-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentSucceededEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.info("Received PaymentSucceededEvent - Topic: {}, Key: {}, Offset: {}", topic, key, offset);
        
        try {
            PaymentSucceededEvent event = objectMapper.readValue(message, PaymentSucceededEvent.class);
            inventoryReservationService.reserveInventory(event);
            acknowledgment.acknowledge();
            logger.info("Successfully processed inventory reservation for order: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to process PaymentSucceededEvent for key: {}", key, e);
            acknowledgment.acknowledge();
        }
    }
}

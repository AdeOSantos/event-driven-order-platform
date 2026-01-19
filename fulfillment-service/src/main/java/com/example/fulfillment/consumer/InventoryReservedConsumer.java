package com.example.fulfillment.consumer;

import com.example.events.inventory.InventoryReservedEvent;
import com.example.fulfillment.application.FulfillmentService;
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
public class InventoryReservedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(InventoryReservedConsumer.class);

    private final FulfillmentService fulfillmentService;
    private final ObjectMapper objectMapper;

    public InventoryReservedConsumer(FulfillmentService fulfillmentService,
                                    ObjectMapper objectMapper) {
        this.fulfillmentService = fulfillmentService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = "inventory.reserved",
        groupId = "fulfillment-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeInventoryReservedEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.info("Received InventoryReservedEvent - Topic: {}, Key: {}, Offset: {}", topic, key, offset);
        
        try {
            InventoryReservedEvent event = objectMapper.readValue(message, InventoryReservedEvent.class);
            fulfillmentService.fulfillOrder(event);
            acknowledgment.acknowledge();
            logger.info("Successfully processed fulfillment for order: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to process InventoryReservedEvent for key: {}", key, e);
            acknowledgment.acknowledge();
        }
    }
}

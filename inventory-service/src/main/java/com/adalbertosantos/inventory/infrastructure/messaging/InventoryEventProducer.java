package com.adalbertosantos.inventory.infrastructure.messaging;

import com.adalbertosantos.events.inventory.InventoryRejectedEvent;
import com.adalbertosantos.events.inventory.InventoryReservedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(InventoryEventProducer.class);
    private static final String INVENTORY_RESERVED_TOPIC = "inventory.reserved";
    private static final String INVENTORY_REJECTED_TOPIC = "inventory.rejected";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public InventoryEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendInventoryReservedEvent(InventoryReservedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(INVENTORY_RESERVED_TOPIC, event.getOrderId().toString(), message);
            logger.info("Sent InventoryReservedEvent for order: {}", event.getOrderId());
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize InventoryReservedEvent", e);
            throw new RuntimeException("Failed to send inventory reserved event", e);
        }
    }

    public void sendInventoryRejectedEvent(InventoryRejectedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(INVENTORY_REJECTED_TOPIC, event.getOrderId().toString(), message);
            logger.info("Sent InventoryRejectedEvent for order: {}", event.getOrderId());
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize InventoryRejectedEvent", e);
            throw new RuntimeException("Failed to send inventory rejected event", e);
        }
    }
}

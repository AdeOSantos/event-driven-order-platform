package com.example.order.infrastructure.messaging;

import com.example.events.order.OrderCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventProducer.class);
    private static final String ORDER_CREATED_TOPIC = "order.created";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OrderEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(ORDER_CREATED_TOPIC, event.getOrderId().toString(), message);
            logger.info("Sent OrderCreatedEvent for order: {}", event.getOrderId());
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize OrderCreatedEvent", e);
            throw new RuntimeException("Failed to send order created event", e);
        }
    }
}

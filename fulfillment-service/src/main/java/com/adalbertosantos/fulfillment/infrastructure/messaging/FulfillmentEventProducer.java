package com.adalbertosantos.fulfillment.infrastructure.messaging;

import com.adalbertosantos.events.order.OrderFulfilledEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FulfillmentEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(FulfillmentEventProducer.class);
    private static final String ORDER_FULFILLED_TOPIC = "order.fulfilled";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public FulfillmentEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendOrderFulfilledEvent(OrderFulfilledEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(ORDER_FULFILLED_TOPIC, event.getOrderId().toString(), message);
            logger.info("Sent OrderFulfilledEvent for order: {}", event.getOrderId());
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize OrderFulfilledEvent", e);
            throw new RuntimeException("Failed to send order fulfilled event", e);
        }
    }
}

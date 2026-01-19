package com.example.payment.infrastructure.messaging;

import com.example.events.payment.PaymentFailedEvent;
import com.example.events.payment.PaymentSucceededEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventProducer.class);
    private static final String PAYMENT_SUCCEEDED_TOPIC = "payment.succeeded";
    private static final String PAYMENT_FAILED_TOPIC = "payment.failed";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PaymentEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendPaymentSucceededEvent(PaymentSucceededEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(PAYMENT_SUCCEEDED_TOPIC, event.getOrderId().toString(), message);
            logger.info("Sent PaymentSucceededEvent for order: {}", event.getOrderId());
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize PaymentSucceededEvent", e);
            throw new RuntimeException("Failed to send payment succeeded event", e);
        }
    }

    public void sendPaymentFailedEvent(PaymentFailedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(PAYMENT_FAILED_TOPIC, event.getOrderId().toString(), message);
            logger.info("Sent PaymentFailedEvent for order: {}", event.getOrderId());
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize PaymentFailedEvent", e);
            throw new RuntimeException("Failed to send payment failed event", e);
        }
    }
}

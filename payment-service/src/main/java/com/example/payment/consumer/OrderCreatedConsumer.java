package com.example.payment.consumer;

import com.example.events.order.OrderCreatedEvent;
import com.example.payment.application.PaymentProcessor;
import com.example.payment.infrastructure.messaging.DeadLetterPublisher;
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
public class OrderCreatedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderCreatedConsumer.class);

    private final PaymentProcessor paymentProcessor;
    private final ObjectMapper objectMapper;
    private final DeadLetterPublisher deadLetterPublisher;

    public OrderCreatedConsumer(PaymentProcessor paymentProcessor, 
                               ObjectMapper objectMapper,
                               DeadLetterPublisher deadLetterPublisher) {
        this.paymentProcessor = paymentProcessor;
        this.objectMapper = objectMapper;
        this.deadLetterPublisher = deadLetterPublisher;
    }

    @KafkaListener(
        topics = "order.created",
        groupId = "payment-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderCreatedEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.info("Received OrderCreatedEvent - Topic: {}, Key: {}, Offset: {}", topic, key, offset);
        
        try {
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            paymentProcessor.processPayment(event);
            acknowledgment.acknowledge();
            logger.info("Successfully processed payment for order: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to process OrderCreatedEvent for key: {}", key, e);
            deadLetterPublisher.publishToDeadLetter(topic, key, message, e.getMessage());
            acknowledgment.acknowledge();
        }
    }
}

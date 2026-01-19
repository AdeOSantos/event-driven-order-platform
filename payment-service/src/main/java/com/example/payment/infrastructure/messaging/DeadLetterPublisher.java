package com.example.payment.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class DeadLetterPublisher {

    private static final Logger logger = LoggerFactory.getLogger(DeadLetterPublisher.class);
    private static final String DEAD_LETTER_TOPIC = "payment.dlq";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public DeadLetterPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishToDeadLetter(String originalTopic, String key, String message, String errorReason) {
        try {
            Map<String, Object> deadLetterMessage = new HashMap<>();
            deadLetterMessage.put("originalTopic", originalTopic);
            deadLetterMessage.put("originalKey", key);
            deadLetterMessage.put("originalMessage", message);
            deadLetterMessage.put("errorReason", errorReason);
            deadLetterMessage.put("timestamp", Instant.now().toString());

            kafkaTemplate.send(DEAD_LETTER_TOPIC, key, message);
            logger.warn("Published message to dead letter queue - Key: {}, Reason: {}", key, errorReason);
        } catch (Exception e) {
            logger.error("Failed to publish to dead letter queue", e);
        }
    }
}

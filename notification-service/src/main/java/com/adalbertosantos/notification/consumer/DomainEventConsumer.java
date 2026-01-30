package com.adalbertosantos.notification.consumer;

import com.adalbertosantos.events.order.OrderCreatedEvent;
import com.adalbertosantos.events.order.OrderFulfilledEvent;
import com.adalbertosantos.events.order.OrderCancelledEvent;
import com.adalbertosantos.events.payment.PaymentSucceededEvent;
import com.adalbertosantos.events.payment.PaymentFailedEvent;
import com.adalbertosantos.events.inventory.InventoryReservedEvent;
import com.adalbertosantos.events.inventory.InventoryRejectedEvent;
import com.adalbertosantos.notification.application.NotificationDispatcher;
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
public class DomainEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DomainEventConsumer.class);

    private final NotificationDispatcher notificationDispatcher;
    private final ObjectMapper objectMapper;

    public DomainEventConsumer(NotificationDispatcher notificationDispatcher,
                              ObjectMapper objectMapper) {
        this.notificationDispatcher = notificationDispatcher;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = "order.created",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderCreatedEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.info("Received OrderCreatedEvent - Key: {}, Offset: {}", key, offset);
        
        try {
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            notificationDispatcher.sendOrderCreatedNotification(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Failed to process OrderCreatedEvent", e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(
        topics = "payment.succeeded",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentSucceededEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.info("Received PaymentSucceededEvent - Key: {}, Offset: {}", key, offset);
        
        try {
            PaymentSucceededEvent event = objectMapper.readValue(message, PaymentSucceededEvent.class);
            notificationDispatcher.sendPaymentSucceededNotification(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Failed to process PaymentSucceededEvent", e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(
        topics = "payment.failed",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentFailedEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.info("Received PaymentFailedEvent - Key: {}, Offset: {}", key, offset);
        
        try {
            PaymentFailedEvent event = objectMapper.readValue(message, PaymentFailedEvent.class);
            notificationDispatcher.sendPaymentFailedNotification(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Failed to process PaymentFailedEvent", e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(
        topics = "inventory.reserved",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeInventoryReservedEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.info("Received InventoryReservedEvent - Key: {}, Offset: {}", key, offset);
        
        try {
            InventoryReservedEvent event = objectMapper.readValue(message, InventoryReservedEvent.class);
            notificationDispatcher.sendInventoryReservedNotification(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Failed to process InventoryReservedEvent", e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(
        topics = "inventory.rejected",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeInventoryRejectedEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.info("Received InventoryRejectedEvent - Key: {}, Offset: {}", key, offset);
        
        try {
            InventoryRejectedEvent event = objectMapper.readValue(message, InventoryRejectedEvent.class);
            notificationDispatcher.sendInventoryRejectedNotification(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Failed to process InventoryRejectedEvent", e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(
        topics = "order.fulfilled",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderFulfilledEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.info("Received OrderFulfilledEvent - Key: {}, Offset: {}", key, offset);
        
        try {
            OrderFulfilledEvent event = objectMapper.readValue(message, OrderFulfilledEvent.class);
            notificationDispatcher.sendOrderFulfilledNotification(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Failed to process OrderFulfilledEvent", e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(
        topics = "order.cancelled",
        groupId = "notification-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderCancelledEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.info("Received OrderCancelledEvent - Key: {}, Offset: {}", key, offset);
        
        try {
            OrderCancelledEvent event = objectMapper.readValue(message, OrderCancelledEvent.class);
            notificationDispatcher.sendOrderCancelledNotification(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Failed to process OrderCancelledEvent", e);
            acknowledgment.acknowledge();
        }
    }
}

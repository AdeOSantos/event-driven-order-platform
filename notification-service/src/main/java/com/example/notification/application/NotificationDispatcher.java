package com.example.notification.application;

import com.example.events.inventory.InventoryRejectedEvent;
import com.example.events.inventory.InventoryReservedEvent;
import com.example.events.order.OrderCancelledEvent;
import com.example.events.order.OrderCreatedEvent;
import com.example.events.order.OrderFulfilledEvent;
import com.example.events.payment.PaymentFailedEvent;
import com.example.events.payment.PaymentSucceededEvent;
import com.example.notification.domain.Notification;
import com.example.notification.domain.NotificationRepository;
import com.example.notification.infrastructure.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(NotificationDispatcher.class);

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public NotificationDispatcher(NotificationRepository notificationRepository,
                                 EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void sendOrderCreatedNotification(OrderCreatedEvent event) {
        logger.info("Sending order created notification for order: {}", event.getOrderId());
        
        String message = String.format(
            "Your order %s has been created successfully. Total amount: $%.2f",
            event.getOrderId(), event.getTotalAmount()
        );
        
        Notification notification = createNotification(
            event.getOrderId(),
            event.getCustomerId(),
            "Order Created",
            message,
            Notification.NotificationType.ORDER_CREATED
        );
        
        emailService.sendEmail(
            "customer@example.com",
            "Order Created - " + event.getOrderId(),
            message
        );
        
        notification.setStatus(Notification.NotificationStatus.SENT);
        notificationRepository.save(notification);
        
        logger.info("Order created notification sent for order: {}", event.getOrderId());
    }

    @Transactional
    public void sendPaymentSucceededNotification(PaymentSucceededEvent event) {
        logger.info("Sending payment succeeded notification for order: {}", event.getOrderId());
        
        String message = String.format(
            "Payment of $%.2f has been processed successfully for order %s",
            event.getAmount(), event.getOrderId()
        );
        
        Notification notification = createNotification(
            event.getOrderId(),
            null,
            "Payment Succeeded",
            message,
            Notification.NotificationType.PAYMENT_SUCCEEDED
        );
        
        emailService.sendEmail(
            "customer@example.com",
            "Payment Successful - " + event.getOrderId(),
            message
        );
        
        notification.setStatus(Notification.NotificationStatus.SENT);
        notificationRepository.save(notification);
    }

    @Transactional
    public void sendPaymentFailedNotification(PaymentFailedEvent event) {
        logger.info("Sending payment failed notification for order: {}", event.getOrderId());
        
        String message = String.format(
            "Payment failed for order %s. Reason: %s",
            event.getOrderId(), event.getReason()
        );
        
        Notification notification = createNotification(
            event.getOrderId(),
            null,
            "Payment Failed",
            message,
            Notification.NotificationType.PAYMENT_FAILED
        );
        
        emailService.sendEmail(
            "customer@example.com",
            "Payment Failed - " + event.getOrderId(),
            message
        );
        
        notification.setStatus(Notification.NotificationStatus.SENT);
        notificationRepository.save(notification);
    }

    @Transactional
    public void sendInventoryReservedNotification(InventoryReservedEvent event) {
        logger.info("Sending inventory reserved notification for order: {}", event.getOrderId());
        
        String message = String.format(
            "Inventory has been reserved for your order %s",
            event.getOrderId()
        );
        
        Notification notification = createNotification(
            event.getOrderId(),
            null,
            "Inventory Reserved",
            message,
            Notification.NotificationType.INVENTORY_RESERVED
        );
        
        notification.setStatus(Notification.NotificationStatus.SENT);
        notificationRepository.save(notification);
    }

    @Transactional
    public void sendInventoryRejectedNotification(InventoryRejectedEvent event) {
        logger.info("Sending inventory rejected notification for order: {}", event.getOrderId());
        
        String message = String.format(
            "Order %s cannot be fulfilled. Reason: %s",
            event.getOrderId(), event.getReason()
        );
        
        Notification notification = createNotification(
            event.getOrderId(),
            null,
            "Order Cannot Be Fulfilled",
            message,
            Notification.NotificationType.INVENTORY_REJECTED
        );
        
        emailService.sendEmail(
            "customer@example.com",
            "Order Cannot Be Fulfilled - " + event.getOrderId(),
            message
        );
        
        notification.setStatus(Notification.NotificationStatus.SENT);
        notificationRepository.save(notification);
    }

    @Transactional
    public void sendOrderFulfilledNotification(OrderFulfilledEvent event) {
        logger.info("Sending order fulfilled notification for order: {}", event.getOrderId());
        
        String message = String.format(
            "Your order %s has been shipped! Tracking number: %s",
            event.getOrderId(), event.getTrackingNumber()
        );
        
        Notification notification = createNotification(
            event.getOrderId(),
            null,
            "Order Shipped",
            message,
            Notification.NotificationType.ORDER_FULFILLED
        );
        
        emailService.sendEmail(
            "customer@example.com",
            "Order Shipped - " + event.getOrderId(),
            message
        );
        
        notification.setStatus(Notification.NotificationStatus.SENT);
        notificationRepository.save(notification);
        
        logger.info("Order fulfilled notification sent for order: {}", event.getOrderId());
    }

    @Transactional
    public void sendOrderCancelledNotification(OrderCancelledEvent event) {
        logger.info("Sending order cancelled notification for order: {}", event.getOrderId());
        
        String message = String.format(
            "Your order %s has been cancelled. Reason: %s",
            event.getOrderId(), event.getReason()
        );
        
        Notification notification = createNotification(
            event.getOrderId(),
            null,
            "Order Cancelled",
            message,
            Notification.NotificationType.ORDER_CANCELLED
        );
        
        emailService.sendEmail(
            "customer@example.com",
            "Order Cancelled - " + event.getOrderId(),
            message
        );
        
        notification.setStatus(Notification.NotificationStatus.SENT);
        notificationRepository.save(notification);
    }

    private Notification createNotification(UUID orderId, UUID customerId, 
                                          String subject, String message,
                                          Notification.NotificationType type) {
        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID());
        notification.setOrderId(orderId);
        notification.setCustomerId(customerId);
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setType(type);
        notification.setStatus(Notification.NotificationStatus.PENDING);
        return notificationRepository.save(notification);
    }
}

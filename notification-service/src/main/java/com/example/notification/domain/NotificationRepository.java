package com.example.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByOrderId(UUID orderId);
    List<Notification> findByCustomerId(UUID customerId);
    List<Notification> findByStatus(Notification.NotificationStatus status);
}

package com.example.events.payment;

import java.time.Instant;
import java.util.UUID;

public class fielPaymentFailedEvent {
    private UUID orderId;
    private UUID paymentId;
    private String reason;
    private Instant timestamp;

    public PaymentFailedEvent() {
    }

    public PaymentFailedEvent(UUID orderId, UUID paymentId, String reason) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.reason = reason;
        this.timestamp = Instant.now();
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}

package com.adalbertosantos.events.order;

import java.time.Instant;
import java.util.UUID;

public class OrderCancelledEvent {
    private UUID orderId;
    private String reason;
    private Instant timestamp;

    public OrderCancelledEvent() {
    }

    public OrderCancelledEvent(UUID orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
        this.timestamp = Instant.now();
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
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

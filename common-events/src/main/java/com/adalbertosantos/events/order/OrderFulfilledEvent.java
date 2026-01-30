package com.adalbertosantos.events.order;

import java.time.Instant;
import java.util.UUID;

public class OrderFulfilledEvent {
    private UUID orderId;
    private String trackingNumber;
    private Instant timestamp;

    public OrderFulfilledEvent() {
    }

    public OrderFulfilledEvent(UUID orderId, String trackingNumber) {
        this.orderId = orderId;
        this.trackingNumber = trackingNumber;
        this.timestamp = Instant.now();
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}

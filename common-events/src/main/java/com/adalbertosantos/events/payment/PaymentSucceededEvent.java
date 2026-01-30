package com.adalbertosantos.events.payment;

import java.time.Instant;
import java.util.UUID;

public class PaymentSucceededEvent {
    private UUID orderId;
    private UUID paymentId;
    private double amount;
    private String paymentMethod;
    private Instant timestamp;

    public PaymentSucceededEvent() {
    }

    public PaymentSucceededEvent(UUID orderId, UUID paymentId, double amount, String paymentMethod) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}

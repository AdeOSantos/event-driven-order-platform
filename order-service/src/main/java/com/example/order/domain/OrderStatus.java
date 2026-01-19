package com.example.order.domain;

public enum OrderStatus {
    PENDING,
    PAYMENT_PROCESSING,
    PAYMENT_FAILED,
    INVENTORY_RESERVED,
    INVENTORY_REJECTED,
    FULFILLED,
    CANCELLED
}

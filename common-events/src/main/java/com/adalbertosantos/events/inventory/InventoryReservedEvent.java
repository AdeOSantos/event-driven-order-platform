package com.adalbertosantos.events.inventory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class InventoryReservedEvent {
    private UUID orderId;
    private UUID reservationId;
    private List<ReservedItem> items;
    private Instant timestamp;

    public InventoryReservedEvent() {
    }

    public InventoryReservedEvent(UUID orderId, UUID reservationId, List<ReservedItem> items) {
        this.orderId = orderId;
        this.reservationId = reservationId;
        this.items = items;
        this.timestamp = Instant.now();
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public List<ReservedItem> getItems() {
        return items;
    }

    public void setItems(List<ReservedItem> items) {
        this.items = items;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static class ReservedItem {
        private UUID productId;
        private int quantity;

        public ReservedItem() {
        }

        public ReservedItem(UUID productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public UUID getProductId() {
            return productId;
        }

        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}

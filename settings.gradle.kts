rootProject.name = "event-driven-order-platform"

include(
    "order-service",
    "payment-service",
    "inventory-service",
    "fulfillment-service",
    "notification-service",
    "common-events"
)
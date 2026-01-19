# Architecture

This document describes the architecture of the event-driven order platform.

## Overview

This is an event-driven microservices architecture for order processing.

## Services

- **Order Service**: Manages order creation and lifecycle
- **Payment Service**: Processes payments
- **Inventory Service**: Manages inventory and reservations
- **Fulfillment Service**: Handles order fulfillment
- **Notification Service**: Sends notifications to customers

## Event Flow

Services communicate asynchronously through events using a message broker.

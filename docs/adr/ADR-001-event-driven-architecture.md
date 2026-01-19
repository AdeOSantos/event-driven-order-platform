# ADR-001: Event-Driven Architecture

## Status
Accepted

## Context
We need to build a scalable order processing platform with loose coupling between services.

## Decision
Use event-driven architecture with Kafka as the message broker.

## Consequences
- Services communicate asynchronously through events
- Eventual consistency model
- Better scalability and resilience
- Increased complexity in debugging and monitoring

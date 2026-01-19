# ADR-003: Saga Choreography Pattern

## Status
Accepted

## Context
We need to coordinate distributed transactions across multiple services.

## Decision
Use saga choreography pattern where services react to events without a central orchestrator.

## Consequences
- No single point of failure
- Services have autonomy
- More difficult to understand overall flow
- Harder to add new steps to the saga
- Need careful event design

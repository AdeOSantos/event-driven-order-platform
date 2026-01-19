#!/bin/bash

echo "Starting local development environment..."

# Start Docker services
docker-compose up -d

echo "Waiting for services to be ready..."
sleep 10

# Build all services
./gradlew build

echo "Local development environment is ready!"

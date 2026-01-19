package com.example.order.infrastructure.persistence;

import com.example.order.domain.Order;
import com.example.order.domain.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaOrderRepository extends JpaRepository<Order, UUID>, OrderRepository {
}

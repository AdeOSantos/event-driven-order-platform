package com.adalbertosantos.order.infrastructure.persistence;

import com.adalbertosantos.order.domain.Order;
import com.adalbertosantos.order.domain.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaOrderRepository extends JpaRepository<Order, UUID>, OrderRepository {
}

package com.example.order.application;

import com.example.events.order.OrderCreatedEvent;
import com.example.order.api.OrderController.CreateOrderRequest;
import com.example.order.domain.Order;
import com.example.order.domain.OrderRepository;
import com.example.order.domain.OrderStatus;
import com.example.order.infrastructure.messaging.OrderEventProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer eventProducer;

    public OrderService(OrderRepository orderRepository, OrderEventProducer eventProducer) {
        this.orderRepository = orderRepository;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        double totalAmount = request.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getOrderId(),
                savedOrder.getCustomerId(),
                request.getItems().stream()
                        .map(item -> new OrderCreatedEvent.OrderItem(
                                item.getProductId(),
                                item.getQuantity(),
                                item.getPrice()))
                        .collect(Collectors.toList()),
                totalAmount
        );

        eventProducer.sendOrderCreatedEvent(event);

        return savedOrder;
    }

    public Optional<Order> getOrder(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Optional<Order> cancelOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(OrderStatus.CANCELLED);
                    return orderRepository.save(order);
                });
    }

    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        orderRepository.findById(orderId)
                .ifPresent(order -> {
                    order.setStatus(status);
                    orderRepository.save(order);
                });
    }
}

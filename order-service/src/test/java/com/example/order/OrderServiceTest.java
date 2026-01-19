package com.example.order;

import com.example.order.application.OrderService;
import com.example.order.domain.Order;
import com.example.order.domain.OrderRepository;
import com.example.order.domain.OrderStatus;
import com.example.order.infrastructure.messaging.OrderEventProducer;
import com.example.order.api.OrderController.CreateOrderRequest;
import com.example.order.api.OrderController.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventProducer eventProducer;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, eventProducer);
    }

    @Test
    void testCreateOrder() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(UUID.randomUUID());
        
        OrderItem item = new OrderItem();
        item.setProductId(UUID.randomUUID());
        item.setQuantity(2);
        item.setPrice(50.0);
        request.setItems(List.of(item));

        Order savedOrder = new Order();
        savedOrder.setOrderId(UUID.randomUUID());
        savedOrder.setCustomerId(request.getCustomerId());
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setTotalAmount(100.0);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(100.0, result.getTotalAmount());
        verify(orderRepository).save(any(Order.class));
        verify(eventProducer).sendOrderCreatedEvent(any());
    }

    @Test
    void testGetOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setOrderId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrder(orderId);

        assertTrue(result.isPresent());
        assertEquals(orderId, result.get().getOrderId());
    }

    @Test
    void testCancelOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Optional<Order> result = orderService.cancelOrder(orderId);

        assertTrue(result.isPresent());
        assertEquals(OrderStatus.CANCELLED, result.get().getStatus());
        verify(orderRepository).save(order);
    }
}

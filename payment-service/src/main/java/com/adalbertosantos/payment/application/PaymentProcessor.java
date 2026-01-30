package com.adalbertosantos.payment.application;

import com.adalbertosantos.events.order.OrderCreatedEvent;
import com.adalbertosantos.events.payment.PaymentFailedEvent;
import com.adalbertosantos.events.payment.PaymentSucceededEvent;
import com.adalbertosantos.payment.domain.Payment;
import com.adalbertosantos.payment.domain.Payment.PaymentStatus;
import com.adalbertosantos.payment.domain.PaymentRepository;
import com.adalbertosantos.payment.infrastructure.external.PaymentProviderClient;
import com.adalbertosantos.payment.infrastructure.external.PaymentProviderClient.PaymentResult;
import com.adalbertosantos.payment.infrastructure.messaging.PaymentEventProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PaymentProcessor.class);

    private final PaymentRepository paymentRepository;
    private final PaymentProviderClient paymentProviderClient;
    private final PaymentEventProducer eventProducer;

    public PaymentProcessor(PaymentRepository paymentRepository,
                          PaymentProviderClient paymentProviderClient,
                          PaymentEventProducer eventProducer) {
        this.paymentRepository = paymentRepository;
        this.paymentProviderClient = paymentProviderClient;
        this.eventProducer = eventProducer;
    }

    @Transactional
    @CircuitBreaker(name = "paymentProcessor", fallbackMethod = "processPaymentFallback")
    @Retry(name = "paymentProcessor")
    public void processPayment(OrderCreatedEvent event) {
        logger.info("Processing payment for order: {}", event.getOrderId());

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID());
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getTotalAmount());
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setStatus(PaymentStatus.PROCESSING);

        payment = paymentRepository.save(payment);

        try {
            PaymentResult result = paymentProviderClient.processPayment(
                event.getOrderId(),
                event.getTotalAmount(),
                event.getCustomerId()
            );

            if (result.isSuccess()) {
                payment.setStatus(PaymentStatus.SUCCEEDED);
                payment.setProviderTransactionId(result.getTransactionId());
                paymentRepository.save(payment);

                PaymentSucceededEvent successEvent = new PaymentSucceededEvent(
                    event.getOrderId(),
                    payment.getPaymentId(),
                    payment.getAmount(),
                    payment.getPaymentMethod()
                );
                eventProducer.sendPaymentSucceededEvent(successEvent);
                
                logger.info("Payment succeeded for order: {}", event.getOrderId());
            } else {
                handlePaymentFailure(payment, event, result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("Payment processing failed for order: {}", event.getOrderId(), e);
            handlePaymentFailure(payment, event, e.getMessage());
            throw e;
        }
    }

    private void handlePaymentFailure(Payment payment, OrderCreatedEvent event, String errorMessage) {
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(errorMessage);
        paymentRepository.save(payment);

        PaymentFailedEvent failedEvent = new PaymentFailedEvent(
            event.getOrderId(),
            payment.getPaymentId(),
            errorMessage
        );
        eventProducer.sendPaymentFailedEvent(failedEvent);
        
        logger.warn("Payment failed for order: {} - Reason: {}", event.getOrderId(), errorMessage);
    }

    private void processPaymentFallback(OrderCreatedEvent event, Exception e) {
        logger.error("Circuit breaker activated for order: {}", event.getOrderId(), e);
        
        Payment payment = paymentRepository.findByOrderId(event.getOrderId())
            .orElseGet(() -> {
                Payment newPayment = new Payment();
                newPayment.setPaymentId(UUID.randomUUID());
                newPayment.setOrderId(event.getOrderId());
                newPayment.setAmount(event.getTotalAmount());
                return newPayment;
            });

        handlePaymentFailure(payment, event, "Payment service temporarily unavailable");
    }
}

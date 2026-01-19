package com.example.payment.infrastructure.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class PaymentProviderClient {

    private static final Logger logger = LoggerFactory.getLogger(PaymentProviderClient.class);
    private final Random random = new Random();

    public PaymentResult processPayment(UUID orderId, double amount, UUID customerId) {
        logger.info("Processing payment with external provider - Order: {}, Amount: {}", orderId, amount);

        try {
            Thread.sleep(100 + random.nextInt(200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Payment processing interrupted", e);
        }

        boolean success = random.nextDouble() > 0.1;

        if (success) {
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
            logger.info("Payment provider succeeded - Transaction: {}", transactionId);
            return new PaymentResult(true, transactionId, null);
        } else {
            String errorMessage = "Insufficient funds";
            logger.warn("Payment provider failed - Reason: {}", errorMessage);
            return new PaymentResult(false, null, errorMessage);
        }
    }

    public static class PaymentResult {
        private final boolean success;
        private final String transactionId;
        private final String errorMessage;

        public PaymentResult(boolean success, String transactionId, String errorMessage) {
            this.success = success;
            this.transactionId = transactionId;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

package com.adalbertosantos.notification.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String to, String subject, String body) {
        logger.info("Sending email to: {}", to);
        logger.info("Subject: {}", subject);
        logger.info("Body: {}", body);
        
        // Simulate email sending
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("Email sent successfully to: {}", to);
    }
}

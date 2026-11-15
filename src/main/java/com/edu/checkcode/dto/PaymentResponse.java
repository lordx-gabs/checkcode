package com.edu.checkcode.dto;

import com.edu.checkcode.model.Payment;
import com.edu.checkcode.util.enums.PaymentMethod;
import com.edu.checkcode.util.enums.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        double amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String statusDescription,
        Instant createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getStatusDescription(),
                payment.getCreatedAt()
        );
    }
}

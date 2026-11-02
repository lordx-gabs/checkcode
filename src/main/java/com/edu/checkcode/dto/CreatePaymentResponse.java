package com.edu.checkcode.dto;

import com.edu.checkcode.util.enums.PaymentMethod;
import com.edu.checkcode.util.enums.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record CreatePaymentResponse(
        UUID paymentId,
        double amount,
        PaymentMethod paymentMethod,
        Instant createdAt,
        PaymentStatus status,
        String statusDescription,
        String preferenceId,
        String initPoint
) {
}

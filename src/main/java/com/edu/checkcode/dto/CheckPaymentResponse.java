package com.edu.checkcode.dto;

import com.edu.checkcode.util.enums.PaymentMethod;
import com.edu.checkcode.util.enums.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record CheckPaymentResponse(
        UUID paymentId,
        double amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        String statusDescription,
        Instant createdAt,
        TicketInfo ticket
) {
    public record TicketInfo(
            UUID ticketId,
            Instant createdAt,
            String eventName,
            String eventDescription
    ) {
    }
}

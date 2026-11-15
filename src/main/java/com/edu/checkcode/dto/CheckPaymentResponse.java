package com.edu.checkcode.dto;

import com.edu.checkcode.model.Payment;
import com.edu.checkcode.util.enums.PaymentMethod;
import com.edu.checkcode.util.enums.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record CheckPaymentResponse(
        Payment payment,
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

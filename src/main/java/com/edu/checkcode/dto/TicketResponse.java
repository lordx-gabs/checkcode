package com.edu.checkcode.dto;

import com.edu.checkcode.model.Ticket;
import com.edu.checkcode.util.enums.TicketStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        Instant createdAt,
        TicketStatus ticketStatus,
        EventResponse event,
        PaymentResponse payment
) {
    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getCreatedAt(),
                ticket.getTicketStatus(),
                EventResponse.from(ticket.getEvent(), List.of()),
                PaymentResponse.from(ticket.getPayment())
        );
    }
}

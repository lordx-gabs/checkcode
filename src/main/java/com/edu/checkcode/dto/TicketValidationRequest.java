package com.edu.checkcode.dto;

import java.util.UUID;

public record TicketValidationRequest(
        UUID ticketId
) {
}

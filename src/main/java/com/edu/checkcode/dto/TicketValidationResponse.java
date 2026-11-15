package com.edu.checkcode.dto;

public record TicketValidationResponse(
        boolean allowed,
        String reason
) {
}

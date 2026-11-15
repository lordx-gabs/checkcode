package com.edu.checkcode.controller;

import com.edu.checkcode.dto.TicketResponse;
import com.edu.checkcode.dto.TicketValidationResponse;
import com.edu.checkcode.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
@Tag(name = "Ticket", description = "Ticket validation endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/my-tickets")
    @Operation(summary = "Get my tickets", description = "Returns all tickets of the authenticated user")
    public ResponseEntity<List<TicketResponse>> getMyTickets() {
        List<TicketResponse> tickets = ticketService.getUserTickets();
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/validate/{ticketId}")
    @Operation(summary = "Validate ticket", description = "Validates a ticket for entry/exit")
    public ResponseEntity<TicketValidationResponse> validateTicket(@PathVariable UUID ticketId) {
        TicketValidationResponse response = ticketService.validateTicket(ticketId);
        return ResponseEntity.ok(response);
    }
}

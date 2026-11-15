package com.edu.checkcode.service;

import com.edu.checkcode.dto.TicketResponse;
import com.edu.checkcode.dto.TicketValidationResponse;
import com.edu.checkcode.exception.UnauthorizedException;
import com.edu.checkcode.model.Event;
import com.edu.checkcode.model.Role;
import com.edu.checkcode.model.Ticket;
import com.edu.checkcode.model.User;
import com.edu.checkcode.repository.TicketRepository;
import com.edu.checkcode.repository.UserRepository;
import com.edu.checkcode.util.enums.TicketStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional
    public TicketValidationResponse validateTicket(UUID ticketId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UnauthorizedException("Authentication not found");
        }

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (role == null) {
            throw new UnauthorizedException("Role not found");
        }

        if (!Role.EMPLOYEE.name().equals(role)) {
            throw new UnauthorizedException("Only employees can validate tickets");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElse(null);

        if (ticket == null) {
            return new TicketValidationResponse(false, "Ticket não encontrado");
        }

        Event event = ticket.getEvent();

        if (!isWithinEventTime(event)) {
            return new TicketValidationResponse(false, "Fora do horário do evento");
        }

        if (ticket.getTicketStatus() == TicketStatus.NEW) {
            ticket.setTicketStatus(TicketStatus.CHECKED_IN);
            ticketRepository.save(ticket);
            return new TicketValidationResponse(true, "Entrada liberada");
        }

        if (ticket.getTicketStatus() == TicketStatus.CHECKED_IN) {
            ticket.setTicketStatus(TicketStatus.CHECKED_OUT);
            ticketRepository.save(ticket);
            return new TicketValidationResponse(true, "Saída liberada");
        }

        return new TicketValidationResponse(false, "Ticket já foi utilizado ou cancelado");
    }

    @Transactional()
    public List<TicketResponse> getUserTickets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UnauthorizedException("Authentication not found");
        }

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (role == null) {
            throw new UnauthorizedException("Role not found");
        }

        if (!Role.USER.name().equals(role)) {
            throw new UnauthorizedException("Only users can view their tickets");
        }

        String userId = authentication.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ticketRepository.findByUserId(user.getId()).stream()
                .map(TicketResponse::from)
                .toList();
    }

    private boolean isWithinEventTime(Event event) {
        LocalDate today = LocalDate.now();
        if (!event.getDate().equals(today)) {
            return false;
        }

        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startTime = LocalTime.parse(event.getStartTime(), timeFormatter);
            LocalTime endTime = LocalTime.parse(event.getEndTime(), timeFormatter);
            LocalTime now = LocalTime.now();

            return !now.isBefore(startTime) && !now.isAfter(endTime);
        } catch (Exception e) {
            return false;
        }
    }
}

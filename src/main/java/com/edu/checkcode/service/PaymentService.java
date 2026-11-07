package com.edu.checkcode.service;

import com.edu.checkcode.dto.CheckPaymentResponse;
import com.edu.checkcode.dto.CreatePaymentResponse;
import com.edu.checkcode.exception.UnauthorizedException;
import com.edu.checkcode.model.Event;
import com.edu.checkcode.model.Payment;
import com.edu.checkcode.model.Role;
import com.edu.checkcode.model.Ticket;
import com.edu.checkcode.repository.EventRepository;
import com.edu.checkcode.repository.PaymentRepository;
import com.edu.checkcode.repository.TicketRepository;
import com.edu.checkcode.util.enums.PaymentMethod;
import com.edu.checkcode.util.enums.PaymentStatus;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final EventRepository eventRepository;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;

    @Value("${mercadopago.back-url.success:https://checkcode-acok.onrender.com/api/payment/success}")
    private String successUrl;

    @Value("${mercadopago.back-url.failure:https://checkcode-acok.onrender.com/api/payment/failure}")
    private String failureUrl;

    @Value("${mercadopago.back-url.pending:https://checkcode-acok.onrender.com/api/payment/pending}")
    private String pendingUrl;

    @Transactional
    public CreatePaymentResponse createPayment(UUID eventId) throws MPException, MPApiException {
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
            throw new UnauthorizedException("Only USER can create a payment");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Payment payment = new Payment();
        payment.setEvent(event);
        payment.setAmount(event.getTicketPrice().doubleValue());

        Payment savedPayment = paymentRepository.save(payment);

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .id(savedPayment.getId().toString())
                .title(event.getName())
                .description(event.getDescription())
                .quantity(1)
                .currencyId("BRL")
                .unitPrice(event.getTicketPrice())
                .build();

        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        PreferenceBackUrlsRequest backUrlsRequest = PreferenceBackUrlsRequest.builder()
                .success(successUrl)
                .failure(failureUrl)
                .pending(pendingUrl)
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrlsRequest)
                .autoReturn("approved")
                .externalReference(eventId.toString())
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return new CreatePaymentResponse(
                savedPayment.getId(),
                savedPayment.getAmount(),
                savedPayment.getPaymentMethod(),
                savedPayment.getCreatedAt(),
                savedPayment.getStatus(),
                savedPayment.getStatusDescription(),
                preference.getId(),
                preference.getInitPoint()
        );
    }

    @Transactional
    public void updatePaymentStatus(String paymentId, String externalReferenceId) throws MPException, MPApiException {
        UUID id = UUID.fromString(externalReferenceId);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        PaymentClient paymentClient = new PaymentClient();
        com.mercadopago.resources.payment.Payment mpPayment = paymentClient.get(Long.parseLong(paymentId));

        payment.setPaymentMethod(PaymentMethod.from(mpPayment.getPaymentTypeId().toLowerCase(Locale.ROOT)));
        String status = mpPayment.getStatus();
        String statusDetail = mpPayment.getStatusDetail();

        PaymentStatus paymentStatus = switch (status) {
            case "approved" -> PaymentStatus.PAID;
            case "rejected" -> PaymentStatus.FAILED;
            default -> PaymentStatus.PENDING;
        };

        payment.setStatus(paymentStatus);
        payment.setStatusDescription(statusDetail);
        paymentRepository.save(payment);

        if (paymentStatus == PaymentStatus.PAID) {
            Ticket ticket = Ticket.from(payment);
            ticketRepository.save(ticket);
        }
    }

    @Transactional
    public void processWebhookNotification(String payload) {
        try {
            if (payload.contains("payment_id")) {
                String paymentId = extractValue(payload, "payment_id");
                updatePaymentStatus(paymentId, null);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing webhook notification", e);
        }
    }

    private String extractValue(String payload, String key) {
        String searchKey = "\"" + key + "\":\"";
        int start = payload.indexOf(searchKey);
        if (start == -1) return null;
        start += searchKey.length();
        int end = payload.indexOf("\"", start);
        return payload.substring(start, end);
    }

    public CheckPaymentResponse checkPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Ticket ticket = ticketRepository.findByPaymentId(paymentId)
                .orElse(null);

        CheckPaymentResponse.TicketInfo ticketInfo = null;
        if (ticket != null) {
            ticketInfo = new CheckPaymentResponse.TicketInfo(
                    ticket.getId(),
                    ticket.getCreatedAt(),
                    payment.getEvent().getName(),
                    payment.getEvent().getDescription()
            );
        }

        return new CheckPaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getStatusDescription(),
                payment.getCreatedAt(),
                ticketInfo
        );
    }
}

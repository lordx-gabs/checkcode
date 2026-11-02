package com.edu.checkcode.controller;

import com.edu.checkcode.dto.CheckPaymentResponse;
import com.edu.checkcode.dto.CreatePaymentResponse;
import com.edu.checkcode.service.PaymentService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create/{eventId}")
    @Operation(summary = "Create payment preference", description = "Creates a Mercado Pago payment preference for an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment preference created successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @Parameter(description = "Event ID", required = true)
            @PathVariable("eventId") UUID eventId
    ) throws MPException, MPApiException {
        CreatePaymentResponse response = paymentService.createPayment(eventId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    @Operation(summary = "Webhook endpoint", description = "Receives Mercado Pago payment notifications")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload) {
        try {
            paymentService.processWebhookNotification(payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/success")
    @Operation(summary = "Payment success redirect", description = "Receives redirect after successful payment")
    public ResponseEntity<String> paymentSuccess(
            @RequestParam("collection_id") String collectionId,
            @RequestParam("collection_status") String collectionStatus,
            @RequestParam("payment_id") String paymentId,
            @RequestParam("external_reference") String externalReference,
            @RequestParam("preference_id") String preferenceId
    ) throws MPException, MPApiException {
        paymentService.updatePaymentStatus(paymentId, externalReference);
        return ResponseEntity.ok("Payment successful! Payment ID: " + paymentId);
    }

    @GetMapping("/failure")
    @Operation(summary = "Payment failure redirect", description = "Receives redirect after failed payment")
    public ResponseEntity<String> paymentFailure(
            @RequestParam("collection_id") String collectionId,
            @RequestParam("collection_status") String collectionStatus,
            @RequestParam("payment_id") String paymentId,
            @RequestParam("external_reference") String externalReference,
            @RequestParam("preference_id") String preferenceId
    ) throws MPException, MPApiException {
        paymentService.updatePaymentStatus(paymentId, externalReference);
        return ResponseEntity.ok("Payment failed. Payment ID: " + paymentId);
    }

    @GetMapping("/pending")
    @Operation(summary = "Payment pending redirect", description = "Receives redirect after pending payment")
    public ResponseEntity<String> paymentPending(
            @RequestParam("collection_id") String collectionId,
            @RequestParam("collection_status") String collectionStatus,
            @RequestParam("payment_id") String paymentId,
            @RequestParam("external_reference") String externalReference,
            @RequestParam("preference_id") String preferenceId
    ) {
        return ResponseEntity.ok("Payment pending. Payment ID: " + paymentId);
    }

    @GetMapping("/check/{paymentId}")
    @Operation(summary = "Check payment status", description = "Returns payment status and associated ticket information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<CheckPaymentResponse> checkPayment(
            @Parameter(description = "Payment ID", required = true)
            @PathVariable("paymentId") UUID paymentId
    ) {
        CheckPaymentResponse response = paymentService.checkPayment(paymentId);
        return ResponseEntity.ok(response);
    }
}

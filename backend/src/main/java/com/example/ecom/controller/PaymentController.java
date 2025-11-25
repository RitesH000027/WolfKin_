package com.example.ecom.controller;

import com.example.ecom.dto.*;
import com.example.ecom.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentOrderResponse> createPaymentOrder(
            @Valid @RequestBody CreatePaymentOrderRequest request,
            Authentication authentication) {
        PaymentOrderResponse response = paymentService.createPaymentOrder(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDto> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request,
            Authentication authentication) {
        OrderDto order = paymentService.verifyPayment(request, authentication.getName());
        return ResponseEntity.ok(order);
    }

    @PostMapping("/confirm-cod/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDto> confirmCODOrder(
            @PathVariable Long orderId,
            Authentication authentication) {
        OrderDto order = paymentService.confirmCODOrder(orderId, authentication.getName());
        return ResponseEntity.ok(order);
    }
}

package com.sam.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.sam.dto.PaymentOrderRequest;
import com.sam.dto.PaymentOrderResponse;
import com.sam.dto.PaymentSuccessResponse;
import com.sam.dto.PaymentVerificationRequest;
import com.sam.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(@RequestBody PaymentOrderRequest paymentOrderRequest) throws RazorpayException {
        return new ResponseEntity<>(paymentService.createPaymentOrder(paymentOrderRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/verify")
    public ResponseEntity<PaymentSuccessResponse> verifyPaymentSignature(@RequestBody PaymentVerificationRequest request) throws RazorpayException {
        return new ResponseEntity<>(paymentService.verifyPayment(request),HttpStatus.CREATED);
    }
}

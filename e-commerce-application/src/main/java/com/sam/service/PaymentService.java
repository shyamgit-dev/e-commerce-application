package com.sam.service;
import com.razorpay.RazorpayException;
import com.sam.dto.PaymentOrderRequest;
import com.sam.dto.PaymentOrderResponse;
import com.sam.dto.PaymentSuccessResponse;
import com.sam.dto.PaymentVerificationRequest;

public interface PaymentService {
    PaymentOrderResponse createPaymentOrder(PaymentOrderRequest orderRequest) throws RazorpayException;
    PaymentSuccessResponse verifyPayment(PaymentVerificationRequest paymentVerificationRequest) throws RazorpayException;
}

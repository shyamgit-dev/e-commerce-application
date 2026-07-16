package com.sam.dao;

import com.sam.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}

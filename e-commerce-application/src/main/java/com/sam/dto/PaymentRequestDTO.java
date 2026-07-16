package com.sam.dto;

import com.sam.constant.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {

    @NotBlank(message = "Must not be null")
    private PaymentStatus paymentMethod;
}

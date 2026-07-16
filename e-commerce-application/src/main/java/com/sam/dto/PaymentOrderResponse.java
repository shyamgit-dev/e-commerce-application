package com.sam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrderResponse {

    private String id;
    private String entity;
    private BigDecimal amount;
    private String status;
    private String currency;
    private LocalDateTime createdAt;

}

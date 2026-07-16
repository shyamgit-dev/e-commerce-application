package com.sam.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PaymentOrderRequest {
    private Long orderId;
}

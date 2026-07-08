package com.sam.dto;


import com.sam.constant.AddressType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {

    private String paymentMethod;
    private Long addressId;
}

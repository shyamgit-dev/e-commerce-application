package com.sam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    private long id;

    private BigDecimal subTotal;

    //private UsersDTO user;

    private List<CartItemDTO> cartItems = new ArrayList<>();
}

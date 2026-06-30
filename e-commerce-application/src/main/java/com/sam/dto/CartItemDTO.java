package com.sam.dto;

import com.sam.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    private Long id;

    private Integer quantity;

    //private Cart cart;

    private ProductDTO product;
}

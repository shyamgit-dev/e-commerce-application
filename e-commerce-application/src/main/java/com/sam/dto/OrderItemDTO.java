package com.sam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long productId;

    private Integer quantity;

/*    // Price at the time of purchase
    private BigDecimal unitPrice;

    // quantity × unitPrice
    private BigDecimal subTotal;

    private Order order;

    private Product product;*/
}

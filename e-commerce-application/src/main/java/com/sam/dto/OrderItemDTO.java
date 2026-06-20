package com.sam.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {

    @NotNull(message = "Product Id Is Not Required")
    private Long productId;

    @NotNull
    @Min(value=0,message = "Quantity must be at least one")
    private Integer quantity;

/*    // Price at the time of purchase
    private BigDecimal unitPrice;

    // quantity × unitPrice
    private BigDecimal subTotal;

    private Order order;

    private Product product;*/
}

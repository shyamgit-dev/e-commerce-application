package com.sam.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Name Can't Be Blank")
    @Size(min = 3,max = 40)
    private String name;

    private String description;

    @NotNull(message = "Price Column Can't be blank")
    @DecimalMin(value = "0.0",inclusive = false)
    private BigDecimal price;

    private boolean isActive;

    @NotNull
    @Min(value = 0,message = "Stock Quantity Must greater than or equals to zero")
    private Integer stockQuantity;

    @NotBlank
    private String category;
}

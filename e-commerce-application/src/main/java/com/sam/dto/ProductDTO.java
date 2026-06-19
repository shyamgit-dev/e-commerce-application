package com.sam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private boolean isActive;

    private Integer stockQuantity;

    private String category;
}

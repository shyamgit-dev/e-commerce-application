package com.sam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAvaialabilityDTO {

    private Boolean available;
    private Integer stock;
}

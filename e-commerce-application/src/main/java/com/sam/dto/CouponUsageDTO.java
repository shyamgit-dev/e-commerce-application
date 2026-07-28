package com.sam.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponUsageDTO {

    private String username;

    private Long orderId;

    private LocalDate usedAt;
}

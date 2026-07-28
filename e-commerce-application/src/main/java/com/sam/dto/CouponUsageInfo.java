package com.sam.dto;

import com.sam.constant.DIscountType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponUsageInfo {

    private String code;
    private String description;

    @Enumerated(EnumType.STRING)
    private DIscountType discountType;

    private BigDecimal discountValue;
    private BigDecimal  minimumOrderAmount;

    private BigDecimal  maximumDiscount;

    private LocalDateTime expiryDate;

    private boolean active;

    private Integer usageLimit;

    private Integer usedCount;

    private Integer maxUsesPerUser;

    private Integer remainingUsage;

    private List<CouponUsageDTO> usages;

}

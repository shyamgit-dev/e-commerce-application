package com.sam.entity;

import com.sam.constant.DIscountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    @OneToMany(mappedBy = "coupon",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Order> orders;

    @OneToMany(mappedBy = "coupon",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CouponUsage> couponUsages;
}

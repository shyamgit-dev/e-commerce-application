package com.sam.dto;

import com.sam.entity.Coupon;
import com.sam.entity.Order;
import com.sam.entity.User;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponUsageDTO {

    private UserDTO user;

    private CouponDTO coupon;

    private OrderDTO order;

    private LocalDate usedAt;
}

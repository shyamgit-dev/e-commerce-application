package com.sam.dao;

import com.sam.entity.Coupon;
import com.sam.entity.CouponUsage;
import com.sam.entity.Order;
import com.sam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUsageRepository extends JpaRepository<CouponUsage,Long> {
    boolean existsByUserAndCoupon(User user, Coupon coupon);
    long countByUserAndCoupon(User user,Coupon coupon);
    boolean existsByOrder(Order order);
}

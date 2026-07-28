package com.sam.service;

import com.sam.dto.CouponDTO;
import com.sam.dto.CouponUsageInfo;

import java.util.List;

public interface CouponService {
    CouponDTO addCoupon(CouponDTO couponDTO);
    List<CouponDTO> getAll();
    CouponDTO getCoupon(Long id);
    CouponDTO updateCoupon(Long id,CouponDTO couponDTO);
    void deleteCoupon(Long id);
    Long activateCoupon(Long id);
    Long deactivateCoupon(Long id);
    CouponUsageInfo usageInformation(Long id);
}

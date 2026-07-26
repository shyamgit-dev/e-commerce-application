package com.sam.service;

import com.sam.dto.CouponDTO;

import java.util.List;

public interface CouponService {
    CouponDTO addCoupon(CouponDTO couponDTO);
    List<CouponDTO> getAll();
    CouponDTO getCoupon(Long id);
}

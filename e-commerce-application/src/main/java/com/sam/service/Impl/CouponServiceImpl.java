package com.sam.service.Impl;

import com.sam.dao.CouponRepository;
import com.sam.dto.CouponDTO;
import com.sam.entity.Coupon;
import com.sam.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("couponService")
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final ModelMapper modelMapper;

    private final CouponRepository couponRepository;

    @Override
    public CouponDTO addCoupon(CouponDTO couponDTO) {
        Coupon coupon = modelMapper.map(couponDTO,Coupon.class);
        coupon.setActive(true);
        coupon.setUsedCount(0);
        Coupon addedCoupon = couponRepository.save(coupon);
        return modelMapper.map(addedCoupon, CouponDTO.class);
    }

    @Override
    public List<CouponDTO> getAll() {
        List<Coupon> couponList = couponRepository.findAll();
        List<CouponDTO> couponDTOS = new ArrayList<>();
        couponList.forEach(coupon -> {
            CouponDTO couponDTO = modelMapper.map(coupon,CouponDTO.class);
            couponDTOS.add(couponDTO);
        });
        return couponDTOS;
    }

    @Override
    public CouponDTO getCoupon(Long id) {

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Coupon not found"));
        return modelMapper.map(coupon,CouponDTO.class);
    }
}

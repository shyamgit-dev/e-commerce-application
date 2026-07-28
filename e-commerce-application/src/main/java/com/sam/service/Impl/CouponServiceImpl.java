package com.sam.service.Impl;

import com.sam.dao.CouponRepository;
import com.sam.dto.CouponDTO;
import com.sam.dto.CouponUsageDTO;
import com.sam.dto.CouponUsageInfo;
import com.sam.entity.Coupon;
import com.sam.entity.CouponUsage;
import com.sam.exception.CouponNotFoundException;
import com.sam.service.CouponService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
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
                .orElseThrow(()-> new CouponNotFoundException("Coupon not found"));
        return modelMapper.map(coupon,CouponDTO.class);
    }

    @Transactional
    @Override
    public CouponDTO updateCoupon(Long id, CouponDTO couponDTO) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(()->new CouponNotFoundException("Coupon Not Found"));
        coupon.setId(id);
        coupon.setCode(coupon.getCode());
        coupon.setExpiryDate(couponDTO.getExpiryDate());
        coupon.setUsageLimit(couponDTO.getUsageLimit());
        coupon.setDescription(couponDTO.getDescription());
        coupon.setDiscountType(couponDTO.getDiscountType());
        coupon.setDiscountValue(couponDTO.getDiscountValue());
        coupon.setMaximumDiscount(couponDTO.getMaximumDiscount());
        coupon.setMaxUsesPerUser(couponDTO.getMaxUsesPerUser());
        coupon.setMinimumOrderAmount(couponDTO.getMinimumOrderAmount());
        Coupon updatedCoupon = couponRepository.save(coupon);
        return modelMapper.map(updatedCoupon,CouponDTO.class);
    }

    @Transactional
    @Override
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(()->new CouponNotFoundException("Coupon Not Found"));
        couponRepository.delete(coupon);
    }

    @Transactional
    @Override
    public Long activateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(()->new CouponNotFoundException("Coupon Not Found"));
        coupon.setActive(true);
        return couponRepository.save(coupon).getId();
    }

    @Transactional
    @Override
    public Long deactivateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(()->new CouponNotFoundException("Coupon Not Found"));
        coupon.setActive(false);
        return couponRepository.save(coupon).getId();
    }

    @Override
    public CouponUsageInfo usageInformation(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(()->new CouponNotFoundException("Coupon Not Found"));
        CouponUsageInfo couponUsageInfo = modelMapper.map(coupon,CouponUsageInfo.class);
        couponUsageInfo.setRemainingUsage(coupon.getUsageLimit()-coupon.getUsedCount());

        List<CouponUsageDTO> couponUsageDTOS = new ArrayList<>();

        for(CouponUsage couponUsage:coupon.getCouponUsages())
        {
            CouponUsageDTO couponUsageDTO = new CouponUsageDTO();
            couponUsageDTO.setOrderId(couponUsage.getOrder().getId());
            couponUsageDTO.setUsername(couponUsage.getUser().getUsername());
            couponUsageDTO.setUsedAt(couponUsage.getUsedAt());
            couponUsageDTOS.add(couponUsageDTO);
        }

        couponUsageInfo.setUsages(couponUsageDTOS);

        return couponUsageInfo;
    }

}

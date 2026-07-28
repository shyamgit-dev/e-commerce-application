package com.sam.controller;

import com.sam.dto.CouponDTO;
import com.sam.dto.CouponUsageInfo;
import com.sam.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/coupons")
    public ResponseEntity<CouponDTO> addCoupons(@RequestBody CouponDTO couponDTO)
    {
        return new ResponseEntity<>(couponService.addCoupon(couponDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/coupons")
    public ResponseEntity<List<CouponDTO>> getAll()
    {
        return new ResponseEntity<>(couponService.getAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/coupons/{id}")
    public ResponseEntity<CouponDTO> getCoupon(@PathVariable Long id)
    {
        return new ResponseEntity<>(couponService.getCoupon(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/coupons/{id}")
    public ResponseEntity<CouponDTO> updateCoupon(@PathVariable Long id,@RequestBody CouponDTO couponDTO)
    {
        return new ResponseEntity<>(couponService.updateCoupon(id,couponDTO), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<String> deleteCoupon(@PathVariable Long id)
    {
        couponService.deleteCoupon(id);
        String result = "Coupon is deleted having Id "+id;
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/coupons/{id}/activate")
    public ResponseEntity<String> activateCoupon(@PathVariable Long id)
    {
        couponService.activateCoupon(id);
        String result = "Coupon is activated having id "+id;
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/coupons/{id}/deactivate")
    public ResponseEntity<String> deActivateCoupon(@PathVariable Long id)
    {
        couponService.deactivateCoupon(id);
        String result = "Coupon is deactivated having id "+id;
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize(("hasRole('ADMIN')"))
    @GetMapping("/coupons/{id}/usage")
    public ResponseEntity<CouponUsageInfo> usageInformation(@PathVariable Long id)
    {
        return new ResponseEntity<>(couponService.usageInformation(id),HttpStatus.OK);
    }
}

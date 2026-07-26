package com.sam.controller;

import com.sam.dto.CouponDTO;
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
}

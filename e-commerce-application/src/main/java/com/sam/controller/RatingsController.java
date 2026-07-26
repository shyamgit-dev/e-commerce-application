package com.sam.controller;

import com.sam.dto.ReviewDTO;
import com.sam.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RatingsController {

    private final ReviewService reviewService;

    @PreAuthorize("hasRole('USER')")
    @PutMapping("products/{productId}/reviews")
    public ResponseEntity<ReviewDTO> postReviews(@PathVariable Long productId,@Valid @RequestBody ReviewDTO reviewDTO)
    {
         return new ResponseEntity<>(reviewService.postReview(productId,reviewDTO), HttpStatus.CREATED);
    }

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReview(@PathVariable Long productId)
    {
        return new ResponseEntity<>(reviewService.getReview(productId),HttpStatus.OK);
    }
}

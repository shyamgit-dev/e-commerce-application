package com.sam.service;

import com.sam.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {
    ReviewDTO postReview(Long productId,ReviewDTO reviewDTO);
    List<ReviewDTO> getReview(Long productId);
}

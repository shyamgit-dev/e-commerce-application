package com.sam.service.Impl;

import com.sam.constant.OrderStatus;
import com.sam.dao.OrderRepository;
import com.sam.dao.ProductRepository;
import com.sam.dao.ReviewRepository;
import com.sam.dto.ReviewDTO;
import com.sam.entity.*;
import com.sam.exception.InvalidActionException;
import com.sam.exception.ProductNotFoundException;
import com.sam.service.ReviewService;
import com.sam.utility.SecurityIntegration;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service("reviewService")
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    private final SecurityIntegration securityIntegration;

    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public ReviewDTO postReview(Long productId,ReviewDTO reviewDTO) {

        //Authenticated User
        User user = securityIntegration.getAuthenticatedUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ProductNotFoundException("Product Not Found"));

        boolean result = orderRepository.existsDeliveredOrderForUserAndProduct(user,productId,OrderStatus.DELIVERED);

        if(!result) throw new InvalidActionException("Product is not purchased");

        Optional<Review> review = reviewRepository.findByUserAndProduct(user,product);

        if(review.isPresent())
        {
            Review updateReview = review.get();
            updateReview.setComments(reviewDTO.getComments());
            updateReview.setRating(reviewDTO.getRating());
            Review updatedReview = reviewRepository.save(updateReview);
            updateAverageRatings(product);
            return modelMapper.map(updatedReview,ReviewDTO.class);
        }
        Review newReview = new Review();
        newReview.setComments(reviewDTO.getComments());
        newReview.setRating(reviewDTO.getRating());
        newReview.setUser(user);
        newReview.setProduct(product);
        newReview.setCreatedAt(LocalDateTime.now());
        product.getReviews().add(newReview);
        Review postedReview = reviewRepository.save(newReview);
        updateAverageRatings(product);

        ReviewDTO dto = modelMapper.map(postedReview,ReviewDTO.class);
        dto.setUsername(postedReview.getUser().getUsername());

        return dto;
    }

    @Override
    public List<ReviewDTO> getReview(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ProductNotFoundException("Product not found"));

        List<Review> reviews = reviewRepository.findByProduct(product);

        List<ReviewDTO> reviewDTOS = new ArrayList<>();

        reviews.forEach(review -> {
            ReviewDTO reviewDTO =modelMapper.map(review,ReviewDTO.class);
            reviewDTO.setUsername(review.getUser().getUsername());
            reviewDTOS.add(reviewDTO);
        });
        return reviewDTOS;
    }

    public void updateAverageRatings(Product product)
    {
        double averageRatings = product.getReviews()
                .stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        product.setAverageRating(averageRatings);
    }
}

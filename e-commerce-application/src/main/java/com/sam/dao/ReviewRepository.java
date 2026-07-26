package com.sam.dao;


import com.sam.entity.Product;
import com.sam.entity.Review;
import com.sam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    Optional<Review> findByUserAndProduct(User user, Product product);

    List<Review> findByProduct(Product product);
}

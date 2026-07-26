package com.sam.dao;

import com.sam.constant.OrderStatus;
import com.sam.entity.Order;
import com.sam.entity.Product;
import com.sam.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("SELECT o FROM Order o WHERE o.user.userId=:userId")
    List<Order> findByUserId(@Param("userId") Long userId);

    Page<Order> findAll(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.userId=:userId and o.id=:orderId")
    Optional<Order> findByUserAndOrder(@Param("userId") Long userId, @Param("orderId") Long orderId);

    List<Order> findByStatus(OrderStatus orderStatus);

    List<Order> findByOrderDateBetween(LocalDateTime start,LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.status=DELIVERED")
    List<Order> findByDeliveredStatus();

    @Query("""
    SELECT COUNT(o) > 0
    FROM Order o
    JOIN o.orderItems oi
    WHERE o.user = :user
      AND oi.product.id = :productId
      AND o.status = :status
    """)
    boolean existsDeliveredOrderForUserAndProduct(
            @Param("user") User user,
            @Param("productId") Long productId,
            @Param("status") OrderStatus status
    );
}

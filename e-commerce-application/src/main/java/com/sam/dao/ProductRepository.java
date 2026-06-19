package com.sam.dao;

import com.sam.dto.ProductDTO;
import com.sam.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {

    @Query("SELECT p FROM Product p WHERE p.id=:productId and p.stockQuantity>:quantity")
    Optional<Product> findbyProductAndQuantity(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    List<Product> searchProduct(@Param("keyword") String keyword);

    //Both are same searchProduct
    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByPriceGreaterThanEqual(BigDecimal price);

    List<Product> findByPriceLessThanEqual(BigDecimal price);

    List<Product> findByPriceBetween(BigDecimal sPrice,BigDecimal ePrice);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity>0")
    List<Product> searchProductInStock();

    @Query("SELECT p FROM Product p WHERE p.stockQuantity=:quantity")
    List<Product> fetchProductOutOfStock(Integer quantity);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity<=:threshold")
    List<Product> fetchProductLowfStock(Integer threshold);

    List<Product> findByCategoryIgnoreCase(String category);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByOrderByPriceDesc(Pageable pageable);
}

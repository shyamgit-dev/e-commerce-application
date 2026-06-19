package com.sam.service;

import com.sam.dto.ProductAvaialabilityDTO;
import com.sam.dto.ProductDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    public ProductDTO post(ProductDTO productDTO);
    public ProductDTO get(Long id);
    public List<ProductDTO> getAll();
    public ProductDTO updateProduct(Long productId,ProductDTO productDTO);
    public Long deleteProduct(Long productId);
    public List<ProductDTO> searchProduct(String keyword);
    public List<ProductDTO> findByPriceLesserThanEqual(BigDecimal price);
    public List<ProductDTO> findByPriceGreaterThanEqual(BigDecimal price);
    public List<ProductDTO> findByPriceBetween(BigDecimal startingPrice,BigDecimal endingPrice);
    public List<ProductDTO> inStockProducts();
    public List<ProductDTO> postAll(List<ProductDTO> productDTOS);
    public List<ProductDTO> getByCategory(String category);
    public Page<ProductDTO> getAllOptimized(int pageNumber, int pageSize, String input);
    public List<ProductDTO> fetchOutOfStockProducts(Integer quantity);
    public List<ProductDTO> fetchLowStockProducts(Integer threshold);
    public List<ProductDTO> sortByInput(String sortFiled);
    public List<ProductDTO> sortPriceASC();
    public List<ProductDTO> sortPriceDESC();
    public Page<ProductDTO> topExpensiveProducts(int pageNumber,int pageSize);
    public ProductAvaialabilityDTO checkProductAvailability(Long productId);
    public ProductDTO softDeleteProduct(Long productId);
}

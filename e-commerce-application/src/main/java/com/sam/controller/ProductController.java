package com.sam.controller;

import com.sam.dto.ProductAvaialabilityDTO;
import com.sam.dto.ProductDTO;
import com.sam.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDTO> post(@Valid @RequestBody ProductDTO productDTO)
    {
        return new ResponseEntity<>(productService.post(productDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> get(@PathVariable Long id)
    {
        return new ResponseEntity<>(productService.get(id),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAll()
    {
        return new ResponseEntity<>(productService.getAll(),HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") Long productId,@Valid @RequestBody ProductDTO productDTO)
    {
        return new ResponseEntity<>(productService.updateProduct(productId,productDTO),HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id)
    {
        Long result = productService.deleteProduct(id);
        String productId = "Product with id "+result+" has been deleted";
        return new ResponseEntity<String>(productId,HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProduct(@RequestParam String name)
    {
        List<ProductDTO> productDTOS = productService.searchProduct(name);
        return new ResponseEntity<>(productDTOS,HttpStatus.OK);
    }

    @GetMapping("/search/min")
    public ResponseEntity<List<ProductDTO>> findByPriceLesserThanEqual(@RequestParam BigDecimal minPrice)
    {
        List<ProductDTO> productDTOS = productService.findByPriceLesserThanEqual(minPrice);
        return new ResponseEntity<>(productDTOS,HttpStatus.OK);
    }

    @GetMapping("/search/max")
    public ResponseEntity<List<ProductDTO>> findByPriceGreaterThanEqual(@RequestParam BigDecimal maxPrice)
    {
        List<ProductDTO> productDTOS = productService.findByPriceGreaterThanEqual(maxPrice);
        return new ResponseEntity<>(productDTOS,HttpStatus.OK);
    }

    @GetMapping("/search/between")
    public ResponseEntity<List<ProductDTO>> findByPriceRange(@RequestParam BigDecimal minPrice,@RequestParam BigDecimal maxPrice )
    {
        List<ProductDTO> productDTOS = productService.findByPriceBetween(minPrice,maxPrice);
        return new ResponseEntity<>(productDTOS,HttpStatus.OK);
    }

    @GetMapping("/in-stock")
    public ResponseEntity<List<ProductDTO>> stockAvailable()
    {
        return new ResponseEntity<>(productService.inStockProducts(),HttpStatus.OK);
    }

    @PostMapping("/bulk-insert")
    public ResponseEntity<List<ProductDTO>> bulkCreate(@Valid @RequestBody List<ProductDTO> productDTOS)
    {
        return new ResponseEntity<>(productService.postAll(productDTOS),HttpStatus.CREATED);
    }

    @GetMapping("/category")
    public ResponseEntity<List<ProductDTO>> getByCategory(@RequestParam("category") String category)
    {
        return new ResponseEntity<>(productService.getByCategory(category),HttpStatus.OK);
    }

    @GetMapping("/optimized")
    public ResponseEntity<Page<ProductDTO>> getAllPagination(
           @RequestParam(defaultValue = "0") int pageSize,
           @RequestParam(defaultValue = "4") int pageNumber,
           @RequestParam(defaultValue = "id") String input)
    {
        return new ResponseEntity<>(productService.getAllOptimized(pageNumber,pageSize,input),HttpStatus.OK);
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<List<ProductDTO>> fetchOutOfStockProducts(
           @RequestParam(defaultValue = "0") Integer quantity)
    {
        return new ResponseEntity<>(productService.fetchOutOfStockProducts(quantity),HttpStatus.OK);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> fetchLowStockProducts(
           @RequestParam(defaultValue = "5") Integer threshold)
    {
        return new ResponseEntity<>(productService.fetchLowStockProducts(threshold),HttpStatus.OK);
    }

    @GetMapping("/sort")
    public ResponseEntity<List<ProductDTO>> sortByPrice(
           @RequestParam(defaultValue = "id") String sort)
    {
       return new ResponseEntity<>(productService.sortByInput(sort),HttpStatus.OK);
    }

    @GetMapping("/sort/price/asc")
    public ResponseEntity<List<ProductDTO>> sortPriceByASC()
    {
        return new ResponseEntity<>(productService.sortPriceASC(),HttpStatus.OK);
    }

    @GetMapping("/sort/price/desc")
    public ResponseEntity<List<ProductDTO>> sortPriceByDESC()
    {
        return new ResponseEntity<>(productService.sortPriceDESC(),HttpStatus.OK);
    }

    @GetMapping("/top-expensive")
    public ResponseEntity<Page<ProductDTO>> topExpensiveProducts(
           @RequestParam(defaultValue = "0") int pageNumber,
           @RequestParam(defaultValue = "4") int pageSize)
    {
        return new ResponseEntity<>(productService.topExpensiveProducts(pageNumber,pageSize),HttpStatus.OK);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<ProductAvaialabilityDTO> checkAvailableStatus(@PathVariable Long id)
    {
         return new ResponseEntity<>(productService.checkProductAvailability(id),HttpStatus.OK);
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<ProductDTO> softDelete(@Valid @PathVariable Long id)
    {
        return new ResponseEntity<>(productService.softDeleteProduct(id),HttpStatus.OK);
    }
}

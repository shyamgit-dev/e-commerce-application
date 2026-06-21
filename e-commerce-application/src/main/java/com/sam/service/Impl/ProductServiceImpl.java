package com.sam.service.Impl;

import com.sam.dao.ProductRepository;
import com.sam.dto.ProductAvaialabilityDTO;
import com.sam.dto.ProductDTO;
import com.sam.entity.Product;
import com.sam.exception.InsufficientStockException;
import com.sam.exception.ProductNotFoundException;
import com.sam.exception.SearchResultNotFoundException;
import com.sam.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("productService")
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    @Override
    public ProductDTO post(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO,Product.class);
        Product savedProduct  =productRepository.save(product);
        log.info("Product created with id {}",product.getId());
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO get(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("No Product Found With this id "+id));
        log.debug("Fetched product with id {}",id);
        return modelMapper.map(product,ProductDTO.class);
    }

    @Override
    public List<ProductDTO> getAll() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = new ArrayList<>();
        products.forEach(product -> {
            ProductDTO productDTO = modelMapper.map(product,ProductDTO.class);
            productDTOS.add(productDTO);
        });
        return productDTOS;
    }

    @Transactional
    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ProductNotFoundException("No Product Found With this id "+productId));
        modelMapper.map(productDTO,product);
        Product updatedProduct =productRepository.save(product);
        log.trace("Updated product with Id {}",productId);
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }

    @Transactional
    @Override
    public Long deleteProduct(Long productId) {
        log.info("Entering to delete product with id {}",productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ProductNotFoundException("No Product Found With this id "+productId));
        productRepository.delete(product);
        return productId;
    }

    @Override
    public List<ProductDTO> searchProduct(String name) {
        List<Product> products= productRepository.findByNameContainingIgnoreCase(name);
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> findByPriceLesserThanEqual(BigDecimal price) {
        List<Product> products= productRepository.findByPriceLessThanEqual(price);
        if(products.isEmpty()) throw new SearchResultNotFoundException("No Product in the price range less than "+price);
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> findByPriceGreaterThanEqual(BigDecimal price) {
        List<Product> products= productRepository.findByPriceGreaterThanEqual(price);
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> findByPriceBetween(BigDecimal startingPrice, BigDecimal endingPrice) {
        List<Product> products= productRepository.findByPriceBetween(startingPrice,endingPrice);
        if(products.isEmpty()) throw new SearchResultNotFoundException("No Product Lies in range of  INR"+startingPrice+" and INR"+endingPrice);
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> inStockProducts() {
        List<Product> products= productRepository.searchProductInStock();
        if(products.isEmpty()) throw new InsufficientStockException("All Products Are Out Of Stock Currently");
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> postAll(List<ProductDTO> productDTOS) {
        List<Product> products = productDTOS.stream()
                .map(dto->modelMapper.map(dto,Product.class))
                .toList();
        List<Product> savedProducts =productRepository.saveAll(products);
        return savedProducts.stream()
                .map(product ->modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> getByCategory(String category) {
        List<Product> products = productRepository.findByCategoryIgnoreCase(category);
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public Page<ProductDTO> getAllOptimized(int pageNumber,int pageSize,String input)
    {
        PageRequest pageRequest = PageRequest.of(
                pageNumber,
                pageSize
                ,Sort.by(input).ascending()
        );
        Page<Product> products = productRepository.findAll(pageRequest);
        return products.map(product -> modelMapper.map(product,ProductDTO.class));
    }

    @Override
    public List<ProductDTO> fetchOutOfStockProducts(Integer quantity) {
        List<Product> products = productRepository.fetchProductOutOfStock(quantity);
        log.warn("Following Products Are Out Of Stock Fill the STOCKs !!");
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> fetchLowStockProducts(Integer threshold) {
        List<Product> products = productRepository.fetchProductLowfStock(threshold);
        log.warn("Following Products Quantity Is Lesser Than Threshold Upstock them ASAP!!");
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> sortByInput(String sortFiled)
    {
        List<Product> products = productRepository.findAll(Sort.by(sortFiled).ascending());
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> sortPriceASC() {
        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.ASC,"price"));
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> sortPriceDESC() {
        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.DESC,"price"));
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public Page<ProductDTO> topExpensiveProducts(int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<Product> products = productRepository.findByOrderByPriceDesc(pageRequest);
        return products.map(product -> modelMapper.map(product,ProductDTO.class));
    }

    @Override
    public ProductAvaialabilityDTO checkProductAvailability(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ProductNotFoundException("No product found with given productId "+productId));
        ProductAvaialabilityDTO productAvaialabilityDTO = new ProductAvaialabilityDTO();
        productAvaialabilityDTO.setStock(product.getStockQuantity());
        boolean isAvailable;
        isAvailable = product.getStockQuantity() > 0;
        productAvaialabilityDTO.setAvailable(isAvailable);
        return productAvaialabilityDTO;
    }

    @Transactional
    @Override
    public ProductDTO softDeleteProduct(Long productId) {
        log.info("Entering to soft delete product with Id {}",productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ProductNotFoundException("No product found with given productId "+productId));
        String name = '('+"deleted"+")";
        product.setName(product.getName()+name);
        product.setActive(false);
        Product savedProduct  = productRepository.save(product);
        log.info("Sof deleted product with Id {}",savedProduct.getId());
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

}

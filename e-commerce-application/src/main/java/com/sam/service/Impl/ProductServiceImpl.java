package com.sam.service.Impl;

import com.sam.dao.ProductRepository;
import com.sam.dto.ProductAvaialabilityDTO;
import com.sam.dto.ProductDTO;
import com.sam.entity.Product;
import com.sam.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service("productService")
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    @Override
    public ProductDTO post(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO,Product.class);
        Product savedProduct  =productRepository.save(product);
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO get(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("No Product Found With this id"));
        return modelMapper.map(product,ProductDTO.class);
    }

    @Override
    public List<ProductDTO> getAll() {
        List<Product> products = productRepository.findAll();
        if(products.isEmpty()) throw new RuntimeException("No product Found");
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
                .orElseThrow(()-> new RuntimeException("Product Not Found"));
        modelMapper.map(productDTO,product);
        Product updatedProduct =productRepository.save(product);
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }

    @Transactional
    @Override
    public Long deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new RuntimeException("Product Not Found"));
        productRepository.delete(product);
        return productId;
    }

    @Override
    public List<ProductDTO> searchProduct(String name) {
        List<Product> products= productRepository.findByNameContainingIgnoreCase(name);
        if(products.isEmpty()) throw new RuntimeException("No Search Result");
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> findByPriceLesserThanEqual(BigDecimal price) {
        List<Product> products= productRepository.findByPriceLessThanEqual(price);
        if(products.isEmpty()) throw new RuntimeException("No Product In this range");
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> findByPriceGreaterThanEqual(BigDecimal price) {
        List<Product> products= productRepository.findByPriceGreaterThanEqual(price);
        if(products.isEmpty()) throw new RuntimeException("No Product In this range");
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> findByPriceBetween(BigDecimal startingPrice, BigDecimal endingPrice) {
        List<Product> products= productRepository.findByPriceBetween(startingPrice,endingPrice);
        if(products.isEmpty()) throw new RuntimeException("No Product In this range");
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> inStockProducts() {
        List<Product> products= productRepository.searchProductInStock();
        if(products.isEmpty()) throw new RuntimeException("All Products Are Out Of Stock Currently");
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
        if(products.isEmpty()) throw  new RuntimeException("No Product Found");
        return products.map(product -> modelMapper.map(product,ProductDTO.class));
    }

    @Override
    public List<ProductDTO> fetchOutOfStockProducts(Integer quantity) {
        List<Product> products = productRepository.fetchProductOutOfStock(quantity);
        return products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> fetchLowStockProducts(Integer threshold) {
        List<Product> products = productRepository.fetchProductLowfStock(threshold);
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
                .orElseThrow(()->new RuntimeException("No product found with given productId"));
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
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new RuntimeException("No Product Found with given Id"));
        String name = '('+"deleted"+")";
        product.setName(product.getName()+name);
        product.setActive(false);
        Product savedProduct  = productRepository.save(product);
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

}

package com.example.ecom.service;

import com.example.ecom.dto.CreateProductRequest;
import com.example.ecom.dto.ProductDto;
import com.example.ecom.entity.Category;
import com.example.ecom.entity.Product;
import com.example.ecom.repository.CategoryRepository;
import com.example.ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Cacheable(value = "products", key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort.toString()")
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable)
                .map(ProductDto::from);
    }

    @Cacheable(value = "products", key = "'search_' + #query + '_' + #pageable.pageNumber")
    public Page<ProductDto> searchProducts(String query, Pageable pageable) {
        if (!StringUtils.hasText(query)) {
            return getAllProducts(pageable);
        }
        return productRepository.findBySearchQuery(query, pageable)
                .map(ProductDto::from);
    }

    public Page<ProductDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategory_Id(categoryId, pageable)
                .map(ProductDto::from);
    }

    public Page<ProductDto> filterProducts(Long categoryId, Integer minPrice, Integer maxPrice, 
                                          String query, Pageable pageable) {
        // Convert price from dollars to cents
        Integer minPriceCents = minPrice != null ? (int)(minPrice * 100) : 0;
        Integer maxPriceCents = maxPrice != null ? (int)(maxPrice * 100) : Integer.MAX_VALUE;
        
        return productRepository.findByFilters(categoryId, minPriceCents, maxPriceCents, 
                                              query != null ? query : "", pageable)
                .map(ProductDto::from);
    }

    @Cacheable(value = "product", key = "#id")
    public Optional<ProductDto> getProductById(Long id) {
        return productRepository.findById(id)
                .filter(Product::getIsActive)
                .map(ProductDto::from);
    }

    @Cacheable(value = "product", key = "'slug_' + #slug")
    public Optional<ProductDto> getProductBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .filter(Product::getIsActive)
                .map(ProductDto::from);
    }

    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public ProductDto createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));

        String slug = generateSlug(request.getName());
        
        Product product = Product.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .priceCents((int)(request.getPrice() * 100))
                .inventoryCount(request.getInventoryCount())
                .imageUrl(request.getImageUrl())
                .category(category)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Created new product: {} with slug: {}", savedProduct.getName(), savedProduct.getSlug());
        
        return ProductDto.from(savedProduct);
    }

    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public ProductDto updateProduct(Long id, CreateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPriceCents((int)(request.getPrice() * 100));
        product.setInventoryCount(request.getInventoryCount());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setUpdatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);
        log.info("Updated product: {}", savedProduct.getName());
        
        return ProductDto.from(savedProduct);
    }

    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        product.setIsActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
        
        log.info("Soft deleted product: {}", product.getName());
    }

    private String generateSlug(String name) {
        String baseSlug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .trim();
        
        String slug = baseSlug;
        int counter = 1;
        
        while (productRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }
}
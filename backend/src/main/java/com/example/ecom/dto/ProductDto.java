package com.example.ecom.dto;

import com.example.ecom.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Serializable {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Double price;
    private Integer inventoryCount;
    private String imageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private CategoryDto category;

    public static ProductDto from(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPriceInDollars())
                .inventoryCount(product.getInventoryCount())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .category(product.getCategory() != null ? CategoryDto.from(product.getCategory()) : null)
                .build();
    }
}
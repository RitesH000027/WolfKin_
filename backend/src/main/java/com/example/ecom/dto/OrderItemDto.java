package com.example.ecom.dto;

import com.example.ecom.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto implements Serializable {
    private Long id;
    private Long orderId;
    private Long productId;
    private Double price;
    private Integer quantity;
    private Double subtotal;
    private ProductDto product;

    public static OrderItemDto from(OrderItem orderItem) {
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .productId(orderItem.getProduct().getId())
                .price(orderItem.getPriceInDollars())
                .quantity(orderItem.getQuantity())
                .subtotal(orderItem.getSubtotalInDollars())
                .product(orderItem.getProduct() != null ? ProductDto.from(orderItem.getProduct()) : null)
                .build();
    }
}
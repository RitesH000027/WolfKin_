package com.example.ecom.dto;

import com.example.ecom.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto implements Serializable {
    private Long id;
    private Long userId;
    private Double total;
    private String status;
    private String shippingAddress;
    private String paymentIntentId;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
    private UserDto user;

    public static OrderDto from(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .total(order.getTotalInDollars())
                .status(order.getStatus().name())
                .shippingAddress(order.getShippingAddressJson())
                .paymentIntentId(order.getPaymentIntentId())
                .createdAt(order.getCreatedAt())
                .items(order.getOrderItems() != null ? 
                       order.getOrderItems().stream()
                           .map(OrderItemDto::from)
                           .collect(Collectors.toList()) : null)
                .user(order.getUser() != null ? UserDto.from(order.getUser()) : null)
                .build();
    }
}
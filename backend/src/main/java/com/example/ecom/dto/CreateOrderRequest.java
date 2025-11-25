package com.example.ecom.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
    
    @NotNull(message = "Shipping address is required")
    @Valid
    private ShippingAddressRequest shippingAddress;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;
        
        @NotNull(message = "Quantity is required")
        @jakarta.validation.constraints.Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingAddressRequest {
        @jakarta.validation.constraints.NotBlank(message = "Full name is required")
        private String fullName;
        
        @jakarta.validation.constraints.NotBlank(message = "Address line 1 is required")
        private String addressLine1;
        
        private String addressLine2;
        
        @jakarta.validation.constraints.NotBlank(message = "City is required")
        private String city;
        
        @jakarta.validation.constraints.NotBlank(message = "State is required")
        private String state;
        
        @jakarta.validation.constraints.NotBlank(message = "Postal code is required")
        private String postalCode;
        
        @jakarta.validation.constraints.NotBlank(message = "Country is required")
        private String country;
    }
}
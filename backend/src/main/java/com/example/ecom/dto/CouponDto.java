package com.example.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto {
    private Long id;
    private String code;
    private String description;
    private String discountType;
    private Integer discountValue;
    private Integer minOrderCents;
    private Integer maxDiscountCents;
    private Boolean isActive;
}

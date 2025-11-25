package com.example.ecom.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private Integer discountValue; // Percentage or amount in cents

    @Column(nullable = false)
    private Integer minOrderCents; // Minimum order amount to apply coupon

    @Column
    private Integer maxDiscountCents; // Maximum discount amount (for percentage type)

    @Column
    private Integer usageLimit; // Null means unlimited

    @Column(nullable = false)
    private Integer usedCount = 0;

    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Column(nullable = false)
    private LocalDateTime validTo;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }

    public boolean isValid() {
        if (!isActive) return false;
        if (usageLimit != null && usedCount >= usageLimit) return false;
        
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(validFrom) && !now.isAfter(validTo);
    }

    public int calculateDiscount(int orderAmountCents) {
        if (!isValid()) return 0;
        if (orderAmountCents < minOrderCents) return 0;

        int discount;
        if (discountType == DiscountType.PERCENTAGE) {
            discount = (orderAmountCents * discountValue) / 100;
            if (maxDiscountCents != null && discount > maxDiscountCents) {
                discount = maxDiscountCents;
            }
        } else {
            discount = discountValue;
        }

        return Math.min(discount, orderAmountCents);
    }
}

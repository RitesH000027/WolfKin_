package com.example.ecom.service;

import com.example.ecom.dto.ApplyCouponRequest;
import com.example.ecom.dto.CouponDto;
import com.example.ecom.dto.CouponValidationResponse;
import com.example.ecom.entity.Coupon;
import com.example.ecom.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public CouponValidationResponse validateCoupon(ApplyCouponRequest request) {
        Coupon coupon = couponRepository.findByCodeAndIsActive(request.getCouponCode().toUpperCase(), true)
                .orElse(null);

        if (coupon == null) {
            return new CouponValidationResponse(
                    false,
                    "Invalid or expired coupon code",
                    0,
                    request.getOrderAmountCents(),
                    null
            );
        }

        if (!coupon.isValid()) {
            return new CouponValidationResponse(
                    false,
                    "Coupon is expired or usage limit reached",
                    0,
                    request.getOrderAmountCents(),
                    null
            );
        }

        if (request.getOrderAmountCents() < coupon.getMinOrderCents()) {
            double minAmount = coupon.getMinOrderCents() / 100.0;
            return new CouponValidationResponse(
                    false,
                    String.format("Minimum order amount is $%.2f", minAmount),
                    0,
                    request.getOrderAmountCents(),
                    null
            );
        }

        int discount = coupon.calculateDiscount(request.getOrderAmountCents());
        int finalAmount = request.getOrderAmountCents() - discount;

        CouponDto couponDto = mapToDto(coupon);
        
        return new CouponValidationResponse(
                true,
                "Coupon applied successfully",
                discount,
                finalAmount,
                couponDto
        );
    }

    @Transactional
    public void incrementUsageCount(String couponCode) {
        couponRepository.findByCode(couponCode.toUpperCase())
                .ifPresent(coupon -> {
                    coupon.setUsedCount(coupon.getUsedCount() + 1);
                    couponRepository.save(coupon);
                    log.info("Coupon {} usage incremented to {}", couponCode, coupon.getUsedCount());
                });
    }

    private CouponDto mapToDto(Coupon coupon) {
        return new CouponDto(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountType().name(),
                coupon.getDiscountValue(),
                coupon.getMinOrderCents(),
                coupon.getMaxDiscountCents(),
                coupon.getIsActive()
        );
    }
}

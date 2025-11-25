package com.example.ecom.repository;

import com.example.ecom.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCodeAndIsActive(String code, Boolean isActive);
    Optional<Coupon> findByCode(String code);
}

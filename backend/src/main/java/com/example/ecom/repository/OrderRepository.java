package com.example.ecom.repository;

import com.example.ecom.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<Order> findByUser_IdAndStatusOrderByCreatedAtDesc(Long userId, Order.OrderStatus status);
    
    Page<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status, Pageable pageable);
    
    @Query("SELECT SUM(o.totalCents) FROM Order o WHERE o.status = :status")
    Long getTotalRevenueByStatus(@Param("status") Order.OrderStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startDate")
    long countOrdersSince(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT SUM(o.totalCents) FROM Order o WHERE o.createdAt >= :startDate AND o.status = :status")
    Long getRevenueSince(@Param("startDate") LocalDateTime startDate, @Param("status") Order.OrderStatus status);
}
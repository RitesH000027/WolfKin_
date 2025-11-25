package com.example.ecom.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @Column(name = "price_cents", nullable = false)
    private Integer priceCents;

    @Column(nullable = false)
    private Integer quantity;

    // Helper method to get price in dollars
    public Double getPriceInDollars() {
        return priceCents / 100.0;
    }

    // Helper method to set price from dollars
    public void setPriceFromDollars(Double price) {
        this.priceCents = (int) Math.round(price * 100);
    }

    // Helper method to get subtotal in cents
    public Integer getSubtotalCents() {
        return priceCents * quantity;
    }

    // Helper method to get subtotal in dollars
    public Double getSubtotalInDollars() {
        return getSubtotalCents() / 100.0;
    }
}
package com.example.ecom.service;

import com.example.ecom.dto.CreateOrderRequest;
import com.example.ecom.dto.OrderDto;
import com.example.ecom.entity.Order;
import com.example.ecom.entity.OrderItem;
import com.example.ecom.entity.Product;
import com.example.ecom.entity.User;
import com.example.ecom.repository.OrderRepository;
import com.example.ecom.repository.ProductRepository;
import com.example.ecom.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderDto createOrder(CreateOrderRequest request, String userEmail) {
        // Find user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Calculate total and validate products
        int totalCents = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));

            // Check inventory
            if (product.getInventoryCount() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient inventory for product: " + product.getName());
            }

            // Check if product is active
            if (!product.getIsActive()) {
                throw new RuntimeException("Product is no longer available: " + product.getName());
            }

            int itemTotal = product.getPriceCents() * itemRequest.getQuantity();
            totalCents += itemTotal;

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .priceCents(product.getPriceCents())
                    .quantity(itemRequest.getQuantity())
                    .build();

            orderItems.add(orderItem);
        }

        // Convert shipping address to JSON
        String shippingAddressJson;
        try {
            shippingAddressJson = objectMapper.writeValueAsString(request.getShippingAddress());
        } catch (Exception e) {
            throw new RuntimeException("Error processing shipping address", e);
        }

        // Create order
        Order order = Order.builder()
                .user(user)
                .totalCents(totalCents)
                .status(Order.OrderStatus.PENDING_PAYMENT)
                .shippingAddressJson(shippingAddressJson)
                .build();

        // Save order first to get ID
        order = orderRepository.save(order);

        // Associate order items with order
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setOrderItems(orderItems);

        // Update product inventory
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setInventoryCount(product.getInventoryCount() - item.getQuantity());
            productRepository.save(product);
        }

        // Save order with items
        order = orderRepository.save(order);

        log.info("Order created: {} for user: {}", order.getId(), userEmail);
        return OrderDto.from(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getMyOrders(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return orderRepository.findByUser_IdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(OrderDto::from);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check if order belongs to user
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return OrderDto.from(order);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        order = orderRepository.save(order);

        log.info("Order {} status updated to: {}", orderId, status);
        return OrderDto.from(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(OrderDto::from);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                .map(OrderDto::from);
    }
}

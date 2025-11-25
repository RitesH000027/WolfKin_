package com.example.ecom.service;

import com.example.ecom.dto.*;
import com.example.ecom.entity.*;
import com.example.ecom.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private final ObjectMapper objectMapper;
    private final RazorpayClient razorpayClient;
    private final String razorpayKey;

    public PaymentService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            PaymentRepository paymentRepository,
            CouponRepository couponRepository,
            CouponService couponService,
            ObjectMapper objectMapper,
            @Value("${razorpay.key.id}") String razorpayKey,
            @Value("${razorpay.key.secret}") String razorpaySecret) throws RazorpayException {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.couponRepository = couponRepository;
        this.couponService = couponService;
        this.objectMapper = objectMapper;
        this.razorpayKey = razorpayKey;
        this.razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);
    }

    @Transactional
    public PaymentOrderResponse createPaymentOrder(CreatePaymentOrderRequest request, String userEmail) {
        try {
            // Find user
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Calculate total and validate products
            int totalCents = 0;
            List<OrderItem> orderItems = new ArrayList<>();

            for (CreatePaymentOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));

                if (product.getInventoryCount() < itemRequest.getQuantity()) {
                    throw new RuntimeException("Insufficient inventory for product: " + product.getName());
                }

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

            // Apply coupon if provided
            int discountCents = 0;
            Coupon coupon = null;
            if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
                coupon = couponRepository.findByCodeAndIsActive(request.getCouponCode().toUpperCase(), true)
                        .orElse(null);
                
                if (coupon != null && coupon.isValid()) {
                    discountCents = coupon.calculateDiscount(totalCents);
                }
            }

            int finalAmountCents = totalCents - discountCents;

            // Convert shipping address to JSON
            String shippingAddressJson = objectMapper.writeValueAsString(request.getShippingAddress());

            // Create order
            com.example.ecom.entity.Order order = com.example.ecom.entity.Order.builder()
                    .user(user)
                    .totalCents(finalAmountCents)
                    .discountCents(discountCents)
                    .coupon(coupon)
                    .status(com.example.ecom.entity.Order.OrderStatus.PENDING_PAYMENT)
                    .shippingAddressJson(shippingAddressJson)
                    .build();

            order = orderRepository.save(order);

            // Associate order items with order
            for (OrderItem item : orderItems) {
                item.setOrder(order);
            }
            order.setOrderItems(orderItems);

            // Create Razorpay order if not COD
            String razorpayOrderId = null;
            if (!"COD".equalsIgnoreCase(request.getPaymentMethod())) {
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", finalAmountCents); // Amount in paise
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "order_" + order.getId());
                orderRequest.put("payment_capture", 1);

                Order razorpayOrder = razorpayClient.orders.create(orderRequest);
                razorpayOrderId = razorpayOrder.get("id");
                
                // Save payment record
                Payment payment = Payment.builder()
                        .order(order)
                        .amountCents(finalAmountCents)
                        .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()))
                        .status(Payment.PaymentStatus.PENDING)
                        .razorpayOrderId(razorpayOrderId)
                        .build();
                paymentRepository.save(payment);
            } else {
                // For COD, create payment record with COD status
                Payment payment = Payment.builder()
                        .order(order)
                        .amountCents(finalAmountCents)
                        .paymentMethod(Payment.PaymentMethod.COD)
                        .status(Payment.PaymentStatus.PENDING)
                        .build();
                paymentRepository.save(payment);
            }

            // Increment coupon usage if applied
            if (coupon != null) {
                couponService.incrementUsageCount(coupon.getCode());
            }

            // Prepare customer details
            String customerName = request.getShippingAddress().getFullName();
            String customerEmail = request.getShippingAddress().getEmail() != null ? 
                    request.getShippingAddress().getEmail() : userEmail;
            String customerPhone = request.getShippingAddress().getPhone();

            log.info("Payment order created: {} for user: {}", order.getId(), userEmail);

            return new PaymentOrderResponse(
                    razorpayOrderId,
                    razorpayKey,
                    finalAmountCents,
                    "INR",
                    order.getId(),
                    customerName,
                    customerEmail,
                    customerPhone
            );

        } catch (Exception e) {
            log.error("Error creating payment order", e);
            throw new RuntimeException("Failed to create payment order: " + e.getMessage());
        }
    }

    @Transactional
    public OrderDto verifyPayment(VerifyPaymentRequest request, String userEmail) {
        try {
            // Verify signature
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(options, razorpayKey);

            if (!isValid) {
                throw new RuntimeException("Invalid payment signature");
            }

            // Find payment by razorpay order ID
            Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            // Update payment status
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);

            // Update order status
            com.example.ecom.entity.Order order = payment.getOrder();
            order.setStatus(com.example.ecom.entity.Order.OrderStatus.PAID);
            orderRepository.save(order);

            // Update product inventory
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.setInventoryCount(product.getInventoryCount() - item.getQuantity());
                productRepository.save(product);
            }

            log.info("Payment verified for order: {}", order.getId());

            return mapToOrderDto(order);

        } catch (Exception e) {
            log.error("Error verifying payment", e);
            throw new RuntimeException("Failed to verify payment: " + e.getMessage());
        }
    }

    @Transactional
    public OrderDto confirmCODOrder(Long orderId, String userEmail) {
        com.example.ecom.entity.Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized access to order");
        }

        // Update payment status
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(Payment.PaymentStatus.PENDING); // COD remains pending until delivery
        paymentRepository.save(payment);

        // Update order status
        order.setStatus(com.example.ecom.entity.Order.OrderStatus.PROCESSING);
        orderRepository.save(order);

        // Update product inventory
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setInventoryCount(product.getInventoryCount() - item.getQuantity());
            productRepository.save(product);
        }

        log.info("COD order confirmed: {}", order.getId());

        return mapToOrderDto(order);
    }

    private OrderDto mapToOrderDto(com.example.ecom.entity.Order order) {
        return OrderDto.from(order);
    }
}

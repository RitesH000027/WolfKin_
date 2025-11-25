package com.example.ecom.service;

import com.example.ecom.entity.Category;
import com.example.ecom.entity.Coupon;
import com.example.ecom.entity.Product;
import com.example.ecom.entity.User;
import com.example.ecom.entity.User.Role;
import com.example.ecom.repository.CategoryRepository;
import com.example.ecom.repository.CouponRepository;
import com.example.ecom.repository.ProductRepository;
import com.example.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void initializeData() {
        if (categoryRepository.count() == 0) {
            log.info("Initializing sample data...");
            initializeCategories();
            initializeProducts();
            initializeUsers();
            initializeCoupons();
            log.info("Sample data initialization completed!");
        } else {
            log.info("Sample data already exists, skipping initialization");
        }
    }

    private void initializeCategories() {
        List<Category> categories = Arrays.asList(
            createCategory("Electronics", "electronics", "Latest gadgets and electronic devices", "https://images.unsplash.com/photo-1498049794561-7780e7231661?w=500"),
            createCategory("Clothing", "clothing", "Fashion and apparel for all occasions", "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=500"),
            createCategory("Books", "books", "Literature, educational, and entertainment books", "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=500"),
            createCategory("Home & Garden", "home-garden", "Everything for your home and garden", "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=500"),
            createCategory("Sports", "sports", "Sports equipment and athletic gear", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=500"),
            createCategory("Beauty", "beauty", "Cosmetics and personal care products", "https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=500")
        );
        
        categoryRepository.saveAll(categories);
        log.info("Created {} categories", categories.size());
    }

    private Category createCategory(String name, String slug, String description, String imageUrl) {
        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);
        category.setDescription(description);
        category.setImageUrl(imageUrl);
        category.setCreatedAt(LocalDateTime.now());
        return category;
    }

    private void initializeProducts() {
        List<Category> categories = categoryRepository.findAll();
        
        // Electronics products
        Category electronics = categories.stream().filter(c -> c.getSlug().equals("electronics")).findFirst().orElse(null);
        if (electronics != null) {
            List<Product> electronicProducts = Arrays.asList(
                createProduct("iPhone 15 Pro", "iphone-15-pro", "Latest iPhone with advanced camera system", 99999, electronics, "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=500", 50, true),
                createProduct("MacBook Air M3", "macbook-air-m3", "Powerful laptop with Apple M3 chip", 129999, electronics, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500", 25, true),
                createProduct("Samsung Galaxy S24", "samsung-galaxy-s24", "Android flagship with AI features", 89999, electronics, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500", 30, true),
                createProduct("Sony WH-1000XM5", "sony-headphones", "Noise-canceling wireless headphones", 34999, electronics, "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=500", 75, true),
                createProduct("iPad Pro 12.9", "ipad-pro", "Professional tablet for creators", 109999, electronics, "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500", 40, true)
            );
            productRepository.saveAll(electronicProducts);
        }

        // Clothing products
        Category clothing = categories.stream().filter(c -> c.getSlug().equals("clothing")).findFirst().orElse(null);
        if (clothing != null) {
            List<Product> clothingProducts = Arrays.asList(
                createProduct("Classic Denim Jacket", "classic-denim-jacket", "Timeless denim jacket for all seasons", 7999, clothing, "https://images.unsplash.com/photo-1544966503-7cc5ac882d5f?w=500", 100, true),
                createProduct("Cotton T-Shirt", "cotton-tshirt", "Comfortable 100% cotton t-shirt", 2499, clothing, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500", 200, true),
                createProduct("Leather Boots", "leather-boots", "Genuine leather boots for durability", 15999, clothing, "https://images.unsplash.com/photo-1544966503-7cc5ac882d5f?w=500", 60, true),
                createProduct("Summer Dress", "summer-dress", "Light and breezy summer dress", 5999, clothing, "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=500", 80, true),
                createProduct("Wool Sweater", "wool-sweater", "Cozy wool sweater for winter", 8999, clothing, "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=500", 45, true)
            );
            productRepository.saveAll(clothingProducts);
        }

        // Books products
        Category books = categories.stream().filter(c -> c.getSlug().equals("books")).findFirst().orElse(null);
        if (books != null) {
            List<Product> bookProducts = Arrays.asList(
                createProduct("The Great Gatsby", "great-gatsby", "Classic American literature", 1299, books, "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=500", 150, true),
                createProduct("Clean Code", "clean-code", "A handbook of agile software craftsmanship", 4599, books, "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=500", 89, true),
                createProduct("Atomic Habits", "atomic-habits", "An easy & proven way to build good habits", 1899, books, "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500", 120, true),
                createProduct("Design Patterns", "design-patterns", "Elements of reusable object-oriented software", 5299, books, "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=500", 75, true)
            );
            productRepository.saveAll(bookProducts);
        }

        // Home & Garden products
        Category homeGarden = categories.stream().filter(c -> c.getSlug().equals("home-garden")).findFirst().orElse(null);
        if (homeGarden != null) {
            List<Product> homeProducts = Arrays.asList(
                createProduct("Coffee Maker", "coffee-maker", "Automatic drip coffee maker", 12999, homeGarden, "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=500", 35, true),
                createProduct("Indoor Plant Set", "indoor-plant-set", "Collection of air-purifying plants", 3999, homeGarden, "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=500", 50, true),
                createProduct("Throw Pillow", "throw-pillow", "Decorative pillow for sofa", 2999, homeGarden, "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=500", 100, true)
            );
            productRepository.saveAll(homeProducts);
        }

        log.info("Created sample products for all categories");
    }

    private Product createProduct(String name, String slug, String description, int priceCents, 
                                Category category, String imageUrl, int inventory, boolean isActive) {
        Product product = new Product();
        product.setName(name);
        product.setSlug(slug);
        product.setDescription(description);
        product.setPriceCents(priceCents);
        product.setCategory(category);
        product.setImageUrl(imageUrl);
        product.setInventoryCount(inventory);
        product.setIsActive(isActive);
        product.setCreatedAt(LocalDateTime.now());
        return product;
    }

    private void initializeUsers() {
        // Create admin user
        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@ecom.local");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setIsEnabled(true);
        admin.setCreatedAt(LocalDateTime.now());

        // Create test customer
        User customer = new User();
        customer.setName("John Doe");
        customer.setEmail("customer@ecom.local");
        customer.setPassword(passwordEncoder.encode("customer123"));
        customer.setRole(Role.USER);
        customer.setIsEnabled(true);
        customer.setCreatedAt(LocalDateTime.now());

        userRepository.saveAll(Arrays.asList(admin, customer));
        log.info("Created admin and customer test users");
    }

    private void initializeCoupons() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusMonths(3);

        List<Coupon> coupons = Arrays.asList(
            Coupon.builder()
                .code("WELCOME10")
                .description("10% off on your first order")
                .discountType(Coupon.DiscountType.PERCENTAGE)
                .discountValue(10)
                .minOrderCents(50000) // $500
                .maxDiscountCents(10000) // Max $100 discount
                .usageLimit(100)
                .usedCount(0)
                .validFrom(now)
                .validTo(futureDate)
                .isActive(true)
                .build(),
            
            Coupon.builder()
                .code("SAVE500")
                .description("Flat ₹500 off on orders above ₹2000")
                .discountType(Coupon.DiscountType.FIXED_AMOUNT)
                .discountValue(50000) // ₹500 in paise
                .minOrderCents(200000) // ₹2000
                .maxDiscountCents(null)
                .usageLimit(null) // Unlimited
                .usedCount(0)
                .validFrom(now)
                .validTo(futureDate)
                .isActive(true)
                .build(),
            
            Coupon.builder()
                .code("MEGA20")
                .description("20% off on orders above ₹3000")
                .discountType(Coupon.DiscountType.PERCENTAGE)
                .discountValue(20)
                .minOrderCents(300000) // ₹3000
                .maxDiscountCents(50000) // Max ₹500 discount
                .usageLimit(50)
                .usedCount(0)
                .validFrom(now)
                .validTo(futureDate)
                .isActive(true)
                .build()
        );

        couponRepository.saveAll(coupons);
        log.info("Created {} sample coupons", coupons.size());
    }
}
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

    @Transactional
    public void resetAndInitializeData() {
        log.info("Resetting all data...");
        // Delete in correct order to respect foreign key constraints
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        couponRepository.deleteAll();
        // Don't delete users to preserve accounts
        log.info("All product and category data deleted");
        
        // Reinitialize with new data
        log.info("Reinitializing with new clothing data...");
        initializeCategories();
        initializeProducts();
        initializeCoupons();
        log.info("Data reset and reinitialization completed!");
    }

    private void initializeCategories() {
        List<Category> categories = Arrays.asList(
            createCategory("Men's Clothing", "mens-clothing", "Stylish apparel for men", "https://images.unsplash.com/photo-1490114538077-0a7f8cb49891?w=500"),
            createCategory("Women's Clothing", "womens-clothing", "Fashion and apparel for women", "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=500"),
            createCategory("Footwear", "footwear", "Shoes, boots, and sneakers for all occasions", "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500"),
            createCategory("Accessories", "accessories", "Complete your outfit with stylish accessories", "https://images.unsplash.com/photo-1492707892479-7bc8d5a4ee93?w=500"),
            createCategory("Activewear", "activewear", "Comfortable athletic and sportswear", "https://images.unsplash.com/photo-1556906781-9a412961c28c?w=500"),
            createCategory("Outerwear", "outerwear", "Jackets, coats, and blazers", "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=500")
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
        
        // Get clothing categories
        Category mensClothing = categories.stream().filter(c -> c.getSlug().equals("mens-clothing")).findFirst().orElse(null);
        Category womensClothing = categories.stream().filter(c -> c.getSlug().equals("womens-clothing")).findFirst().orElse(null);
        Category footwear = categories.stream().filter(c -> c.getSlug().equals("footwear")).findFirst().orElse(null);
        Category accessories = categories.stream().filter(c -> c.getSlug().equals("accessories")).findFirst().orElse(null);
        Category activewear = categories.stream().filter(c -> c.getSlug().equals("activewear")).findFirst().orElse(null);
        Category outerwear = categories.stream().filter(c -> c.getSlug().equals("outerwear")).findFirst().orElse(null);
        
        // Men's Clothing products
        if (mensClothing != null) {
            List<Product> mensProducts = Arrays.asList(
                createProduct("Cotton T-Shirt", "cotton-tshirt", "Comfortable 100% cotton t-shirt", 2499, mensClothing, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500", 200, true),
                createProduct("Slim Fit Jeans", "slim-fit-jeans", "Classic blue denim jeans with perfect fit", 4999, mensClothing, "https://images.unsplash.com/photo-1542272604-787c3835535d?w=500", 150, true),
                createProduct("Polo Shirt", "polo-shirt", "Classic polo shirt for casual wear", 2999, mensClothing, "https://images.unsplash.com/photo-1586790170083-2f9ceadc732d?w=500", 180, true),
                createProduct("Cargo Pants", "cargo-pants", "Utility cargo pants with multiple pockets", 4499, mensClothing, "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=500", 75, true),
                createProduct("Denim Shirt", "denim-shirt", "Classic denim shirt for layering", 3999, mensClothing, "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=500", 85, true),
                createProduct("Formal Suit", "formal-suit", "Premium formal suit for business", 24999, mensClothing, "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=500", 35, true)
            );
            productRepository.saveAll(mensProducts);
        }
        
        // Women's Clothing products
        if (womensClothing != null) {
            List<Product> womensProducts = Arrays.asList(
                createProduct("Summer Dress", "summer-dress", "Light and breezy summer dress", 5999, womensClothing, "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=500", 80, true),
                createProduct("Wool Sweater", "wool-sweater", "Cozy wool sweater for winter", 8999, womensClothing, "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=500", 45, true),
                createProduct("Floral Blouse", "floral-blouse", "Elegant floral pattern blouse", 3499, womensClothing, "https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=500", 90, true),
                createProduct("High-Waist Jeans", "high-waist-jeans", "Trendy high-waist denim jeans", 4999, womensClothing, "https://images.unsplash.com/photo-1582418702059-97ebafb35d09?w=500", 120, true),
                createProduct("Maxi Skirt", "maxi-skirt", "Flowing maxi skirt for elegant look", 4499, womensClothing, "https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=500", 70, true)
            );
            productRepository.saveAll(womensProducts);
        }
        
        // Footwear products
        if (footwear != null) {
            List<Product> footwearProducts = Arrays.asList(
                createProduct("Leather Boots", "leather-boots", "Genuine leather boots for durability", 15999, footwear, "https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=500", 60, true),
                createProduct("Running Shoes", "running-shoes", "Lightweight athletic shoes for running", 8999, footwear, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500", 90, true),
                createProduct("Casual Sneakers", "casual-sneakers", "Everyday comfortable sneakers", 5999, footwear, "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500", 110, true),
                createProduct("High Heels", "high-heels", "Elegant high heels for formal occasions", 7999, footwear, "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=500", 55, true)
            );
            productRepository.saveAll(footwearProducts);
        }
        
        // Accessories products
        if (accessories != null) {
            List<Product> accessoryProducts = Arrays.asList(
                createProduct("Leather Belt", "leather-belt", "Premium leather belt with metal buckle", 1999, accessories, "https://images.unsplash.com/photo-1624222247344-550fb60583bb?w=500", 150, true),
                createProduct("Designer Handbag", "designer-handbag", "Stylish designer handbag", 12999, accessories, "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=500", 45, true),
                createProduct("Sunglasses", "sunglasses", "UV protection sunglasses", 2499, accessories, "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=500", 100, true)
            );
            productRepository.saveAll(accessoryProducts);
        }
        
        // Activewear products
        if (activewear != null) {
            List<Product> activewearProducts = Arrays.asList(
                createProduct("Sports Jersey", "sports-jersey", "Breathable athletic jersey", 3999, activewear, "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=500", 100, true),
                createProduct("Track Pants", "track-pants", "Comfortable athletic track pants", 2999, activewear, "https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=500", 140, true),
                createProduct("Casual Hoodie", "casual-hoodie", "Comfortable cotton blend hoodie", 3499, activewear, "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=500", 120, true),
                createProduct("Yoga Pants", "yoga-pants", "Flexible yoga pants for workout", 2999, activewear, "https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=500", 130, true)
            );
            productRepository.saveAll(activewearProducts);
        }
        
        // Outerwear products
        if (outerwear != null) {
            List<Product> outerwearProducts = Arrays.asList(
                createProduct("Classic Denim Jacket", "classic-denim-jacket", "Timeless denim jacket for all seasons", 7999, outerwear, "https://images.unsplash.com/photo-1544966503-7cc5ac882d5f?w=500", 100, true),
                createProduct("Formal Blazer", "formal-blazer", "Professional blazer for business occasions", 12999, outerwear, "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=500", 40, true),
                createProduct("Leather Jacket", "leather-jacket", "Premium genuine leather jacket", 19999, outerwear, "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=500", 30, true),
                createProduct("Winter Coat", "winter-coat", "Warm insulated coat for cold weather", 14999, outerwear, "https://images.unsplash.com/photo-1539533113208-f6df8cc8b543?w=500", 50, true),
                createProduct("Trench Coat", "trench-coat", "Classic trench coat for rainy days", 11999, outerwear, "https://images.unsplash.com/photo-1539533113208-f6df8cc8b543?w=500", 40, true)
            );
            productRepository.saveAll(outerwearProducts);
        }

        log.info("Created sample clothing products across all categories");
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
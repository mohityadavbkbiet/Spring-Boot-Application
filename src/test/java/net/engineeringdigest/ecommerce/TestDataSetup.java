package net.engineeringdigest.ecommerce;

import net.engineeringdigest.ecommerce.dto.SignupRequest;
import net.engineeringdigest.ecommerce.entity.Product;
import net.engineeringdigest.ecommerce.service.ProductService;
import net.engineeringdigest.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class TestDataSetup {

    public static void main(String[] args) {
        SpringApplication.run(TestDataSetup.class, args);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Bean
    @Profile("test")
    public CommandLineRunner setupTestData() {
        return args -> {
            // Create admin user
            SignupRequest adminSignup = new SignupRequest();
            adminSignup.setEmail("admin@test.com");
            adminSignup.setPassword("admin123");
            adminSignup.setFirstName("Admin");
            adminSignup.setLastName("User");
            adminSignup.setRoles(List.of("ROLE_ADMIN"));
            
            try {
                userService.createUser(adminSignup);
                System.out.println("Admin user created successfully");
            } catch (Exception e) {
                System.out.println("Admin user already exists or error: " + e.getMessage());
            }

            // Add test products
            try {
                Product product1 = Product.builder()
                    .name("Gaming Laptop")
                    .description("High-performance gaming laptop with RTX 3080")
                    .price(new BigDecimal("1499.99"))
                    .category("Electronics")
                    .stockQuantity(10)
                    .imageUrl("https://example.com/gaming-laptop.jpg")
                    .active(true)
                    .build();
                productService.createProduct(product1);

                Product product2 = Product.builder()
                    .name("Wireless Mouse")
                    .description("Ergonomic wireless mouse with long battery life")
                    .price(new BigDecimal("29.99"))
                    .category("Electronics")
                    .stockQuantity(50)
                    .imageUrl("https://example.com/wireless-mouse.jpg")
                    .active(true)
                    .build();
                productService.createProduct(product2);

                Product product3 = Product.builder()
                    .name("Mechanical Keyboard")
                    .description("RGB mechanical keyboard with Cherry MX switches")
                    .price(new BigDecimal("89.99"))
                    .category("Electronics")
                    .stockQuantity(25)
                    .imageUrl("https://example.com/mechanical-keyboard.jpg")
                    .active(true)
                    .build();
                productService.createProduct(product3);

                System.out.println("Test products added successfully");
            } catch (Exception e) {
                System.out.println("Error adding test products: " + e.getMessage());
            }
        };
    }
}

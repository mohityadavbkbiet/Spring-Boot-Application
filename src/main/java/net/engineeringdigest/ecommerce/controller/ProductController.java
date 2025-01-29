package net.engineeringdigest.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import net.engineeringdigest.ecommerce.entity.Product;
import net.engineeringdigest.ecommerce.entity.Review;
import net.engineeringdigest.ecommerce.service.ProductService;
import net.engineeringdigest.ecommerce.dto.ErrorResponse;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final MongoTemplate mongoTemplate;

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} creating new product", authentication.getName());
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create product", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable String id) {
        try {
            log.info("Fetching product with id: {}", id);
            Optional<Product> product = Optional.ofNullable(productService.getProduct(id));
            return product
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching product: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch product", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy) {
        try {
            log.info("Fetching all products with page: {}, size: {}, sortBy: {}", page, size, sortBy);
            List<Product> products = productService.getAllProducts();
            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error fetching products: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch products", e.getMessage()));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String category) {
        try {
            log.info("Fetching products in category: {}", category);
            List<Product> products = productService.getProductsByCategory(category);
            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error fetching products by category: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch products by category", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody Product product) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} updating product with id: {}", authentication.getName(), id);
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update product", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} deleting product with id: {}", authentication.getName(), id);
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting product: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to delete product", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String query) {
        try {
            log.info("Searching products with query: {}", query);
            List<Product> products = productService.searchProducts(query);
            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error searching products: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to search products", e.getMessage()));
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable String id, @RequestParam int quantity) {
        log.info("Updating stock for product with id: {}, quantity: {}", id, quantity);
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }

    @PostMapping("/{productId}/reviews")
    public ResponseEntity<Review> addReview(@PathVariable String productId, @RequestBody Review review) {
        log.info("Adding review for product with id: {}", productId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addReview(productId, review));
    }

    @PutMapping("/{productId}/reviews/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @PathVariable String productId,
            @PathVariable String reviewId,
            @RequestBody Review review) {
        log.info("Updating review {} for product {}", reviewId, productId);
        return ResponseEntity.ok(productService.updateReview(productId, reviewId, review));
    }

    @GetMapping("/test-mongodb")
    public ResponseEntity<String> testMongoDBConnection() {
        try {
            mongoTemplate.getDb().runCommand(new Document("ping", 1));
            return ResponseEntity.ok("Successfully connected to MongoDB!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to connect to MongoDB: " + e.getMessage());
        }
    }
}

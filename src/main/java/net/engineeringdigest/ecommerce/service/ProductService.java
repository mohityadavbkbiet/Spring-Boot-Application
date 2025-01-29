package net.engineeringdigest.ecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.ecommerce.entity.Product;
import net.engineeringdigest.ecommerce.entity.Review;
import net.engineeringdigest.ecommerce.exception.ResourceNotFoundException;
import net.engineeringdigest.ecommerce.repository.ProductRepository;
import net.engineeringdigest.ecommerce.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final RedisService redisService;

    @Transactional
    public Product createProduct(Product product) {
        validateProduct(product);
        
        product.setId(UUID.randomUUID().toString());
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        log.info("Created new product with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    public Product getProduct(String id) {
        String cacheKey = "product:" + id;
        Product cachedProduct = redisService.get(cacheKey, Product.class);
        
        if (cachedProduct != null) {
            log.debug("Product {} found in cache", id);
            if (!cachedProduct.isActive()) {
                log.warn("Product {} is not active", id);
                throw new ResourceNotFoundException("Product not found or inactive");
            }
            return cachedProduct;
        }
        
        log.debug("Product {} not found in cache, fetching from database", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
        if (!product.isActive()) {
            log.warn("Product {} is not active", id);
            throw new ResourceNotFoundException("Product not found or inactive");
        }
        
        redisService.set(cacheKey, product);
        return product;
    }

    public List<Product> getAllProducts() {
        return productRepository.findByActive(true);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndActive(category, true);
    }

    public List<Product> searchProducts(String query) {
        log.info("Searching products with query: {}", query);
        
        // Create case-insensitive search criteria
        String searchRegex = ".*" + query.toLowerCase() + ".*";
        
        return productRepository.findAll().stream()
            .filter(product -> 
                (product.getName() != null && product.getName().toLowerCase().matches(searchRegex)) ||
                (product.getDescription() != null && product.getDescription().toLowerCase().matches(searchRegex)) ||
                (product.getCategory() != null && product.getCategory().toLowerCase().matches(searchRegex))
            )
            .filter(Product::isActive)
            .toList();
    }

    @Transactional
    public Review addReview(String productId, Review review) {
        Product product = getProduct(productId);
        validateReview(review);
        
        review.setId(UUID.randomUUID().toString());
        review.setProductId(productId);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        
        Review savedReview = reviewRepository.save(review);
        
        product.setUpdatedAt(LocalDateTime.now());
        product.getReviews().add(savedReview);
        productRepository.save(product);
        
        redisService.delete("product:" + productId);
        
        log.info("Added review {} to product {}", savedReview.getId(), productId);
        return savedReview;
    }

    @Transactional
    public Product updateStock(String id, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }
        
        Product product = getProduct(id);
        
        if (product.getStockQuantity() < quantity) {
            log.warn("Insufficient stock for product {}. Current stock: {}", id, product.getStockQuantity());
            throw new IllegalArgumentException("Insufficient stock");
        }
        
        log.info("Updating stock for product {}", id);
        product.setStockQuantity(product.getStockQuantity() - quantity);
        product.setUpdatedAt(LocalDateTime.now());
        
        Product updatedProduct = productRepository.save(product);
        redisService.delete("product:" + id);
        return updatedProduct;
    }

    @Transactional
    public Product updateProduct(String id, Product updatedProduct) {
        Product product = getProduct(id);
        
        if (updatedProduct.getName() != null && !updatedProduct.getName().isEmpty()) {
            product.setName(updatedProduct.getName());
        }
        
        if (updatedProduct.getDescription() != null && !updatedProduct.getDescription().isEmpty()) {
            product.setDescription(updatedProduct.getDescription());
        }
        
        if (updatedProduct.getPrice() != null) {
            product.setPrice(updatedProduct.getPrice());
        }
        
        if (updatedProduct.getStockQuantity() >= 0) {
            product.setStockQuantity(updatedProduct.getStockQuantity());
        }
        
        if (updatedProduct.getCategory() != null && !updatedProduct.getCategory().isEmpty()) {
            product.setCategory(updatedProduct.getCategory());
        }

        if (updatedProduct.getImageUrl() != null && !updatedProduct.getImageUrl().isEmpty()) {
            product.setImageUrl(updatedProduct.getImageUrl());
        }
        
        product.setUpdatedAt(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        redisService.delete("product:" + id);
        
        log.info("Updated product {}", id);
        return savedProduct;
    }

    @Transactional
    public Review updateReview(String productId, String reviewId, Review updatedReview) {
        Product product = getProduct(productId);
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
            
        if (!review.getProductId().equals(productId)) {
            throw new IllegalArgumentException("Review does not belong to the specified product");
        }
        
        if (updatedReview.getUserId() != null && !updatedReview.getUserId().equals(review.getUserId())) {
            review.setUserId(updatedReview.getUserId());
        }
        
        if (updatedReview.getRating() > 0) {
            review.setRating(updatedReview.getRating());
        }
        
        if (updatedReview.getComment() != null && !updatedReview.getComment().equals(review.getComment())) {
            review.setComment(updatedReview.getComment());
        }
        
        review.setUpdatedAt(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);
        redisService.delete("product:" + productId);
        return savedReview;
    }

    @Transactional
    public void deleteProduct(String id) {
        Product product = getProduct(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        
        // Soft delete by setting active to false
        product.setActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
        
        // Remove from cache if using caching
        String cacheKey = "product:" + id;
        redisService.delete(cacheKey);
        
        log.info("Deleted product with ID: {}", id);
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Product description cannot be null or empty");
        }
        if (product.getPrice() == null || product.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Product price must be non-negative");
        }
        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Product stock quantity must be non-negative");
        }
        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }
    }

    private void validateReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }
        if (review.getUserId() == null || review.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("Review user ID cannot be null or empty");
        }
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Review rating must be between 1 and 5");
        }
        if (review.getComment() == null || review.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Review comment cannot be null or empty");
        }
    }
}

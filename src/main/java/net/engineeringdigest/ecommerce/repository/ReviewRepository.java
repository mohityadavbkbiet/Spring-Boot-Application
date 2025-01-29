package net.engineeringdigest.ecommerce.repository;

import net.engineeringdigest.ecommerce.entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByProductId(String productId);
    List<Review> findByUserId(String userId);
    Optional<Review> findByProductIdAndId(String productId, String reviewId);
    void deleteByProductId(String productId);
    boolean existsByProductIdAndUserIdAndId(String productId, String userId, String reviewId);
}

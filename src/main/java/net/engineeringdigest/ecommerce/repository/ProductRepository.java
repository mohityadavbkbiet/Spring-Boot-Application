package net.engineeringdigest.ecommerce.repository;

import net.engineeringdigest.ecommerce.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategory(String category);
    List<Product> findByActive(boolean active);
    List<Product> findByCategoryAndActive(String category, boolean active);
}

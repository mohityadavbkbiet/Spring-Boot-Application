package net.engineeringdigest.ecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String productId;
    private String userId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

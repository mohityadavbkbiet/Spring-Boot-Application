package net.engineeringdigest.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private String category;
    private String imageUrl;
}

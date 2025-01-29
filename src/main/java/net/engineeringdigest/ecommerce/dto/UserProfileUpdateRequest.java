package net.engineeringdigest.ecommerce.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String email;
    private String firstName;
    private String lastName;
}

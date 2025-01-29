package net.engineeringdigest.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.engineeringdigest.ecommerce.dto.SignupRequest;
import net.engineeringdigest.ecommerce.dto.UserProfileUpdateRequest;
import net.engineeringdigest.ecommerce.dto.ErrorResponse;
import net.engineeringdigest.ecommerce.entity.User;
import net.engineeringdigest.ecommerce.repository.UserRepository;
import net.engineeringdigest.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            log.info("Fetching profile for user: {}", username);
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching user profile: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch user profile", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateCurrentUserProfile(@RequestBody UserProfileUpdateRequest updateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            log.info("Updating profile for user: {}", username);
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
            if (updateRequest.getEmail() != null) {
                user.setEmail(updateRequest.getEmail());
            }
            if (updateRequest.getFirstName() != null) {
                user.setFirstName(updateRequest.getFirstName());
            }
            if (updateRequest.getLastName() != null) {
                user.setLastName(updateRequest.getLastName());
            }
            
            User updatedUser = userRepository.save(user);
            log.info("Successfully updated profile for user: {}", username);
            return ResponseEntity.ok(updatedUser);
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating user profile: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update user profile", e.getMessage()));
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody SignupRequest signupRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("Admin {} creating new user", authentication.getName());
            User createdUser = userService.createUser(signupRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create user", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} updating user with id: {}", authentication.getName(), id);
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update user", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} deleting user with id: {}", authentication.getName(), id);
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to delete user", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("User {} fetching user with id: {}", authentication.getName(), id);
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch user", e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("Admin {} fetching all users", authentication.getName());
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error fetching all users: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch users", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            log.info("Fetching current user details for: {}", username);
            
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching current user: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch current user", e.getMessage()));
        }
    }
}

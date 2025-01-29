package net.engineeringdigest.ecommerce.service;

import lombok.RequiredArgsConstructor;
import net.engineeringdigest.ecommerce.dto.SignupRequest;
import net.engineeringdigest.ecommerce.entity.User;
import net.engineeringdigest.ecommerce.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public User createUser(SignupRequest signupRequest) {
        log.debug("Creating new user with username: {}", signupRequest.getUsername());
        
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            log.error("Username already exists: {}", signupRequest.getUsername());
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            log.error("Email already exists: {}", signupRequest.getEmail());
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRoles(Collections.singletonList("ROLE_USER"));
        
        log.debug("Saving new user to database: {}", user.getUsername());
        User savedUser = userRepository.save(user);
        log.info("Successfully created new user: {}", savedUser.getUsername());
        return savedUser;
    }

    public User updateUser(String id, User userDetails) {
        log.debug("Updating user with id: {}", id);
        User user = getUserById(id);

        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        
        User updatedUser = userRepository.save(user);
        log.info("Successfully updated user: {}", updatedUser.getUsername());
        return updatedUser;
    }

    public void deleteUser(String id) {
        log.debug("Deleting user with id: {}", id);
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("Successfully deleted user with id: {}", id);
    }

    public User getUserById(String id) {
        log.debug("Fetching user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        log.debug("Fetching all users");
        List<User> users = userRepository.findAll();
        log.debug("Successfully fetched {} users", users.size());
        return users;
    }
}

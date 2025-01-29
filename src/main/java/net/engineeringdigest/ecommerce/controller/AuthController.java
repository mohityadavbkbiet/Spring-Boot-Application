package net.engineeringdigest.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.engineeringdigest.ecommerce.dto.LoginRequest;
import net.engineeringdigest.ecommerce.dto.LoginResponse;
import net.engineeringdigest.ecommerce.dto.SignupRequest;
import net.engineeringdigest.ecommerce.dto.ErrorResponse;
import net.engineeringdigest.ecommerce.entity.User;
import net.engineeringdigest.ecommerce.security.JwtTokenProvider;
import net.engineeringdigest.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        log.info("Received signup request for user: {}", signupRequest.getUsername());
        try {
            User user = userService.createUser(signupRequest);
            log.info("Successfully created user: {}", user.getUsername());
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            log.error("Failed to create user: {}", signupRequest.getUsername(), e);
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Failed to create user",
                    e.getMessage()
                ));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("Received login request for user: {}", loginRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            log.info("Successfully authenticated user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(new LoginResponse(jwt, loginRequest.getUsername()));
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Authentication failed",
                    e.getMessage()
                ));
        }
    }
}

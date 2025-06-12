package com.cultureradar.controller;

import com.cultureradar.dto.AuthRequest;
import com.cultureradar.dto.AuthResponse;
import com.cultureradar.dto.RegisterRequest;
import com.cultureradar.dto.UserDTO;
import com.cultureradar.model.User;
import com.cultureradar.service.UserService;
import com.cultureradar.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * REST controller for authentication endpoints.
 * Handles login, registration, and authentication status operations.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    
    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }
    
    /**
     * Authenticates user and returns JWT token
     * 
     * @param authRequest login credentials
     * @return JWT token with user info
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String jwt = jwtTokenProvider.generateToken(authentication);
            UserDTO userDetails = userService.getUserByUsername(authRequest.getUsername());
            
            return ResponseEntity.ok(new AuthResponse(jwt, userDetails));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * Registers a new user
     * 
     * @param registerRequest user registration data
     * @return newly created user data
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Check if username is already taken
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
        }
        
        // Check if email is already in use
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
        }
        
        UserDTO createdUser = userService.createUser(registerRequest);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdUser);
    }
    
    /**
     * Returns the current user's information
     * 
     * @param authentication current authentication details
     * @return current user data
     */
    @GetMapping("/current-user")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        UserDTO userDetails = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(userDetails);
    }
    
    /**
     * Validates if a token is still valid
     * 
     * @param token JWT token
     * @return validation status
     */
    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestHeader("Authorization") String token) {
        token = token.startsWith("Bearer ") ? token.substring(7) : token;
        
        boolean isValid = jwtTokenProvider.validateToken(token);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }
    
    /**
     * Logs out the current user (client-side only in JWT)
     * 
     * @return confirmation message
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // In JWT, server-side logout is not really necessary
        // The client should discard the token
        // For a more complete logout, you would need to implement a token blacklist
        
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}

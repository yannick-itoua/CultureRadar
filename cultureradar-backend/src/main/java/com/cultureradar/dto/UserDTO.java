package com.cultureradar.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for User information.
 * Used to transfer user data between the controller and client without exposing sensitive information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    private String firstName;
    
    private String lastName;
    
    // Don't include password in responses to clients
    @JsonIgnore
    private String password;
    
    private Set<String> roles;
    
    private String city;
    
    private String province;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime lastLoginAt;
    
    private Boolean enabled = true;
    
    /**
     * Returns the user's full name if both first and last names are available
     * @return Full name or username if full name is not available
     */
    public String getFullName() {
        if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        } else if (firstName != null && !firstName.isEmpty()) {
            return firstName;
        } else {
            return username;
        }
    }
    
    /**
     * Checks if the user has a specific role
     * @param roleName Role to check for
     * @return true if the user has the specified role
     */
    public boolean hasRole(String roleName) {
        return roles != null && roles.contains(roleName);
    }
    
    /**
     * Checks if the user is an administrator
     * @return true if the user has the ROLE_ADMIN role
     */
    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }
    
    /**
     * Checks if the user is a moderator
     * @return true if the user has the ROLE_MODERATOR role
     */
    public boolean isModerator() {
        return hasRole("ROLE_MODERATOR");
    }
}

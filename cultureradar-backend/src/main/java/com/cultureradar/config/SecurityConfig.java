package com.cultureradar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security configuration for the CultureRadar application.
 * Configures endpoint security, authentication, and authorization rules.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain with specific rules for endpoints.
     * 
     * @param http HttpSecurity object to configure
     * @return The configured SecurityFilterChain
     * @throws Exception if there's an error during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Since we're building a REST API that will be accessed from a separate frontend
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // Public endpoints
                .requestMatchers(
                    "/api/events/public/**",
                    "/api/events/search/**",
                    "/api/locations/public/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                // Admin endpoints require authentication
                .requestMatchers(
                    "/api/events/admin/**", 
                    "/api/events/*/approve"
                ).hasRole("ADMIN")
                // All other endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated())
            .httpBasic(httpBasic -> {});
        
        return http.build();
    }
    
    /**
     * Creates an in-memory user store with a default admin user.
     * This should be replaced with a database-backed solution in production.
     * 
     * @param passwordEncoder The password encoder to use
     * @return UserDetailsService with configured users
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails adminUser = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin"))
            .roles("ADMIN")
            .build();
            
        UserDetails moderatorUser = User.builder()
            .username("mod")
            .password(passwordEncoder.encode("mod"))
            .roles("MODERATOR")
            .build();
            
        return new InMemoryUserDetailsManager(adminUser, moderatorUser);
    }
    
    /**
     * Creates a password encoder for securing stored credentials.
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Configures CORS for the application.
     * 
     * @return CorsConfigurationSource with appropriate settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // Frontend URL
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

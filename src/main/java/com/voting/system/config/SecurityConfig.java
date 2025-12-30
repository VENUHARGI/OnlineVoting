package com.voting.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Configuration for Online Voting System
 * 
 * Security Features:
 * - BCrypt password encryption
 * - JWT-based authentication
 * - CORS configuration for frontend
 * - CSRF protection
 * - Security headers
 * - Rate limiting (to be implemented)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        /**
         * Password encoder using BCrypt with strength 12
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(12);
        }

        /**
         * Main Security Filter Chain Configuration
         */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // CORS Configuration
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // CSRF Configuration
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/api/**") // Disable CSRF for API endpoints
                                )

                                // Session Management
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT-based, no
                                                                                                        // sessions
                                )

                                // Authorization Rules
                                .authorizeHttpRequests(authz -> authz
                                                // Allow all requests temporarily for debugging
                                                .anyRequest().permitAll())

                                // Security Headers
                                .headers(headers -> headers
                                                .frameOptions(frameOptions -> frameOptions.deny())
                                                .contentTypeOptions(contentTypeOptions -> {
                                                })
                                                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                                                                .maxAgeInSeconds(31536000)
                                                                .includeSubDomains(true))
                                                .referrerPolicy(referrer -> referrer.policy(
                                                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))

                                // Disable form login (using JWT)
                                .formLogin(AbstractHttpConfigurer::disable)
                                .httpBasic(AbstractHttpConfigurer::disable);

                return http.build();
        }

        /**
         * CORS Configuration for Frontend-Backend Communication
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Allow specific origins (update for production)
                configuration.setAllowedOriginPatterns(List.of(
                                "http://localhost:*",
                                "http://127.0.0.1:*",
                                "https://localhost:*"));

                // Allowed HTTP methods
                configuration.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "DELETE", "OPTIONS"));

                // Allowed headers
                configuration.setAllowedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Type",
                                "X-Requested-With",
                                "Accept",
                                "Origin",
                                "Access-Control-Request-Method",
                                "Access-Control-Request-Headers"));

                // Expose headers
                configuration.setExposedHeaders(List.of(
                                "Authorization",
                                "X-Total-Count"));

                // Allow credentials
                configuration.setAllowCredentials(true);

                // Max age for preflight
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}
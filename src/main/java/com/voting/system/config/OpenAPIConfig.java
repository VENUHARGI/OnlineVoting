package com.voting.system.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Configuration for Online Voting System
 * 
 * Provides comprehensive API documentation with security schemes and examples
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Online Voting System API",
        version = "1.0.0",
        description = """
            **Secure Online Voting System API**
            
            A comprehensive REST API for managing digital voting processes with the following features:
            
            ### 🔐 Security Features
            - Two-factor authentication with email OTP
            - JWT-based session management
            - Rate limiting and fraud prevention
            - Encrypted data storage and transmission
            
            ### ⚡ Core Capabilities
            - **User Management**: Registration, authentication, profile management
            - **Voting Process**: Constituency-based voting with party selection
            - **Election Administration**: Constituency and party management
            - **Analytics & Reporting**: Vote tracking and statistical analysis
            
            ### 📋 API Documentation
            - All endpoints include detailed request/response schemas
            - Error codes and response examples provided
            - Authentication requirements clearly marked
            - Rate limiting information included
            
            ### 🛡️ Compliance & Audit
            - Comprehensive audit logging
            - Vote anonymity protection
            - Fraud detection and prevention
            - Data integrity verification
            
            For detailed usage instructions and examples, explore the individual endpoints below.
            """,
        contact = @Contact(
            name = "Online Voting System Team",
            email = "admin@onlinevoting.system",
            url = "https://github.com/voting-system/online-voting"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            description = "Development Server",
            url = "http://localhost:8080"
        ),
        @Server(
            description = "Production Server", 
            url = "https://api.onlinevoting.system"
        )
    }
)
public class OpenAPIConfig {

    /**
     * Configure OpenAPI with security schemes
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";
        
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("""
                        **JWT Bearer Token Authentication**
                        
                        To access protected endpoints, include the JWT token in the Authorization header:
                        
                        ```
                        Authorization: Bearer <your-jwt-token>
                        ```
                        
                        **How to obtain a token:**
                        1. Register a new user via `/api/auth/signup`
                        2. Verify email with OTP via `/api/auth/verify-otp`
                        3. Login via `/api/auth/login` 
                        4. Verify login OTP via `/api/auth/verify-login-otp`
                        5. Use the returned token in subsequent requests
                        
                        **Token expiration:** Tokens are valid for 24 hours by default.
                        """)
                )
            );
    }
}
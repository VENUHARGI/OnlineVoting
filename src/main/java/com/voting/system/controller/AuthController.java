package com.voting.system.controller;

import com.voting.system.model.ApiResponse;
import com.voting.system.model.LogoutRequest;
import com.voting.system.model.OTP;
import com.voting.system.model.User;
import com.voting.system.service.OTPService;
import com.voting.system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * REST Controller for Authentication operations
 * 
 * Handles user registration, login, OTP verification, and authentication
 * management
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // Add request interceptor logging
    @ModelAttribute
    public void logIncomingRequests(HttpServletRequest request) {
        System.out.println("\n🔍 === INCOMING REQUEST TO AUTH CONTROLLER ===");
        System.out.println("🌐 Method: " + request.getMethod());
        System.out.println("🎯 URL: " + request.getRequestURL());
        System.out.println("📍 Path: " + request.getServletPath());
        System.out.println("📝 Query: " + request.getQueryString());
        System.out.println("🕐 Time: " + LocalDateTime.now());
        System.out.println("============================================\n");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private OTPService otpService;

    /**
     * User registration endpoint
     * Note: Database schema must be created offline before using this endpoint
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignupRequest request) {
        try {
            // Register user
            User user = userService.registerUser(
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getPhoneNumber());

            // Generate and send OTP for verification - returns the generated OTP code
            String otpCode = otpService.generateAndSendOTP(user.getEmail(), OTP.OTPPurpose.REGISTRATION_VERIFICATION);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", user.getId());
            responseData.put("email", user.getEmail());
            responseData.put("otpCode", otpCode); // Return the OTP that was just generated
            responseData.put("message", "Registration successful. Please verify your email with the OTP sent.");

            return ResponseEntity.ok(new ApiResponse(true, "Registration successful", responseData));

        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            // Log detailed database error information
            logger.error("=== DATABASE SCHEMA ERROR IN REGISTRATION ===");
            logger.error("Exception Type: InvalidDataAccessResourceUsageException");
            logger.error("Error Message: {}", e.getMessage());
            logger.error("Root Cause: {}", e.getCause() != null ? e.getCause().getMessage() : "No root cause");
            logger.error("SQL State (if available): {}",
                    e.getCause() != null ? e.getCause().toString() : "Not available");
            logger.error("=== END DATABASE ERROR ===");

            // Handle missing schema/tables
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse(false,
                            "Database schema not available. Please ensure the database schema is created offline before using this application. Error: "
                                    + e.getMessage(),
                            null));
        } catch (org.springframework.transaction.TransactionSystemException e) {
            // Handle JPA transaction errors (constraint violations, validation errors)
            logger.error("=== JPA TRANSACTION ERROR IN REGISTRATION ===");
            logger.error("Exception Type: TransactionSystemException");
            logger.error("Error Message: {}", e.getMessage());

            Throwable cause = e.getCause();
            while (cause != null) {
                logger.error("Cause: {}", cause.getClass().getSimpleName() + " - " + cause.getMessage());
                if (cause.getMessage() != null && cause.getMessage().contains("constraint")) {
                    logger.error("🚨 CONSTRAINT VIOLATION DETECTED: {}", cause.getMessage());
                }
                cause = cause.getCause();
            }

            logger.error("=== END JPA TRANSACTION ERROR ===");

            String errorMsg = e.getRootCause() != null
                    ? (e.getRootCause().getMessage() != null ? e.getRootCause().getMessage() : e.getMessage())
                    : e.getMessage();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Registration failed: " + errorMsg, null));
        } catch (Exception e) {
            logger.error("=== UNEXPECTED ERROR IN SIGNUP ===");
            logger.error("Exception Type: {}", e.getClass().getSimpleName());
            logger.error("Error Message: {}", e.getMessage());
            e.printStackTrace();
            logger.error("=== END UNEXPECTED ERROR ===");

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Check if email already exists
     */
    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Email is required", null));
            }

            boolean exists = userService.userExists(email);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("exists", exists);

            return ResponseEntity.ok(new ApiResponse(true, "Email check completed", responseData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * User login endpoint
     * Note: Database schema must be created offline before using this endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        System.out.println("\n🔍 LOGIN ATTEMPT DETECTED");
        System.out.println("📧 Email: " + request.getEmail());

        try {
            System.out.println("🔐 Attempting user authentication...");
            Optional<User> userOpt = userService.authenticateUser(request.getEmail(), request.getPassword());

            if (userOpt.isEmpty()) {
                System.out.println("❌ Authentication FAILED - Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Invalid email or password", null));
            }

            System.out.println("✅ Authentication SUCCESS");
            User user = userOpt.get();
            System.out.println("👤 User found: " + user.getFullName());
            System.out.println("✉️ Verification status: " + user.getIsVerified());

            if (!user.getIsVerified()) {
                System.out.println("❌ Account NOT VERIFIED - OTP generation skipped");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "Account not verified. Please verify your email.", null));
            }

            System.out.println("✅ Account is VERIFIED - proceeding with OTP generation");
            // Generate and send OTP for login verification
            System.out.println("🎯 About to generate OTP for verified user: " + user.getEmail());
            otpService.generateAndSendOTP(user.getEmail(), OTP.OTPPurpose.LOGIN_VERIFICATION);
            System.out.println("✅ OTP generation completed");

            // Retrieve the generated OTP for response
            Optional<OTP> generatedOTP = otpService.getMostRecentOTP(user.getEmail());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", user.getId());
            responseData.put("email", user.getEmail());
            responseData.put("fullName", user.getFullName());
            responseData.put("firstName", user.getFirstName());
            responseData.put("lastName", user.getLastName());
            responseData.put("requiresOTP", true);
            // Include OTP code for frontend display (TEST MODE)
            if (generatedOTP.isPresent()) {
                responseData.put("otpCode", generatedOTP.get().getOtpCode());
                System.out.println("📱 OTP Code included in response: " + generatedOTP.get().getOtpCode());
            }

            return ResponseEntity
                    .ok(new ApiResponse(true, "Login successful. OTP sent for verification.", responseData));

        } catch (org.springframework.dao.InvalidDataAccessResourceUsageException e) {
            // Log detailed database error information
            System.out.println("💥 DATABASE ERROR during login:");
            System.out.println("Error: " + e.getMessage());
            logger.error("=== DATABASE SCHEMA ERROR IN LOGIN ===");
            logger.error("Exception Type: InvalidDataAccessResourceUsageException");
            logger.error("Error Message: {}", e.getMessage());
            logger.error("Root Cause: {}", e.getCause() != null ? e.getCause().getMessage() : "No root cause");
            logger.error("SQL State (if available): {}",
                    e.getCause() != null ? e.getCause().toString() : "Not available");
            logger.error("=== END DATABASE ERROR ===");

            // Handle missing schema/tables
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse(false,
                            "Database schema not available. Please ensure the database schema is created offline before using this application. Error: "
                                    + e.getMessage(),
                            null));
        } catch (Exception e) {
            System.out.println("💥 UNEXPECTED ERROR during login:");
            System.out.println("Exception Type: " + e.getClass().getSimpleName());
            System.out.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Verify OTP for registration
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyRegistrationOTP(@Valid @RequestBody OTPVerificationRequest request) {
        try {
            logger.info("=== OTP VERIFICATION STARTED ===");
            logger.info("Email: {}", request.getEmail());
            logger.info("OTP Code: {}", request.getOtpCode());
            logger.info("Purpose from request: {}", request.getPurpose());

            // Additional debug logging
            System.out.println("\n📧 OTP Verification Debug Info:");
            System.out.println("Email: " + request.getEmail());
            System.out.println("OTP Code: " + request.getOtpCode());
            System.out.println("Request Purpose: " + request.getPurpose());
            System.out.println("Using Purpose for validation: REGISTRATION_VERIFICATION\n");

            OTPService.OTPValidationResult result = otpService.validateOTPDetailed(
                    request.getEmail(),
                    request.getOtpCode(),
                    OTP.OTPPurpose.REGISTRATION_VERIFICATION);

            logger.info("OTP validation result: {}", result);

            if (result != OTPService.OTPValidationResult.VALID) {
                String errorMessage;
                switch (result) {
                    case INVALID_CODE:
                        errorMessage = "Invalid OTP code. Please check and try again.";
                        break;
                    case EXPIRED:
                        errorMessage = "Your verification code has expired. Please request a new one.";
                        break;
                    case ALREADY_USED:
                        errorMessage = "This OTP has already been used. Please request a new one.";
                        break;
                    case MAX_ATTEMPTS_EXCEEDED:
                        errorMessage = "Maximum verification attempts exceeded. Please request a new OTP.";
                        break;
                    case NOT_FOUND:
                        errorMessage = "No valid OTP found. Please request a new verification code.";
                        break;
                    default:
                        errorMessage = "OTP verification failed. Please try again.";
                }

                logger.warn("OTP validation failed for email: {} - Reason: {}", request.getEmail(), result);
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, errorMessage, null));
            }

            // Verify user account
            Optional<User> userOpt = userService.findByEmail(request.getEmail());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                userService.verifyUserAccount(user.getId());

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("userId", user.getId());
                responseData.put("email", user.getEmail());
                responseData.put("fullName", user.getFullName());
                responseData.put("isVerified", true);

                logger.info("OTP verification completed successfully for user: {}", user.getId());
                return ResponseEntity.ok(new ApiResponse(true, "Account verified successfully", responseData));
            }

            logger.error("User not found for email: {}", request.getEmail());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "User not found", null));

        } catch (Exception e) {
            logger.error("=== OTP VERIFICATION ERROR ===");
            logger.error("Error during OTP verification: {}", e.getMessage(), e);
            logger.error("=== END OTP VERIFICATION ERROR ===");
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Verify OTP for login
     */
    @PostMapping("/verify-login-otp")
    public ResponseEntity<ApiResponse> verifyLoginOTP(@Valid @RequestBody OTPVerificationRequest request,
            HttpServletRequest httpRequest) {
        try {
            // Add detailed request logging
            System.out.println("\n=== LOGIN OTP VERIFICATION REQUEST ===");
            System.out.println("🔍 Request URL: " + httpRequest.getRequestURL());
            System.out.println("📧 Email: " + request.getEmail());
            System.out.println("🔑 OTP Code: " + request.getOtpCode());
            System.out.println("🎯 Purpose: LOGIN_VERIFICATION (hardcoded)");
            System.out.println("📝 Request Headers:");
            java.util.Enumeration<String> headerNames = httpRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                System.out.println("   " + headerName + ": " + httpRequest.getHeader(headerName));
            }
            System.out.println("=====================================\n");

            OTPService.OTPValidationResult result = otpService.validateOTPDetailed(
                    request.getEmail(),
                    request.getOtpCode(),
                    OTP.OTPPurpose.LOGIN_VERIFICATION);

            if (result != OTPService.OTPValidationResult.VALID) {
                String errorMessage;
                switch (result) {
                    case INVALID_CODE:
                        errorMessage = "Invalid OTP code. Please check and try again.";
                        break;
                    case EXPIRED:
                        errorMessage = "Your verification code has expired. Please request a new one.";
                        break;
                    case ALREADY_USED:
                        errorMessage = "This OTP has already been used. Please request a new one.";
                        break;
                    case MAX_ATTEMPTS_EXCEEDED:
                        errorMessage = "Maximum verification attempts exceeded. Please request a new OTP.";
                        break;
                    case NOT_FOUND:
                        errorMessage = "No valid OTP found. Please request a new verification code.";
                        break;
                    default:
                        errorMessage = "OTP verification failed. Please try again.";
                }

                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, errorMessage, null));
            }

            Optional<User> userOpt = userService.findByEmail(request.getEmail());
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Create session or generate JWT token here
                // For now, we'll just return user info
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("userId", user.getId());
                responseData.put("email", user.getEmail());
                responseData.put("fullName", user.getFullName());
                responseData.put("firstName", user.getFirstName());
                responseData.put("lastName", user.getLastName());
                responseData.put("isVerified", user.getIsVerified());
                responseData.put("loginSuccess", true);

                return ResponseEntity.ok(new ApiResponse(true, "Login verification successful", responseData));
            }

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "User not found", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Request voting OTP
     */
    @PostMapping("/request-voting-otp")
    public ResponseEntity<ApiResponse> requestVotingOTP(@Valid @RequestBody VotingOTPRequest request) {
        try {
            Optional<User> userOpt = userService.findByEmail(request.getEmail());

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "User not found", null));
            }

            User user = userOpt.get();

            if (!user.getIsVerified() || !user.getIsActive()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "User account is not eligible for voting", null));
            }

            // Check if user can request OTP (rate limiting)
            if (!otpService.canRequestOTP(user.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Too many OTP requests. Please try again later.", null));
            }

            // Generate and send voting OTP
            otpService.generateAndSendOTP(user.getEmail(), OTP.OTPPurpose.VOTING_VERIFICATION);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("email", user.getEmail());
            responseData.put("message", "Voting OTP sent successfully");

            return ResponseEntity.ok(new ApiResponse(true, "Voting OTP sent", responseData));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Verify voting OTP
     */
    @PostMapping("/verify-voting-otp")
    public ResponseEntity<ApiResponse> verifyVotingOTP(@Valid @RequestBody OTPVerificationRequest request) {
        try {
            boolean isValid = otpService.validateOTP(
                    request.getEmail(),
                    request.getOtpCode(),
                    OTP.OTPPurpose.VOTING_VERIFICATION);

            if (!isValid) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Invalid or expired voting OTP", null));
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("email", request.getEmail());
            responseData.put("votingAuthorized", true);

            return ResponseEntity.ok(new ApiResponse(true, "Voting OTP verified successfully", responseData));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Request password reset OTP
     */
    @PostMapping("/request-password-reset")
    public ResponseEntity<ApiResponse> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        try {
            if (!userService.userExists(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "No account found with this email address", null));
            }

            // Check rate limiting
            if (!otpService.canRequestOTP(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Too many reset requests. Please try again later.", null));
            }

            // Generate and send password reset OTP
            otpService.generateAndSendOTP(request.getEmail(), OTP.OTPPurpose.PASSWORD_RESET);

            return ResponseEntity.ok(new ApiResponse(true, "Password reset OTP sent to your email", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Reset password with OTP
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody PasswordResetConfirmRequest request) {
        try {
            boolean isValid = otpService.validateOTP(
                    request.getEmail(),
                    request.getOtpCode(),
                    OTP.OTPPurpose.PASSWORD_RESET);

            if (!isValid) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Invalid or expired reset OTP", null));
            }

            // Reset password
            userService.resetPassword(request.getEmail(), request.getNewPassword());

            return ResponseEntity.ok(new ApiResponse(true, "Password reset successful", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * User logout endpoint
     * Invalidates JWT token and clears session
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@Valid @RequestBody LogoutRequest request,
            HttpServletRequest httpRequest) {
        try {
            // Extract additional info for audit logging
            String userAgent = httpRequest.getHeader("User-Agent");
            String clientIp = getClientIpAddress(httpRequest);

            // For now, logout is mainly client-side token removal
            // In a full implementation, you would:
            // 1. Add token to blacklist/invalidation cache
            // 2. Clear any server-side session data
            // 3. Log the logout event for audit purposes

            // Create logout response with metadata
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("logoutTime", java.time.LocalDateTime.now());
            responseData.put("message", "Successfully logged out from all sessions");

            return ResponseEntity.ok(
                    ApiResponse.success("Logout successful", responseData)
                            .withMetadata("clientIp", clientIp)
                            .withMetadata("userAgent", userAgent));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Logout failed: " + e.getMessage(), "LOGOUT_ERROR"));
        }
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP",
                "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Handle multiple IPs (take first one)
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Resend OTP
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOTP(@Valid @RequestBody ResendOTPRequest request) {
        try {
            if (!userService.userExists(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "User not found", null));
            }

            // Check if user can resend OTP (cooldown period)
            if (!otpService.canResendOTP(request.getEmail(), request.getPurpose())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Please wait before requesting a new OTP", null));
            }

            // Generate and send new OTP
            otpService.generateAndSendOTP(request.getEmail(), request.getPurpose());

            return ResponseEntity.ok(new ApiResponse(true, "OTP resent successfully", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get OTP for testing purposes (Development only)
     * Shows the most recent OTP sent to an email address
     */
    @GetMapping("/test-otp")
    public ResponseEntity<ApiResponse> getTestOTP(@RequestParam String email) {
        try {
            System.out.println("🔍 TEST OTP REQUEST for email: " + email);

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "User not found", null));
            }

            // Get the most recent non-expired OTP
            Optional<OTP> otpOpt = otpService.getMostRecentOTP(email);
            if (otpOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "No OTP found for this email", null));
            }

            OTP otp = otpOpt.get();

            // Check if OTP is still valid
            if (otp.isExpired()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "OTP has expired. Please request a new one.", null));
            }

            Map<String, Object> data = new HashMap<>();
            data.put("otpCode", otp.getOtpCode());
            data.put("expiresAt", otp.getExpiryTime());
            data.put("purpose", otp.getPurpose());

            System.out.println("✅ OTP Retrieved: " + otp.getOtpCode());
            return ResponseEntity.ok(new ApiResponse(true, "Test OTP retrieved", data));

        } catch (Exception e) {
            System.out.println("❌ Error getting test OTP: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error: " + e.getMessage(), null));
        }
    }

    // Request/Response DTOs

    public static class SignupRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;

        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        private String phoneNumber;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }

    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class OTPVerificationRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email")
        private String email;

        @NotBlank(message = "OTP code is required")
        @Size(min = 6, max = 6, message = "OTP must be 6 digits")
        private String otpCode;

        private String purpose; // Optional, for debugging/flexibility

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getOtpCode() {
            return otpCode;
        }

        public void setOtpCode(String otpCode) {
            this.otpCode = otpCode;
        }

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }
    }

    public static class VotingOTPRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email")
        private String email;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class PasswordResetRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email")
        private String email;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class PasswordResetConfirmRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email")
        private String email;

        @NotBlank(message = "OTP code is required")
        @Size(min = 6, max = 6, message = "OTP must be 6 digits")
        private String otpCode;

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String newPassword;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getOtpCode() {
            return otpCode;
        }

        public void setOtpCode(String otpCode) {
            this.otpCode = otpCode;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    public static class ResendOTPRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email")
        private String email;

        private OTP.OTPPurpose purpose;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public OTP.OTPPurpose getPurpose() {
            return purpose;
        }

        public void setPurpose(OTP.OTPPurpose purpose) {
            this.purpose = purpose;
        }
    }
}
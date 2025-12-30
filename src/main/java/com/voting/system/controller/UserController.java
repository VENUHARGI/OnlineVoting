package com.voting.system.controller;

import com.voting.system.model.ApiResponse;
import com.voting.system.model.User;
import com.voting.system.model.VotingHistory;
import com.voting.system.service.UserService;
import com.voting.system.service.VotingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for User Profile Management
 * 
 * Handles user profile operations, password changes, and user information
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VotingService votingService;

    /**
     * Get user profile by ID
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse> getUserProfile(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                Map<String, Object> profileData = new HashMap<>();
                profileData.put("id", user.getId());
                profileData.put("email", user.getEmail());
                profileData.put("firstName", user.getFirstName());
                profileData.put("lastName", user.getLastName());
                profileData.put("fullName", user.getFullName());
                profileData.put("phoneNumber", user.getPhoneNumber());
                profileData.put("isVerified", user.getIsVerified());
                profileData.put("isActive", user.getIsActive());
                profileData.put("createdAt", user.getCreatedAt());
                profileData.put("updatedAt", user.getUpdatedAt());
                profileData.put("isAccountLocked", user.isAccountLocked());

                return ResponseEntity.ok(new ApiResponse(true, "Profile retrieved successfully", profileData));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse> updateUserProfile(@PathVariable Long userId,
            @Valid @RequestBody ProfileUpdateRequest request) {
        try {
            User updatedUser = userService.updateUserProfile(
                    userId,
                    request.getFirstName(),
                    request.getLastName(),
                    request.getPhoneNumber());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", updatedUser.getId());
            responseData.put("firstName", updatedUser.getFirstName());
            responseData.put("lastName", updatedUser.getLastName());
            responseData.put("fullName", updatedUser.getFullName());
            responseData.put("phoneNumber", updatedUser.getPhoneNumber());
            responseData.put("updatedAt", updatedUser.getUpdatedAt());

            return ResponseEntity.ok(new ApiResponse(true, "Profile updated successfully", responseData));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Change user password
     */
    @PutMapping("/password/{userId}")
    public ResponseEntity<ApiResponse> changePassword(@PathVariable Long userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        try {
            userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());

            return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get user by email
     */
    @GetMapping("/profile/by-email/{email}")
    public ResponseEntity<ApiResponse> getUserByEmail(@PathVariable String email) {
        try {
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("email", user.getEmail());
                userData.put("fullName", user.getFullName());
                userData.put("isVerified", user.getIsVerified());
                userData.put("isActive", user.getIsActive());

                return ResponseEntity.ok(new ApiResponse(true, "User found", userData));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Check if email exists
     */
    @GetMapping("/exists/{email}")
    public ResponseEntity<ApiResponse> checkEmailExists(@PathVariable String email) {
        try {
            boolean exists = userService.userExists(email);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("exists", exists);
            responseData.put("email", email);

            return ResponseEntity.ok(new ApiResponse(true, "Email check completed", responseData));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Search users by name (admin function - could be secured)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchUsers(@RequestParam String name) {
        try {
            List<User> users = userService.searchUsersByName(name);

            // Return limited user info for privacy
            List<Map<String, Object>> userList = users.stream().map(user -> {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("fullName", user.getFullName());
                userData.put("email", user.getEmail().replaceAll("(.{2}).*@", "$1***@")); // Mask email
                userData.put("isVerified", user.getIsVerified());
                userData.put("isActive", user.getIsActive());
                return userData;
            }).toList();

            return ResponseEntity.ok(new ApiResponse(true, "Search results retrieved", userList));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get user statistics (admin function - could be secured)
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse> getUserStatistics() {
        try {
            UserService.UserStats stats = userService.getUserStatistics();
            return ResponseEntity.ok(new ApiResponse(true, "Statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Deactivate user account (soft delete)
     */
    @DeleteMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse> deactivateAccount(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Account deactivated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get verified and active users (admin function - could be secured)
     */
    @GetMapping("/verified")
    public ResponseEntity<ApiResponse> getVerifiedUsers() {
        try {
            List<User> users = userService.getVerifiedActiveUsers();

            // Return limited user info
            List<Map<String, Object>> userList = users.stream().map(user -> {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("fullName", user.getFullName());
                userData.put("email", user.getEmail());
                userData.put("createdAt", user.getCreatedAt());
                return userData;
            }).toList();

            return ResponseEntity.ok(new ApiResponse(true, "Verified users retrieved", userList));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get user voting history
     * Returns anonymized voting history without revealing which party was voted for
     */
    @GetMapping("/voting-history/{userId}")
    public ResponseEntity<ApiResponse> getVotingHistory(@PathVariable Long userId) {
        try {
            // Verify user exists
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "User not found", null));
            }

            // Get voting history for the user
            List<VotingHistory> votingHistory = votingService.getVotingHistoryForUser(userId);

            // Prepare response data
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", userId);
            responseData.put("totalVotes", votingHistory.size());
            responseData.put("votingHistory", votingHistory);
            responseData.put("lastUpdated", java.time.LocalDateTime.now());

            return ResponseEntity.ok(new ApiResponse(true, "Voting history retrieved successfully", responseData));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve voting history: " + e.getMessage(), null));
        }
    }

    // Inner classes for request/response DTOs
    public static class ProfileUpdateRequest {
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        private String firstName;

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        private String lastName;

        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        private String phoneNumber;

        // Getters and setters
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

    public static class PasswordChangeRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "New password must be at least 8 characters")
        private String newPassword;

        // Getters and setters
        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
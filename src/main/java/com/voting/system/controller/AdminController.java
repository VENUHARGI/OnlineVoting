package com.voting.system.controller;

import com.voting.system.model.Constituency;
import com.voting.system.model.Party;
import com.voting.system.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Administrative operations
 * 
 * Handles system administration, constituency/party management, and analytics
 * Note: In production, this should be secured with proper admin authentication
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private VotingService votingService;

    @Autowired
    private ConstituencyService constituencyService;

    // Database Schema Management

    /**
     * Check database connectivity and schema status
     */
    @GetMapping("/database/health")
    public ResponseEntity<ApiResponse> checkDatabaseHealth() {
        try {
            // Simple connectivity test
            userService.getUserStatistics();
            Map<String, Object> result = new HashMap<>();
            result.put("status", "healthy");
            result.put("message", "Database connection successful and schema accessible");
            return ResponseEntity.ok(new ApiResponse(true, "Database healthy", result));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "unhealthy");
            error.put("error", e.getMessage());
            error.put("solution", "Ensure Oracle database is running and schema is created offline");
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Database connection failed", error));
        }
    }

    // System Statistics and Monitoring

    /**
     * Get comprehensive system statistics
     */
    @GetMapping("/statistics/overview")
    public ResponseEntity<ApiResponse> getSystemOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();

            overview.put("userStats", userService.getUserStatistics());
            overview.put("otpStats", otpService.getOTPStatistics());
            overview.put("votingStats", votingService.getVotingStatistics());
            overview.put("constituencyStats", constituencyService.getStatistics());

            return ResponseEntity.ok(new ApiResponse(true, "System overview retrieved", overview));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get suspicious voting patterns
     */
    @GetMapping("/security/suspicious-patterns")
    public ResponseEntity<ApiResponse> getSuspiciousVotingPatterns(@RequestParam(defaultValue = "3") int threshold) {
        try {
            List<VotingService.SuspiciousVotingPattern> patterns = votingService.getSuspiciousVotingPatterns(threshold);
            return ResponseEntity.ok(new ApiResponse(true, "Suspicious patterns retrieved", patterns));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Constituency Management

    /**
     * Create new constituency
     */
    @PostMapping("/constituencies")
    public ResponseEntity<ApiResponse> createConstituency(@Valid @RequestBody ConstituencyCreateRequest request) {
        try {
            Constituency constituency = constituencyService.createConstituency(
                    request.getName(),
                    request.getState(),
                    request.getDescription());

            return ResponseEntity.ok(new ApiResponse(true, "Constituency created successfully", constituency));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Update constituency
     */
    @PutMapping("/constituencies/{id}")
    public ResponseEntity<ApiResponse> updateConstituency(@PathVariable Long id,
            @Valid @RequestBody ConstituencyUpdateRequest request) {
        try {
            Constituency constituency = constituencyService.updateConstituency(
                    id,
                    request.getName(),
                    request.getState(),
                    request.getDescription());

            return ResponseEntity.ok(new ApiResponse(true, "Constituency updated successfully", constituency));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Activate/Deactivate constituency
     */
    @PutMapping("/constituencies/{id}/status")
    public ResponseEntity<ApiResponse> updateConstituencyStatus(@PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        try {
            constituencyService.updateConstituencyStatus(id, request.getIsActive());
            return ResponseEntity.ok(new ApiResponse(true, "Constituency status updated", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Party Management

    /**
     * Create new party - DISABLED
     * Note: In the candidate-based structure, parties are independent
     */
    /*
     * @PostMapping("/parties")
     * public ResponseEntity<ApiResponse> createParty(@Valid @RequestBody
     * PartyCreateRequest request) {
     * try {
     * Party party = constituencyService.createParty(
     * request.getName(),
     * request.getSymbol(),
     * request.getDescription(),
     * request.getConstituencyId(),
     * request.getLogoUrl());
     * 
     * return ResponseEntity.ok(new ApiResponse(true, "Party created successfully",
     * party));
     * } catch (Exception e) {
     * return ResponseEntity.badRequest()
     * .body(new ApiResponse(false, e.getMessage(), null));
     * }
     * }
     */

    /**
     * Update party
     */
    @PutMapping("/parties/{id}")
    public ResponseEntity<ApiResponse> updateParty(@PathVariable Long id,
            @Valid @RequestBody PartyUpdateRequest request) {
        try {
            Party party = constituencyService.updateParty(
                    id,
                    request.getName(),
                    request.getSymbol(),
                    request.getDescription(),
                    request.getLogoUrl());

            return ResponseEntity.ok(new ApiResponse(true, "Party updated successfully", party));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Activate/Deactivate party
     */
    @PutMapping("/parties/{id}/status")
    public ResponseEntity<ApiResponse> updatePartyStatus(@PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        try {
            constituencyService.updatePartyStatus(id, request.getIsActive());
            return ResponseEntity.ok(new ApiResponse(true, "Party status updated", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // User Management

    /**
     * Get locked user accounts
     */
    @GetMapping("/users/locked")
    public ResponseEntity<ApiResponse> getLockedUsers() {
        try {
            var lockedUsers = userService.getLockedAccounts();
            return ResponseEntity.ok(new ApiResponse(true, "Locked users retrieved", lockedUsers));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Unlock user account
     */
    @PutMapping("/users/{userId}/unlock")
    public ResponseEntity<ApiResponse> unlockUser(@PathVariable Long userId) {
        try {
            userService.unlockUserAccount(userId);
            return ResponseEntity.ok(new ApiResponse(true, "User account unlocked", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Update user active status
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse> updateUserStatus(@PathVariable Long userId,
            @Valid @RequestBody StatusUpdateRequest request) {
        try {
            userService.updateUserActiveStatus(userId, request.getIsActive());
            return ResponseEntity.ok(new ApiResponse(true, "User status updated", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Vote Management

    /**
     * Flag vote for review
     */
    @PutMapping("/votes/{voteId}/flag")
    public ResponseEntity<ApiResponse> flagVote(@PathVariable Long voteId,
            @Valid @RequestBody FlagVoteRequest request) {
        try {
            votingService.flagVote(voteId, request.getReason());
            return ResponseEntity.ok(new ApiResponse(true, "Vote flagged for review", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Analytics and Reports

    /**
     * Get top parties by votes
     */
    @GetMapping("/analytics/top-parties")
    public ResponseEntity<ApiResponse> getTopParties() {
        try {
            var topParties = constituencyService.getTopPartiesByVotes();
            return ResponseEntity.ok(new ApiResponse(true, "Top parties retrieved", topParties));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get constituencies without candidates
     */
    @GetMapping("/analytics/constituencies-without-candidates")
    public ResponseEntity<ApiResponse> getConstituenciesWithoutCandidates() {
        try {
            var constituencies = constituencyService.getConstituenciesWithoutCandidates();
            return ResponseEntity
                    .ok(new ApiResponse(true, "Constituencies without candidates retrieved", constituencies));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get parties without votes
     */
    @GetMapping("/analytics/parties-without-votes")
    public ResponseEntity<ApiResponse> getPartiesWithoutVotes() {
        try {
            var parties = constituencyService.getPartiesWithoutVotes();
            return ResponseEntity.ok(new ApiResponse(true, "Parties without votes retrieved", parties));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Request DTOs

    public static class ConstituencyCreateRequest {
        @NotBlank(message = "Name is required")
        @Size(max = 200, message = "Name must not exceed 200 characters")
        private String name;

        @NotBlank(message = "State is required")
        @Size(max = 100, message = "State must not exceed 100 characters")
        private String state;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class ConstituencyUpdateRequest {
        @NotBlank(message = "Name is required")
        @Size(max = 200, message = "Name must not exceed 200 characters")
        private String name;

        @NotBlank(message = "State is required")
        @Size(max = 100, message = "State must not exceed 100 characters")
        private String state;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class PartyCreateRequest {
        @NotBlank(message = "Name is required")
        @Size(max = 200, message = "Name must not exceed 200 characters")
        private String name;

        @NotBlank(message = "Symbol is required")
        @Size(max = 100, message = "Symbol must not exceed 100 characters")
        private String symbol;

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        private String description;

        @NotNull(message = "Constituency ID is required")
        private Long constituencyId;

        @Size(max = 255, message = "Logo URL must not exceed 255 characters")
        private String logoUrl;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getConstituencyId() {
            return constituencyId;
        }

        public void setConstituencyId(Long constituencyId) {
            this.constituencyId = constituencyId;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }
    }

    public static class PartyUpdateRequest {
        @NotBlank(message = "Name is required")
        @Size(max = 200, message = "Name must not exceed 200 characters")
        private String name;

        @NotBlank(message = "Symbol is required")
        @Size(max = 100, message = "Symbol must not exceed 100 characters")
        private String symbol;

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        private String description;

        @Size(max = 255, message = "Logo URL must not exceed 255 characters")
        private String logoUrl;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }
    }

    public static class StatusUpdateRequest {
        @NotNull(message = "Status is required")
        private Boolean isActive;

        // Getters and setters
        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }
    }

    public static class FlagVoteRequest {
        @NotBlank(message = "Reason is required")
        @Size(max = 1000, message = "Reason must not exceed 1000 characters")
        private String reason;

        // Getters and setters
        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;

        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters and setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}
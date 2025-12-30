package com.voting.system.controller;

import com.voting.system.model.ApiResponse;
import com.voting.system.model.Candidate;
import com.voting.system.model.Constituency;
import com.voting.system.model.Party;
import com.voting.system.model.Vote;
import com.voting.system.service.CandidateService;
import com.voting.system.service.ConstituencyService;
import com.voting.system.service.VotingService;
import com.voting.system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Voting operations
 * 
 * Handles constituency management, party listings, vote casting, and voting
 * analytics
 */
@RestController
@RequestMapping("/api/voting")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VotingController {

    @Autowired
    private VotingService votingService;

    @Autowired
    private ConstituencyService constituencyService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private UserService userService;

    /**
     * Get general voting status (simplified for testing without user
     * authentication)
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse> getGeneralVotingStatus() {
        try {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("hasVoted", false); // Always false for testing
            responseData.put("votingOpen", true); // Always open for testing
            responseData.put("message", "Voting is currently open");

            return ResponseEntity.ok(new ApiResponse(true, "Voting status retrieved", responseData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Simple test endpoint to check API access
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse> testEndpoint() {
        return ResponseEntity.ok(new ApiResponse(true, "API is working!", "Test successful"));
    }

    /**
     * Get all active constituencies
     */
    @GetMapping("/constituencies")
    public ResponseEntity<ApiResponse> getAllConstituencies() {
        System.out.println("🔍 CONSTITUENCIES ENDPOINT HIT - Starting method");
        try {
            // Get real constituencies from database
            System.out.println("🔍 Calling constituencyService.getAllActiveConstituencies()");
            List<Constituency> constituencies = constituencyService.getAllActiveConstituencies();

            System.out.println("🔍 Retrieved " + constituencies.size() + " constituencies from database");

            if (constituencies.isEmpty()) {
                System.out.println("⚠️ WARNING: No constituencies found in database!");
            } else {
                System.out.println("✅ Found constituencies: " + constituencies.stream().map(c -> c.getName()).toList());
            }

            return ResponseEntity
                    .ok(new ApiResponse(true, "Constituencies retrieved successfully", constituencies));
        } catch (Exception e) {
            System.out.println("❌ Error fetching constituencies: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get constituencies by state
     */
    @GetMapping("/constituencies/by-state/{state}")
    public ResponseEntity<ApiResponse> getConstituenciesByState(@PathVariable String state) {
        try {
            List<Constituency> constituencies = constituencyService.getConstituenciesByState(state);
            return ResponseEntity.ok(new ApiResponse(true, "Constituencies retrieved successfully", constituencies));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get all states
     */
    @GetMapping("/states")
    public ResponseEntity<ApiResponse> getAllStates() {
        try {
            List<String> states = constituencyService.getAllStates();
            return ResponseEntity.ok(new ApiResponse(true, "States retrieved successfully", states));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get constituency by ID
     */
    @GetMapping("/constituencies/{id}")
    public ResponseEntity<ApiResponse> getConstituencyById(@PathVariable Long id) {
        try {
            Optional<Constituency> constituency = constituencyService.getConstituencyById(id);
            if (constituency.isPresent()) {
                return ResponseEntity.ok(new ApiResponse(true, "Constituency found", constituency.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get candidates for a specific constituency
     */
    @GetMapping("/constituencies/{constituencyId}/parties")
    public ResponseEntity<ApiResponse> getPartiesByConstituency(@PathVariable Long constituencyId) {
        try {
            // Get real candidates from database for this constituency
            List<Candidate> candidates = candidateService.getCandidatesByConstituencyId(constituencyId);

            System.out.println(
                    "Retrieved " + candidates.size() + " candidates for constituency " + constituencyId
                            + " from database");

            return ResponseEntity.ok(new ApiResponse(true, "Candidates retrieved successfully", candidates));
        } catch (Exception e) {
            System.out.println("Error fetching candidates: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get party by ID
     */
    @GetMapping("/parties/{id}")
    public ResponseEntity<ApiResponse> getPartyById(@PathVariable Long id) {
        try {
            // Get real party from database
            Optional<Party> party = constituencyService.getPartyById(id);

            if (party.isPresent()) {
                System.out.println("Retrieved party for ID: " + id + " from database");
                return ResponseEntity.ok(new ApiResponse(true, "Party found", party.get()));
            } else {
                System.out.println("Party not found for ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("Error fetching party: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Cast a vote
     */
    @PostMapping("/cast-vote")
    public ResponseEntity<ApiResponse> castVote(@Valid @RequestBody VoteRequest request,
            HttpServletRequest httpRequest) {
        try {
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            Vote vote = votingService.castVote(
                    request.getUserId(),
                    request.getConstituencyId(),
                    request.getPartyId(),
                    request.getCandidateId(),
                    ipAddress,
                    userAgent);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("voteId", vote.getId());
            responseData.put("constituency", vote.getConstituencyName());
            responseData.put("candidate", vote.getCandidateFullInfo());
            responseData.put("votedAt", vote.getVotedAt());
            responseData.put("sessionId", vote.getSessionId());
            responseData.put("transactionId", vote.getSessionId()); // Using sessionId as transactionId

            return ResponseEntity.ok(new ApiResponse(true, "Vote cast successfully", responseData));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Check voting eligibility
     */
    @GetMapping("/eligibility/{userId}")
    public ResponseEntity<ApiResponse> checkVotingEligibility(@PathVariable Long userId) {
        try {
            VotingService.VotingEligibility eligibility = votingService.checkVotingEligibility(userId);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("eligible", eligibility.isEligible());
            responseData.put("message", eligibility.getMessage());
            if (eligibility.getUser() != null) {
                responseData.put("userEmail", eligibility.getUser().getEmail());
                responseData.put("userFullName", eligibility.getUser().getFullName());
            }

            return ResponseEntity.ok(new ApiResponse(true, "Eligibility checked", responseData));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Check if user has voted
     */
    @GetMapping("/status/{userId}")
    public ResponseEntity<ApiResponse> getVotingStatus(@PathVariable Long userId) {
        try {
            boolean hasVoted = votingService.hasUserVoted(userId);
            List<Vote> voteHistory = votingService.getUserVoteHistory(userId);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("hasVoted", hasVoted);
            responseData.put("voteHistory", voteHistory);

            return ResponseEntity.ok(new ApiResponse(true, "Voting status retrieved", responseData));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get voting results for constituency
     */
    @GetMapping("/results/{constituencyId}")
    public ResponseEntity<ApiResponse> getConstituencyResults(@PathVariable Long constituencyId) {
        try {
            List<VotingService.VoteResult> results = votingService.getConstituencyResults(constituencyId);
            return ResponseEntity.ok(new ApiResponse(true, "Results retrieved successfully", results));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get overall voting statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse> getVotingStatistics() {
        try {
            VotingService.VotingStatistics stats = votingService.getVotingStatistics();
            return ResponseEntity.ok(new ApiResponse(true, "Statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get constituency-wise vote summary
     */
    @GetMapping("/summary/constituencies")
    public ResponseEntity<ApiResponse> getConstituencyVoteSummary() {
        try {
            List<VotingService.ConstituencyVoteSummary> summary = votingService.getConstituencyVoteSummary();
            return ResponseEntity.ok(new ApiResponse(true, "Summary retrieved successfully", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get hourly vote distribution
     */
    @GetMapping("/distribution/hourly")
    public ResponseEntity<ApiResponse> getHourlyVoteDistribution() {
        try {
            List<VotingService.HourlyVoteDistribution> distribution = votingService.getHourlyVoteDistribution();
            return ResponseEntity.ok(new ApiResponse(true, "Distribution retrieved successfully", distribution));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Search constituencies
     */
    @GetMapping("/constituencies/search")
    public ResponseEntity<ApiResponse> searchConstituencies(@RequestParam String query) {
        try {
            List<Constituency> constituencies = constituencyService.searchConstituencies(query);
            return ResponseEntity.ok(new ApiResponse(true, "Search results retrieved", constituencies));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Search parties
     */
    @GetMapping("/parties/search")
    public ResponseEntity<ApiResponse> searchParties(@RequestParam String query) {
        try {
            List<Party> parties = constituencyService.searchParties(query);
            return ResponseEntity.ok(new ApiResponse(true, "Search results retrieved", parties));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get constituencies with candidate count
     */
    @GetMapping("/constituencies/with-candidate-count")
    public ResponseEntity<ApiResponse> getConstituenciesWithCandidateCount() {
        try {
            List<ConstituencyService.ConstituencyWithCandidateCount> data = constituencyService
                    .getConstituenciesWithCandidateCount();
            return ResponseEntity.ok(new ApiResponse(true, "Data retrieved successfully", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get parties with vote count for constituency - DISABLED
     * Note: Use candidate-based endpoints instead
     */
    /*
     * @GetMapping("/constituencies/{constituencyId}/parties/with-votes")
     * public ResponseEntity<ApiResponse> getPartiesWithVoteCount(@PathVariable Long
     * constituencyId) {
     * try {
     * List<ConstituencyService.PartyWithVoteCount> data = constituencyService
     * .getPartiesWithVoteCount(constituencyId);
     * return ResponseEntity.ok(new ApiResponse(true, "Data retrieved successfully",
     * data));
     * } catch (Exception e) {
     * return ResponseEntity.badRequest()
     * .body(new ApiResponse(false, e.getMessage(), null));
     * }
     * }
     */

    /**
     * Get user's vote receipt information
     */
    @GetMapping("/receipt")
    public ResponseEntity<ApiResponse> getVoteReceipt(@RequestParam(required = false) Long userId) {
        try {
            // If userId not provided, try to get from security context
            if (userId == null) {
                userId = getCurrentUserId();
            }

            System.out.println("\n🔍 === RECEIPT REQUEST ===");
            System.out.println("📍 Received userId parameter: " + userId);

            if (userId == null) {
                System.out.println("❌ User ID is NULL");
                return ResponseEntity.status(401)
                        .body(new ApiResponse(false, "User not authenticated. Please provide userId parameter.", null));
            }

            // Get user's latest vote
            System.out.println("🔎 Fetching vote history for userId: " + userId);
            List<Vote> userVotes = votingService.getUserVoteHistory(userId);

            System.out.println("📋 Found " + userVotes.size() + " votes for user");

            if (userVotes.isEmpty()) {
                System.out.println("⚠️ No votes found for userId: " + userId);
                return ResponseEntity.status(404)
                        .body(new ApiResponse(false, "No vote found for user", null));
            }

            Vote vote = userVotes.get(0); // Get latest vote

            System.out.println("📦 Vote Details:");
            System.out.println("   - SessionId: " + vote.getSessionId());
            System.out.println("   - UserId: " + vote.getUser().getId());
            System.out.println("   - VotedAt: " + vote.getVotedAt());
            System.out.println("   - Constituency: "
                    + (vote.getConstituency() != null ? vote.getConstituency().getName() : "NULL"));
            System.out.println(
                    "   - Candidate: " + (vote.getCandidate() != null ? vote.getCandidate().getName() : "NULL"));
            System.out.println("   - Party: " + (vote.getCandidate() != null && vote.getCandidate().getParty() != null
                    ? vote.getCandidate().getParty().getName()
                    : "NULL"));

            Map<String, Object> receipt = new HashMap<>();
            receipt.put("transactionId", vote.getSessionId());
            receipt.put("voterId", userId);
            receipt.put("constituencyName", vote.getConstituency().getName());
            receipt.put("partyName", vote.getCandidate().getParty().getName());
            receipt.put("candidateName", vote.getCandidate().getName());
            receipt.put("timestamp", vote.getVotedAt()); // Gets VOTED_AT from database
            receipt.put("status", "CAST");

            System.out.println("✅ Receipt prepared: " + receipt);
            System.out.println("=========================\n");

            return ResponseEntity.ok(new ApiResponse(true, "Receipt retrieved", receipt));
        } catch (Exception e) {
            System.out.println("💥 ERROR in getVoteReceipt:");
            System.out.println("   " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get election information
     */
    @GetMapping("/election-info")
    public ResponseEntity<ApiResponse> getElectionInfo() {
        try {
            long totalVotes = votingService.getTotalVoteCount();
            List<Constituency> allConstituencies = constituencyService.getAllActiveConstituencies();

            Map<String, Object> electionInfo = new HashMap<>();
            electionInfo.put("totalRegisteredVoters", 1000000); // Mock value
            electionInfo.put("totalVotesCast", totalVotes);
            electionInfo.put("turnoutPercentage", Math.round((totalVotes / 1000000.0) * 100));
            electionInfo.put("activeConstituencies", allConstituencies.size());
            electionInfo.put("startDate", "2025-01-01");
            electionInfo.put("endDate", "2025-01-31");

            return ResponseEntity.ok(new ApiResponse(true, "Election info retrieved", electionInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Helper method to get current user ID from JWT token
     */
    private Long getCurrentUserId() {
        // This should extract user ID from JWT token in Authorization header
        // For now, return null to indicate not authenticated
        // In production, integrate with your JWT token parser
        try {
            // Get from session or principal
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof String) {
                String username = auth.getPrincipal().toString();
                com.voting.system.model.User user = userService.findByEmail(username)
                        .orElse(null);
                return user != null ? user.getId() : null;
            }
        } catch (Exception e) {
            System.err.println("Error getting current user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    // Request DTOs

    public static class VoteRequest {
        @NotNull(message = "User ID is required")
        private Long userId;

        @NotNull(message = "Constituency ID is required")
        private Long constituencyId;

        @NotNull(message = "Party ID is required")
        private Long partyId;

        @NotNull(message = "Candidate ID is required")
        private Long candidateId;

        // Getters and setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getConstituencyId() {
            return constituencyId;
        }

        public void setConstituencyId(Long constituencyId) {
            this.constituencyId = constituencyId;
        }

        public Long getPartyId() {
            return partyId;
        }

        public void setPartyId(Long partyId) {
            this.partyId = partyId;
        }

        public Long getCandidateId() {
            return candidateId;
        }

        public void setCandidateId(Long candidateId) {
            this.candidateId = candidateId;
        }
    }
}
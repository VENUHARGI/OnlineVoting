package com.voting.system.service;

import com.voting.system.model.*;
import com.voting.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for Voting operations
 * 
 * Handles vote casting, validation, eligibility checks, and voting analytics
 */
@Service
@Transactional
public class VotingService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConstituencyRepository constituencyRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    /**
     * Cast a vote
     */
    public Vote castVote(Long userId, Long constituencyId, Long partyId, Long candidateId, String ipAddress,
            String userAgent) {
        // Validate user
        User user = userRepository.findById(userId != null ? userId : 0L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsActive()) {
            throw new RuntimeException("User account is not active");
        }

        if (!user.getIsVerified()) {
            throw new RuntimeException("User account is not verified. Please verify your email first.");
        }

        if (user.isAccountLocked()) {
            throw new RuntimeException("User account is locked");
        }

        // Check if user has already voted in ANY constituency (prevent multiple votes)
        if (voteRepository.existsByUser(user)) {
            throw new RuntimeException("You have already voted. A user can only vote once in the election.");
        }

        // Validate constituency
        Constituency constituency = constituencyRepository.findById(constituencyId != null ? constituencyId : 0L)
                .orElseThrow(() -> new RuntimeException("Constituency not found"));

        if (!constituency.getIsActive()) {
            throw new RuntimeException("Constituency is not active");
        }

        // Validate candidate
        Candidate candidate = candidateRepository.findById(candidateId != null ? candidateId : 0L)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (!candidate.getIsActive()) {
            throw new RuntimeException("Candidate is not active");
        }

        // Validate candidate belongs to constituency
        if (!candidate.getConstituency().getId().equals(constituencyId)) {
            throw new RuntimeException("Candidate does not belong to the selected constituency");
        }

        // Validate party
        Party party = candidate.getParty();
        if (party == null || !party.getIsActive()) {
            throw new RuntimeException("Candidate's party is not active");
        }

        // Additional validation: ensure partyId matches if provided
        if (partyId != null && !party.getId().equals(partyId)) {
            throw new RuntimeException("Party ID does not match candidate's party");
        }

        // Create and save vote
        String sessionId = UUID.randomUUID().toString();
        Vote vote = new Vote(user, constituency, candidate, sessionId, ipAddress, userAgent);
        vote.setId(voteRepository.getNextId()); // Set ID manually for schemas without auto-increment

        Vote savedVote = voteRepository.save(vote);

        return savedVote;
    }

    /**
     * Check if user is eligible to vote
     */
    public VotingEligibility checkVotingEligibility(Long userId) {
        User user = userRepository.findById(userId != null ? userId : 0L)
                .orElse(null);

        if (user == null) {
            return new VotingEligibility(false, "User not found", null);
        }

        if (!user.getIsActive()) {
            return new VotingEligibility(false, "User account is not active", user);
        }

        if (!user.getIsVerified()) {
            return new VotingEligibility(false, "User account is not verified", user);
        }

        if (user.isAccountLocked()) {
            return new VotingEligibility(false, "User account is locked", user);
        }

        // Check if user has already voted anywhere
        if (voteRepository.existsByUser(user)) {
            return new VotingEligibility(false, "User has already voted", user);
        }

        return new VotingEligibility(true, "User is eligible to vote", user);
    }

    /**
     * Check if user has voted in specific constituency
     */
    public boolean hasUserVotedInConstituency(Long userId, Long constituencyId) {
        Optional<User> userOpt = userRepository.findById(userId != null ? userId : 0L);
        Optional<Constituency> constituencyOpt = constituencyRepository
                .findById(constituencyId != null ? constituencyId : 0L);

        if (userOpt.isEmpty() || constituencyOpt.isEmpty()) {
            return false;
        }

        return voteRepository.existsByUserAndConstituency(userOpt.get(), constituencyOpt.get());
    }

    /**
     * Check if user has voted anywhere
     */
    public boolean hasUserVoted(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId != null ? userId : 0L);
        return userOpt.isPresent() && voteRepository.existsByUser(userOpt.get());
    }

    /**
     * Get user's vote history
     */
    public List<Vote> getUserVoteHistory(Long userId) {
        User user = userRepository.findById(userId != null ? userId : 0L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return voteRepository.findByUserOrderByVotedAtDesc(user);
    }

    /**
     * Get vote results for a constituency
     */
    public List<VoteResult> getConstituencyResults(Long constituencyId) {
        List<Object[]> results = voteRepository.getVoteResultsByConstituencyId(constituencyId);

        return results.stream()
                .map(row -> new VoteResult(
                        (Party) row[0],
                        (Long) row[1]))
                .toList();
    }

    /**
     * Get overall voting statistics
     */
    public VotingStatistics getVotingStatistics() {
        VotingStatistics stats = new VotingStatistics();

        stats.setTotalVotes(voteRepository.countTotalValidVotes());
        stats.setUniqueVoters(voteRepository.countUniqueVoters());
        stats.setConstituenciesWithVotes(voteRepository.countConstituenciesWithVotes());
        stats.setPartiesWithVotes(voteRepository.countPartiesWithVotes());

        // Calculate voter turnout percentage
        long totalEligibleVoters = userRepository.countActiveVerifiedUsers();
        if (totalEligibleVoters > 0) {
            double turnoutPercentage = (double) stats.getUniqueVoters() / totalEligibleVoters * 100;
            stats.setVoterTurnoutPercentage(turnoutPercentage);
        }

        stats.setVotesToday(voteRepository.findVotesToday().size());

        return stats;
    }

    /**
     * Get constituency-wise vote summary
     */
    public List<ConstituencyVoteSummary> getConstituencyVoteSummary() {
        List<Object[]> results = voteRepository.getConstituencyWiseVoteSummary();

        return results.stream()
                .map(row -> new ConstituencyVoteSummary(
                        (String) row[0], // constituency name
                        (Long) row[1] // vote count
                ))
                .toList();
    }

    /**
     * Get hourly vote distribution for today
     */
    public List<HourlyVoteDistribution> getHourlyVoteDistribution() {
        List<Object[]> results = voteRepository.getHourlyVoteDistribution();

        return results.stream()
                .map(row -> new HourlyVoteDistribution(
                        (Integer) row[0], // hour
                        (Long) row[1] // vote count
                ))
                .toList();
    }

    /**
     * Flag vote for review (admin function)
     */
    public void flagVote(Long voteId, String reason) {
        Vote vote = voteRepository.findById(voteId != null ? voteId : 0L)
                .orElseThrow(() -> new RuntimeException("Vote not found"));

        vote.setStatus(Vote.VoteStatus.FLAGGED);
        voteRepository.save(vote);
    }

    /**
     * Get suspicious voting patterns
     */
    public List<SuspiciousVotingPattern> getSuspiciousVotingPatterns(int threshold) {
        List<Object[]> results = voteRepository.findSuspiciousVotingPatterns(threshold);

        return results.stream()
                .map(row -> new SuspiciousVotingPattern(
                        (String) row[0], // IP address
                        (Long) row[1] // vote count
                ))
                .toList();
    }

    /**
     * Get voting history for a specific user
     * Returns anonymized voting history without revealing which party was voted for
     */
    public List<VotingHistory> getVotingHistoryForUser(Long userId) {
        List<Vote> votes = voteRepository.findByUserIdOrderByVotedAtDesc(userId);

        return votes.stream().map(vote -> {
            Constituency constituency = vote.getConstituency();

            VotingHistory history = new VotingHistory();
            history.setId(vote.getId());
            history.setConstituencyName(constituency.getName());
            history.setState(constituency.getState());
            history.setVotedAt(vote.getVotedAt());
            history.setSessionId(vote.getSessionId());
            history.setStatus("CONFIRMED");
            history.setTransactionId(generateTransactionId(vote));

            return history;
        }).collect(Collectors.toList());
    }

    /**
     * Generate transaction ID for voting history
     */
    private String generateTransactionId(Vote vote) {
        // Generate a unique transaction ID based on vote details
        // This provides a reference without exposing sensitive information
        return "VTX" + vote.getId() + "-" +
                vote.getVotedAt().toLocalDate().toString().replace("-", "") +
                "-" + String.format("%04d", vote.getId() % 10000);
    }

    // DTOs for service responses

    public static class VotingEligibility {
        private boolean eligible;
        private String message;
        private User user;

        public VotingEligibility(boolean eligible, String message, User user) {
            this.eligible = eligible;
            this.message = message;
            this.user = user;
        }

        // Getters
        public boolean isEligible() {
            return eligible;
        }

        public String getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }
    }

    public static class VoteResult {
        private Party party;
        private Long voteCount;

        public VoteResult(Party party, Long voteCount) {
            this.party = party;
            this.voteCount = voteCount;
        }

        // Getters
        public Party getParty() {
            return party;
        }

        public Long getVoteCount() {
            return voteCount;
        }
    }

    public static class VotingStatistics {
        private long totalVotes;
        private long uniqueVoters;
        private long constituenciesWithVotes;
        private long partiesWithVotes;
        private double voterTurnoutPercentage;
        private long votesToday;

        // Getters and setters
        public long getTotalVotes() {
            return totalVotes;
        }

        public void setTotalVotes(long totalVotes) {
            this.totalVotes = totalVotes;
        }

        public long getUniqueVoters() {
            return uniqueVoters;
        }

        public void setUniqueVoters(long uniqueVoters) {
            this.uniqueVoters = uniqueVoters;
        }

        public long getConstituenciesWithVotes() {
            return constituenciesWithVotes;
        }

        public void setConstituenciesWithVotes(long constituenciesWithVotes) {
            this.constituenciesWithVotes = constituenciesWithVotes;
        }

        public long getPartiesWithVotes() {
            return partiesWithVotes;
        }

        public void setPartiesWithVotes(long partiesWithVotes) {
            this.partiesWithVotes = partiesWithVotes;
        }

        public double getVoterTurnoutPercentage() {
            return voterTurnoutPercentage;
        }

        public void setVoterTurnoutPercentage(double voterTurnoutPercentage) {
            this.voterTurnoutPercentage = voterTurnoutPercentage;
        }

        public long getVotesToday() {
            return votesToday;
        }

        public void setVotesToday(long votesToday) {
            this.votesToday = votesToday;
        }
    }

    public static class ConstituencyVoteSummary {
        private String constituencyName;
        private Long totalVotes;

        public ConstituencyVoteSummary(String constituencyName, Long totalVotes) {
            this.constituencyName = constituencyName;
            this.totalVotes = totalVotes;
        }

        // Getters
        public String getConstituencyName() {
            return constituencyName;
        }

        public Long getTotalVotes() {
            return totalVotes;
        }
    }

    public static class HourlyVoteDistribution {
        private Integer hour;
        private Long voteCount;

        public HourlyVoteDistribution(Integer hour, Long voteCount) {
            this.hour = hour;
            this.voteCount = voteCount;
        }

        // Getters
        public Integer getHour() {
            return hour;
        }

        public Long getVoteCount() {
            return voteCount;
        }
    }

    public static class SuspiciousVotingPattern {
        private String ipAddress;
        private Long voteCount;

        public SuspiciousVotingPattern(String ipAddress, Long voteCount) {
            this.ipAddress = ipAddress;
            this.voteCount = voteCount;
        }

        // Getters
        public String getIpAddress() {
            return ipAddress;
        }

        public Long getVoteCount() {
            return voteCount;
        }
    }

    /**
     * Get total vote count
     */
    public long getTotalVoteCount() {
        return voteRepository.count();
    }
}
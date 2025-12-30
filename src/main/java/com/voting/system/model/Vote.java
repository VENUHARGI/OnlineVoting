package com.voting.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Vote Entity for Cast Votes
 * 
 * Represents a vote cast by a user for a candidate in a specific constituency
 */
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "VOTING_VOTES", indexes = {
        @Index(name = "idx_voting_votes_user", columnList = "user_id"),
        @Index(name = "idx_voting_votes_constituency", columnList = "constituency_id"),
        @Index(name = "idx_voting_votes_candidate", columnList = "candidate_id"),
        @Index(name = "idx_voting_votes_session", columnList = "session_id"),
        @Index(name = "idx_voting_votes_timestamp", columnList = "voted_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_voting_votes_user_constituency", columnNames = { "user_id", "constituency_id" })
})
public class Vote {

    @Id
    @Column(name = "ID")
    private Long id;

    @NotNull(message = "User is required for vote")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @NotNull(message = "Constituency is required for vote")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONSTITUENCY_ID", nullable = false)
    private Constituency constituency;

    @NotNull(message = "Candidate is required for vote")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CANDIDATE_ID", nullable = false)
    private Candidate candidate;

    @CreationTimestamp
    @Column(name = "VOTED_AT", nullable = false, updatable = false)
    private LocalDateTime votedAt;

    @NotBlank(message = "Session ID is required")
    @Size(max = 255, message = "Session ID must not exceed 255 characters")
    @Column(name = "SESSION_ID", nullable = false)
    private String sessionId;

    @Column(name = "IP_ADDRESS")
    private String ipAddress;

    @Column(name = "USER_AGENT")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private VoteStatus status = VoteStatus.CAST;

    // Vote Status Enum
    public enum VoteStatus {
        CAST("Vote Cast Successfully"),
        PENDING("Vote Pending Verification"),
        VERIFIED("Vote Verified"),
        FLAGGED("Vote Flagged for Review"),
        CANCELLED("Vote Cancelled");

        private final String description;

        VoteStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Constructors
    public Vote() {
    }

    public Vote(User user, Constituency constituency, Candidate candidate, String sessionId) {
        this.user = user;
        this.constituency = constituency;
        this.candidate = candidate;
        this.sessionId = sessionId;
    }

    public Vote(User user, Constituency constituency, Candidate candidate, String sessionId,
            String ipAddress, String userAgent) {
        this(user, constituency, candidate, sessionId);
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Constituency getConstituency() {
        return constituency;
    }

    public void setConstituency(Constituency constituency) {
        this.constituency = constituency;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public LocalDateTime getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(LocalDateTime votedAt) {
        this.votedAt = votedAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public VoteStatus getStatus() {
        return status;
    }

    public void setStatus(VoteStatus status) {
        this.status = status;
    }

    // Convenience methods
    public String getUserFullName() {
        return user != null ? user.getFullName() : null;
    }

    public String getConstituencyName() {
        return constituency != null ? constituency.getName() : null;
    }

    public String getCandidateFullInfo() {
        if (candidate != null) {
            return candidate.getName() + " (" + candidate.getParty().getName() + " - "
                    + candidate.getParty().getSymbol() + ")";
        }
        return null;
    }

    public boolean isValidVote() {
        return user != null && constituency != null && candidate != null &&
                user.getIsVerified() && user.getIsActive() &&
                constituency.getIsActive() && candidate.getIsActive() &&
                candidate.getParty().getIsActive();
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Vote vote = (Vote) obj;
        return Objects.equals(id, vote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", user=" + (user != null ? user.getEmail() : null) +
                ", constituency=" + (constituency != null ? constituency.getName() : null) +
                ", candidate=" + (candidate != null ? candidate.getName() : null) +
                ", party=" + (candidate != null && candidate.getParty() != null ? candidate.getParty().getName() : null)
                +
                ", votedAt=" + votedAt +
                '}';
    }
}
package com.voting.system.model;

import java.time.LocalDateTime;

/**
 * Model representing a user's voting history entry
 * 
 * Contains anonymized voting information for audit and user reference
 * Does not expose which specific party was voted for to maintain vote secrecy
 */
public class VotingHistory {
    
    private Long id;
    private String constituencyName;
    private String state;
    private LocalDateTime votedAt;
    private String sessionId;
    private String status;
    private String transactionId;
    
    // Default constructor
    public VotingHistory() {}
    
    // Constructor with essential fields
    public VotingHistory(Long id, String constituencyName, String state, 
                        LocalDateTime votedAt, String status, String transactionId) {
        this.id = id;
        this.constituencyName = constituencyName;
        this.state = state;
        this.votedAt = votedAt;
        this.status = status;
        this.transactionId = transactionId;
    }
    
    // Constructor with all fields
    public VotingHistory(Long id, String constituencyName, String state, 
                        LocalDateTime votedAt, String sessionId, String status, String transactionId) {
        this(id, constituencyName, state, votedAt, status, transactionId);
        this.sessionId = sessionId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getConstituencyName() {
        return constituencyName;
    }
    
    public void setConstituencyName(String constituencyName) {
        this.constituencyName = constituencyName;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    @Override
    public String toString() {
        return "VotingHistory{" +
                "id=" + id +
                ", constituencyName='" + constituencyName + '\'' +
                ", state='" + state + '\'' +
                ", votedAt=" + votedAt +
                ", sessionId='" + sessionId + '\'' +
                ", status='" + status + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}
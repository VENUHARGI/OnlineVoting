package com.voting.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Party Entity for Political Parties
 * 
 * Represents political parties participating in elections within constituencies
 */
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "VOTING_PARTIES", indexes = {
        @Index(name = "idx_voting_parties_name", columnList = "name"),
        @Index(name = "idx_voting_parties_active", columnList = "is_active")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_voting_parties_name", columnNames = { "name" })
})
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "party_seq")
    @SequenceGenerator(name = "party_seq", sequenceName = "SEQ_VOTING_PARTIES", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "Party name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    @Column(name = "NAME", nullable = false)
    private String name;

    @NotBlank(message = "Party symbol is required")
    @Size(max = 100, message = "Symbol must not exceed 100 characters")
    @Column(name = "SYMBOL", nullable = false)
    private String symbol;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "DESCRIPTION")
    private String description;

    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    @Column(name = "LOGO_URL")
    private String logoUrl;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Party() {
    }

    public Party(String name, String symbol, String description) {
        this.name = name;
        this.symbol = symbol;
        this.description = description;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Convenience methods
    public String getFullPartyInfo() {
        return name + " (" + symbol + ")";
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Party party = (Party) obj;
        return Objects.equals(id, party.id) &&
                Objects.equals(name, party.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Party{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
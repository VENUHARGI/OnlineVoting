package com.voting.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Candidate Entity for Election Candidates
 * 
 * Represents individual candidates representing parties in specific
 * constituencies
 */
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "VOTING_CANDIDATES", indexes = {
        @Index(name = "idx_voting_candidates_name", columnList = "name"),
        @Index(name = "idx_voting_candidates_party", columnList = "party_id"),
        @Index(name = "idx_voting_candidates_constituency", columnList = "constituency_id"),
        @Index(name = "idx_voting_candidates_active", columnList = "is_active")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_voting_candidates_party_constituency", columnNames = { "party_id",
                "constituency_id" })
})
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "candidate_seq")
    @SequenceGenerator(name = "candidate_seq", sequenceName = "SEQ_VOTING_CANDIDATES", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "Candidate name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    @Column(name = "NAME", nullable = false)
    private String name;

    @Size(max = 50, message = "Age must not exceed 50 characters")
    @Column(name = "AGE")
    private Integer age;

    @Size(max = 500, message = "Qualification must not exceed 500 characters")
    @Column(name = "QUALIFICATION")
    private String qualification;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    @Column(name = "BIO")
    private String bio;

    @Size(max = 255, message = "Photo URL must not exceed 255 characters")
    @Column(name = "PHOTO_URL")
    private String photoUrl;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    // Many-to-One relationship with party
    @NotNull(message = "Party is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTY_ID", nullable = false)
    private Party party;

    // Many-to-One relationship with constituency
    @NotNull(message = "Constituency is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONSTITUENCY_ID", nullable = false)
    private Constituency constituency;

    // Constructors
    public Candidate() {
    }

    public Candidate(String name, Party party, Constituency constituency) {
        this.name = name;
        this.party = party;
        this.constituency = constituency;
        this.isActive = true;
    }

    public Candidate(String name, Integer age, String qualification, Party party, Constituency constituency) {
        this.name = name;
        this.age = age;
        this.qualification = qualification;
        this.party = party;
        this.constituency = constituency;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Constituency getConstituency() {
        return constituency;
    }

    public void setConstituency(Constituency constituency) {
        this.constituency = constituency;
    }

    // Convenience methods
    public String getFullCandidateInfo() {
        return name + " (" + (party != null ? party.getName() : "Unknown Party") + ")";
    }

    public String getPartyName() {
        return party != null ? party.getName() : null;
    }

    public String getPartySymbol() {
        return party != null ? party.getSymbol() : null;
    }

    public Long getPartyId() {
        return party != null ? party.getId() : null;
    }

    public String getPartyColorCode() {
        // Default color since Party entity doesn't have colorCode field
        return "#007bff";
    }

    public String getConstituencyName() {
        return constituency != null ? constituency.getName() : null;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Candidate candidate = (Candidate) obj;
        return Objects.equals(id, candidate.id) &&
                Objects.equals(name, candidate.name) &&
                Objects.equals(party, candidate.party) &&
                Objects.equals(constituency, candidate.constituency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, party, constituency);
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", party=" + (party != null ? party.getName() : null) +
                ", constituency=" + (constituency != null ? constituency.getName() : null) +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
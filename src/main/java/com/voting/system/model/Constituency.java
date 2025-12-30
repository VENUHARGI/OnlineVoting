package com.voting.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Constituency Entity for Electoral Districts
 * 
 * Represents voting constituencies/electoral districts with candidates
 */
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "VOTING_CONSTITUENCIES", indexes = {
        @Index(name = "idx_voting_constituencies_name", columnList = "name"),
        @Index(name = "idx_voting_constituencies_state", columnList = "state"),
        @Index(name = "idx_voting_constituencies_active", columnList = "is_active")
})
public class Constituency {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "constituency_seq")
    @SequenceGenerator(name = "constituency_seq", sequenceName = "SEQ_VOTING_CONSTITUENCIES", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "Constituency name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    @Column(name = "STATE", nullable = false)
    private String state;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Constituency() {
    }

    public Constituency(String name, String state, String description) {
        this.name = name;
        this.state = state;
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

    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Constituency that = (Constituency) obj;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Constituency{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
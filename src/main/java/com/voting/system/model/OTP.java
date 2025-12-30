package com.voting.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * OTP Entity for Email Verification and Authentication
 * 
 * Stores temporary OTP codes for user verification processes
 */
@Entity
@Table(name = "VOTING_OTP_VERIFICATION", indexes = {
        @Index(name = "idx_voting_otp_email", columnList = "email"),
        @Index(name = "idx_voting_otp_code", columnList = "otp_code"),
        @Index(name = "idx_voting_otp_expiry", columnList = "expiry_time")
})
public class OTP {

    @Id
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "Email is required for OTP")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "EMAIL", nullable = false)
    private String email;

    @NotBlank(message = "OTP code is required")
    @Size(min = 6, max = 6, message = "OTP code must be exactly 6 characters")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP code must contain only digits")
    @Column(name = "OTP_CODE", nullable = false)
    private String otpCode;

    @NotNull(message = "Expiry time is required")
    @Future(message = "Expiry time must be in the future")
    @Column(name = "EXPIRY_TIME", nullable = false)
    private LocalDateTime expiryTime;

    @NotNull(message = "OTP purpose is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "PURPOSE", nullable = false)
    private OTPPurpose purpose;

    @Column(name = "ATTEMPTS", nullable = false)
    private Integer attempts = 0;

    @Column(name = "IS_USED", nullable = false)
    private Boolean isUsed = false;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "USED_AT")
    private LocalDateTime usedAt;

    // OTP Purpose Enum
    public enum OTPPurpose {
        REGISTRATION_VERIFICATION("Account Registration Verification"),
        LOGIN_VERIFICATION("Login Verification"),
        VOTING_VERIFICATION("Voting Verification"),
        PASSWORD_RESET("Password Reset");

        private final String description;

        OTPPurpose(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Constructors
    public OTP() {
    }

    public OTP(String email, String otpCode, LocalDateTime expiryTime, OTPPurpose purpose) {
        this.email = email;
        this.otpCode = otpCode;
        this.expiryTime = expiryTime;
        this.purpose = purpose;
        this.attempts = 0;
        this.isUsed = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public OTPPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(OTPPurpose purpose) {
        this.purpose = purpose;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    // Convenience methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public boolean isValid() {
        return !isUsed && !isExpired();
    }

    public void incrementAttempts() {
        this.attempts = (this.attempts == null ? 0 : this.attempts) + 1;
    }

    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        OTP otp = (OTP) obj;
        return Objects.equals(id, otp.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OTP{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", purpose=" + purpose +
                ", isUsed=" + isUsed +
                ", expiryTime=" + expiryTime +
                ", createdAt=" + createdAt +
                '}';
    }
}
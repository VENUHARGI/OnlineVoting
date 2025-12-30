package com.voting.system.repository;

import com.voting.system.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OTP entity
 * 
 * Provides custom queries for OTP management, validation, and cleanup
 */
@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {

        /**
         * Get next available ID using Oracle sequence (thread-safe)
         */
        @Query(value = "SELECT SEQ_VOTING_OTP_VERIFICATION.NEXTVAL FROM DUAL", nativeQuery = true)
        Long getNextId();

        /**
         * Find valid (unused and not expired) OTP by email and code
         */
        @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.otpCode = :otpCode AND o.isUsed = false AND o.expiryTime > :currentTime")
        Optional<OTP> findValidOTP(@Param("email") String email,
                        @Param("otpCode") String otpCode,
                        @Param("currentTime") LocalDateTime currentTime);

        /**
         * Find valid OTP by email, code and purpose
         */
        @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.otpCode = :otpCode AND o.purpose = :purpose AND o.isUsed = false AND o.expiryTime > :currentTime")
        Optional<OTP> findValidOTPByPurpose(@Param("email") String email,
                        @Param("otpCode") String otpCode,
                        @Param("purpose") OTP.OTPPurpose purpose,
                        @Param("currentTime") LocalDateTime currentTime);

        /**
         * Find latest valid OTP for email and purpose
         */
        @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.purpose = :purpose AND o.isUsed = false AND o.expiryTime > :currentTime ORDER BY o.createdAt DESC")
        Optional<OTP> findLatestValidOTPByEmailAndPurpose(@Param("email") String email,
                        @Param("purpose") OTP.OTPPurpose purpose,
                        @Param("currentTime") LocalDateTime currentTime);

        /**
         * Find latest OTP for email and purpose (regardless of expiry or usage)
         */
        @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.purpose = :purpose ORDER BY o.createdAt DESC")
        Optional<OTP> findLatestOTPByEmailAndPurpose(@Param("email") String email,
                        @Param("purpose") OTP.OTPPurpose purpose);

        /**
         * Find all OTPs by email
         */
        List<OTP> findByEmailOrderByCreatedAtDesc(String email);

        /**
         * Find all valid OTPs by email
         */
        @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.isUsed = false AND o.expiryTime > :currentTime ORDER BY o.createdAt DESC")
        List<OTP> findValidOTPsByEmail(@Param("email") String email,
                        @Param("currentTime") LocalDateTime currentTime);

        /**
         * Find OTPs by purpose
         */
        List<OTP> findByPurpose(OTP.OTPPurpose purpose);

        /**
         * Find expired OTPs
         */
        @Query("SELECT o FROM OTP o WHERE o.expiryTime < :currentTime")
        List<OTP> findExpiredOTPs(@Param("currentTime") LocalDateTime currentTime);

        /**
         * Find used OTPs
         */
        List<OTP> findByIsUsedTrue();

        /**
         * Count valid OTPs by email in time range (for rate limiting)
         */
        @Query("SELECT COUNT(o) FROM OTP o WHERE o.email = :email AND o.createdAt > :timeThreshold")
        long countOTPsByEmailSince(@Param("email") String email,
                        @Param("timeThreshold") LocalDateTime timeThreshold);

        /**
         * Count OTPs by email and purpose in time range
         */
        @Query("SELECT COUNT(o) FROM OTP o WHERE o.email = :email AND o.purpose = :purpose AND o.createdAt > :timeThreshold")
        long countOTPsByEmailAndPurposeSince(@Param("email") String email,
                        @Param("purpose") OTP.OTPPurpose purpose,
                        @Param("timeThreshold") LocalDateTime timeThreshold);

        /**
         * Mark OTP as used
         */
        @Modifying
        @Query("UPDATE OTP o SET o.isUsed = true, o.usedAt = :usedAt WHERE o.id = :otpId")
        void markOTPAsUsed(@Param("otpId") Long otpId, @Param("usedAt") LocalDateTime usedAt);

        /**
         * Increment OTP attempts
         */
        @Modifying
        @Query("UPDATE OTP o SET o.attempts = o.attempts + 1 WHERE o.id = :otpId")
        void incrementOTPAttempts(@Param("otpId") Long otpId);

        /**
         * Delete expired OTPs (cleanup)
         */
        @Modifying
        @Query("DELETE FROM OTP o WHERE o.expiryTime < :currentTime")
        int deleteExpiredOTPs(@Param("currentTime") LocalDateTime currentTime);

        /**
         * Delete used OTPs older than specified time
         */
        @Modifying
        @Query("DELETE FROM OTP o WHERE o.isUsed = true AND o.usedAt < :cutoffTime")
        int deleteUsedOTPsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

        /**
         * Delete all OTPs by email (for account deletion)
         */
        @Modifying
        void deleteByEmail(String email);

        /**
         * Get OTP statistics
         */
        @Query("SELECT COUNT(o) FROM OTP o WHERE o.isUsed = false AND o.expiryTime > :currentTime")
        long countActiveOTPs(@Param("currentTime") LocalDateTime currentTime);

        @Query("SELECT COUNT(o) FROM OTP o WHERE o.expiryTime < :currentTime")
        long countExpiredOTPs(@Param("currentTime") LocalDateTime currentTime);

        @Query("SELECT COUNT(o) FROM OTP o WHERE o.isUsed = true")
        long countUsedOTPs();

        /**
         * Find OTPs created today
         */
        @Query("SELECT o FROM OTP o WHERE FUNCTION('DATE', o.createdAt) = CURRENT_DATE ORDER BY o.createdAt DESC")
        List<OTP> findOTPsCreatedToday();

        /**
         * Find recent failed OTP attempts by email
         */
        @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.attempts > 0 AND o.createdAt > :timeThreshold ORDER BY o.createdAt DESC")
        List<OTP> findRecentFailedAttempts(@Param("email") String email,
                        @Param("timeThreshold") LocalDateTime timeThreshold);
}
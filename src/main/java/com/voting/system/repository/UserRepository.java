package com.voting.system.repository;

import com.voting.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * 
 * Provides custom queries for user management, authentication, and security
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Get next available ID using Oracle sequence (thread-safe)
     */
    @Query(value = "SELECT SEQ_VOTING_USERS.NEXTVAL FROM DUAL", nativeQuery = true)
    Long getNextId();

    /**
     * Find user by email address
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email address (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);

    /**
     * Find user by phone number
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if email exists (case insensitive)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    /**
     * Check if phone number exists
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Find verified users
     */
    List<User> findByIsVerifiedTrueAndIsActiveTrue();

    /**
     * Find unverified users created before specified time
     */
    @Query("SELECT u FROM User u WHERE u.isVerified = false AND u.createdAt < :cutoffTime")
    List<User> findUnverifiedUsersOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Find locked accounts
     */
    @Query("SELECT u FROM User u WHERE u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil > :currentTime")
    List<User> findLockedAccounts(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find users with high failed login attempts
     */
    @Query("SELECT u FROM User u WHERE u.failedLoginAttempts >= :threshold")
    List<User> findUsersWithFailedAttempts(@Param("threshold") Integer threshold);

    /**
     * Update failed login attempts
     */
    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = :attempts WHERE u.id = :userId")
    void updateFailedLoginAttempts(@Param("userId") Long userId, @Param("attempts") Integer attempts);

    /**
     * Lock user account
     */
    @Modifying
    @Query("UPDATE User u SET u.accountLockedUntil = :lockUntil, u.failedLoginAttempts = :attempts WHERE u.id = :userId")
    void lockUserAccount(@Param("userId") Long userId,
            @Param("lockUntil") LocalDateTime lockUntil,
            @Param("attempts") Integer attempts);

    /**
     * Unlock user account
     */
    @Modifying
    @Query("UPDATE User u SET u.accountLockedUntil = NULL, u.failedLoginAttempts = 0 WHERE u.id = :userId")
    void unlockUserAccount(@Param("userId") Long userId);

    /**
     * Verify user account
     */
    @Modifying
    @Query("UPDATE User u SET u.isVerified = true WHERE u.id = :userId")
    void verifyUserAccount(@Param("userId") Long userId);

    /**
     * Activate/Deactivate user account
     */
    @Modifying
    @Query("UPDATE User u SET u.isActive = :isActive WHERE u.id = :userId")
    void updateUserActiveStatus(@Param("userId") Long userId, @Param("isActive") Boolean isActive);

    /**
     * Update user password
     */
    @Modifying
    @Query("UPDATE User u SET u.passwordHash = :passwordHash WHERE u.id = :userId")
    void updateUserPassword(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);

    /**
     * Get user statistics
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isVerified = true AND u.isActive = true")
    long countActiveVerifiedUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.isVerified = false")
    long countUnverifiedUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil > :currentTime")
    long countLockedUsers(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find users created today
     */
    @Query("SELECT u FROM User u WHERE FUNCTION('DATE', u.createdAt) = CURRENT_DATE")
    List<User> findUsersCreatedToday();

    /**
     * Find users by name pattern
     */
    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContaining(@Param("name") String name);
}
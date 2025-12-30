package com.voting.system.service;

import com.voting.system.model.User;
import com.voting.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for User management
 * 
 * Handles user registration, authentication, profile management, and security
 * features
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${voting.security.account-lockout-attempts:5}")
    private int maxFailedAttempts;

    @Value("${voting.security.account-lockout-duration-minutes:30}")
    private int lockoutDurationMinutes;

    /**
     * Register a new user
     */
    public User registerUser(String email, String password, String firstName, String lastName, String phoneNumber) {
        logger.info("=== USER REGISTRATION STARTED ===");
        logger.info("Email: {}", email);
        logger.info("Phone: {}", phoneNumber);

        try {
            // Check if user already exists
            logger.info("Checking if user exists with email: {}", email);
            if (userRepository.existsByEmailIgnoreCase(email)) {
                logger.warn("User registration failed: User with email {} already exists", email);
                throw new RuntimeException("User with this email already exists");
            }

            if (phoneNumber != null && userRepository.existsByPhoneNumber(phoneNumber)) {
                logger.warn("User registration failed: User with phone {} already exists", phoneNumber);
                throw new RuntimeException("User with this phone number already exists");
            }

            logger.info("User validation passed, proceeding with ID generation...");

            // Get next ID manually
            Long nextId = userRepository.getNextId();
            logger.info("Generated next user ID: {}", nextId);

            // Create new user
            logger.info("Creating new user object...");
            User user = new User();
            user.setId(nextId); // Set ID manually for schemas without auto-increment
            user.setEmail(email.toLowerCase());
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            user.setIsVerified(false);
            user.setIsActive(true);
            user.setFailedLoginAttempts(0);

            logger.info("Saving user to database...");
            User savedUser = userRepository.save(user);
            logger.info("User saved successfully with ID: {}", savedUser.getId());
            logger.info("=== USER REGISTRATION COMPLETED ===");

            return savedUser;

        } catch (Exception e) {
            logger.error("=== USER REGISTRATION FAILED ===");
            logger.error("Error during user registration: {}", e.getMessage(), e);
            logger.error("=== END USER REGISTRATION ERROR ===");
            throw e;
        }
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id != null ? id : 0L);
    }

    /**
     * Authenticate user credentials
     */
    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = findByEmail(email);

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        // Check if account is locked
        if (user.isAccountLocked()) {
            throw new RuntimeException("Account is locked. Please try again later.");
        }

        // Check if account is active
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated. Please contact support.");
        }

        // Verify password
        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            // Reset failed login attempts on successful login
            user.resetFailedLoginAttempts();
            userRepository.save(user);
            return Optional.of(user);
        } else {
            // Increment failed login attempts
            handleFailedLogin(user);
            return Optional.empty();
        }
    }

    /**
     * Handle failed login attempt
     */
    private void handleFailedLogin(User user) {
        user.incrementFailedLoginAttempts();

        if (user.getFailedLoginAttempts() >= maxFailedAttempts) {
            // Lock the account
            user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(lockoutDurationMinutes));
        }

        userRepository.save(user);
    }

    /**
     * Verify user account
     */
    public void verifyUserAccount(Long userId) {
        userRepository.verifyUserAccount(userId);
    }

    /**
     * Update user profile
     */
    public User updateUserProfile(Long userId, String firstName, String lastName, String phoneNumber) {
        User user = userRepository.findById(userId != null ? userId : 0L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if phone number is already taken by another user
        if (phoneNumber != null && !phoneNumber.equals(user.getPhoneNumber())) {
            Optional<User> existingUser = userRepository.findByPhoneNumber(phoneNumber);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new RuntimeException("Phone number is already in use");
            }
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);

        return userRepository.save(user);
    }

    /**
     * Change user password
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId != null ? userId : 0L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.resetFailedLoginAttempts(); // Reset failed attempts on password change

        userRepository.save(user);
    }

    /**
     * Reset password (without current password validation)
     */
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.resetFailedLoginAttempts();

        userRepository.save(user);
    }

    /**
     * Unlock user account
     */
    public void unlockUserAccount(Long userId) {
        userRepository.unlockUserAccount(userId);
    }

    /**
     * Activate/Deactivate user account
     */
    public void updateUserActiveStatus(Long userId, Boolean isActive) {
        userRepository.updateUserActiveStatus(userId, isActive);
    }

    /**
     * Get all verified and active users
     */
    public List<User> getVerifiedActiveUsers() {
        return userRepository.findByIsVerifiedTrueAndIsActiveTrue();
    }

    /**
     * Get unverified users older than specified hours
     */
    public List<User> getUnverifiedUsersOlderThan(int hours) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);
        return userRepository.findUnverifiedUsersOlderThan(cutoffTime);
    }

    /**
     * Get locked accounts
     */
    public List<User> getLockedAccounts() {
        return userRepository.findLockedAccounts(LocalDateTime.now());
    }

    /**
     * Get user statistics
     */
    public UserStats getUserStatistics() {
        UserStats stats = new UserStats();
        stats.setTotalUsers(userRepository.count());
        stats.setActiveVerifiedUsers(userRepository.countActiveVerifiedUsers());
        stats.setUnverifiedUsers(userRepository.countUnverifiedUsers());
        stats.setLockedUsers(userRepository.countLockedUsers(LocalDateTime.now()));
        stats.setUsersCreatedToday(userRepository.findUsersCreatedToday().size());
        return stats;
    }

    /**
     * Search users by name
     */
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContaining(name);
    }

    /**
     * Check if user exists
     */
    public boolean userExists(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    /**
     * Delete user account (soft delete by deactivating)
     */
    public void deleteUser(Long userId) {
        updateUserActiveStatus(userId, false);
    }

    /**
     * User Statistics DTO
     */
    public static class UserStats {
        private long totalUsers;
        private long activeVerifiedUsers;
        private long unverifiedUsers;
        private long lockedUsers;
        private long usersCreatedToday;

        // Getters and setters
        public long getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(long totalUsers) {
            this.totalUsers = totalUsers;
        }

        public long getActiveVerifiedUsers() {
            return activeVerifiedUsers;
        }

        public void setActiveVerifiedUsers(long activeVerifiedUsers) {
            this.activeVerifiedUsers = activeVerifiedUsers;
        }

        public long getUnverifiedUsers() {
            return unverifiedUsers;
        }

        public void setUnverifiedUsers(long unverifiedUsers) {
            this.unverifiedUsers = unverifiedUsers;
        }

        public long getLockedUsers() {
            return lockedUsers;
        }

        public void setLockedUsers(long lockedUsers) {
            this.lockedUsers = lockedUsers;
        }

        public long getUsersCreatedToday() {
            return usersCreatedToday;
        }

        public void setUsersCreatedToday(long usersCreatedToday) {
            this.usersCreatedToday = usersCreatedToday;
        }
    }
}
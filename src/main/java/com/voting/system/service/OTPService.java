package com.voting.system.service;

import com.voting.system.model.OTP;
import com.voting.system.repository.OTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for OTP management
 * 
 * Handles OTP generation, validation, cleanup, and rate limiting
 */
@Service
@Transactional
public class OTPService {

    private static final Logger logger = LoggerFactory.getLogger(OTPService.class);

    @Autowired
    private OTPRepository otpRepository;

    @Value("${otp.expiration-minutes:10}")
    private int otpExpirationMinutes;

    @Value("${otp.max-attempts:3}")
    private int maxOtpAttempts;

    @Value("${otp.resend-cooldown-minutes:2}")
    private int resendCooldownMinutes;

    @Value("${rate-limit.otp.requests-per-hour:3}")
    private int maxOtpRequestsPerHour;

    @Value("${rate-limit.otp.requests-per-day:10}")
    private int maxOtpRequestsPerDay;

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generate and send OTP
     */
    public String generateAndSendOTP(String email, OTP.OTPPurpose purpose) {
        // Check rate limiting
        // TODO: Re-enable for production
        // checkRateLimit(email);

        // Invalidate any existing valid OTPs for this email and purpose
        invalidateExistingOTPs(email, purpose);

        // Generate new OTP
        String otpCode = generateOTPCode();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(otpExpirationMinutes);

        OTP otp = new OTP(email, otpCode, expiryTime, purpose);
        otp.setId(otpRepository.getNextId()); // Set ID manually for schemas without auto-increment
        otpRepository.save(otp);

        logger.info("=== OTP GENERATED ===");
        logger.info("📧 Email: {}", email);
        logger.info("🎯 Purpose: {}", purpose);
        logger.info("🔑 Code: {}", otpCode);
        logger.info("⏰ Expires: {} minutes", otpExpirationMinutes);
        logger.info("===================");

        // Additional console output for guaranteed visibility
        System.out.println("\n=== OTP GENERATION DEBUG ===");
        System.out.println("📧 Email: " + email);
        System.out.println("🎯 Purpose: " + purpose);
        System.out.println("🔑 OTP Code: " + otpCode);
        System.out.println("⏰ Expires in: " + otpExpirationMinutes + " minutes");
        System.out.println("============================\n");

        // Email sending disabled - OTP displayed in logs for testing
        System.out.println("\n📱 TEST MODE - OTP Code for " + email + " -> " + otpCode);
        System.out.println("🔑 YOUR OTP CODE: " + otpCode);
        System.out.println("💡 Use this code to continue authentication");
        System.out.println("==========================================\n");
        logger.info("✅ OTP generated successfully for: {}", email);

        // Return the generated OTP code
        return otpCode;
    }

    /**
     * OTP validation result enumeration
     */
    public enum OTPValidationResult {
        VALID,
        INVALID_CODE,
        EXPIRED,
        ALREADY_USED,
        MAX_ATTEMPTS_EXCEEDED,
        NOT_FOUND;
    }

    /**
     * Validate OTP with detailed result
     */
    public OTPValidationResult validateOTPDetailed(String email, String otpCode, OTP.OTPPurpose purpose) {
        System.out.println("\n🗄️  === DATABASE OTP VALIDATION STARTED ===");
        System.out.println("📧 Email: " + email);
        System.out.println("🔑 Input Code: " + otpCode);
        System.out.println("🎯 Purpose: " + purpose);
        System.out.println("🕐 Current Time: " + LocalDateTime.now());
        System.out.println("🔍 About to query VOTING_OTP_VERIFICATION table...");

        logger.info("=== DETAILED OTP VALIDATION DEBUG ===");
        logger.info("Email: {}", email);
        logger.info("Input Code: {}", otpCode);
        logger.info("Purpose: {}", purpose);
        logger.info("Current Time: {}", LocalDateTime.now());

        // Check ALL OTPs for this email first
        System.out.println("\n🔍 DATABASE QUERY 1: Finding ALL OTPs for email: " + email);
        List<OTP> allOtps = otpRepository.findByEmailOrderByCreatedAtDesc(email);
        System.out.println("✅ DATABASE QUERY 1 COMPLETED: Found " + allOtps.size() + " OTPs");

        logger.info("Total OTPs found for email {}: {}", email, allOtps.size());
        for (int i = 0; i < allOtps.size(); i++) {
            OTP otp = allOtps.get(i);
            System.out.println("📝 OTP " + (i + 1) + ": Code=" + otp.getOtpCode() +
                    ", Purpose=" + otp.getPurpose() +
                    ", Expired=" + otp.getExpiryTime().isBefore(LocalDateTime.now()) +
                    ", Used=" + otp.getIsUsed() +
                    ", Expiry=" + otp.getExpiryTime());

            logger.info("OTP {}: Code={}, Purpose={}, Expired={}, Used={}, Expiry={}",
                    i + 1, otp.getOtpCode(), otp.getPurpose(),
                    otp.getExpiryTime().isBefore(LocalDateTime.now()),
                    otp.getIsUsed(), otp.getExpiryTime());
        }

        // First, check if there's any OTP for this email and purpose
        System.out.println("\n🔍 DATABASE QUERY 2: Finding VALID OTP for email=" + email + ", purpose=" + purpose);
        Optional<OTP> latestOtp = otpRepository.findLatestValidOTPByEmailAndPurpose(email, purpose,
                LocalDateTime.now());
        System.out.println("✅ DATABASE QUERY 2 COMPLETED: Valid OTP found = " + latestOtp.isPresent());

        logger.info("Latest valid OTP found: {}", latestOtp.isPresent());

        if (latestOtp.isEmpty()) {
            // Check if there's any OTP (even expired) to see if code matches
            System.out.println("\n🔍 DATABASE QUERY 3: Finding ANY OTP (including expired) for email=" + email
                    + ", purpose=" + purpose);
            Optional<OTP> anyOtp = otpRepository.findLatestOTPByEmailAndPurpose(email, purpose);
            System.out.println("✅ DATABASE QUERY 3 COMPLETED: Any OTP found = " + anyOtp.isPresent());
            logger.info("Any OTP (including expired) found: {}", anyOtp.isPresent());
            if (anyOtp.isPresent()) {
                OTP otp = anyOtp.get();
                logger.info("Found OTP - Code: {}, Expired: {}, Used: {}",
                        otp.getOtpCode(), otp.getExpiryTime().isBefore(LocalDateTime.now()), otp.getIsUsed());
                if (otp.getOtpCode().equals(otpCode)) {
                    if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
                        logger.info("OTP EXPIRED");
                        System.out.println("❌ OTP VALIDATION FAILED: OTP is EXPIRED");
                        System.out.println("🗄️  === DATABASE OTP VALIDATION COMPLETED ===\n");
                        return OTPValidationResult.EXPIRED;
                    }
                    if (otp.getIsUsed()) {
                        logger.info("OTP ALREADY USED");
                        System.out.println("❌ OTP VALIDATION FAILED: OTP is ALREADY USED");
                        System.out.println("🗄️  === DATABASE OTP VALIDATION COMPLETED ===\n");
                        return OTPValidationResult.ALREADY_USED;
                    }
                }
            }
            logger.info("NO MATCHING OTP FOUND");
            System.out.println("❌ OTP VALIDATION FAILED: NO MATCHING OTP FOUND in database");
            System.out.println("🗄️  === DATABASE OTP VALIDATION COMPLETED ===\n");
            return OTPValidationResult.NOT_FOUND;
        }

        OTP otp = latestOtp.get();

        // Check if OTP has exceeded max attempts
        if (otp.getAttempts() >= maxOtpAttempts) {
            System.out.println("❌ OTP VALIDATION FAILED: MAX ATTEMPTS EXCEEDED");
            System.out.println("🗄️  === DATABASE OTP VALIDATION COMPLETED ===\n");
            return OTPValidationResult.MAX_ATTEMPTS_EXCEEDED;
        }

        // Check if the code matches
        if (!otp.getOtpCode().equals(otpCode)) {
            System.out.println(
                    "❌ OTP VALIDATION FAILED: INVALID CODE (Expected: " + otp.getOtpCode() + ", Got: " + otpCode + ")");
            otp.incrementAttempts();
            otpRepository.save(otp);

            if (otp.getAttempts() >= maxOtpAttempts) {
                otp.setIsUsed(true); // Mark as used to prevent further attempts
                otpRepository.save(otp);
            }
            System.out.println("🗄️  === DATABASE OTP VALIDATION COMPLETED ===\n");
            return OTPValidationResult.INVALID_CODE;
        }

        // Check if expired
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            System.out.println("❌ OTP VALIDATION FAILED: OTP is EXPIRED (Expiry: " + otp.getExpiryTime() + ", Now: "
                    + LocalDateTime.now() + ")");
            System.out.println("🗄️  === DATABASE OTP VALIDATION COMPLETED ===\n");
            return OTPValidationResult.EXPIRED;
        }

        // Check if already used
        if (otp.getIsUsed()) {
            System.out.println("❌ OTP VALIDATION FAILED: OTP is ALREADY USED");
            System.out.println("🗄️  === DATABASE OTP VALIDATION COMPLETED ===\n");
            return OTPValidationResult.ALREADY_USED;
        }

        // Valid OTP - mark as used
        otp.markAsUsed();
        otpRepository.save(otp);

        System.out.println("✅ OTP VALIDATION SUCCESS: OTP is VALID and marked as USED");
        System.out.println("🗄️  === DATABASE OTP VALIDATION COMPLETED ===\n");

        return OTPValidationResult.VALID;
    }

    /**
     * Validate OTP (legacy method for backward compatibility)
     */
    public boolean validateOTP(String email, String otpCode, OTP.OTPPurpose purpose) {
        return validateOTPDetailed(email, otpCode, purpose) == OTPValidationResult.VALID;
    }

    /**
     * Check if user can request new OTP (rate limiting)
     */
    public boolean canRequestOTP(String email) {
        try {
            // TODO: Re-enable for production
            // checkRateLimit(email);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if user can resend OTP (cooldown period)
     */
    public boolean canResendOTP(String email, OTP.OTPPurpose purpose) {
        LocalDateTime cooldownThreshold = LocalDateTime.now().minusMinutes(resendCooldownMinutes);
        Optional<OTP> latestOtp = otpRepository.findLatestValidOTPByEmailAndPurpose(email, purpose,
                LocalDateTime.now());

        return latestOtp.isEmpty() || latestOtp.get().getCreatedAt().isBefore(cooldownThreshold);
    }

    /**
     * Get remaining time for OTP expiry
     */
    public Optional<Integer> getRemainingOTPTime(String email, OTP.OTPPurpose purpose) {
        Optional<OTP> otpOpt = otpRepository.findLatestValidOTPByEmailAndPurpose(email, purpose, LocalDateTime.now());

        if (otpOpt.isPresent()) {
            OTP otp = otpOpt.get();
            LocalDateTime now = LocalDateTime.now();
            if (otp.getExpiryTime().isAfter(now)) {
                return Optional.of((int) java.time.Duration.between(now, otp.getExpiryTime()).toMinutes());
            }
        }

        return Optional.empty();
    }

    /**
     * Generate 6-digit OTP code
     */
    private String generateOTPCode() {
        return String.format("%06d", secureRandom.nextInt(1000000));
    }

    /**
     * Invalidate existing valid OTPs for email and purpose
     */
    private void invalidateExistingOTPs(String email, OTP.OTPPurpose purpose) {
        List<OTP> validOtps = otpRepository.findValidOTPsByEmail(email, LocalDateTime.now());
        for (OTP otp : validOtps) {
            if (otp.getPurpose() == purpose) {
                otp.setIsUsed(true);
                otpRepository.save(otp);
            }
        }
    }

    /**
     * Get OTP statistics
     */
    public OTPStats getOTPStatistics() {
        OTPStats stats = new OTPStats();
        LocalDateTime now = LocalDateTime.now();

        stats.setTotalOTPs(otpRepository.count());
        stats.setActiveOTPs(otpRepository.countActiveOTPs(now));
        stats.setExpiredOTPs(otpRepository.countExpiredOTPs(now));
        stats.setUsedOTPs(otpRepository.countUsedOTPs());
        stats.setOtpsCreatedToday(otpRepository.findOTPsCreatedToday().size());

        return stats;
    }

    /**
     * Cleanup expired and old used OTPs (scheduled task)
     * Handles case where schema doesn't exist (offline schema management)
     */
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void cleanupExpiredOTPs() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime cutoffTime = now.minusDays(7); // Keep used OTPs for 7 days

            int expiredDeleted = otpRepository.deleteExpiredOTPs(now);
            int usedDeleted = otpRepository.deleteUsedOTPsOlderThan(cutoffTime);

            if (expiredDeleted > 0 || usedDeleted > 0) {
                System.out.println("OTP Cleanup: Deleted " + expiredDeleted + " expired OTPs and " + usedDeleted
                        + " old used OTPs");
            }
        } catch (InvalidDataAccessResourceUsageException e) {
            // Tables don't exist yet (offline schema management) - skip cleanup
            System.out.println("OTP Cleanup: Skipped - schema not yet initialized (offline management)");
        } catch (Exception e) {
            // Log other unexpected errors
            System.err.println("OTP Cleanup: Unexpected error - " + e.getMessage());
        }
    }

    /**
     * Get OTPs by email (for admin purposes)
     */
    public List<OTP> getOTPsByEmail(String email) {
        return otpRepository.findByEmailOrderByCreatedAtDesc(email);
    }

    /**
     * Delete all OTPs for email (for account deletion)
     */
    public void deleteOTPsByEmail(String email) {
        otpRepository.deleteByEmail(email);
    }

    /**
     * Get the most recent OTP for an email (for testing purposes)
     */
    public Optional<OTP> getMostRecentOTP(String email) {
        List<OTP> otps = otpRepository.findByEmailOrderByCreatedAtDesc(email);
        if (otps.isEmpty()) {
            return Optional.empty();
        }
        // Return the most recent one
        return Optional.of(otps.get(0));
    }

    /**
     * OTP Statistics DTO
     */
    public static class OTPStats {
        private long totalOTPs;
        private long activeOTPs;
        private long expiredOTPs;
        private long usedOTPs;
        private long otpsCreatedToday;

        // Getters and setters
        public long getTotalOTPs() {
            return totalOTPs;
        }

        public void setTotalOTPs(long totalOTPs) {
            this.totalOTPs = totalOTPs;
        }

        public long getActiveOTPs() {
            return activeOTPs;
        }

        public void setActiveOTPs(long activeOTPs) {
            this.activeOTPs = activeOTPs;
        }

        public long getExpiredOTPs() {
            return expiredOTPs;
        }

        public void setExpiredOTPs(long expiredOTPs) {
            this.expiredOTPs = expiredOTPs;
        }

        public long getUsedOTPs() {
            return usedOTPs;
        }

        public void setUsedOTPs(long usedOTPs) {
            this.usedOTPs = usedOTPs;
        }

        public long getOtpsCreatedToday() {
            return otpsCreatedToday;
        }

        public void setOtpsCreatedToday(long otpsCreatedToday) {
            this.otpsCreatedToday = otpsCreatedToday;
        }
    }
}
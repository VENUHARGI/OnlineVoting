package com.voting.system;

import com.voting.system.config.DotEnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot Application Class for Online Voting System
 * 
 * Features:
 * - Secure user registration with email OTP verification
 * - Two-factor authentication for voting
 * - Constituency and party-based voting system
 * - Oracle database integration
 * - Email notifications via Gmail SMTP
 * 
 * @author Voting System Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class VotingSystemApplication {

    public static void main(String[] args) {
        // Banner and startup info
        System.out.println("===============================================");
        System.out.println("🗳️  Online Voting System Starting...");
        System.out.println("===============================================");
        System.out.println("Version: 1.0.0");
        System.out.println("Security: BCrypt OTP Authentication");
        System.out.println("Database: Oracle SQL");
        System.out.println("===============================================");

        // Create Spring Application with .env file loading
        SpringApplication app = new SpringApplication(VotingSystemApplication.class);
        app.addInitializers(new DotEnvConfig());
        app.run(args);

        System.out.println("===============================================");
        System.out.println("🚀 Online Voting System Started Successfully!");
        System.out.println("🌐 Access: http://localhost:8080/voting");
        System.out.println("===============================================");
    }
}
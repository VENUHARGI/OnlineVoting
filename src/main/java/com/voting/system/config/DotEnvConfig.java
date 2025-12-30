package com.voting.system.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class to automatically load .env file
 * This will load environment variables from .env file in the project root
 */
public class DotEnvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@org.springframework.lang.NonNull ConfigurableApplicationContext applicationContext) {
        try {
            // Load .env file from project root
            Map<String, Object> envMap = loadEnvFile(".env");

            if (!envMap.isEmpty()) {
                // Add to Spring's property sources with highest priority
                ConfigurableEnvironment environment = applicationContext.getEnvironment();
                environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envMap));

                // Also set as system properties for immediate availability
                envMap.forEach((key, value) -> System.setProperty(key, value.toString()));

                System.out.println("✅ Successfully loaded .env file with " + envMap.size() + " variables");
            }

        } catch (Exception e) {
            System.err.println("⚠️  Could not load .env file: " + e.getMessage());
            // Don't fail startup if .env file is missing
        }
    }

    private Map<String, Object> loadEnvFile(String filename) {
        Map<String, Object> envMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Parse key=value pairs
                int equalIndex = line.indexOf('=');
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String value = line.substring(equalIndex + 1).trim();
                    envMap.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read .env file: " + e.getMessage());
        }

        return envMap;
    }
}
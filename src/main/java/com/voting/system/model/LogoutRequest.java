package com.voting.system.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Request model for user logout operation
 * 
 * Contains necessary information for secure logout including token validation
 */
public class LogoutRequest {
    
    @NotBlank(message = "Token is required for logout")
    private String token;
    
    private String deviceInfo;
    private String sessionId;
    
    // Default constructor
    public LogoutRequest() {}
    
    // Constructor with token
    public LogoutRequest(String token) {
        this.token = token;
    }
    
    // Constructor with all fields
    public LogoutRequest(String token, String deviceInfo, String sessionId) {
        this.token = token;
        this.deviceInfo = deviceInfo;
        this.sessionId = sessionId;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getDeviceInfo() {
        return deviceInfo;
    }
    
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    @Override
    public String toString() {
        return "LogoutRequest{" +
                "token='[PROTECTED]'" +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
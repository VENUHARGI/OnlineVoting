package com.voting.system.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Standardized API Response wrapper for all REST endpoints
 * 
 * Provides consistent response structure across the application with:
 * - Success/failure status
 * - Human-readable messages
 * - Optional data payload
 * - Error codes and metadata
 * - Timestamp for request tracking
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private String errorCode;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
    
    // Default constructor
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
        this.metadata = new HashMap<>();
    }
    
    // Constructor for basic success/failure responses
    public ApiResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }
    
    // Constructor with data
    public ApiResponse(boolean success, String message, Object data) {
        this(success, message);
        this.data = data;
    }
    
    // Constructor with error code
    public ApiResponse(boolean success, String message, String errorCode) {
        this(success, message);
        this.errorCode = errorCode;
    }
    
    // Constructor with data and error code
    public ApiResponse(boolean success, String message, Object data, String errorCode) {
        this(success, message, data);
        this.errorCode = errorCode;
    }
    
    // Static factory methods for common responses
    public static ApiResponse success(String message) {
        return new ApiResponse(true, message);
    }
    
    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }
    
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message);
    }
    
    public static ApiResponse error(String message, String errorCode) {
        return new ApiResponse(false, message, errorCode);
    }
    
    public static ApiResponse error(String message, Object data, String errorCode) {
        return new ApiResponse(false, message, data, errorCode);
    }
    
    // Builder pattern support
    public ApiResponse withMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
        return this;
    }
    
    public ApiResponse withMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", errorCode='" + errorCode + '\'' +
                ", timestamp=" + timestamp +
                ", metadata=" + metadata +
                '}';
    }
}
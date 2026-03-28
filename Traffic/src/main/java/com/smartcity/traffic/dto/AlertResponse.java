package com.smartcity.traffic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartcity.traffic.model.Alert;
import java.time.LocalDateTime;

public class AlertResponse {
    private Long id;
    private String type;
    private String severity;
    private String message;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String status;

    public AlertResponse() {
    }

    public AlertResponse(Long id, String type, String severity, String message,
                         LocalDateTime timestamp, String status) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
    }

    public static AlertResponse fromAlert(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getType(),
                alert.getSeverity(),
                alert.getMessage(),
                alert.getTimestamp(),
                alert.getStatus()
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.smartcity.traffic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class Alert {
    private Long id;
    private String type; // CONGESTION, INCIDENT, PEDESTRIAN_WARNING, etc.
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private String message;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String status; // ACTIVE, RESOLVED, DISMISSED
    private String intersectionId;
    private String cameraId;

    public Alert() {
    }

    public Alert(String type, String severity, String message, LocalDateTime timestamp,
                 String status, String intersectionId, String cameraId) {
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.intersectionId = intersectionId;
        this.cameraId = cameraId;
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

    public String getIntersectionId() {
        return intersectionId;
    }

    public void setIntersectionId(String intersectionId) {
        this.intersectionId = intersectionId;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", severity='" + severity + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", intersectionId='" + intersectionId + '\'' +
                ", cameraId='" + cameraId + '\'' +
                '}';
    }
}

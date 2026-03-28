package com.smartcity.traffic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class Recommendation {
    private Long id;
    private String intersectionId;
    private String cameraId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String recommendation;
    private String reason;
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

    public Recommendation() {
    }

    public Recommendation(String intersectionId, String cameraId, LocalDateTime timestamp,
                          String recommendation, String reason, String priority) {
        this.intersectionId = intersectionId;
        this.cameraId = cameraId;
        this.timestamp = timestamp;
        this.recommendation = recommendation;
        this.reason = reason;
        this.priority = priority;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "id=" + id +
                ", intersectionId='" + intersectionId + '\'' +
                ", cameraId='" + cameraId + '\'' +
                ", timestamp=" + timestamp +
                ", recommendation='" + recommendation + '\'' +
                ", reason='" + reason + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }
}

package com.smartcity.traffic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartcity.traffic.model.Recommendation;
import java.time.LocalDateTime;

public class RecommendationResponse {
    private Long id;
    private String intersectionId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String recommendation;
    private String reason;
    private String priority;

    public RecommendationResponse() {
    }

    public RecommendationResponse(Long id, String intersectionId, LocalDateTime timestamp,
                                  String recommendation, String reason, String priority) {
        this.id = id;
        this.intersectionId = intersectionId;
        this.timestamp = timestamp;
        this.recommendation = recommendation;
        this.reason = reason;
        this.priority = priority;
    }

    public static RecommendationResponse fromRecommendation(Recommendation rec) {
        return new RecommendationResponse(
                rec.getId(),
                rec.getIntersectionId(),
                rec.getTimestamp(),
                rec.getRecommendation(),
                rec.getReason(),
                rec.getPriority()
        );
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
}

package com.smartcity.traffic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartcity.traffic.model.TrafficAnalysis;
import java.time.LocalDateTime;

public class TrafficLatestResponse {
    private String intersectionId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String trafficState;
    private String severity;
    private String recommendation;

    public TrafficLatestResponse() {
    }

    public TrafficLatestResponse(String intersectionId, LocalDateTime timestamp,
                                 String trafficState, String severity, String recommendation) {
        this.intersectionId = intersectionId;
        this.timestamp = timestamp;
        this.trafficState = trafficState;
        this.severity = severity;
        this.recommendation = recommendation;
    }

    public static TrafficLatestResponse fromTrafficAnalysis(TrafficAnalysis analysis) {
        return new TrafficLatestResponse(
                analysis.getIntersectionId(),
                analysis.getTimestamp(),
                analysis.getTrafficState(),
                analysis.getSeverity(),
                analysis.getRecommendation()
        );
    }

    // Getters and Setters
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

    public String getTrafficState() {
        return trafficState;
    }

    public void setTrafficState(String trafficState) {
        this.trafficState = trafficState;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}

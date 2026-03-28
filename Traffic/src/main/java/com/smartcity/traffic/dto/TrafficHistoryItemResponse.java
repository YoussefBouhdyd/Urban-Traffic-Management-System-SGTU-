package com.smartcity.traffic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartcity.traffic.model.TrafficAnalysis;
import java.time.LocalDateTime;

public class TrafficHistoryItemResponse {
    private Long id;
    private String intersectionId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String trafficState;
    private String severity;
    private int vehicleCount;
    private double averageSpeed;
    private String recommendation;

    public TrafficHistoryItemResponse() {
    }

    public TrafficHistoryItemResponse(Long id, String intersectionId, LocalDateTime timestamp,
                                      String trafficState, String severity, int vehicleCount,
                                      double averageSpeed, String recommendation) {
        this.id = id;
        this.intersectionId = intersectionId;
        this.timestamp = timestamp;
        this.trafficState = trafficState;
        this.severity = severity;
        this.vehicleCount = vehicleCount;
        this.averageSpeed = averageSpeed;
        this.recommendation = recommendation;
    }

    public static TrafficHistoryItemResponse fromTrafficAnalysis(TrafficAnalysis analysis) {
        return new TrafficHistoryItemResponse(
                analysis.getId(),
                analysis.getIntersectionId(),
                analysis.getTimestamp(),
                analysis.getTrafficState(),
                analysis.getSeverity(),
                analysis.getVehicleCount(),
                analysis.getAverageSpeed(),
                analysis.getRecommendation()
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

    public int getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(int vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}

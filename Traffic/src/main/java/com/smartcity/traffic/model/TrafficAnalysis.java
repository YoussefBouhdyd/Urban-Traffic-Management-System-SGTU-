package com.smartcity.traffic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class TrafficAnalysis {
    private Long id;
    private String intersectionId;
    private String cameraId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String trafficState; // NORMAL, BUSY, CONGESTED, INCIDENT
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private int vehicleCount;
    private double averageSpeed;
    private String recommendation;

    public TrafficAnalysis() {
    }

    public TrafficAnalysis(String intersectionId, String cameraId, LocalDateTime timestamp,
                           String trafficState, String severity, int vehicleCount,
                           double averageSpeed, String recommendation) {
        this.intersectionId = intersectionId;
        this.cameraId = cameraId;
        this.timestamp = timestamp;
        this.trafficState = trafficState;
        this.severity = severity;
        this.vehicleCount = vehicleCount;
        this.averageSpeed = averageSpeed;
        this.recommendation = recommendation;
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

    @Override
    public String toString() {
        return "TrafficAnalysis{" +
                "id=" + id +
                ", intersectionId='" + intersectionId + '\'' +
                ", cameraId='" + cameraId + '\'' +
                ", timestamp=" + timestamp +
                ", trafficState='" + trafficState + '\'' +
                ", severity='" + severity + '\'' +
                ", vehicleCount=" + vehicleCount +
                ", averageSpeed=" + averageSpeed +
                ", recommendation='" + recommendation + '\'' +
                '}';
    }
}

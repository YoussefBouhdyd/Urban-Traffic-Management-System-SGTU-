package com.smartcity.traffic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class CameraEvent {
    private Long id;
    private String cameraId;
    private String intersectionId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private int vehicleCount;
    private double averageSpeed;
    private int stoppedVehicles;
    private boolean pedestrianCrossing;
    private boolean suspectedIncident;
    private String congestionHint; // LOW, MEDIUM, HIGH

    public CameraEvent() {
    }

    public CameraEvent(String cameraId, String intersectionId, LocalDateTime timestamp,
                       int vehicleCount, double averageSpeed, int stoppedVehicles,
                       boolean pedestrianCrossing, boolean suspectedIncident, String congestionHint) {
        this.cameraId = cameraId;
        this.intersectionId = intersectionId;
        this.timestamp = timestamp;
        this.vehicleCount = vehicleCount;
        this.averageSpeed = averageSpeed;
        this.stoppedVehicles = stoppedVehicles;
        this.pedestrianCrossing = pedestrianCrossing;
        this.suspectedIncident = suspectedIncident;
        this.congestionHint = congestionHint;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
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

    public int getStoppedVehicles() {
        return stoppedVehicles;
    }

    public void setStoppedVehicles(int stoppedVehicles) {
        this.stoppedVehicles = stoppedVehicles;
    }

    public boolean isPedestrianCrossing() {
        return pedestrianCrossing;
    }

    public void setPedestrianCrossing(boolean pedestrianCrossing) {
        this.pedestrianCrossing = pedestrianCrossing;
    }

    public boolean isSuspectedIncident() {
        return suspectedIncident;
    }

    public void setSuspectedIncident(boolean suspectedIncident) {
        this.suspectedIncident = suspectedIncident;
    }

    public String getCongestionHint() {
        return congestionHint;
    }

    public void setCongestionHint(String congestionHint) {
        this.congestionHint = congestionHint;
    }

    @Override
    public String toString() {
        return "CameraEvent{" +
                "id=" + id +
                ", cameraId='" + cameraId + '\'' +
                ", intersectionId='" + intersectionId + '\'' +
                ", timestamp=" + timestamp +
                ", vehicleCount=" + vehicleCount +
                ", averageSpeed=" + averageSpeed +
                ", stoppedVehicles=" + stoppedVehicles +
                ", pedestrianCrossing=" + pedestrianCrossing +
                ", suspectedIncident=" + suspectedIncident +
                ", congestionHint='" + congestionHint + '\'' +
                '}';
    }
}

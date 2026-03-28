package com.smartcity.traffic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class CameraStatus {
    private String cameraId;
    private String intersectionId;
    private String status; // RUNNING, STOPPED, ERROR
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdate;

    public CameraStatus() {
    }

    public CameraStatus(String cameraId, String intersectionId, String status, LocalDateTime lastUpdate) {
        this.cameraId = cameraId;
        this.intersectionId = intersectionId;
        this.status = status;
        this.lastUpdate = lastUpdate;
    }

    // Getters and Setters
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "CameraStatus{" +
                "cameraId='" + cameraId + '\'' +
                ", intersectionId='" + intersectionId + '\'' +
                ", status='" + status + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}

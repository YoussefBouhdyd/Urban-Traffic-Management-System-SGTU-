package com.smartcity.traffic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartcity.traffic.model.CameraStatus;
import java.time.LocalDateTime;

public class CameraStatusResponse {
    private String cameraId;
    private String intersectionId;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdate;

    public CameraStatusResponse() {
    }

    public CameraStatusResponse(String cameraId, String intersectionId,
                                String status, LocalDateTime lastUpdate) {
        this.cameraId = cameraId;
        this.intersectionId = intersectionId;
        this.status = status;
        this.lastUpdate = lastUpdate;
    }

    public static CameraStatusResponse fromCameraStatus(CameraStatus cameraStatus) {
        return new CameraStatusResponse(
                cameraStatus.getCameraId(),
                cameraStatus.getIntersectionId(),
                cameraStatus.getStatus(),
                cameraStatus.getLastUpdate()
        );
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
}

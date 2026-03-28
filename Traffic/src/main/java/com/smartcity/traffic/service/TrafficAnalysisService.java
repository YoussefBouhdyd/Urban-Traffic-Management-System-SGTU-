package com.smartcity.traffic.service;

import com.smartcity.traffic.model.Alert;
import com.smartcity.traffic.model.CameraEvent;
import com.smartcity.traffic.model.Recommendation;
import com.smartcity.traffic.model.TrafficAnalysis;
import com.smartcity.traffic.repository.AlertRepository;
import com.smartcity.traffic.repository.CameraEventRepository;
import com.smartcity.traffic.repository.RecommendationRepository;
import com.smartcity.traffic.repository.TrafficAnalysisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class TrafficAnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(TrafficAnalysisService.class);
    
    private final CameraEventRepository eventRepository;
    private final TrafficAnalysisRepository analysisRepository;
    private final AlertRepository alertRepository;
    private final RecommendationRepository recommendationRepository;

    public TrafficAnalysisService() {
        this.eventRepository = new CameraEventRepository();
        this.analysisRepository = new TrafficAnalysisRepository();
        this.alertRepository = new AlertRepository();
        this.recommendationRepository = new RecommendationRepository();
    }

    /**
     * Analyze a camera event and generate traffic state, alerts, and recommendations
     */
    public void analyzeEvent(CameraEvent event) {
        try {
            // Save the raw event first
            eventRepository.save(event);
            
            // Perform analysis based on rules
            TrafficAnalysis analysis = performAnalysis(event);
            
            // Save analysis
            analysisRepository.save(analysis);
            
            // Generate alerts if needed
            generateAlerts(event, analysis);
            
            // Generate recommendations
            generateRecommendations(event, analysis);
            
            logger.info("Analysis completed for event from camera {}: state={}, severity={}", 
                    event.getCameraId(), analysis.getTrafficState(), analysis.getSeverity());
            
        } catch (SQLException e) {
            logger.error("Error analyzing camera event", e);
        }
    }

    private TrafficAnalysis performAnalysis(CameraEvent event) {
        String trafficState;
        String severity;
        String recommendation;

        // Rule 5: Incident detection (highest priority)
        if (event.isSuspectedIncident()) {
            trafficState = "INCIDENT";
            severity = "CRITICAL";
            recommendation = "Dispatch verification team and consider route deviation";
        }
        // Rule 4: Heavy congestion
        else if (event.getVehicleCount() > 45 && event.getAverageSpeed() < 8 && event.getStoppedVehicles() > 6) {
            trafficState = "CONGESTED";
            severity = "CRITICAL";
            recommendation = "Apply traffic diversion strategy immediately";
        }
        // Rule 3: Congested
        else if (event.getVehicleCount() > 35 && event.getAverageSpeed() < 15) {
            trafficState = "CONGESTED";
            severity = "HIGH";
            recommendation = "Increase green light duration by 20 seconds";
        }
        // Rule 2: Busy
        else if (event.getVehicleCount() >= 20 && event.getVehicleCount() <= 35 && 
                 event.getAverageSpeed() >= 15 && event.getAverageSpeed() <= 30) {
            trafficState = "BUSY";
            severity = "MEDIUM";
            recommendation = "Monitor traffic density closely";
        }
        // Rule 1: Normal
        else if (event.getVehicleCount() < 20 && event.getAverageSpeed() >= 30) {
            trafficState = "NORMAL";
            severity = "LOW";
            recommendation = "No action needed";
        }
        // Default fallback
        else {
            trafficState = "NORMAL";
            severity = "LOW";
            recommendation = "Continue monitoring";
        }

        return new TrafficAnalysis(
                event.getIntersectionId(),
                event.getCameraId(),
                event.getTimestamp(),
                trafficState,
                severity,
                event.getVehicleCount(),
                event.getAverageSpeed(),
                recommendation
        );
    }

    private void generateAlerts(CameraEvent event, TrafficAnalysis analysis) throws SQLException {
        // Generate alert for incidents
        if (event.isSuspectedIncident()) {
            Alert alert = new Alert(
                    "INCIDENT",
                    analysis.getSeverity(),
                    "Suspected incident detected at intersection " + event.getIntersectionId(),
                    LocalDateTime.now(),
                    "ACTIVE",
                    event.getIntersectionId(),
                    event.getCameraId()
            );
            alertRepository.save(alert);
        }

        // Generate alert for heavy congestion
        if ("CONGESTED".equals(analysis.getTrafficState()) && "CRITICAL".equals(analysis.getSeverity())) {
            Alert alert = new Alert(
                    "CONGESTION",
                    "CRITICAL",
                    "Critical congestion detected at intersection " + event.getIntersectionId(),
                    LocalDateTime.now(),
                    "ACTIVE",
                    event.getIntersectionId(),
                    event.getCameraId()
            );
            alertRepository.save(alert);
        }
        
        // Generate alert for high congestion
        else if ("CONGESTED".equals(analysis.getTrafficState())) {
            Alert alert = new Alert(
                    "CONGESTION",
                    "HIGH",
                    "Heavy congestion detected at intersection " + event.getIntersectionId(),
                    LocalDateTime.now(),
                    "ACTIVE",
                    event.getIntersectionId(),
                    event.getCameraId()
            );
            alertRepository.save(alert);
        }

        // Rule 6: Pedestrian warning
        if (event.isPedestrianCrossing() && event.getVehicleCount() > 30) {
            Alert alert = new Alert(
                    "PEDESTRIAN_WARNING",
                    "MEDIUM",
                    "High pedestrian activity detected with heavy traffic at intersection " + event.getIntersectionId(),
                    LocalDateTime.now(),
                    "ACTIVE",
                    event.getIntersectionId(),
                    event.getCameraId()
            );
            alertRepository.save(alert);
        }
    }

    private void generateRecommendations(CameraEvent event, TrafficAnalysis analysis) throws SQLException {
        // Only generate recommendations for non-normal states
        if (!"NORMAL".equals(analysis.getTrafficState()) || event.isPedestrianCrossing()) {
            String reason = buildReason(event, analysis);
            
            Recommendation recommendation = new Recommendation(
                    event.getIntersectionId(),
                    event.getCameraId(),
                    LocalDateTime.now(),
                    analysis.getRecommendation(),
                    reason,
                    analysis.getSeverity()
            );
            recommendationRepository.save(recommendation);
        }
    }

    private String buildReason(CameraEvent event, TrafficAnalysis analysis) {
        StringBuilder reason = new StringBuilder();
        
        if (event.isSuspectedIncident()) {
            reason.append("Incident suspected. ");
        }
        
        if (event.getVehicleCount() > 35) {
            reason.append("Vehicle density is high (").append(event.getVehicleCount()).append(" vehicles). ");
        }
        
        if (event.getAverageSpeed() < 15) {
            reason.append("Average speed is low (").append(event.getAverageSpeed()).append(" km/h). ");
        }
        
        if (event.getStoppedVehicles() > 6) {
            reason.append("Many stopped vehicles (").append(event.getStoppedVehicles()).append("). ");
        }
        
        if (event.isPedestrianCrossing()) {
            reason.append("Pedestrian crossing activity detected. ");
        }
        
        return reason.toString().trim();
    }
}

package com.smartcity.traffic.service;

import com.smartcity.traffic.model.CameraEvent;
import com.smartcity.traffic.model.CameraStatus;
import com.smartcity.traffic.kafka.CameraEventProducer;
import com.smartcity.traffic.repository.CameraStatusRepository;
import com.smartcity.traffic.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraSimulationService {
    private static final Logger logger = LoggerFactory.getLogger(CameraSimulationService.class);
    
    private final String cameraId;
    private final String intersectionId;
    private final int intervalSeconds;
    private final CameraEventProducer producer;
    private final CameraStatusRepository statusRepository;
    private final Random random;
    private final AtomicBoolean running;
    private ScheduledExecutorService scheduler;
    private CameraEvent lastGeneratedEvent;

    public CameraSimulationService() {
        this.cameraId = Config.getCameraId();
        this.intersectionId = Config.getIntersectionId();
        this.intervalSeconds = Config.getCameraEventIntervalSeconds();
        this.producer = new CameraEventProducer();
        this.statusRepository = new CameraStatusRepository();
        this.random = new Random();
        this.running = new AtomicBoolean(false);
        
        logger.info("CameraSimulationService initialized for camera {} at intersection {}", 
                cameraId, intersectionId);
    }

    public void start() {
        if (running.get()) {
            logger.warn("Simulation already running");
            return;
        }

        running.set(true);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        
        updateCameraStatus("RUNNING");

        scheduler.scheduleAtFixedRate(() -> {
            try {
                generateAndSendEvent();
            } catch (Exception e) {
                logger.error("Error generating camera event", e);
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS);

        logger.info("Camera simulation started");
    }

    public void stop() {
        if (!running.get()) {
            logger.warn("Simulation not running");
            return;
        }

        running.set(false);
        if (scheduler != null) {
            scheduler.shutdown();
        }
        
        updateCameraStatus("STOPPED");
        producer.close();

        logger.info("Camera simulation stopped");
    }

    private void generateAndSendEvent() {
        CameraEvent event = generateEvent();
        lastGeneratedEvent = event;
        
        // Send to Kafka
        producer.sendEvent(event);
        
        // Update camera status
        updateCameraStatus("RUNNING");
        
        logger.info("Generated and sent camera event: vehicleCount={}, avgSpeed={}, state={}", 
                event.getVehicleCount(), event.getAverageSpeed(), event.getCongestionHint());
    }

    private CameraEvent generateEvent() {
        // Generate realistic simulated values
        int vehicleCount = random.nextInt(61); // 0-60
        double averageSpeed = vehicleCount > 40 ? random.nextDouble() * 20 : 
                             vehicleCount > 20 ? 15 + random.nextDouble() * 25 :
                             30 + random.nextDouble() * 30;
        int stoppedVehicles = vehicleCount > 35 ? random.nextInt(16) : random.nextInt(6);
        boolean pedestrianCrossing = random.nextDouble() < 0.3; // 30% chance
        boolean suspectedIncident = random.nextDouble() < 0.05; // 5% chance
        
        String congestionHint;
        if (vehicleCount < 20) {
            congestionHint = "LOW";
        } else if (vehicleCount < 40) {
            congestionHint = "MEDIUM";
        } else {
            congestionHint = "HIGH";
        }

        return new CameraEvent(
                cameraId,
                intersectionId,
                LocalDateTime.now(),
                vehicleCount,
                Math.round(averageSpeed * 10.0) / 10.0, // Round to 1 decimal
                stoppedVehicles,
                pedestrianCrossing,
                suspectedIncident,
                congestionHint
        );
    }

    private void updateCameraStatus(String status) {
        try {
            CameraStatus cameraStatus = new CameraStatus(cameraId, intersectionId, status, LocalDateTime.now());
            statusRepository.saveOrUpdate(cameraStatus);
        } catch (SQLException e) {
            logger.error("Error updating camera status", e);
        }
    }

    public String getStatus() {
        return running.get() ? "RUNNING" : "STOPPED";
    }

    public CameraEvent getLastGeneratedEvent() {
        return lastGeneratedEvent;
    }
}

package com.smartcity.traffic;

import com.smartcity.traffic.rmi.CameraRemoteService;
import com.smartcity.traffic.rmi.CameraRemoteServiceImpl;
import com.smartcity.traffic.service.CameraSimulationService;
import com.smartcity.traffic.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Camera Simulator Main Application
 * 
 * This service:
 * - Simulates traffic camera metadata generation
 * - Publishes events to Kafka
 * - Exposes RMI interface for remote control
 */
public class CameraSimulatorMain {
    private static final Logger logger = LoggerFactory.getLogger(CameraSimulatorMain.class);

    public static void main(String[] args) {
        logger.info("=== Starting Camera Simulator Service ===");

        try {
            // Create simulation service
            CameraSimulationService simulationService = new CameraSimulationService();

            // Create and register RMI service
            int rmiPort = Config.getRmiPort();
            String serviceName = Config.getRmiServiceName();
            
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            CameraRemoteService remoteService = new CameraRemoteServiceImpl(simulationService);
            registry.rebind(serviceName, remoteService);
            
            logger.info("RMI Service registered: rmi://localhost:{}/{}", rmiPort, serviceName);

            // Start the camera simulation
            simulationService.start();
            logger.info("Camera simulation started");

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down Camera Simulator...");
                simulationService.stop();
                logger.info("Camera Simulator stopped");
            }));

            logger.info("=== Camera Simulator Service Running ===");
            logger.info("Camera ID: {}", Config.getCameraId());
            logger.info("Intersection ID: {}", Config.getIntersectionId());
            logger.info("Event interval: {} seconds", Config.getCameraEventIntervalSeconds());
            logger.info("Press Ctrl+C to stop");

            // Keep the main thread alive
            Thread.currentThread().join();

        } catch (Exception e) {
            logger.error("Error starting Camera Simulator", e);
            System.exit(1);
        }
    }
}

package com.smartcity.traffic;

import com.smartcity.traffic.api.AlertResource;
import com.smartcity.traffic.api.CameraResource;
import com.smartcity.traffic.api.RecommendationResource;
import com.smartcity.traffic.api.TrafficResource;
import com.smartcity.traffic.kafka.CameraEventConsumer;
import com.smartcity.traffic.service.TrafficAnalysisService;
import com.smartcity.traffic.util.Config;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * Central Analysis Service Main Application
 * 
 * This service:
 * - Consumes camera events from Kafka
 * - Analyzes traffic conditions
 * - Stores results in MySQL
 * - Exposes REST APIs for dashboard
 */
public class CentralAnalysisMain {
    private static final Logger logger = LoggerFactory.getLogger(CentralAnalysisMain.class);

    public static void main(String[] args) {
        logger.info("=== Starting Central Traffic Analysis Service ===");

        try {
            // Start REST API Server
            HttpServer server = startApiServer();
            String apiUrl = String.format("http://%s:%d/", Config.getApiHost(), Config.getApiPort());
            logger.info("API Server started at: {}", apiUrl);

            // Start Kafka Consumer
            TrafficAnalysisService analysisService = new TrafficAnalysisService();
            CameraEventConsumer consumer = new CameraEventConsumer(analysisService);
            
            Thread consumerThread = new Thread(() -> {
                logger.info("Starting Kafka consumer thread...");
                consumer.start();
            });
            consumerThread.start();

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down Central Analysis Service...");
                consumer.stop();
                server.shutdownNow();
                logger.info("Service stopped");
            }));

            logger.info("=== Central Traffic Analysis Service Running ===");
            logger.info("Press Ctrl+C to stop");

            // Keep the main thread alive
            Thread.currentThread().join();

        } catch (Exception e) {
            logger.error("Error starting Central Analysis Service", e);
            System.exit(1);
        }
    }

    private static HttpServer startApiServer() {
        String host = Config.getApiHost();
        int port = Config.getApiPort();
        String baseUri = String.format("http://%s:%d/", host, port);

        ResourceConfig config = new ResourceConfig()
                .register(TrafficResource.class)
                .register(AlertResource.class)
                .register(RecommendationResource.class)
                .register(CameraResource.class)
                .register(JacksonFeature.class)
                .register(new ObjectMapperContextResolver())
                .register(com.smartcity.traffic.filter.CorsFilter.class); // Add CORS support

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), config);
    }
}

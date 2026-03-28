package service.centrale;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import service.centrale.Kafka.KafkaConsumerListener;

public class Main {
    public static void main(String[] args) {
        // Default port
        int port = 9999;
        
        // Allow port override from command line
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default: 9999");
            }
        }
        
        // Create Jetty server
        Server server = new Server(port);
        
        // Create servlet context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/centrale");
        server.setHandler(context);
        
        // Add CORS filter
        context.addFilter(service.centrale.Filters.CorsFilter.class, "/api/*", null);
        
        // Configure Jersey servlet for REST
        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/api/*");
        jerseyServlet.setInitOrder(1);
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "service.centrale.Services");
        
        try {
            // Start Kafka consumer listener
            KafkaConsumerListener kafkaListener = new KafkaConsumerListener();
            kafkaListener.contextInitialized(null);
            
            // Start server
            System.out.println("======================================");
            System.out.println("Starting Service Centrale REST API");
            System.out.println("======================================");
            System.out.println("Port: " + port);
            System.out.println("Base URL: http://localhost:" + port + "/centrale/api");
            System.out.println("======================================");
            
            server.start();
            System.out.println("✓ Service Centrale started successfully!");
            System.out.println("\nAvailable endpoints:");
            System.out.println("  GET  /Flux              - Get all traffic flow data");
            System.out.println("  GET  /Flux/latest       - Get latest traffic flow");
            System.out.println("  GET  /Flux/route/{name} - Get flow for specific route");
            System.out.println("  GET  /Alert             - Get all alerts");
            System.out.println("  GET  /Feux/etat         - Get traffic lights state");
            System.out.println("  GET  /Feux/config       - Get traffic lights config");
            System.out.println("  POST /Feux/config       - Update traffic lights config");
            System.out.println("  POST /Feux/force/{name} - Force route to green");
            System.out.println("\nPress Ctrl+C to stop");
            
            server.join();
        } catch (Exception e) {
            System.err.println("Failed to start Service Centrale: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
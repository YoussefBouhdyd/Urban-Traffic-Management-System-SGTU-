package service.hub;

import java.io.IOException;
import java.util.Scanner;

import jakarta.xml.ws.Endpoint;
import service.hub.Services.ServiceFeux;
import service.hub.Services.ServiceFlux;

public class Main {
    public static void main(String[] args) {
        // SOAP services on original ports (8080/8081) for client compatibility
        String urlFlux = "http://localhost:8080/ServiceFlux";
        String urlFeux = "http://localhost:8081/ServiceFeux";

        System.out.println("======================================");
        System.out.println("Starting SOAP Services");
        System.out.println("======================================");
        
        Endpoint endpointFlux = Endpoint.publish(urlFlux, new ServiceFlux());
        System.out.println("✓ ServiceFlux started on: " + urlFlux);
        
        Endpoint endpointFeux = Endpoint.publish(urlFeux, new ServiceFeux());
        System.out.println("✓ ServiceFeux started on: " + urlFeux);
        
        System.out.println("======================================");
        System.out.println("Services running. Will stay alive until process killed.");
        System.out.println("======================================");
        
        // Keep service alive without waiting for input
        try {
            Object lock = new Object();
            synchronized (lock) {
                lock.wait(); // Wait forever
            }
        } catch (InterruptedException e) {
            endpointFeux.stop();
            endpointFlux.stop();
            System.out.println("Services stopped");
        }
    }
}
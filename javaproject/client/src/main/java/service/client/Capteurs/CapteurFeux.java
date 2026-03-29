package service.client.Capteurs;

import service.client.Models.Feux;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class CapteurFeux implements Runnable {
    private static final String[] ROUTE_IDS = {
        CapteurFlux.NORD,
        CapteurFlux.SUD,
        CapteurFlux.EST,
        CapteurFlux.OUEST
    };
    private static final String[]  NAMES    = {"Av Ibn Rochd", "Av Ibn Rochd", "Av Ma El Aynayne", "Av Ma El Aynayne"};
    private static final boolean[] SEGMENTS = {true, true, false, false};

    private final HttpClient httpClient;
    private static final String CENTRALE_URL = "http://localhost:9999/centrale/api/Feux/maj";

    public CapteurFeux() {
        this.httpClient = HttpClient.newHttpClient();
        System.out.println("[CapteurFeux] Initialized, will send data to Centrale API");
    }

    private boolean isGreen(boolean segment) {
        return CapteurFlux.getIntersectionGreen() == segment;
    }

    @Override
    public void run() {
        try {
            int iteration = 0;
            while (true) {
                Thread.sleep(1000);

                int remaining = CapteurFlux.getTimeRemaining();
 
                // Build JSON array of traffic light states
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < 4; i++) {
                    if (i > 0) json.append(",");
                    boolean green = isGreen(SEGMENTS[i]);
                    json.append("{")
                        .append("\"name\":\"").append(NAMES[i]).append("\",")
                        .append("\"segment\":").append(SEGMENTS[i]).append(",")
                        .append("\"remaining\":").append(remaining)
                        .append("}");
                    
                    String color = green ? "vert" : "rouge";
                    System.out.println("Feux " + NAMES[i] + " : "
                        + "[" + ROUTE_IDS[i] + "] "
                        + color
                        + " | temps restant: " + remaining + "s");
                }
                json.append("]");

                // Send to Centrale API every 5 seconds
                if (iteration % 5 == 0) {
                    try {
                        HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(CENTRALE_URL))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                            .build();
                        
                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                            .thenAccept(response -> {
                                if (response.statusCode() != 200) {
                                    System.err.println("[CapteurFeux] HTTP " + response.statusCode());
                                }
                            });
                    } catch (Exception e) {
                        System.err.println("[CapteurFeux] Error sending to Centrale: " + e.getMessage());
                    }
                }

                iteration++;
                System.out.println("iteration : " + iteration + "\n-------\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 

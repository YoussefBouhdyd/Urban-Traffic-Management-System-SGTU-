package service.hub.Services;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import service.hub.Interfaces.IServiceFeux;
import service.hub.Model.Feux;
import service.hub.Utilities.HttpUtil;
import service.hub.Utilities.KafkaProducerUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebService(
    targetNamespace = "http://Services.hub.service/",
    serviceName = "ServiceFeux",
    portName = "ServiceFeuxPort"
)
public class ServiceFeux implements IServiceFeux {
    private static int feuxDuration = 10;
    private static boolean northSouthGreen = false;
    private static int commandVersion = 0;
    private static final Map<String, Integer> deliveredCommandVersions = new ConcurrentHashMap<>();

    @WebMethod(operationName = "setFeux")
    public boolean setFeux(@WebParam(name = "list") Feux[] list) {
        String payload = "[]";
        try { //conversion -->json
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.length; i++) {
                sb.append(list[i].toString());
                if (i < list.length - 1) sb.append(",");
            }
            sb.append("]");
            payload = sb.toString();

            KafkaProducerUtil.publish("feux", payload);
        } catch (Exception e) {
            System.out.println("[Kafka Error] " + e.getMessage());
        }

        try {
            String response = HttpUtil.post(
                "http://localhost:9999/centrale/api/Feux/maj", 
                payload
            );
            System.out.println("[HTTP Response] " + response);
        } catch (Exception e) {
            System.out.println("[HTTP Error] " + e.getMessage());
        }
        return true;
    }

    @Override
    @WebMethod(operationName = "getFeuxTemp")
    public synchronized int getFeuxTemp(@WebParam(name = "name") String name) {
        String routeKey = normalize(name);
        int deliveredVersion = deliveredCommandVersions.getOrDefault(routeKey, -1);
        if (deliveredVersion == commandVersion) {
            return 0;
        }

        deliveredCommandVersions.put(routeKey, commandVersion);
        boolean routeGreen = isRouteGreen(name);
        return routeGreen ? feuxDuration : -feuxDuration;
    }

    @Override
    @WebMethod(operationName = "setFeuxTemp")
    public synchronized boolean setFeuxTemp(
        @WebParam(name = "name") String name,
        @WebParam(name = "feux") int feux
    ) {
        int nextDuration = Math.max(1, Math.abs(feux));
        boolean routeShouldBeGreen = feux >= 0;
        boolean routeOnNorthSouth = isNorthSouth(name);

        feuxDuration = nextDuration;
        northSouthGreen = routeShouldBeGreen ? routeOnNorthSouth : !routeOnNorthSouth;
        commandVersion++;

        System.out.println("[ServiceFeux] duration=" + feuxDuration
            + ", northSouthGreen=" + northSouthGreen + ", route=" + name + ", raw=" + feux);
        return true;
    }

    private boolean isRouteGreen(String name) {
        boolean routeOnNorthSouth = isNorthSouth(name);
        return routeOnNorthSouth == northSouthGreen;
    }

    private boolean isNorthSouth(String name) {
        if (name == null) {
            return false;
        }
        String normalized = name.trim().toLowerCase();
        return normalized.equals("nord")
            || normalized.equals("sud")
            || normalized.equals("northbound")
            || normalized.equals("southbound");
    }

    private String normalize(String name) {
        if (name == null) {
            return "";
        }
        return name.trim().toLowerCase();
    }
}

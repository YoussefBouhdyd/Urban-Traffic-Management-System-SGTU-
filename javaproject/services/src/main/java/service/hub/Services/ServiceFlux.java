package service.hub.Services;

import jakarta.jws.WebService;
import service.hub.Interfaces.IServiceFlux;
import service.hub.Utilities.KafkaProducerUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebService(
    targetNamespace = "http://Services.hub.service/",
    portName = "ServiceFluxPort",
    serviceName = "ServiceFlux",
    endpointInterface = "service.hub.Interfaces.IServiceFlux"
)
public class ServiceFlux implements IServiceFlux {
    @Override
    public void sendFlux(int flux, String name) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String json = String.format(
            "{\"flux\":%d,\"name\":\"%s\",\"timestamp\":\"%s\"}",
            flux, name, timestamp
        );
        try {
            KafkaProducerUtil.publish(name, json);
        } catch (Exception e) {
            System.out.println("[Kafka Error] " + e.getMessage());
            e.printStackTrace();
        }
    }
}

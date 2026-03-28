package service.centrale.Soap;

import jakarta.xml.ws.Service;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

public final class ServiceFeuxControlClient {
    private static final String WSDL_URL = System.getenv()
        .getOrDefault("SERVICE_FEUX_WSDL_URL", "http://localhost:8081/ServiceFeux?wsdl");
    private static final QName SERVICE_QNAME =
        new QName("http://Services.hub.service/", "ServiceFeux");
    private static final QName PORT_QNAME =
        new QName("http://Services.hub.service/", "ServiceFeuxPort");

    private ServiceFeuxControlClient() {
    }

    public static boolean setFeuxTemp(String name, int feux) {
        return getPort().setFeuxTemp(name, feux);
    }

    private static ServiceFeuxControlPort getPort() {
        try {
            Service service = Service.create(new URL(WSDL_URL), SERVICE_QNAME);
            return service.getPort(PORT_QNAME, ServiceFeuxControlPort.class);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid ServiceFeux WSDL URL: " + WSDL_URL, e);
        }
    }
}

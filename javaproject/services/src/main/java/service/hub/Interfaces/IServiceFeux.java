package service.hub.Interfaces;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(name = "ServiceFeux", targetNamespace = "http://Services.hub.service/")
public interface IServiceFeux {

    @WebMethod(operationName = "getFeuxTemp")
    int getFeuxTemp(@WebParam(name = "name") String name);

    @WebMethod(operationName = "setFeuxTemp")
    boolean setFeuxTemp(
        @WebParam(name = "name") String name,
        @WebParam(name = "feux") int feux
    );
}

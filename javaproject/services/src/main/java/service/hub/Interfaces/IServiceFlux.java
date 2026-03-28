package service.hub.Interfaces;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IServiceFlux {

    @WebMethod
    void sendFlux(int flux, String name);
}
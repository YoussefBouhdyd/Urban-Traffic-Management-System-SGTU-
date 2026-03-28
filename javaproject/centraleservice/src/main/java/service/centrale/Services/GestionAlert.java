package service.centrale.Services;

import java.util.ArrayList;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import service.centrale.Dtos.ReceptionFlux;

@Path("/Alert")
public class GestionAlert {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ReceptionFlux> getAlerts()
    {
        ArrayList<ReceptionFlux> list=new ArrayList<>(GestionFlux.getList());
        GestionFlux.refresh();
        return list;
    }
}

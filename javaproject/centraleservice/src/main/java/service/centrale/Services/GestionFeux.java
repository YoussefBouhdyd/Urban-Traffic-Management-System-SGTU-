package service.centrale.Services;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import service.centrale.Dtos.Feux;
import service.centrale.Dtos.FeuxCommand;
import service.centrale.Dtos.FeuxConfig;
import service.centrale.Dtos.FeuxEtat;
import service.centrale.Dtos.RouteNomUpdate;
import service.centrale.Soap.ServiceFeuxControlClient;

@Path("/Feux")
public class GestionFeux {
    private static final String[] ROUTE_IDS = {"nord", "sud", "est", "ouest"};//chac 1 route-->identificateur 
    private static final String[] ROUTE_NAMES = {
        "Av Ibn Rochd", "Av Ibn Rochd", "Av Ma El Aynayne", "Av Ma El Aynayne"
    };
    private static final boolean[] ROUTE_SEGMENTS = {true, true, false, false};
    private static int duration = 10;
    private static boolean segmentGreen = false;
    private static List<Feux> listFeux=new ArrayList<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<FeuxEtat> getStates()
    {
        ensureStateInitialized();
        return buildEtatSnapshot();
    }

    @GET
    @Path("/etat")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FeuxEtat> getFeuxEtat()
    {
        ensureStateInitialized();
        return buildEtatSnapshot();
    }

    @GET
    @Path("/config")
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized FeuxConfig getConfig() {
        return new FeuxConfig(duration, segmentGreen);
    }

    @POST
    @Path("/maj")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized List<Feux> addFeux(List<Feux> incoming) {
        List<Feux> modifications = new ArrayList<>();
        boolean shouldFlipSegment = false;
        List<Feux> updatedFeux = new ArrayList<>();

        for (int i = 0; i < incoming.size(); i++) {
            Feux newFeux = incoming.get(i);
            Feux existing = i < listFeux.size() ? listFeux.get(i) : null;

            if (existing == null 
                || existing.isSegment() != newFeux.isSegment() 
                || existing.getRemaining() != newFeux.getRemaining()) {
                modifications.add(newFeux);
            }

            if (existing != null && newFeux.getRemaining() > existing.getRemaining()) {
                shouldFlipSegment = true;
            }

            updatedFeux.add(new Feux(
                newFeux.getRemaining(),
                newFeux.isSegment(),
                currentRouteName(i)
            ));
        }

        if (shouldFlipSegment) {
            segmentGreen = !segmentGreen;
        }
        listFeux = updatedFeux;
        return modifications;
    }

    @POST
    @Path("/config")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized FeuxConfig updateConfig(FeuxConfig update) {
        int nextDuration = update.getDuration() > 0 ? update.getDuration() : duration;
        applyCentralCommand(update.isSegmentGreen() ? ROUTE_IDS[0] : ROUTE_IDS[2], nextDuration, true);
        return new FeuxConfig(duration, segmentGreen);
    }

    @POST
    @Path("/force/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized FeuxConfig forceFeux(@PathParam("name") String name, FeuxCommand command) {
        int nextDuration = command.getDuration() > 0 ? command.getDuration() : duration;
        applyCentralCommand(name, nextDuration, command.isGreen());
        return new FeuxConfig(duration, segmentGreen);
    }

    @POST
    @Path("/nom/{routeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized FeuxEtat renameRoute(
        @PathParam("routeId") String routeId,
        RouteNomUpdate update
    ) {
        if (update == null || update.getName() == null || update.getName().trim().isEmpty()) {
            throw new BadRequestException("Le nom de la route est obligatoire");
        }

        int index = indexForRouteId(routeId);
        if (index < 0) {
            throw new NotFoundException("Route inconnue: " + routeId);
        }

        ROUTE_NAMES[index] = update.getName().trim();
        ensureStateInitialized();
        if (index < listFeux.size()) {
            listFeux.get(index).setName(ROUTE_NAMES[index]);
        }

        return buildEtatSnapshot().get(index);
    }

    private synchronized List<FeuxEtat> buildEtatSnapshot() {
        List<FeuxEtat> states = new ArrayList<>();
        for (int i = 0; i < listFeux.size(); i++) {
            Feux feux = listFeux.get(i);
            states.add(new FeuxEtat(
                feux.getRemaining(),
                feux.isSegment(),
                currentRouteName(i),
                routeIdForIndex(i),
                feux.isSegment() == segmentGreen
            ));
        }
        return states;
    }

    private void applyCentralCommand(String routeName, int nextDuration, boolean green) {
        int command = green ? nextDuration : -nextDuration;
        ServiceFeuxControlClient.setFeuxTemp(routeName, command);
        duration = nextDuration;
        segmentGreen = green ? isNorthSouth(routeName) : !isNorthSouth(routeName);
        applyConfigToCurrentState();
    }

    private void applyConfigToCurrentState() {
        ensureStateInitialized();
        List<Feux> updated = new ArrayList<>();
        for (int i = 0; i < listFeux.size(); i++) {
            Feux feux = listFeux.get(i);
            updated.add(new Feux(duration * 2, feux.isSegment(), currentRouteName(i)));
        }
        listFeux = updated;
    }

    private void ensureStateInitialized() {
        if (!listFeux.isEmpty()) {
            return;
        }

        List<Feux> defaults = new ArrayList<>();
        for (int i = 0; i < ROUTE_NAMES.length; i++) {
            defaults.add(new Feux(duration * 2, ROUTE_SEGMENTS[i], currentRouteName(i)));
        }
        listFeux = defaults;
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

    private String routeIdForIndex(int index) {
        if (index >= 0 && index < ROUTE_IDS.length) {
            return ROUTE_IDS[index];
        }
        return "route-" + index;
    }

    private int indexForRouteId(String routeId) {
        if (routeId == null) {
            return -1;
        }

        for (int i = 0; i < ROUTE_IDS.length; i++) {
            if (ROUTE_IDS[i].equalsIgnoreCase(routeId)) {
                return i;
            }
        }
        return -1;
    }

    private String currentRouteName(int index) {
        if (index >= 0 && index < ROUTE_NAMES.length) {
            return ROUTE_NAMES[index];
        }
        return "Route " + index;
    }
}

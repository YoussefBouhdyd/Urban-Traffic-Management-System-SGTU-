package service.centrale.Dtos;

public class FeuxEtat extends Feux {
    private String routeId;
    private boolean green;

    public FeuxEtat() {
    }

    public FeuxEtat(int remaining, boolean segment, String name, String routeId, boolean green) {
        super(remaining, segment, name);
        this.routeId = routeId;
        this.green = green;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public boolean isGreen() {
        return green;
    }

    public void setGreen(boolean green) {
        this.green = green;
    }
}

package service.centrale.Dtos;

public class RouteNomUpdate {
    private String name;

    public RouteNomUpdate() {
    }

    public RouteNomUpdate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

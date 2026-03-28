package service.centrale.Dtos;

public class PointFlux {
    private int flux;
    private String name;
    public PointFlux() {}
    public int getFlux() {
        return flux;
    }
    public void setFlux(int flux) {
        this.flux = flux;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public PointFlux(int flux, String name) {
        this.flux = flux;
        this.name = name;
    }
    public PointFlux(int flux) {
        this.flux = flux;
    }
    public PointFlux(String name) {
        this.name = name;
    }
    
}

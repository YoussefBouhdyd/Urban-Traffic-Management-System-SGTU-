package service.centrale.Dtos;

public class ReceptionFlux extends PointFlux {
    

    public String timestamp;
    public ReceptionFlux() {}
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ReceptionFlux(int flux, String name, String timestamp) {
        super(flux, name);
        this.timestamp = timestamp;
    }

    public ReceptionFlux(int flux, String timestamp) {
        super(flux);
        this.timestamp = timestamp;
    }

    public ReceptionFlux(String name, String timestamp) {
        super(name);
        this.timestamp = timestamp;
    }
    
}

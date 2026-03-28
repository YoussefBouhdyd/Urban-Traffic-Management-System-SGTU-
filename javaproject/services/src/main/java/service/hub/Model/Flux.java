package service.hub.Model;

public class Flux {
    

    private String name;
    private int flux;
    private String timestamp;

    public Flux(String name,int flux,String timesamp)
    {
        this.name=name;
        this.flux=flux;
        this.timestamp=timesamp;
    }

    public String getName() {
        return name;
    }

    public int getFlux() {
        return flux;
    }

    public String getTimestamp() {
        return timestamp;
    }

}

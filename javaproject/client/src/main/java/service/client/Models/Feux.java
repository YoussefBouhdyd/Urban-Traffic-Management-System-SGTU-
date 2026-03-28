package service.client.Models;
 

public class Feux {
    private int remaining;
    private boolean segment;
    private String name;

    public Feux() {}  

    public Feux(int remaining, boolean segment, String name) {
        this.remaining = remaining;
        this.segment = segment;
        this.name = name;
    }

    public int getRemaining()         { return remaining; }
    public void setRemaining(int r)   { this.remaining = r; }
    public boolean isSegment()        { return segment; }
    public void setSegment(boolean s) { this.segment = s; }
    public String getName()           { return name; }
    public void setName(String n)     { this.name = n; }

    @Override
    public String toString() {
        return "{\"name\":\"" + name + "\",\"segment\":" + segment 
             + ",\"remaining\":" + remaining + "}";
    }
}
package com.traffic.central.models;

/**
 * ============================================================================
 * CLASSE : PollutionData
 * ============================================================================
 * 
 * RÔLE : Modèle de données pour la pollution
 * 
 * ============================================================================
 */
public class PollutionData {
    
    private String zone_id;
    private double niveau_co2;
    private String timestamp;
    
    public PollutionData() {
    }
    
    public PollutionData(String zone_id, double niveau_co2, String timestamp) {
        this.zone_id = zone_id;
        this.niveau_co2 = niveau_co2;
        this.timestamp = timestamp;
    }
    
    public String getZone_id() {
        return zone_id;
    }
    
    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
    }
    
    public double getNiveau_co2() {
        return niveau_co2;
    }
    
    public void setNiveau_co2(double niveau_co2) {
        this.niveau_co2 = niveau_co2;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "PollutionData{" +
                "zone_id='" + zone_id + '\'' +
                ", niveau_co2=" + niveau_co2 +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
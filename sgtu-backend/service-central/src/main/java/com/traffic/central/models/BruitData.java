package com.traffic.central.models;

/**
 * ============================================================================
 * CLASSE : BruitData
 * ============================================================================
 * 
 * RÔLE : Modèle de données pour le bruit
 * 
 * ============================================================================
 */
public class BruitData {
    
    private String zone_id;
    private double niveau_decibels;
    private String timestamp;
    
    public BruitData() {
    }
    
    public BruitData(String zone_id, double niveau_decibels, String timestamp) {
        this.zone_id = zone_id;
        this.niveau_decibels = niveau_decibels;
        this.timestamp = timestamp;
    }
    
    public String getZone_id() {
        return zone_id;
    }
    
    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
    }
    
    public double getNiveau_decibels() {
        return niveau_decibels;
    }
    
    public void setNiveau_decibels(double niveau_decibels) {
        this.niveau_decibels = niveau_decibels;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "BruitData{" +
                "zone_id='" + zone_id + '\'' +
                ", niveau_decibels=" + niveau_decibels +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
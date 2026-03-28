package com.traffic.pollution.models;

/**
 * ============================================================================
 * CLASSE : PollutionData
 * ============================================================================
 * 
 * RÔLE :
 * Modèle de données représentant une mesure de pollution.
 * Cette classe est identique à celle du Service REST.
 * 
 * UTILISATION :
 * - Le simulateur crée des objets PollutionData
 * - Gson les convertit en JSON
 * - Le JSON est envoyé au Service REST
 * 
 * ============================================================================
 */
public class PollutionData {
    
    private String zone_id;
    private double niveau_co2;
    private String timestamp;
    
    /**
     * CONSTRUCTEUR VIDE
     */
    public PollutionData() {
    }
    
    /**
     * CONSTRUCTEUR AVEC PARAMÈTRES
     */
    public PollutionData(String zone_id, double niveau_co2, String timestamp) {
        this.zone_id = zone_id;
        this.niveau_co2 = niveau_co2;
        this.timestamp = timestamp;
    }
    
    // GETTERS ET SETTERS
    
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
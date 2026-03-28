package com.traffic.bruit.models;

/**
 * ============================================================================
 * CLASSE : BruitData
 * ============================================================================
 * 
 * RÔLE :
 * Modèle de données représentant une mesure de bruit.
 * Cette classe est identique à celle du Service TCP.
 * 
 * UTILISATION :
 * - Le simulateur crée des objets BruitData
 * - Gson les convertit en JSON
 * - Le JSON est envoyé au Service TCP via Socket
 * 
 * ============================================================================
 */
public class BruitData {
    
    private String zone_id;
    private double niveau_decibels;
    private String timestamp;
    
    /**
     * CONSTRUCTEUR VIDE
     */
    public BruitData() {
    }
    
    /**
     * CONSTRUCTEUR AVEC PARAMÈTRES
     */
    public BruitData(String zone_id, double niveau_decibels, String timestamp) {
        this.zone_id = zone_id;
        this.niveau_decibels = niveau_decibels;
        this.timestamp = timestamp;
    }
    
    // GETTERS ET SETTERS
    
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
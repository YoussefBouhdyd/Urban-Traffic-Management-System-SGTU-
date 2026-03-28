package com.traffic.bruit.models;

/**
 * ============================================================================
 * CLASSE : BruitData
 * ============================================================================
 * 
 * RÔLE :
 * Modèle de données représentant une mesure de bruit.
 * 
 * ATTRIBUTS :
 * - zone_id : Identifiant de la zone (ex: "Zone_Centre")
 * - niveau_decibels : Niveau de bruit en décibels (ex: 75.5)
 * - timestamp : Date et heure de la mesure (ex: "2026-03-15T14:30:00")
 * 
 * UTILISATION :
 * - Gson convertit le JSON reçu en objet BruitData
 * - Puis on peut accéder aux données via les getters
 * 
 * EXEMPLE JSON :
 * {
 *   "zone_id": "Zone_Nord",
 *   "niveau_decibels": 75.5,
 *   "timestamp": "2026-03-15T14:30:00"
 * }
 * 
 * ============================================================================
 */
public class BruitData {
    
    // ========================================================================
    // ATTRIBUTS
    // ========================================================================
    
    /**
     * Identifiant de la zone géographique
     */
    private String zone_id;
    
    /**
     * Niveau de bruit mesuré en décibels (dB)
     * Plage normale : 40-90 dB
     * Seuil d'alerte : > 85 dB
     */
    private double niveau_decibels;
    
    /**
     * Horodatage de la mesure au format ISO 8601
     */
    private String timestamp;
    
    
    // ========================================================================
    // CONSTRUCTEURS
    // ========================================================================
    
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
    
    
    // ========================================================================
    // GETTERS ET SETTERS
    // ========================================================================
    
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
    
    
    // ========================================================================
    // MÉTHODE UTILITAIRE
    // ========================================================================
    
    @Override
    public String toString() {
        return "BruitData{" +
                "zone_id='" + zone_id + '\'' +
                ", niveau_decibels=" + niveau_decibels +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
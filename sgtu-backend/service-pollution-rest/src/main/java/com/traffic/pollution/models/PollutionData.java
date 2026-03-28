package com.traffic.pollution.models;

/**
 * ============================================================================
 * CLASSE : PollutionData
 * ============================================================================
 * 
 * RÔLE :
 * Cette classe représente une donnée de pollution reçue depuis un capteur.
 * C'est un modèle de données (POJO - Plain Old Java Object).
 * 
 * ATTRIBUTS :
 * - zone_id : Identifiant de la zone (ex: "Zone_Centre")
 * - niveau_co2 : Niveau de CO2 en µg/m³ (ex: 68.5)
 * - timestamp : Date et heure de la mesure (ex: "2026-03-15T14:30:00")
 * 
 * UTILISATION :
 * - Gson convertit automatiquement le JSON reçu en objet PollutionData
 * - Puis on peut accéder aux données via les getters
 * 
 * EXEMPLE JSON :
 * {
 *   "zone_id": "Zone_Centre",
 *   "niveau_co2": 68.5,
 *   "timestamp": "2026-03-15T14:30:00"
 * }
 * 
 * ============================================================================
 */
public class PollutionData {
    
    // ========================================================================
    // ATTRIBUTS (propriétés de la classe)
    // ========================================================================
    
    /**
     * Identifiant de la zone géographique
     * Exemples : "Zone_Centre", "Zone_Nord", "Zone_Sud"
     */
    private String zone_id;
    
    /**
     * Niveau de CO2 mesuré en microgrammes par mètre cube (µg/m³)
     * Plage normale : 20-100 µg/m³
     * Seuil d'alerte : > 80 µg/m³
     */
    private double niveau_co2;
    
    /**
     * Horodatage de la mesure au format ISO 8601
     * Format : "YYYY-MM-DDTHH:mm:ss"
     * Exemple : "2026-03-15T14:30:00"
     */
    private String timestamp;
    
    
    // ========================================================================
    // CONSTRUCTEURS
    // ========================================================================
    
    /**
     * CONSTRUCTEUR VIDE
     * Nécessaire pour que Gson puisse créer l'objet automatiquement
     * lors de la désérialisation JSON
     */
    public PollutionData() {
        // Constructeur vide - Gson l'utilise pour créer l'objet
    }
    
    /**
     * CONSTRUCTEUR AVEC PARAMÈTRES
     * Permet de créer un objet PollutionData en fournissant toutes les valeurs
     * 
     * @param zone_id : Identifiant de la zone
     * @param niveau_co2 : Niveau de CO2 mesuré
     * @param timestamp : Date et heure de la mesure
     */
    public PollutionData(String zone_id, double niveau_co2, String timestamp) {
        this.zone_id = zone_id;
        this.niveau_co2 = niveau_co2;
        this.timestamp = timestamp;
    }
    
    
    // ========================================================================
    // GETTERS ET SETTERS
    // Ces méthodes permettent d'accéder et de modifier les attributs privés
    // ========================================================================
    
    /**
     * Récupère l'identifiant de la zone
     * @return zone_id
     */
    public String getZone_id() {
        return zone_id;
    }
    
    /**
     * Modifie l'identifiant de la zone
     * @param zone_id : Nouveau zone_id
     */
    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
    }
    
    /**
     * Récupère le niveau de CO2
     * @return niveau_co2
     */
    public double getNiveau_co2() {
        return niveau_co2;
    }
    
    /**
     * Modifie le niveau de CO2
     * @param niveau_co2 : Nouveau niveau
     */
    public void setNiveau_co2(double niveau_co2) {
        this.niveau_co2 = niveau_co2;
    }
    
    /**
     * Récupère le timestamp
     * @return timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }
    
    /**
     * Modifie le timestamp
     * @param timestamp : Nouveau timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    
    // ========================================================================
    // MÉTHODE UTILITAIRE
    // ========================================================================
    
    /**
     * Convertit l'objet en chaîne de caractères lisible
     * Utile pour afficher l'objet dans la console avec System.out.println()
     * 
     * @return Représentation textuelle de l'objet
     */
    @Override
    public String toString() {
        return "PollutionData{" +
                "zone_id='" + zone_id + '\'' +
                ", niveau_co2=" + niveau_co2 +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
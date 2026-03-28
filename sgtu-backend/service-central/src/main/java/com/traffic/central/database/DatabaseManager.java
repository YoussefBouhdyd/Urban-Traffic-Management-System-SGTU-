package com.traffic.central.database;

import com.traffic.central.models.BruitData;
import com.traffic.central.models.PollutionData;

import java.sql.*;

/**
 * ============================================================================
 * CLASSE : DatabaseManager
 * ============================================================================
 * 
 * RÔLE :
 * Gère toutes les interactions avec la base de données MySQL.
 * 
 * FONCTIONNALITÉS :
 * - Connexion à MySQL
 * - Sauvegarde des données de pollution
 * - Sauvegarde des données de bruit
 * - Création d'alertes
 * - Création de recommandations
 * 
 * CONFIGURATION MYSQL :
 * - URL : jdbc:mysql://localhost:3306/traffic_pollution_bruit
 * - Username : root
 * - Password : root123
 * 
 * ============================================================================
 */
public class DatabaseManager {
    
    // ========================================================================
    // CONSTANTES - CONFIGURATION MYSQL
    // ========================================================================
    
    /**
     * URL de connexion JDBC à MySQL
     */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/integrated_traffic_system";
    
    /**
     * Nom d'utilisateur MySQL
     */
    private static final String DB_USER = "root";
    
    /**
     * Mot de passe MySQL
     */
    private static final String DB_PASSWORD = "";
    
    /**
     * Objet Connection partagé
     */
    private Connection connection;
    
    
    // ========================================================================
    // CONSTRUCTEUR
    // ========================================================================
    
    /**
     * CONSTRUCTEUR
     * Établit la connexion à MySQL au démarrage
     */
    public DatabaseManager() {
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Créer la connexion
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            System.out.println("[DATABASE] ✓ Connexion à MySQL établie avec succès !");
            System.out.println("[DATABASE] Base de données : traffic_pollution_bruit");
            
        } catch (ClassNotFoundException e) {
            System.err.println("[DATABASE] ✗ Driver MySQL introuvable : " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[DATABASE] ✗ Erreur de connexion MySQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    // ========================================================================
    // MÉTHODES PUBLIQUES - SAUVEGARDE DES DONNÉES
    // ========================================================================
    
    /**
     * MÉTHODE : savePollutionData
     * 
     * RÔLE :
     * Sauvegarde une donnée de pollution dans la table `pollution`
     * 
     * @param data : Les données de pollution à sauvegarder
     */
    public void savePollutionData(PollutionData data) {
        
        String sql = "INSERT INTO pollution (zone_id, niveau_co2, timestamp) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            // Remplir les paramètres de la requête
            stmt.setString(1, data.getZone_id());
            stmt.setDouble(2, data.getNiveau_co2());
            stmt.setString(3, data.getTimestamp());
            
            // Exécuter l'INSERT
            int rowsInserted = stmt.executeUpdate();
            
            if (rowsInserted > 0) {
                System.out.println("[DATABASE] ✓ Pollution sauvegardée : " + data.getZone_id() + 
                                  " - " + data.getNiveau_co2() + " µg/m³");
            }
            
        } catch (SQLException e) {
            System.err.println("[DATABASE] ✗ Erreur lors de la sauvegarde pollution : " + e.getMessage());
        }
    }
    
    /**
     * MÉTHODE : saveBruitData
     * 
     * RÔLE :
     * Sauvegarde une donnée de bruit dans la table `bruit`
     * 
     * @param data : Les données de bruit à sauvegarder
     */
    public void saveBruitData(BruitData data) {
        
        String sql = "INSERT INTO bruit (zone_id, niveau_decibels, timestamp) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, data.getZone_id());
            stmt.setDouble(2, data.getNiveau_decibels());
            stmt.setString(3, data.getTimestamp());
            
            int rowsInserted = stmt.executeUpdate();
            
            if (rowsInserted > 0) {
                System.out.println("[DATABASE] ✓ Bruit sauvegardé : " + data.getZone_id() + 
                                  " - " + data.getNiveau_decibels() + " dB");
            }
            
        } catch (SQLException e) {
            System.err.println("[DATABASE] ✗ Erreur lors de la sauvegarde bruit : " + e.getMessage());
        }
    }
    
    
    // ========================================================================
    // MÉTHODES PUBLIQUES - GESTION DES ALERTES
    // ========================================================================
    
    /**
     * MÉTHODE : createAlerte
     * 
     * RÔLE :
     * Crée une alerte dans la table `alertes`
     * 
     * @param typeAlerte : Type d'alerte (ex: "POLLUTION", "BRUIT")
     * @param zoneId : Zone concernée
     * @param niveauGravite : Niveau de gravité ("HAUTE", "MOYENNE", "BASSE")
     * @param message : Message descriptif de l'alerte
     * @return ID de l'alerte créée (pour lier les recommandations)
     */
    public int createAlerte(String typeAlerte, String zoneId, String niveauGravite, String message) {
        
        String sql = "INSERT INTO alertes (type_alerte, zone_id, niveau_gravite, message) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, typeAlerte);
            stmt.setString(2, zoneId);
            stmt.setString(3, niveauGravite);
            stmt.setString(4, message);
            
            int rowsInserted = stmt.executeUpdate();
            
            if (rowsInserted > 0) {
                // Récupérer l'ID auto-généré
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int alerteId = generatedKeys.getInt(1);
                    System.out.println("[DATABASE] 🚨 ALERTE créée (ID:" + alerteId + ") : " + 
                                      typeAlerte + " - " + zoneId + " - " + niveauGravite);
                    return alerteId;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("[DATABASE] ✗ Erreur lors de la création de l'alerte : " + e.getMessage());
        }
        
        return -1; // Erreur
    }
    
    
    // ========================================================================
    // MÉTHODES PUBLIQUES - GESTION DES RECOMMANDATIONS
    // ========================================================================
    
    /**
     * MÉTHODE : createRecommandation
     * 
     * RÔLE :
     * Crée une recommandation dans la table `recommandations`
     * 
     * @param alerteId : ID de l'alerte associée
     * @param actionRecommandee : L'action recommandée
     */
    public void createRecommandation(int alerteId, String actionRecommandee) {
        
        String sql = "INSERT INTO recommandations (alerte_id, action_recommandee) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, alerteId);
            stmt.setString(2, actionRecommandee);
            
            int rowsInserted = stmt.executeUpdate();
            
            if (rowsInserted > 0) {
                System.out.println("[DATABASE] 💡 RECOMMANDATION créée pour alerte #" + alerteId + 
                                  " : " + actionRecommandee);
            }
            
        } catch (SQLException e) {
            System.err.println("[DATABASE] ✗ Erreur lors de la création de la recommandation : " + 
                              e.getMessage());
        }
    }
    
    
    // ========================================================================
    // MÉTHODES PUBLIQUES - FERMETURE
    // ========================================================================
    
    /**
     * MÉTHODE : close
     * 
     * RÔLE :
     * Ferme proprement la connexion MySQL
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DATABASE] Connexion MySQL fermée.");
            } catch (SQLException e) {
                System.err.println("[DATABASE] ✗ Erreur lors de la fermeture : " + e.getMessage());
            }
        }
    }
}
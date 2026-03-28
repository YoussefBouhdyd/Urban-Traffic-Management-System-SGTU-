package com.traffic.pollution;

import com.google.gson.Gson;
import com.traffic.pollution.models.PollutionData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * ============================================================================
 * CLASSE : SimulateurPollution
 * ============================================================================
 * 
 * RÔLE :
 * Simule des capteurs de pollution qui génèrent des données en continu.
 * 
 * FONCTIONNEMENT :
 * 1. Génère des données de pollution aléatoires toutes les 15 secondes
 * 2. Crée un objet PollutionData avec ces données
 * 3. Convertit l'objet en JSON avec Gson
 * 4. Envoie le JSON vers le Service REST via HTTP POST
 * 5. Répète indéfiniment (boucle infinie)
 * 
 * ZONES SIMULÉES :
 * - Zone_Centre
 * - Zone_Nord
 * - Zone_Sud
 * 
 * PLAGE DE POLLUTION :
 * - Minimum : 20 µg/m³
 * - Maximum : 100 µg/m³
 * - Variation réaliste selon l'heure (plus élevé aux heures de pointe)
 * 
 * URL CIBLE : http://localhost:8080/api/pollution
 * 
 * ============================================================================
 */
public class SimulateurPollution {
    
    // ========================================================================
    // CONSTANTES
    // ========================================================================
    
    /**
     * URL du service REST qui va recevoir les données
     */
    private static final String SERVICE_URL = "http://localhost:8080/api/pollution";
    
    /**
     * Liste des zones géographiques à simuler
     */
    private static final String[] ZONES = {
        "Zone_Centre",
        "Zone_Nord",
        "Zone_Sud"
    };
    
    /**
     * Intervalle entre chaque génération de données (en millisecondes)
     * 15 secondes = 15000 ms
     */
    private static final int INTERVAL_MS = 15000;
    
    /**
     * Générateur de nombres aléatoires
     */
    private static final Random random = new Random();
    
    /**
     * Objet Gson pour convertir objets Java → JSON
     */
    private static final Gson gson = new Gson();
    
    /**
     * Formateur pour les timestamps au format ISO 8601
     */
    private static final DateTimeFormatter timeFormatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    
    // ========================================================================
    // MÉTHODE PRINCIPALE
    // ========================================================================
    
    /**
     * MÉTHODE : main
     * 
     * Point d'entrée du simulateur
     * Lance la boucle infinie de génération de données
     */
    public static void main(String[] args) {
        
        // Afficher le header de démarrage
        printHeader();
        
        // Compteur de messages envoyés
        int messageCount = 0;
        
        System.out.println("[SIMULATEUR] Démarrage de la simulation...");
        System.out.println("[SIMULATEUR] Envoi de données toutes les " + (INTERVAL_MS / 1000) + " secondes");
        System.out.println("[SIMULATEUR] Zones surveillées : " + String.join(", ", ZONES));
        System.out.println("[SIMULATEUR] Appuyez sur Ctrl+C pour arrêter\n");
        
        // ====================================================================
        // BOUCLE INFINIE : GÉNÉRATION ET ENVOI DE DONNÉES
        // ====================================================================
        
        while (true) {
            try {
                // Pour chaque zone, générer et envoyer des données
                for (String zone : ZONES) {
                    
                    messageCount++;
                    
                    System.out.println("\n========================================");
                    System.out.println("[SIMULATEUR] Message #" + messageCount);
                    System.out.println("========================================");
                    
                    // Étape 1 : Générer des données de pollution
                    PollutionData data = genererDonneesPollution(zone);
                    
                    // Étape 2 : Convertir en JSON
                    String json = gson.toJson(data);
                    
                    System.out.println("[SIMULATEUR] Données générées : " + data);
                    System.out.println("[SIMULATEUR] JSON créé : " + json);
                    
                    // Étape 3 : Envoyer vers le service REST
                    envoyerVersService(json);
                    
                    System.out.println("========================================\n");
                }
                
                // Attendre 15 secondes avant la prochaine itération
                System.out.println("[SIMULATEUR] ⏳ Attente de " + (INTERVAL_MS / 1000) + " secondes...\n");
                Thread.sleep(INTERVAL_MS);
                
            } catch (InterruptedException e) {
                System.err.println("[SIMULATEUR] ✗ Simulation interrompue");
                break;
            } catch (Exception e) {
                System.err.println("[SIMULATEUR] ✗ Erreur : " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("\n[SIMULATEUR] Arrêt du simulateur.");
    }
    
    
    // ========================================================================
    // MÉTHODES DE GÉNÉRATION DE DONNÉES
    // ========================================================================
    
    /**
     * MÉTHODE : genererDonneesPollution
     * 
     * RÔLE :
     * Génère des données de pollution réalistes pour une zone donnée
     * 
     * LOGIQUE :
     * - Niveau de base : 20-100 µg/m³
     * - Bonus heures de pointe (7h-9h et 17h-19h) : +20 µg/m³
     * - Variation aléatoire pour rendre réaliste
     * 
     * @param zone : La zone géographique
     * @return Objet PollutionData avec les données générées
     */
    private static PollutionData genererDonneesPollution(String zone) {
        
        // Générer un niveau de pollution de base entre 20 et 80
        double niveauBase = 20 + (random.nextDouble() * 60);
        
        // Ajouter un bonus si on est en heure de pointe
        int heure = LocalDateTime.now().getHour();
        boolean heureDePointe = (heure >= 7 && heure <= 9) || (heure >= 17 && heure <= 19);
        
        if (heureDePointe) {
            // En heure de pointe, ajouter 10-20 µg/m³
            niveauBase += 10 + (random.nextDouble() * 10);
            System.out.println("[SIMULATEUR] 🚗 Heure de pointe détectée ! Pollution augmentée");
        }
        
        // Arrondir à 2 décimales
        double niveauFinal = Math.round(niveauBase * 100.0) / 100.0;
        
        // Créer le timestamp au format ISO 8601
        String timestamp = LocalDateTime.now().format(timeFormatter);
        
        // Créer et retourner l'objet PollutionData
        return new PollutionData(zone, niveauFinal, timestamp);
    }
    
    
    // ========================================================================
    // MÉTHODES D'ENVOI HTTP
    // ========================================================================
    
    /**
     * MÉTHODE : envoyerVersService
     * 
     * RÔLE :
     * Envoie le JSON vers le Service REST via HTTP POST
     * 
     * ÉTAPES :
     * 1. Créer une connexion HTTP vers l'URL du service
     * 2. Configurer la méthode POST et les headers
     * 3. Envoyer le JSON dans le body de la requête
     * 4. Lire la réponse du serveur
     * 5. Afficher le résultat
     * 
     * @param jsonData : Le JSON à envoyer
     */
    private static void envoyerVersService(String jsonData) {
        
        HttpURLConnection connection = null;
        
        try {
            // ================================================================
            // ÉTAPE 1 : CRÉER LA CONNEXION HTTP
            // ================================================================
            
            URL url = new URL(SERVICE_URL);
            connection = (HttpURLConnection) url.openConnection();
            
            
            // ================================================================
            // ÉTAPE 2 : CONFIGURER LA REQUÊTE
            // ================================================================
            
            // Méthode HTTP : POST
            connection.setRequestMethod("POST");
            
            // Headers HTTP
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            
            // Activer l'envoi de données
            connection.setDoOutput(true);
            
            
            // ================================================================
            // ÉTAPE 3 : ENVOYER LE JSON
            // ================================================================
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            System.out.println("[SIMULATEUR] 📤 Envoi HTTP POST vers " + SERVICE_URL);
            
            
            // ================================================================
            // ÉTAPE 4 : LIRE LA RÉPONSE
            // ================================================================
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("[SIMULATEUR] ✓ Réponse du serveur : " + responseCode + " OK");
                System.out.println("[SIMULATEUR] ✓ Données envoyées avec succès !");
            } else {
                System.err.println("[SIMULATEUR] ✗ Erreur HTTP : " + responseCode);
            }
            
        } catch (IOException e) {
            System.err.println("[SIMULATEUR] ✗ Erreur lors de l'envoi HTTP : " + e.getMessage());
            System.err.println("[SIMULATEUR] ℹ Vérifiez que le Service REST est bien démarré sur le port 8080");
        } finally {
            // Fermer la connexion
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    
    // ========================================================================
    // MÉTHODES UTILITAIRES
    // ========================================================================
    
    /**
     * MÉTHODE : printHeader
     * 
     * RÔLE :
     * Affiche un joli header dans la console au démarrage
     */
    private static void printHeader() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║        SIMULATEUR DE CAPTEURS DE POLLUTION                 ║");
        System.out.println("║        Système de Gestion du Trafic Urbain                 ║");
        System.out.println("║                                                            ║");
        System.out.println("║        Version : 1.0.0                                     ║");
        System.out.println("║        Intervalle : 15 secondes                            ║");
        System.out.println("║        URL Cible : localhost:8080/api/pollution            ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("\n");
    }
}
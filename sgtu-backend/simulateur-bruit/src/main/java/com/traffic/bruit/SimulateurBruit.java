package com.traffic.bruit;

import com.google.gson.Gson;
import com.traffic.bruit.models.BruitData;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * ============================================================================
 * CLASSE : SimulateurBruit
 * ============================================================================
 * 
 * RÔLE :
 * Simule des capteurs de bruit qui génèrent des données en continu.
 * 
 * FONCTIONNEMENT :
 * 1. Génère des données de bruit aléatoires toutes les 10 secondes
 * 2. Crée un objet BruitData avec ces données
 * 3. Convertit l'objet en JSON avec Gson
 * 4. Se connecte au serveur TCP sur localhost:9999
 * 5. Envoie le JSON via le Socket TCP
 * 6. Répète indéfiniment (boucle infinie)
 * 
 * ZONES SIMULÉES :
 * - Zone_Centre
 * - Zone_Nord
 * - Zone_Sud
 * 
 * PLAGE DE BRUIT :
 * - Minimum : 40 dB (très calme)
 * - Maximum : 90 dB (très bruyant)
 * - Variation réaliste selon l'heure
 * 
 * CONNEXION : localhost:9999 (TCP)
 * 
 * ============================================================================
 */
public class SimulateurBruit {
    
    // ========================================================================
    // CONSTANTES
    // ========================================================================
    
    /**
     * Adresse du serveur TCP
     */
    private static final String SERVER_HOST = "localhost";
    
    /**
     * Port du serveur TCP
     */
    private static final int SERVER_PORT = 5000;
    
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
     * 10 secondes = 10000 ms
     */
    private static final int INTERVAL_MS = 10000;
    
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
     * Lance la boucle infinie de génération et envoi de données
     */
    public static void main(String[] args) {
        
        // Afficher le header de démarrage
        printHeader();
        
        // Compteur de messages envoyés
        int messageCount = 0;
        
        System.out.println("[SIMULATEUR] Démarrage de la simulation...");
        System.out.println("[SIMULATEUR] Envoi de données toutes les " + (INTERVAL_MS / 1000) + " secondes");
        System.out.println("[SIMULATEUR] Zones surveillées : " + String.join(", ", ZONES));
        System.out.println("[SIMULATEUR] Serveur TCP : " + SERVER_HOST + ":" + SERVER_PORT);
        System.out.println("[SIMULATEUR] Appuyez sur Ctrl+C pour arrêter\n");
        
        // ====================================================================
        // BOUCLE INFINIE : GÉNÉRATION ET ENVOI DE DONNÉES
        // ====================================================================
        
        while (true) {
            
            Socket socket = null;
            PrintWriter writer = null;
            
            try {
                // ============================================================
                // ÉTAPE 1 : SE CONNECTER AU SERVEUR TCP
                // ============================================================
                
                socket = new Socket(SERVER_HOST, SERVER_PORT);
                
                // Créer un PrintWriter pour envoyer du texte via le socket
                // true = autoFlush (envoie automatiquement après chaque println)
                writer = new PrintWriter(socket.getOutputStream(), true);
                
                System.out.println("[SIMULATEUR] ✓ Connecté au serveur TCP " + SERVER_HOST + ":" + SERVER_PORT);
                
                
                // ============================================================
                // ÉTAPE 2 : POUR CHAQUE ZONE, GÉNÉRER ET ENVOYER
                // ============================================================
                
                for (String zone : ZONES) {
                    
                    messageCount++;
                    
                    System.out.println("\n========================================");
                    System.out.println("[SIMULATEUR] Message #" + messageCount);
                    System.out.println("========================================");
                    
                    // Étape 2.1 : Générer des données de bruit
                    BruitData data = genererDonneesBruit(zone);
                    
                    // Étape 2.2 : Convertir en JSON
                    String json = gson.toJson(data);
                    
                    System.out.println("[SIMULATEUR] Données générées : " + data);
                    System.out.println("[SIMULATEUR] JSON créé : " + json);
                    
                    // Étape 2.3 : Envoyer via le socket TCP
                    // IMPORTANT : Ajouter \n à la fin pour que le serveur sache où finit le message
                    writer.println(json);
                    
                    System.out.println("[SIMULATEUR] ✓ Message envoyé via TCP !");
                    System.out.println("========================================\n");
                }
                
                
                // ============================================================
                // ÉTAPE 3 : FERMER LA CONNEXION
                // ============================================================
                
                writer.close();
                socket.close();
                
                System.out.println("[SIMULATEUR] ✓ Connexion fermée");
                
                
                // ============================================================
                // ÉTAPE 4 : ATTENDRE AVANT LA PROCHAINE ITÉRATION
                // ============================================================
                
                System.out.println("[SIMULATEUR] ⏳ Attente de " + (INTERVAL_MS / 1000) + " secondes...\n");
                Thread.sleep(INTERVAL_MS);
                
            } catch (IOException e) {
                System.err.println("[SIMULATEUR] ✗ Erreur de connexion TCP : " + e.getMessage());
                System.err.println("[SIMULATEUR] ℹ Vérifiez que le serveur TCP est démarré sur le port " + SERVER_PORT);
                
                // Attendre un peu avant de réessayer
                try {
                    System.out.println("[SIMULATEUR] ⏳ Réessai dans 5 secondes...\n");
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    break;
                }
                
            } catch (InterruptedException e) {
                System.err.println("[SIMULATEUR] ✗ Simulation interrompue");
                break;
                
            } catch (Exception e) {
                System.err.println("[SIMULATEUR] ✗ Erreur : " + e.getMessage());
                e.printStackTrace();
                
            } finally {
                // Fermer proprement les ressources
                try {
                    if (writer != null) {
                        writer.close();
                    }
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException e) {
                    // Ignorer les erreurs de fermeture
                }
            }
        }
        
        System.out.println("\n[SIMULATEUR] Arrêt du simulateur.");
    }
    
    
    // ========================================================================
    // MÉTHODES DE GÉNÉRATION DE DONNÉES
    // ========================================================================
    
    /**
     * MÉTHODE : genererDonneesBruit
     * 
     * RÔLE :
     * Génère des données de bruit réalistes pour une zone donnée
     * 
     * LOGIQUE :
     * - Niveau de base : 40-80 dB
     * - Bonus heures de pointe (7h-9h et 17h-19h) : +5-10 dB
     * - Variation aléatoire pour rendre réaliste
     * 
     * @param zone : La zone géographique
     * @return Objet BruitData avec les données générées
     */
    private static BruitData genererDonneesBruit(String zone) {
        
        // Générer un niveau de bruit de base entre 40 et 75 dB
        double niveauBase = 40 + (random.nextDouble() * 35);
        
        // Ajouter un bonus si on est en heure de pointe
        int heure = LocalDateTime.now().getHour();
        boolean heureDePointe = (heure >= 7 && heure <= 9) || (heure >= 17 && heure <= 19);
        
        if (heureDePointe) {
            // En heure de pointe, ajouter 5-15 dB (plus de trafic = plus de bruit)
            niveauBase += 5 + (random.nextDouble() * 10);
            System.out.println("[SIMULATEUR] 🚗 Heure de pointe détectée ! Bruit augmenté");
        }
        
        // Arrondir à 2 décimales
        double niveauFinal = Math.round(niveauBase * 100.0) / 100.0;
        
        // Créer le timestamp au format ISO 8601
        String timestamp = LocalDateTime.now().format(timeFormatter);
        
        // Créer et retourner l'objet BruitData
        return new BruitData(zone, niveauFinal, timestamp);
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
        System.out.println("║        SIMULATEUR DE CAPTEURS DE BRUIT                     ║");
        System.out.println("║        Système de Gestion du Trafic Urbain                 ║");
        System.out.println("║                                                            ║");
        System.out.println("║        Version : 1.0.0                                     ║");
        System.out.println("║        Intervalle : 10 secondes                            ║");
        System.out.println("║        Connexion : localhost:9999 (TCP)                    ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("\n");
    }
}
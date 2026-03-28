package com.traffic.central;

import com.traffic.central.consumers.BruitConsumer;
import com.traffic.central.consumers.PollutionConsumer;
import com.traffic.central.database.DatabaseManager;

/**
 * ============================================================================
 * CLASSE : ServiceCentral
 * ============================================================================
 * 
 * RÔLE :
 * Point d'entrée du Service Central.
 * Lance les 2 consumers Kafka en parallèle (multithreading).
 * 
 * FONCTIONNEMENT :
 * 1. Initialise la connexion MySQL via DatabaseManager
 * 2. Crée 2 consumers : PollutionConsumer et BruitConsumer
 * 3. Lance chaque consumer dans son propre Thread
 * 4. Les 2 threads tournent en parallèle indéfiniment
 * 5. Chaque consumer lit son topic, analyse, et sauvegarde dans MySQL
 * 
 * ARCHITECTURE :
 * 
 *   ┌─────────────────────────────────────────────────────────────┐
 *   │                    SERVICE CENTRAL                          │
 *   │                                                             │
 *   │   Thread 1: PollutionConsumer  ←→  pollution-topic         │
 *   │        ↓                                                    │
 *   │   [Analyse pollution]                                       │
 *   │        ↓                                                    │
 *   │   Thread 2: BruitConsumer      ←→  bruit-topic             │
 *   │        ↓                                                    │
 *   │   [Analyse bruit]                                           │
 *   │        ↓                                                    │
 *   │   DatabaseManager  →  MySQL (alertes + recommandations)    │
 *   │                                                             │
 *   └─────────────────────────────────────────────────────────────┘
 * 
 * ============================================================================
 */
public class ServiceCentral {
    
    // ========================================================================
    // MÉTHODE PRINCIPALE
    // ========================================================================
    
    public static void main(String[] args) {
        
        // Afficher le header
        printHeader();
        
        System.out.println("[MAIN] Démarrage du Service Central...\n");
        
        try {
            // ================================================================
            // ÉTAPE 1 : INITIALISER LA BASE DE DONNÉES
            // ================================================================
            
            System.out.println("[MAIN] Initialisation de la base de données...");
            DatabaseManager databaseManager = new DatabaseManager();
            System.out.println();
            
            
            // ================================================================
            // ÉTAPE 2 : CRÉER LES CONSUMERS KAFKA
            // ================================================================
            
            System.out.println("[MAIN] Création des Kafka Consumers...");
            
            // Consumer pour la pollution
            PollutionConsumer pollutionConsumer = new PollutionConsumer(databaseManager);
            
            // Consumer pour le bruit
            BruitConsumer bruitConsumer = new BruitConsumer(databaseManager);
            
            System.out.println("[MAIN] ✓ Consumers créés !\n");
            
            
            // ================================================================
            // ÉTAPE 3 : CRÉER LES THREADS
            // ================================================================
            
            System.out.println("[MAIN] Lancement des threads...");
            
            // Thread 1 : Pour le consumer de pollution
            Thread pollutionThread = new Thread(pollutionConsumer);
            pollutionThread.setName("PollutionConsumerThread");
            
            // Thread 2 : Pour le consumer de bruit
            Thread bruitThread = new Thread(bruitConsumer);
            bruitThread.setName("BruitConsumerThread");
            
            
            // ================================================================
            // ÉTAPE 4 : DÉMARRER LES THREADS
            // ================================================================
            
            // Démarrer le thread pollution
            pollutionThread.start();
            System.out.println("[MAIN] ✓ Thread Pollution démarré");
            
            // Démarrer le thread bruit
            bruitThread.start();
            System.out.println("[MAIN] ✓ Thread Bruit démarré");
            
            System.out.println("\n========================================");
            System.out.println("SERVICE CENTRAL EST ACTIF");
            System.out.println("========================================");
            System.out.println("Les 2 consumers Kafka sont en écoute :");
            System.out.println("  - PollutionConsumer sur pollution-topic");
            System.out.println("  - BruitConsumer sur bruit-topic");
            System.out.println("\nAnalyse en temps réel...");
            System.out.println("Alertes et recommandations générées automatiquement.");
            System.out.println("\nAppuyez sur Ctrl+C pour arrêter");
            System.out.println("========================================\n");
            
            
            // ================================================================
            // ÉTAPE 5 : ATTENDRE QUE LES THREADS SE TERMINENT
            // ================================================================
            
            // join() bloque jusqu'à ce que le thread se termine
            // Comme nos threads tournent en boucle infinie, on reste bloqué ici
            pollutionThread.join();
            bruitThread.join();
            
        } catch (Exception e) {
            System.err.println("[MAIN] ✗ ERREUR FATALE : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n[MAIN] Arrêt du Service Central.");
    }
    
    
    // ========================================================================
    // MÉTHODES UTILITAIRES
    // ========================================================================
    
    private static void printHeader() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║        SERVICE CENTRAL                                     ║");
        System.out.println("║        Système de Gestion du Trafic Urbain                 ║");
        System.out.println("║                                                            ║");
        System.out.println("║        Version : 1.0.0                                     ║");
        System.out.println("║        Consumers : 2 (Pollution + Bruit)                   ║");
        System.out.println("║        Database : MySQL                                    ║");
        System.out.println("║        Topics : pollution-topic, bruit-topic               ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("\n");
    }
}
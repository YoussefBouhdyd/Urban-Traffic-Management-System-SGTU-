package com.traffic.pollution;

import com.traffic.pollution.resources.PollutionResource;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * ============================================================================
 * CLASSE : Main
 * ============================================================================
 * 
 * RÔLE :
 * Point d'entrée de l'application Service Pollution REST.
 * Cette classe démarre le serveur HTTP Grizzly avec Jersey.
 * 
 * FONCTIONNEMENT :
 * 1. Configure Jersey avec notre classe PollutionResource
 * 2. Démarre un serveur HTTP sur http://localhost:8080/
 * 3. Attend que l'utilisateur appuie sur Entrée pour arrêter
 * 
 * POUR LANCER :
 * - Clic-droit sur Main.java → Run As → Java Application
 * 
 * POUR ARRÊTER :
 * - Appuyez sur Entrée dans la console Eclipse
 * 
 * ============================================================================
 */
public class Main {
    
    // ========================================================================
    // CONSTANTES
    // ========================================================================
    
    /**
     * URL de base du serveur
     * Le service sera accessible sur http://localhost:8082/
     */
    private static final String BASE_URI = "http://localhost:8082/";
    
    
    // ========================================================================
    // MÉTHODE PRINCIPALE
    // ========================================================================
    
    /**
     * MÉTHODE : main
     * 
     * Point d'entrée de l'application
     * C'est cette méthode qui est exécutée quand on lance le programme
     * 
     * @param args : Arguments de la ligne de commande (non utilisés ici)
     */
    public static void main(String[] args) {
        
        // Afficher le header de démarrage
        printHeader();
        
        try {
            // ================================================================
            // ÉTAPE 1 : CRÉER LE SERVEUR HTTP
            // ================================================================
            
            System.out.println("[MAIN] Démarrage du serveur HTTP...");
            
            // Créer le serveur avec la configuration Jersey
            HttpServer server = startServer();
            
            System.out.println("[MAIN] ✓ Serveur HTTP démarré avec succès !");
            System.out.println("[MAIN] URL de base : " + BASE_URI);
            System.out.println("[MAIN] Endpoints disponibles :");
            System.out.println("[MAIN]   - POST " + BASE_URI + "api/pollution");
            System.out.println("[MAIN]   - GET  " + BASE_URI + "api/pollution/test");
            
            
            // ================================================================
            // ÉTAPE 2 : AFFICHER LES INSTRUCTIONS
            // ================================================================
            
            System.out.println("\n========================================");
            System.out.println("SERVICE POLLUTION REST EST ACTIF");
            System.out.println("========================================");
            System.out.println("Le service écoute sur le port 8080");
            System.out.println("En attente de requêtes HTTP POST...");
            System.out.println("\nPour tester :");
            System.out.println("1. Ouvrez votre navigateur");
            System.out.println("2. Allez sur : http://localhost:8080/api/pollution/test");
            System.out.println("\nAppuyez sur ENTRÉE pour arrêter le serveur...");
            System.out.println("========================================\n");
            
            
            // ================================================================
            // ÉTAPE 3 : GARDER LE SERVEUR EN VIE
            // ================================================================
            
            // Garder le serveur actif en arrière-plan
            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }
            
            
            // ================================================================
            // ÉTAPE 4 : ARRÊTER LE SERVEUR PROPREMENT
            // ================================================================
            
            System.out.println("\n[MAIN] Arrêt du serveur...");
            server.shutdown();
            System.out.println("[MAIN] ✓ Serveur arrêté.");
            System.out.println("[MAIN] Au revoir !");
            
        } catch (InterruptedException e) {
            System.err.println("[MAIN] ✗ Service interrompu :");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[MAIN] ✗ ERREUR lors du démarrage du serveur :");
            e.printStackTrace();
        }
    }
    
    
    // ========================================================================
    // MÉTHODES UTILITAIRES
    // ========================================================================
    
    /**
     * MÉTHODE : startServer
     * 
     * RÔLE :
     * Crée et démarre le serveur HTTP avec la configuration Jersey
     * 
     * FONCTIONNEMENT :
     * 1. Crée une ResourceConfig (configuration Jersey)
     * 2. Enregistre notre classe PollutionResource
     * 3. Démarre le serveur Grizzly avec cette configuration
     * 
     * @return Le serveur HTTP démarré
     */
    private static HttpServer startServer() {
        
        // Créer la configuration Jersey
        // ResourceConfig indique à Jersey quelles classes utiliser
        final ResourceConfig config = new ResourceConfig();
        
        // Enregistrer notre classe PollutionResource
        // Jersey va scanner cette classe et créer les endpoints automatiquement
        config.register(PollutionResource.class);
        
        // Créer et démarrer le serveur HTTP Grizzly
        // GrizzlyHttpServerFactory crée un serveur HTTP léger
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }
    
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
        System.out.println("║        SERVICE POLLUTION REST (JAX-RS)                     ║");
        System.out.println("║        Système de Gestion du Trafic Urbain                 ║");
        System.out.println("║                                                            ║");
        System.out.println("║        Version : 1.0.0                                     ║");
        System.out.println("║        Port    : 8080                                      ║");
        System.out.println("║        Topic   : pollution-topic                           ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("\n");
    }
}
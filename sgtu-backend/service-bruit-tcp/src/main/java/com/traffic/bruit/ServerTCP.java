package com.traffic.bruit;

import com.traffic.bruit.handlers.ClientHandler;
import com.traffic.bruit.kafka.KafkaProducerConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ============================================================================
 * CLASSE : ServerTCP
 * ============================================================================
 * 
 * RÔLE :
 * Serveur TCP qui écoute les connexions des clients (simulateurs de bruit).
 * 
 * FONCTIONNEMENT :
 * 1. Crée un ServerSocket qui écoute sur le port 9999
 * 2. Attend les connexions des clients en boucle infinie
 * 3. Pour chaque client connecté :
 *    - Crée un nouveau Thread avec ClientHandler
 *    - Le Thread gère la communication avec ce client
 * 4. Continue à accepter de nouveaux clients (multithreading)
 * 
 * PORT : 9999
 * PROTOCOLE : TCP
 * FORMAT : JSON terminé par \n (newline)
 * 
 * MULTITHREADING :
 * Le serveur peut gérer plusieurs clients simultanément.
 * Chaque client a son propre Thread (ClientHandler).
 * 
 * POUR ARRÊTER :
 * Appuyez sur Ctrl+C dans la console Eclipse
 * 
 * ============================================================================
 */
public class ServerTCP {
    
    // ========================================================================
    // CONSTANTES
    // ========================================================================
    
    /**
     * Port d'écoute du serveur TCP
     */
    private static final int PORT = 5000;
    
    /**
     * Kafka Producer partagé entre tous les clients
     */
    private static KafkaProducerConfig kafkaProducer;
    
    
    // ========================================================================
    // MÉTHODE PRINCIPALE
    // ========================================================================
    
    /**
     * MÉTHODE : main
     * 
     * Point d'entrée du serveur TCP
     * Lance le serveur et attend les connexions
     */
    public static void main(String[] args) {
        
        // Afficher le header de démarrage
        printHeader();
        
        ServerSocket serverSocket = null;
        
        try {
            // ================================================================
            // ÉTAPE 1 : INITIALISER KAFKA PRODUCER
            // ================================================================
            
            System.out.println("[SERVEUR] Initialisation du Kafka Producer...");
            kafkaProducer = new KafkaProducerConfig();
            System.out.println("[SERVEUR] ✓ Kafka Producer initialisé !\n");
            
            
            // ================================================================
            // ÉTAPE 2 : CRÉER LE SERVEUR TCP
            // ================================================================
            
            System.out.println("[SERVEUR] Démarrage du serveur TCP...");
            
            // Créer le ServerSocket qui écoute sur le port 9999
            serverSocket = new ServerSocket(PORT);
            
            System.out.println("[SERVEUR] ✓ Serveur TCP démarré avec succès !");
            System.out.println("[SERVEUR] Port d'écoute : " + PORT);
            System.out.println("[SERVEUR] Topic Kafka : bruit-topic");
            
            System.out.println("\n========================================");
            System.out.println("SERVEUR BRUIT TCP EST ACTIF");
            System.out.println("========================================");
            System.out.println("Le serveur écoute sur le port " + PORT);
            System.out.println("En attente de connexions des clients...");
            System.out.println("\nPour tester :");
            System.out.println("1. Lancez le simulateur de bruit");
            System.out.println("2. Le simulateur se connectera automatiquement");
            System.out.println("\nAppuyez sur Ctrl+C pour arrêter le serveur");
            System.out.println("========================================\n");
            
            
            // ================================================================
            // ÉTAPE 3 : BOUCLE INFINIE - ACCEPTER LES CLIENTS
            // ================================================================
            
            // Compteur de clients connectés
            int clientCount = 0;
            
            while (true) {
                // Attendre qu'un client se connecte (bloquant)
                // accept() bloque jusqu'à ce qu'un client se connecte
                Socket clientSocket = serverSocket.accept();
                
                clientCount++;
                
                System.out.println("\n[SERVEUR] ✓ Nouveau client connecté ! (Total : " + clientCount + ")");
                System.out.println("[SERVEUR] Adresse IP : " + clientSocket.getInetAddress().getHostAddress());
                System.out.println("[SERVEUR] Port : " + clientSocket.getPort());
                
                // ============================================================
                // ÉTAPE 4 : CRÉER UN THREAD POUR GÉRER CE CLIENT
                // ============================================================
                
                // Créer un ClientHandler pour gérer ce client
                ClientHandler handler = new ClientHandler(clientSocket, kafkaProducer);
                
                // Créer un Thread qui exécutera le ClientHandler
                Thread clientThread = new Thread(handler);
                
                // Donner un nom au Thread (pour le debugging)
                clientThread.setName("ClientHandler-" + clientCount);
                
                // Démarrer le Thread
                // Le Thread va exécuter la méthode run() du ClientHandler
                clientThread.start();
                
                System.out.println("[SERVEUR] Thread '" + clientThread.getName() + "' démarré pour gérer le client");
                System.out.println("[SERVEUR] Le serveur continue à écouter pour d'autres clients...\n");
                
                // Le serveur revient immédiatement à accept() pour attendre
                // le prochain client, pendant que le Thread gère le client actuel
            }
            
        } catch (IOException e) {
            System.err.println("\n[SERVEUR] ✗ ERREUR FATALE : " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            // ================================================================
            // ÉTAPE 5 : NETTOYAGE ET FERMETURE
            // ================================================================
            
            System.out.println("\n[SERVEUR] Arrêt du serveur...");
            
            // Fermer le ServerSocket
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                    System.out.println("[SERVEUR] ✓ ServerSocket fermé.");
                } catch (IOException e) {
                    System.err.println("[SERVEUR] ✗ Erreur lors de la fermeture : " + e.getMessage());
                }
            }
            
            // Fermer le Kafka Producer
            if (kafkaProducer != null) {
                kafkaProducer.close();
            }
            
            System.out.println("[SERVEUR] Au revoir !");
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
        System.out.println("║        SERVICE BRUIT TCP (SOCKET TCP)                      ║");
        System.out.println("║        Système de Gestion du Trafic Urbain                 ║");
        System.out.println("║                                                            ║");
        System.out.println("║        Version : 1.0.0                                     ║");
        System.out.println("║        Port    : 9999                                      ║");
        System.out.println("║        Topic   : bruit-topic                               ║");
        System.out.println("║        Protocole : TCP                                     ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("\n");
    }
}
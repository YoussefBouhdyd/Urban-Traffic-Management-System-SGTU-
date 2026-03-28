package com.traffic.bruit.handlers;

import com.google.gson.Gson;
import com.traffic.bruit.kafka.KafkaProducerConfig;
import com.traffic.bruit.models.BruitData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * ============================================================================
 * CLASSE : ClientHandler
 * ============================================================================
 * 
 * RÔLE :
 * Gère la communication avec UN client TCP dans un Thread séparé.
 * 
 * FONCTIONNEMENT :
 * 1. Reçoit une connexion Socket d'un client
 * 2. Lit les messages JSON ligne par ligne
 * 3. Parse le JSON en objet BruitData
 * 4. Valide les données
 * 5. Envoie vers Kafka via KafkaProducerConfig
 * 6. Ferme la connexion proprement
 * 
 * MULTITHREADING :
 * Chaque client est géré dans son propre Thread.
 * Cela permet au serveur d'accepter plusieurs clients simultanément.
 * 
 * FORMAT DES MESSAGES :
 * Le client envoie un JSON suivi d'un retour à la ligne (\n)
 * Exemple : {"zone_id":"Zone_Centre","niveau_decibels":75.5,"timestamp":"..."}\n
 * 
 * ============================================================================
 */
public class ClientHandler implements Runnable {
    
    // ========================================================================
    // ATTRIBUTS
    // ========================================================================
    
    /**
     * Le socket de connexion avec le client
     */
    private Socket clientSocket;
    
    /**
     * Le Producer Kafka pour envoyer les messages
     */
    private KafkaProducerConfig kafkaProducer;
    
    /**
     * Objet Gson pour parser le JSON
     */
    private Gson gson;
    
    /**
     * Compteur de clients (pour affichage)
     */
    private static int clientCounter = 0;
    
    /**
     * ID de ce client
     */
    private int clientId;
    
    
    // ========================================================================
    // CONSTRUCTEUR
    // ========================================================================
    
    /**
     * CONSTRUCTEUR
     * 
     * @param clientSocket : La connexion Socket avec le client
     * @param kafkaProducer : Le Producer Kafka partagé
     */
    public ClientHandler(Socket clientSocket, KafkaProducerConfig kafkaProducer) {
        this.clientSocket = clientSocket;
        this.kafkaProducer = kafkaProducer;
        this.gson = new Gson();
        
        // Incrémenter le compteur de clients
        clientCounter++;
        this.clientId = clientCounter;
        
        System.out.println("[CLIENT #" + clientId + "] Nouveau client connecté : " + 
                          clientSocket.getInetAddress().getHostAddress());
    }
    
    
    // ========================================================================
    // MÉTHODE RUN (EXÉCUTÉE DANS LE THREAD)
    // ========================================================================
    
    /**
     * MÉTHODE : run
     * 
     * RÔLE :
     * Code exécuté dans le Thread dédié à ce client.
     * Lit les messages du client en boucle jusqu'à déconnexion.
     */
    @Override
    public void run() {
        
        BufferedReader reader = null;
        
        try {
            // ================================================================
            // ÉTAPE 1 : CRÉER UN READER POUR LIRE LES DONNÉES DU CLIENT
            // ================================================================
            
            // InputStreamReader lit les bytes du socket
            // BufferedReader permet de lire ligne par ligne
            reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), "UTF-8")
            );
            
            System.out.println("[CLIENT #" + clientId + "] Prêt à recevoir des données...");
            
            
            // ================================================================
            // ÉTAPE 2 : BOUCLE DE LECTURE DES MESSAGES
            // ================================================================
            
            String line;
            int messageCount = 0;
            
            // Lire ligne par ligne tant que le client est connecté
            while ((line = reader.readLine()) != null) {
                
                messageCount++;
                
                System.out.println("\n========================================");
                System.out.println("[CLIENT #" + clientId + "] Message #" + messageCount + " reçu");
                System.out.println("========================================");
                System.out.println("[CLIENT #" + clientId + "] JSON reçu : " + line);
                
                // Traiter le message
                traiterMessage(line);
                
                System.out.println("========================================\n");
            }
            
            // Si on arrive ici, le client s'est déconnecté
            System.out.println("[CLIENT #" + clientId + "] Client déconnecté.");
            System.out.println("[CLIENT #" + clientId + "] Total messages reçus : " + messageCount);
            
        } catch (IOException e) {
            System.err.println("[CLIENT #" + clientId + "] ✗ Erreur de communication : " + 
                              e.getMessage());
        } finally {
            // ================================================================
            // ÉTAPE 3 : FERMER PROPREMENT LA CONNEXION
            // ================================================================
            
            try {
                if (reader != null) {
                    reader.close();
                }
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
                System.out.println("[CLIENT #" + clientId + "] Connexion fermée.");
            } catch (IOException e) {
                System.err.println("[CLIENT #" + clientId + "] ✗ Erreur lors de la fermeture : " + 
                                  e.getMessage());
            }
        }
    }
    
    
    // ========================================================================
    // MÉTHODES PRIVÉES
    // ========================================================================
    
    /**
     * MÉTHODE : traiterMessage
     * 
     * RÔLE :
     * Parse le JSON, valide les données, et envoie vers Kafka
     * 
     * @param jsonLine : La ligne JSON reçue du client
     */
    private void traiterMessage(String jsonLine) {
        
        try {
            // ================================================================
            // ÉTAPE 1 : PARSER LE JSON → Objet BruitData
            // ================================================================
            
            BruitData data = gson.fromJson(jsonLine, BruitData.class);
            
            System.out.println("[CLIENT #" + clientId + "] Données parsées : " + data);
            
            
            // ================================================================
            // ÉTAPE 2 : VALIDER LES DONNÉES
            // ================================================================
            
            // Vérifier que zone_id n'est pas vide
            if (data.getZone_id() == null || data.getZone_id().trim().isEmpty()) {
                System.err.println("[CLIENT #" + clientId + "] ✗ Erreur : zone_id est vide !");
                return;
            }
            
            // Vérifier que niveau_decibels est dans une plage valide (0-120 dB)
            if (data.getNiveau_decibels() < 0 || data.getNiveau_decibels() > 120) {
                System.err.println("[CLIENT #" + clientId + "] ✗ Erreur : niveau_decibels invalide (" + 
                                  data.getNiveau_decibels() + ")");
                return;
            }
            
            // Vérifier que timestamp n'est pas vide
            if (data.getTimestamp() == null || data.getTimestamp().trim().isEmpty()) {
                System.err.println("[CLIENT #" + clientId + "] ✗ Erreur : timestamp est vide !");
                return;
            }
            
            System.out.println("[CLIENT #" + clientId + "] ✓ Validation réussie !");
            
            
            // ================================================================
            // ÉTAPE 3 : ENVOYER VERS KAFKA
            // ================================================================
            
            // Utiliser zone_id comme clé et le JSON complet comme valeur
            kafkaProducer.sendMessage(data.getZone_id(), jsonLine);
            
            System.out.println("[CLIENT #" + clientId + "] ✓ Message envoyé vers Kafka !");
            
        } catch (Exception e) {
            System.err.println("[CLIENT #" + clientId + "] ✗ Erreur lors du traitement : " + 
                              e.getMessage());
            e.printStackTrace();
        }
    }
}
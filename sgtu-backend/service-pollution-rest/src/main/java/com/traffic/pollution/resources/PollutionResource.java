package com.traffic.pollution.resources;

import com.google.gson.Gson;
import com.traffic.pollution.kafka.KafkaProducerConfig;
import com.traffic.pollution.models.PollutionData;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * ============================================================================
 * CLASSE : PollutionResource
 * ============================================================================
 * 
 * RÔLE :
 * Cette classe est un ENDPOINT REST qui reçoit les données de pollution.
 * C'est ici que les requêtes HTTP arrivent depuis le simulateur.
 * 
 * TECHNOLOGIE : JAX-RS (Jersey)
 * 
 * ANNOTATIONS :
 * - @Path : Définit le chemin de l'URL (ici : /api/pollution)
 * - @POST : Indique que cette méthode répond aux requêtes HTTP POST
 * - @Consumes : Type de données acceptées (ici : JSON)
 * - @Produces : Type de données retournées (ici : JSON)
 * 
 * FLUX DE DONNÉES :
 * 1. Le simulateur envoie une requête POST avec un JSON
 * 2. Jersey reçoit la requête et appelle la méthode receivePollutionData()
 * 3. On parse le JSON en objet PollutionData
 * 4. On valide les données
 * 5. On envoie vers Kafka via KafkaProducerConfig
 * 6. On retourne une réponse HTTP au simulateur
 * 
 * URL COMPLÈTE : http://localhost:8080/api/pollution
 * 
 * ============================================================================
 */
@Path("/api/pollution")
public class PollutionResource {
    
    // ========================================================================
    // ATTRIBUTS
    // ========================================================================
    
    /**
     * Le Producer Kafka qui va envoyer les messages vers pollution-topic
     */
    private KafkaProducerConfig kafkaProducer;
    
    /**
     * L'objet Gson pour convertir JSON ↔ Objets Java
     */
    private Gson gson;
    
    
    // ========================================================================
    // CONSTRUCTEUR
    // ========================================================================
    
    /**
     * CONSTRUCTEUR
     * Initialise le Producer Kafka et Gson au démarrage du service
     */
    public PollutionResource() {
        // Créer le Producer Kafka
        this.kafkaProducer = new KafkaProducerConfig();
        
        // Créer l'objet Gson pour manipuler le JSON
        this.gson = new Gson();
        
        System.out.println("[REST] PollutionResource initialisé !");
    }
    
    
    // ========================================================================
    // ENDPOINTS REST
    // ========================================================================
    
    /**
     * ENDPOINT : POST /api/pollution
     * 
     * RÔLE :
     * Reçoit les données de pollution en JSON, les valide, et les envoie vers Kafka
     * 
     * MÉTHODE HTTP : POST
     * URL : http://localhost:8080/api/pollution
     * BODY : JSON avec les données de pollution
     * 
     * EXEMPLE DE REQUÊTE :
     * POST http://localhost:8080/api/pollution
     * Content-Type: application/json
     * 
     * {
     *   "zone_id": "Zone_Centre",
     *   "niveau_co2": 68.5,
     *   "timestamp": "2026-03-15T14:30:00"
     * }
     * 
     * @param jsonData : Le JSON reçu en tant que String
     * @return Response HTTP avec un message de confirmation ou d'erreur
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response receivePollutionData(String jsonData) {
        
        System.out.println("\n========================================");
        System.out.println("[REST] Nouvelle requête POST reçue");
        System.out.println("[REST] JSON reçu : " + jsonData);
        System.out.println("========================================");
        
        try {
            // ================================================================
            // ÉTAPE 1 : PARSER LE JSON → Objet PollutionData
            // ================================================================
            
            // Gson convertit automatiquement le JSON en objet PollutionData
            PollutionData data = gson.fromJson(jsonData, PollutionData.class);
            
            System.out.println("[REST] Données parsées : " + data);
            
            
            // ================================================================
            // ÉTAPE 2 : VALIDER LES DONNÉES
            // ================================================================
            
            // Vérifier que zone_id n'est pas vide
            if (data.getZone_id() == null || data.getZone_id().trim().isEmpty()) {
                System.err.println("[REST] ✗ Erreur : zone_id est vide !");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\",\"message\":\"zone_id est requis\"}")
                        .build();
            }
            
            // Vérifier que niveau_co2 est dans une plage valide (0-200)
            if (data.getNiveau_co2() < 0 || data.getNiveau_co2() > 200) {
                System.err.println("[REST] ✗ Erreur : niveau_co2 invalide (" + data.getNiveau_co2() + ")");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\",\"message\":\"niveau_co2 doit être entre 0 et 200\"}")
                        .build();
            }
            
            // Vérifier que timestamp n'est pas vide
            if (data.getTimestamp() == null || data.getTimestamp().trim().isEmpty()) {
                System.err.println("[REST] ✗ Erreur : timestamp est vide !");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":\"error\",\"message\":\"timestamp est requis\"}")
                        .build();
            }
            
            System.out.println("[REST] ✓ Validation réussie !");
            
            
            // ================================================================
            // ÉTAPE 3 : ENVOYER VERS KAFKA
            // ================================================================
            
            // Utiliser zone_id comme clé et le JSON complet comme valeur
            kafkaProducer.sendMessage(data.getZone_id(), jsonData);
            
            
            // ================================================================
            // ÉTAPE 4 : RETOURNER UNE RÉPONSE HTTP 200 OK
            // ================================================================
            
            String responseJson = String.format(
                "{\"status\":\"success\",\"message\":\"Données de pollution reçues et envoyées vers Kafka\",\"zone_id\":\"%s\",\"niveau_co2\":%.2f}",
                data.getZone_id(),
                data.getNiveau_co2()
            );
            
            System.out.println("[REST] ✓ Réponse envoyée au client : " + responseJson);
            System.out.println("========================================\n");
            
            return Response.ok(responseJson).build();
            
        } catch (Exception e) {
            // ================================================================
            // GESTION DES ERREURS
            // ================================================================
            
            System.err.println("[REST] ✗ ERREUR lors du traitement : " + e.getMessage());
            e.printStackTrace();
            
            String errorJson = String.format(
                "{\"status\":\"error\",\"message\":\"Erreur serveur : %s\"}",
                e.getMessage()
            );
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorJson)
                    .build();
        }
    }
    
    
    /**
     * ENDPOINT : GET /api/pollution/test
     * 
     * RÔLE :
     * Endpoint de test pour vérifier que le service REST fonctionne
     * 
     * MÉTHODE HTTP : GET
     * URL : http://localhost:8080/api/pollution/test
     * 
     * UTILISATION :
     * Ouvrez un navigateur et allez sur http://localhost:8080/api/pollution/test
     * Vous devriez voir un message JSON indiquant que le service est actif
     * 
     * @return Message de test en JSON
     */
    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testEndpoint() {
        System.out.println("[REST] Endpoint de test appelé");
        
        String testResponse = "{\"status\":\"ok\",\"message\":\"Service Pollution REST est actif\",\"timestamp\":\"" + 
                              java.time.LocalDateTime.now() + "\"}";
        
        return Response.ok(testResponse).build();
    }
}
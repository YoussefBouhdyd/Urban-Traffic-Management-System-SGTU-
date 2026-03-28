package com.traffic.central.consumers;

import com.google.gson.Gson;
import com.traffic.central.database.DatabaseManager;
import com.traffic.central.models.PollutionData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * ============================================================================
 * CLASSE : PollutionConsumer
 * ============================================================================
 * 
 * RÔLE :
 * Consumer Kafka qui lit les messages du topic "pollution-topic"
 * 
 * FONCTIONNEMENT :
 * 1. Se connecte à Kafka et s'abonne au topic "pollution-topic"
 * 2. Lit les messages en boucle infinie
 * 3. Parse le JSON en objet PollutionData
 * 4. Analyse les données (détection seuils)
 * 5. Sauvegarde dans MySQL
 * 6. Génère des alertes et recommandations si nécessaire
 * 
 * SEUILS DE POLLUTION :
 * - < 50 µg/m³ : NORMAL (pas d'alerte)
 * - 50-80 µg/m³ : MOYEN (alerte moyenne)
 * - > 80 µg/m³ : CRITIQUE (alerte haute + recommandation)
 * 
 * ============================================================================
 */
public class PollutionConsumer implements Runnable {
    
    // ========================================================================
    // ATTRIBUTS
    // ========================================================================
    
    /**
     * Le consumer Kafka
     */
    private KafkaConsumer<String, String> consumer;
    
    /**
     * Le gestionnaire de base de données
     */
    private DatabaseManager databaseManager;
    
    /**
     * Objet Gson pour parser le JSON
     */
    private Gson gson;
    
    /**
     * Nom du topic Kafka
     */
    private static final String TOPIC_NAME = "pollution-topic";
    
    /**
     * Seuil d'alerte pour la pollution (en µg/m³)
     */
    private static final double SEUIL_ALERTE_POLLUTION = 80.0;
    
    /**
     * Seuil moyen pour la pollution (en µg/m³)
     */
    private static final double SEUIL_MOYEN_POLLUTION = 50.0;
    
    
    // ========================================================================
    // CONSTRUCTEUR
    // ========================================================================
    
    /**
     * CONSTRUCTEUR
     * 
     * @param databaseManager : Le gestionnaire de base de données partagé
     */
    public PollutionConsumer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.gson = new Gson();
        
        // Initialiser le consumer Kafka
        initKafkaConsumer();
    }
    
    
    // ========================================================================
    // INITIALISATION
    // ========================================================================
    
    /**
     * MÉTHODE : initKafkaConsumer
     * 
     * RÔLE :
     * Initialise la configuration du Consumer Kafka
     */
    private void initKafkaConsumer() {
        
        // Configuration Kafka
        Properties props = new Properties();
        
        // Adresse du broker Kafka
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        // Group ID : identifiant du groupe de consumers
        // Tous les consumers avec le même group_id partagent les messages
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "service-central-group");
        
        // Désérialiseur pour la clé (convertit bytes → String)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
                  StringDeserializer.class.getName());
        
        // Désérialiseur pour la valeur (convertit bytes → String)
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
                  StringDeserializer.class.getName());
        
        // Auto offset reset : Lire depuis le début si pas d'offset sauvegardé
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        // Créer le consumer
        this.consumer = new KafkaConsumer<>(props);
        
        // S'abonner au topic pollution-topic
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        
        System.out.println("[POLLUTION CONSUMER] ✓ Consumer Kafka initialisé");
        System.out.println("[POLLUTION CONSUMER] Topic : " + TOPIC_NAME);
        System.out.println("[POLLUTION CONSUMER] Group ID : service-central-group");
    }
    
    
    // ========================================================================
    // MÉTHODE RUN (EXÉCUTÉE DANS LE THREAD)
    // ========================================================================
    
    /**
     * MÉTHODE : run
     * 
     * RÔLE :
     * Boucle infinie qui consomme les messages Kafka
     */
    @Override
    public void run() {
        
        System.out.println("[POLLUTION CONSUMER] 🚀 Démarrage de la consommation...");
        System.out.println("[POLLUTION CONSUMER] En attente de messages sur " + TOPIC_NAME + "...\n");
        
        int messageCount = 0;
        
        try {
            // ================================================================
            // BOUCLE INFINIE : CONSOMMER LES MESSAGES
            // ================================================================
            
            while (true) {
                
                // Poll : récupérer les messages disponibles
                // Timeout de 1000ms = attend max 1 seconde pour des messages
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                // Traiter chaque message reçu
                for (ConsumerRecord<String, String> record : records) {
                    
                    messageCount++;
                    
                    System.out.println("\n========================================");
                    System.out.println("[POLLUTION CONSUMER] Message #" + messageCount + " reçu");
                    System.out.println("========================================");
                    System.out.println("[POLLUTION CONSUMER] Topic : " + record.topic());
                    System.out.println("[POLLUTION CONSUMER] Partition : " + record.partition());
                    System.out.println("[POLLUTION CONSUMER] Offset : " + record.offset());
                    System.out.println("[POLLUTION CONSUMER] Clé : " + record.key());
                    System.out.println("[POLLUTION CONSUMER] Valeur : " + record.value());
                    
                    // Traiter le message
                    traiterMessage(record.value());
                    
                    System.out.println("========================================\n");
                }
            }
            
        } catch (Exception e) {
            System.err.println("[POLLUTION CONSUMER] ✗ Erreur : " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            // Fermer le consumer proprement
            consumer.close();
            System.out.println("[POLLUTION CONSUMER] Consumer fermé.");
        }
    }
    
    
    // ========================================================================
    // TRAITEMENT DES MESSAGES
    // ========================================================================
    
    /**
     * MÉTHODE : traiterMessage
     * 
     * RÔLE :
     * Parse le JSON, analyse les données, sauvegarde, génère alertes
     * 
     * @param jsonMessage : Le message JSON reçu de Kafka
     */
    private void traiterMessage(String jsonMessage) {
        
        try {
            // ================================================================
            // ÉTAPE 1 : PARSER LE JSON → Objet PollutionData
            // ================================================================
            
            PollutionData data = gson.fromJson(jsonMessage, PollutionData.class);
            
            System.out.println("[POLLUTION CONSUMER] Données parsées : " + data);
            
            
            // ================================================================
            // ÉTAPE 2 : SAUVEGARDER DANS MYSQL (table pollution)
            // ================================================================
            
            databaseManager.savePollutionData(data);
            
            
            // ================================================================
            // ÉTAPE 3 : ANALYSER LES DONNÉES (détecter les seuils)
            // ================================================================
            
            analyserPollution(data);
            
        } catch (Exception e) {
            System.err.println("[POLLUTION CONSUMER] ✗ Erreur lors du traitement : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    /**
     * MÉTHODE : analyserPollution
     * 
     * RÔLE :
     * Analyse les niveaux de pollution et génère des alertes si nécessaire
     * 
     * LOGIQUE :
     * - < 50 µg/m³ : NORMAL
     * - 50-80 µg/m³ : MOYEN (alerte moyenne)
     * - > 80 µg/m³ : CRITIQUE (alerte haute + recommandation)
     * 
     * @param data : Les données de pollution à analyser
     */
    private void analyserPollution(PollutionData data) {
        
        double niveau = data.getNiveau_co2();
        String zone = data.getZone_id();
        
        System.out.println("[POLLUTION CONSUMER] 🔍 Analyse : " + zone + " = " + niveau + " µg/m³");
        
        // ====================================================================
        // CAS 1 : POLLUTION CRITIQUE (> 80 µg/m³)
        // ====================================================================
        
        if (niveau > SEUIL_ALERTE_POLLUTION) {
            
            System.out.println("[POLLUTION CONSUMER] 🚨 ALERTE POLLUTION CRITIQUE !");
            
            // Créer l'alerte dans la base de données
            String message = String.format(
                "Pollution critique détectée dans %s : %.2f µg/m³ (seuil: %.0f)",
                zone, niveau, SEUIL_ALERTE_POLLUTION
            );
            
            int alerteId = databaseManager.createAlerte(
                "POLLUTION",          // type_alerte
                zone,                 // zone_id
                "HAUTE",              // niveau_gravite
                message               // message
            );
            
            // Créer une recommandation associée
            if (alerteId > 0) {
                String recommandation = "Réduire le trafic dans la zone " + zone + 
                                      ". Mettre en place des restrictions de circulation.";
                databaseManager.createRecommandation(alerteId, recommandation);
            }
        }
        
        // ====================================================================
        // CAS 2 : POLLUTION MOYENNE (50-80 µg/m³)
        // ====================================================================
        
        else if (niveau > SEUIL_MOYEN_POLLUTION) {
            
            System.out.println("[POLLUTION CONSUMER] ⚠️ Pollution moyenne détectée");
            
            String message = String.format(
                "Pollution moyenne dans %s : %.2f µg/m³",
                zone, niveau
            );
            
            databaseManager.createAlerte(
                "POLLUTION",
                zone,
                "MOYENNE",
                message
            );
        }
        
        // ====================================================================
        // CAS 3 : POLLUTION NORMALE (< 50 µg/m³)
        // ====================================================================
        
        else {
            System.out.println("[POLLUTION CONSUMER] ✓ Niveau normal");
        }
    }
}
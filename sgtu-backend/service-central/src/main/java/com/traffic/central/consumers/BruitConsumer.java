package com.traffic.central.consumers;

import com.google.gson.Gson;
import com.traffic.central.database.DatabaseManager;
import com.traffic.central.models.BruitData;
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
 * CLASSE : BruitConsumer
 * ============================================================================
 * 
 * RÔLE :
 * Consumer Kafka qui lit les messages du topic "bruit-topic"
 * 
 * FONCTIONNEMENT :
 * 1. Se connecte à Kafka et s'abonne au topic "bruit-topic"
 * 2. Lit les messages en boucle infinie
 * 3. Parse le JSON en objet BruitData
 * 4. Analyse les données (détection seuils)
 * 5. Sauvegarde dans MySQL
 * 6. Génère des alertes et recommandations si nécessaire
 * 
 * SEUILS DE BRUIT :
 * - < 70 dB : NORMAL (pas d'alerte)
 * - 70-85 dB : MOYEN (alerte moyenne)
 * - > 85 dB : CRITIQUE (alerte haute + recommandation)
 * 
 * ============================================================================
 */
public class BruitConsumer implements Runnable {
    
    // ========================================================================
    // ATTRIBUTS
    // ========================================================================
    
    private KafkaConsumer<String, String> consumer;
    private DatabaseManager databaseManager;
    private Gson gson;
    
    private static final String TOPIC_NAME = "bruit-topic";
    private static final double SEUIL_ALERTE_BRUIT = 85.0;
    private static final double SEUIL_MOYEN_BRUIT = 70.0;
    
    
    // ========================================================================
    // CONSTRUCTEUR
    // ========================================================================
    
    public BruitConsumer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.gson = new Gson();
        initKafkaConsumer();
    }
    
    
    // ========================================================================
    // INITIALISATION
    // ========================================================================
    
    private void initKafkaConsumer() {
        
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "service-central-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
                  StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
                  StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        
        System.out.println("[BRUIT CONSUMER] ✓ Consumer Kafka initialisé");
        System.out.println("[BRUIT CONSUMER] Topic : " + TOPIC_NAME);
        System.out.println("[BRUIT CONSUMER] Group ID : service-central-group");
    }
    
    
    // ========================================================================
    // MÉTHODE RUN
    // ========================================================================
    
    @Override
    public void run() {
        
        System.out.println("[BRUIT CONSUMER] 🚀 Démarrage de la consommation...");
        System.out.println("[BRUIT CONSUMER] En attente de messages sur " + TOPIC_NAME + "...\n");
        
        int messageCount = 0;
        
        try {
            while (true) {
                
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : records) {
                    
                    messageCount++;
                    
                    System.out.println("\n========================================");
                    System.out.println("[BRUIT CONSUMER] Message #" + messageCount + " reçu");
                    System.out.println("========================================");
                    System.out.println("[BRUIT CONSUMER] Topic : " + record.topic());
                    System.out.println("[BRUIT CONSUMER] Partition : " + record.partition());
                    System.out.println("[BRUIT CONSUMER] Offset : " + record.offset());
                    System.out.println("[BRUIT CONSUMER] Clé : " + record.key());
                    System.out.println("[BRUIT CONSUMER] Valeur : " + record.value());
                    
                    traiterMessage(record.value());
                    
                    System.out.println("========================================\n");
                }
            }
            
        } catch (Exception e) {
            System.err.println("[BRUIT CONSUMER] ✗ Erreur : " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            consumer.close();
            System.out.println("[BRUIT CONSUMER] Consumer fermé.");
        }
    }
    
    
    // ========================================================================
    // TRAITEMENT DES MESSAGES
    // ========================================================================
    
    private void traiterMessage(String jsonMessage) {
        
        try {
            // Parser le JSON
            BruitData data = gson.fromJson(jsonMessage, BruitData.class);
            
            System.out.println("[BRUIT CONSUMER] Données parsées : " + data);
            
            // Sauvegarder dans MySQL
            databaseManager.saveBruitData(data);
            
            // Analyser les données
            analyserBruit(data);
            
        } catch (Exception e) {
            System.err.println("[BRUIT CONSUMER] ✗ Erreur lors du traitement : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    private void analyserBruit(BruitData data) {
        
        double niveau = data.getNiveau_decibels();
        String zone = data.getZone_id();
        
        System.out.println("[BRUIT CONSUMER] 🔍 Analyse : " + zone + " = " + niveau + " dB");
        
        // ====================================================================
        // CAS 1 : BRUIT CRITIQUE (> 85 dB)
        // ====================================================================
        
        if (niveau > SEUIL_ALERTE_BRUIT) {
            
            System.out.println("[BRUIT CONSUMER] 🚨 ALERTE BRUIT CRITIQUE !");
            
            String message = String.format(
                "Bruit critique détecté dans %s : %.2f dB (seuil: %.0f)",
                zone, niveau, SEUIL_ALERTE_BRUIT
            );
            
            int alerteId = databaseManager.createAlerte(
                "BRUIT",
                zone,
                "HAUTE",
                message
            );
            
            if (alerteId > 0) {
                String recommandation = "Contrôler les sources de bruit dans la zone " + zone + 
                                      ". Renforcer la surveillance sonore.";
                databaseManager.createRecommandation(alerteId, recommandation);
            }
        }
        
        // ====================================================================
        // CAS 2 : BRUIT MOYEN (70-85 dB)
        // ====================================================================
        
        else if (niveau > SEUIL_MOYEN_BRUIT) {
            
            System.out.println("[BRUIT CONSUMER] ⚠️ Bruit moyen détecté");
            
            String message = String.format(
                "Bruit moyen dans %s : %.2f dB",
                zone, niveau
            );
            
            databaseManager.createAlerte(
                "BRUIT",
                zone,
                "MOYENNE",
                message
            );
        }
        
        // ====================================================================
        // CAS 3 : BRUIT NORMAL (< 70 dB)
        // ====================================================================
        
        else {
            System.out.println("[BRUIT CONSUMER] ✓ Niveau normal");
        }
    }
}
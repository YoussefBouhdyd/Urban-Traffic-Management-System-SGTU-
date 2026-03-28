package com.traffic.bruit.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * ============================================================================
 * CLASSE : KafkaProducerConfig
 * ============================================================================
 * 
 * RÔLE :
 * Configure et gère le Producer Kafka pour le Service Bruit TCP.
 * 
 * TOPIC CIBLE : "bruit-topic"
 * 
 * FONCTIONNEMENT :
 * Identique au Producer du Service Pollution, mais envoie vers "bruit-topic"
 * 
 * ============================================================================
 */
public class KafkaProducerConfig {
    
    // ========================================================================
    // ATTRIBUTS
    // ========================================================================
    
    /**
     * L'objet KafkaProducer qui envoie les messages
     */
    private KafkaProducer<String, String> producer;
    
    /**
     * Nom du topic Kafka
     */
    private static final String TOPIC_NAME = "bruit-topic";
    
    
    // ========================================================================
    // CONSTRUCTEUR
    // ========================================================================
    
    /**
     * CONSTRUCTEUR
     * Initialise le Producer Kafka avec toutes les configurations
     */
    public KafkaProducerConfig() {
        
        // Créer l'objet Properties pour la configuration
        Properties props = new Properties();
        
        // Adresse du broker Kafka
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        // Sérialiseur pour la clé
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
                  StringSerializer.class.getName());
        
        // Sérialiseur pour la valeur
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
                  StringSerializer.class.getName());
        
        // Configuration de fiabilité
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        
        // Créer le Producer Kafka
        this.producer = new KafkaProducer<>(props);
        
        System.out.println("[KAFKA] Producer Kafka initialisé avec succès !");
        System.out.println("[KAFKA] Topic cible : " + TOPIC_NAME);
        System.out.println("[KAFKA] Broker : localhost:9092");
    }
    
    
    // ========================================================================
    // MÉTHODES PUBLIQUES
    // ========================================================================
    
    /**
     * MÉTHODE : sendMessage
     * 
     * RÔLE :
     * Envoie un message JSON vers le topic Kafka "bruit-topic"
     * 
     * @param key : Clé du message (zone_id)
     * @param value : Valeur du message (JSON)
     */
    public void sendMessage(String key, String value) {
        try {
            // Créer le ProducerRecord
            ProducerRecord<String, String> record = 
                new ProducerRecord<>(TOPIC_NAME, key, value);
            
            // Envoyer vers Kafka
            producer.send(record).get();
            
            System.out.println("[KAFKA] ✓ Message envoyé vers " + TOPIC_NAME);
            System.out.println("[KAFKA]   Clé : " + key);
            System.out.println("[KAFKA]   Valeur : " + value);
            
        } catch (Exception e) {
            System.err.println("[KAFKA] ✗ Erreur lors de l'envoi : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * MÉTHODE : close
     * 
     * RÔLE :
     * Ferme proprement le Producer Kafka
     */
    public void close() {
        if (producer != null) {
            System.out.println("[KAFKA] Fermeture du Producer Kafka...");
            producer.close();
            System.out.println("[KAFKA] Producer fermé.");
        }
    }
}
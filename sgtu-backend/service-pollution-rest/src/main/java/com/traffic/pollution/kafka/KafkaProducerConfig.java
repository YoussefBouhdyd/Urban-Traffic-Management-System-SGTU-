package com.traffic.pollution.kafka;

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
 * Cette classe configure et gère le Producer Kafka.
 * Le Producer est responsable d'envoyer les messages vers le topic Kafka.
 * 
 * FONCTIONNEMENT :
 * 1. Initialise la configuration Kafka (adresse du broker, sérialiseurs, etc.)
 * 2. Crée un objet KafkaProducer
 * 3. Fournit une méthode pour envoyer des messages vers un topic
 * 
 * TOPIC CIBLE : "pollution-topic"
 * 
 * CONFIGURATION :
 * - Bootstrap Server : localhost:9092 (adresse du broker Kafka)
 * - Serializer : StringSerializer (convertit les objets en String pour Kafka)
 * 
 * ============================================================================
 */
public class KafkaProducerConfig {
    
    // ========================================================================
    // ATTRIBUTS
    // ========================================================================
    
    /**
     * L'objet KafkaProducer qui va envoyer les messages
     * Type : <String, String> signifie que la clé ET la valeur sont des String
     */
    private KafkaProducer<String, String> producer;
    
    /**
     * Nom du topic Kafka où on envoie les messages
     */
    private static final String TOPIC_NAME = "pollution-topic";
    
    
    // ========================================================================
    // CONSTRUCTEUR
    // ========================================================================
    
    /**
     * CONSTRUCTEUR
     * Initialise le Producer Kafka avec toutes les configurations nécessaires
     */
    public KafkaProducerConfig() {
        
        // Étape 1 : Créer un objet Properties pour stocker la configuration
        Properties props = new Properties();
        
        // Étape 2 : Configurer l'adresse du serveur Kafka (broker)
        // localhost:9092 = Kafka tourne sur la même machine, port 9092
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        // Étape 3 : Configurer le sérialiseur de la CLÉ
        // La clé identifie le message (ici on utilise des String)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
                  StringSerializer.class.getName());
        
        // Étape 4 : Configurer le sérialiseur de la VALEUR
        // La valeur est le contenu du message (ici notre JSON en String)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
                  StringSerializer.class.getName());
        
        // Étape 5 : Configuration supplémentaire pour la fiabilité
        // acks=all : Le broker confirme la réception avant de continuer
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        
        // Étape 6 : Nombre de tentatives en cas d'échec
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        
        // Étape 7 : Créer l'objet KafkaProducer avec cette configuration
        this.producer = new KafkaProducer<>(props);
        
        // Message de confirmation dans la console
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
     * Envoie un message (JSON) vers le topic Kafka "pollution-topic"
     * 
     * PARAMÈTRES :
     * @param key : Clé du message (identifiant unique, ex: zone_id)
     * @param value : Valeur du message (le JSON des données de pollution)
     * 
     * FONCTIONNEMENT :
     * 1. Crée un ProducerRecord (enveloppe contenant topic, clé, valeur)
     * 2. Envoie le record vers Kafka
     * 3. Attend la confirmation (get() bloque jusqu'à réception)
     * 4. Gère les erreurs potentielles
     */
    public void sendMessage(String key, String value) {
        try {
            // Étape 1 : Créer un ProducerRecord
            // C'est l'enveloppe qui contient :
            // - Le nom du topic de destination
            // - La clé du message
            // - La valeur (le contenu JSON)
            ProducerRecord<String, String> record = 
                new ProducerRecord<>(TOPIC_NAME, key, value);
            
            // Étape 2 : Envoyer le message vers Kafka
            // send() est asynchrone, mais get() attend la confirmation
            producer.send(record).get();
            
            // Étape 3 : Afficher un message de succès
            System.out.println("[KAFKA] ✓ Message envoyé vers " + TOPIC_NAME);
            System.out.println("[KAFKA]   Clé : " + key);
            System.out.println("[KAFKA]   Valeur : " + value);
            
        } catch (Exception e) {
            // En cas d'erreur, afficher le message d'erreur
            System.err.println("[KAFKA] ✗ Erreur lors de l'envoi vers Kafka : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * MÉTHODE : close
     * 
     * RÔLE :
     * Ferme proprement le Producer Kafka
     * Doit être appelée avant d'arrêter l'application
     */
    public void close() {
        if (producer != null) {
            System.out.println("[KAFKA] Fermeture du Producer Kafka...");
            producer.close();
            System.out.println("[KAFKA] Producer fermé.");
        }
    }
}
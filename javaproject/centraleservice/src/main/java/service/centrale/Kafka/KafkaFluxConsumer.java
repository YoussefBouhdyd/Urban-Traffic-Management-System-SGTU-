package service.centrale.Kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import service.centrale.Dtos.ReceptionFlux;
import service.centrale.Services.GestionFlux;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class KafkaFluxConsumer implements Runnable {
    private static final String BOOTSTRAP_SERVERS =
        System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
    private static final String TOPIC =
        System.getenv().getOrDefault("KAFKA_TOPIC", "service-flux");
    private static final String GROUP_ID =
        System.getenv().getOrDefault("KAFKA_GROUP_ID", "centrale-flux-consumer");

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaConsumer<String, String> consumer;
    private volatile boolean running = true;

    public KafkaFluxConsumer() {
        this.consumer = new KafkaConsumer<>(buildProperties());
    }

    @Override
    public void run() {
        consumer.subscribe(List.of(TOPIC));
        System.out.println("[Kafka Consumer] Started on topic " + TOPIC);

        try {
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records) {
                    handleRecord(record);
                }
            }
        } catch (WakeupException e) {
            if (running) {
                throw e;
            }
        } finally {
            consumer.close();
            System.out.println("[Kafka Consumer] Stopped");
        }
    }

    public void shutdown() {
        running = false;
        consumer.wakeup();
    }

    private void handleRecord(ConsumerRecord<String, String> record) {
        if ("feux".equals(record.key())) {
            return;
        }

        try {
            ReceptionFlux flux = objectMapper.readValue(record.value(), ReceptionFlux.class);
            GestionFlux.insertData(flux.getName(), flux.getFlux(), flux.getTimestamp());
            System.out.println("[Kafka Consumer] Stored flux for " + flux.getName());
        } catch (Exception e) {
            System.out.println("[Kafka Consumer] Error processing record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Properties buildProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        return properties;
    }
}

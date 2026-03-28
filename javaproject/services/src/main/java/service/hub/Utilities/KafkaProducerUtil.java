package service.hub.Utilities;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;

public final class KafkaProducerUtil {
    private static final String BOOTSTRAP_SERVERS =
        System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
    private static final String TOPIC =
        System.getenv().getOrDefault("KAFKA_TOPIC", "service-flux");

    private static final KafkaProducer<String, String> PRODUCER = createProducer();

    private KafkaProducerUtil() {
    }

    private static KafkaProducer<String, String> createProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        return new KafkaProducer<>(properties);
    }

    public static void publish(String key, String message) throws Exception {
        Future<RecordMetadata> future = PRODUCER.send(new ProducerRecord<>(TOPIC, key, message));
        RecordMetadata metadata = future.get();
        System.out.println("[Kafka] Sent to topic=" + metadata.topic() + ", partition="
            + metadata.partition() + ", offset=" + metadata.offset());
    }
}

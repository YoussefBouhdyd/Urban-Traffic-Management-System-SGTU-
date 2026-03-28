package com.smartcity.traffic.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smartcity.traffic.model.CameraEvent;
import com.smartcity.traffic.util.Config;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class CameraEventProducer {
    private static final Logger logger = LoggerFactory.getLogger(CameraEventProducer.class);
    private final KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper;
    private final String topic;

    public CameraEventProducer() {
        this.topic = Config.getKafkaTopicCameraData();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Config.getKafkaBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        this.producer = new KafkaProducer<>(props);
        logger.info("Kafka Producer initialized for topic: {}", topic);
    }

    public void sendEvent(CameraEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, event.getCameraId(), eventJson);

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    logger.error("Error sending camera event to Kafka", exception);
                } else {
                    logger.debug("Camera event sent to topic {} partition {} offset {}",
                            metadata.topic(), metadata.partition(), metadata.offset());
                }
            });
        } catch (Exception e) {
            logger.error("Error serializing camera event", e);
        }
    }

    public void close() {
        if (producer != null) {
            producer.flush();
            producer.close();
            logger.info("Kafka Producer closed");
        }
    }
}

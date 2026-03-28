package com.smartcity.traffic.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smartcity.traffic.model.CameraEvent;
import com.smartcity.traffic.service.TrafficAnalysisService;
import com.smartcity.traffic.util.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(CameraEventConsumer.class);
    private final KafkaConsumer<String, String> consumer;
    private final ObjectMapper objectMapper;
    private final TrafficAnalysisService analysisService;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final String topic;

    public CameraEventConsumer(TrafficAnalysisService analysisService) {
        this.analysisService = analysisService;
        this.topic = Config.getKafkaTopicCameraData();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Config.getKafkaBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, Config.getKafkaGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList(topic));
        logger.info("Kafka Consumer initialized for topic: {}", topic);
    }

    public void start() {
        logger.info("Starting camera event consumer...");
        while (running.get()) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    processEvent(record.value());
                }
            } catch (Exception e) {
                logger.error("Error consuming camera events", e);
            }
        }
    }

    private void processEvent(String eventJson) {
        try {
            CameraEvent event = objectMapper.readValue(eventJson, CameraEvent.class);
            logger.info("Received camera event: {}", event);
            
            // Process the event through the analysis service
            analysisService.analyzeEvent(event);
        } catch (Exception e) {
            logger.error("Error processing camera event", e);
        }
    }

    public void stop() {
        running.set(false);
        if (consumer != null) {
            consumer.close();
            logger.info("Kafka Consumer closed");
        }
    }
}

package com.smartcity.traffic.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("Unable to find application.properties");
            } else {
                properties.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    // Database
    public static String getDbUrl() {
        return get("db.url");
    }

    public static String getDbUsername() {
        return get("db.username");
    }

    public static String getDbPassword() {
        return get("db.password");
    }

    // Kafka
    public static String getKafkaBootstrapServers() {
        return get("kafka.bootstrap.servers");
    }

    public static String getKafkaGroupId() {
        return get("kafka.group.id");
    }

    public static String getKafkaTopicCameraData() {
        return get("kafka.topic.camera.data");
    }

    public static String getKafkaTopicAlerts() {
        return get("kafka.topic.alerts");
    }

    public static String getKafkaTopicRecommendations() {
        return get("kafka.topic.recommendations");
    }

    // RMI
    public static int getRmiPort() {
        return getInt("rmi.port", 1099);
    }

    public static String getRmiServiceName() {
        return get("rmi.service.name");
    }

    // API Server
    public static String getApiHost() {
        return get("api.host", "localhost");
    }

    public static int getApiPort() {
        return getInt("api.port", 8080);
    }

    // Camera
    public static String getCameraId() {
        return get("camera.id", "CAM-01");
    }

    public static String getIntersectionId() {
        return get("intersection.id", "INT-01");
    }

    public static int getCameraEventIntervalSeconds() {
        return getInt("camera.event.interval.seconds", 5);
    }
}

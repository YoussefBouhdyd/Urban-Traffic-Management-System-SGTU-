package service.centrale.Kafka;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class KafkaConsumerListener implements ServletContextListener {
    private KafkaFluxConsumer kafkaFluxConsumer;
    private Thread consumerThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        kafkaFluxConsumer = new KafkaFluxConsumer();
        consumerThread = new Thread(kafkaFluxConsumer, "kafka-flux-consumer");
        consumerThread.setDaemon(true);
        consumerThread.start();
        System.out.println("[Kafka Consumer] Listener initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (kafkaFluxConsumer != null) {
            kafkaFluxConsumer.shutdown();
        }

        if (consumerThread != null) {
            try {
                consumerThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("[Kafka Consumer] Listener destroyed");
    }
}

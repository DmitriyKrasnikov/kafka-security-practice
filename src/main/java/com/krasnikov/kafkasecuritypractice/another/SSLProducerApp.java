package com.krasnikov.kafkasecuritypractice.another;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;

/**
 * Kafka SSL Producer
 */
public class SSLProducerApp {
    private static final Logger logger = LoggerFactory.getLogger(SSLProducerApp.class);

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());


        // SSL Configuration with JKS
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "kafka-1-creds/kafka-1.truststore.jks"); // Truststore
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "your-password"); // Truststore password
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "kafka-1-creds/kafka-1.keystore.jks"); // Keystore
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "your-password"); // Keystore password
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "your-password"); // Key password
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, ""); // Отключение проверки hostname

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            for (int i = 1; i <= 10; i++) {
                String key = "key-" + UUID.randomUUID();
                String value = "SSL message " + i;

                logger.info("Sending message {}: key={}, value={}", i, key, value);

                ProducerRecord<String, String> record = new ProducerRecord<>("ssl-topic", key, value);
                producer.send(record, (metadata, exception) -> {
                    if (exception == null) {
                        logger.info("Message sent: partition={}, offset={}", metadata.partition(), metadata.offset());
                    } else {
                        logger.error("Error while producing: ", exception);
                        exception.printStackTrace();
                    }
                });

                Thread.sleep(600);
            }
            producer.flush();
        } catch (Exception e) {
            logger.error("Error while producing: ", e);
            e.printStackTrace();
        }
    }
}

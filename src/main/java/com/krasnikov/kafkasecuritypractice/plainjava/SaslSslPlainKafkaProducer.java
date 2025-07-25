package com.krasnikov.kafkasecuritypractice.plainjava;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SaslSslPlainKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(SaslSslPlainKafkaProducer.class);

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094,localhost:9194,localhost:9294");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Безопасность: SASL_SSL + PLAIN
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                        "username=\"user\" password=\"user-secret\";");

        // SSL-сертификаты
        //TO DO: Создать общий truststore со всеми сертификатами брокера.
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "kafka-1-creds/certs/kafka-1.truststore.jks");
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "your-password");
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "kafka-1-creds/certs/kafka-1.keystore.jks");
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "your-password");
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "your-password");
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {

            ProducerRecord<String, String> record1 = new ProducerRecord<>("topic-1", "key1", "Сообщение в topic-1");
            ProducerRecord<String, String> record2 = new ProducerRecord<>("topic-2", "key2", "Сообщение в topic-2");

            producer.send(record1, (metadata, exception) -> {
                if (exception == null) {
                    logger.info("Сообщение отправлено в topic-1: {}", metadata);
                } else {
                    logger.error("Ошибка при отправке в topic-1", exception);
                }
            });

            producer.send(record2, (metadata, exception) -> {
                if (exception == null) {
                    logger.info("Сообщение отправлено в topic-2: {}", metadata);
                } else {
                    logger.error("Ошибка при отправке в topic-2", exception);
                }
            });

            producer.flush();
        } catch (Throwable e) {
            logger.error("Ошибка в Kafka Producer", e);
        }
    }
}
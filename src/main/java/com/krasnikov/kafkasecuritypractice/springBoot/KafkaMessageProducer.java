package com.krasnikov.kafkasecuritypractice.springBoot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void sendMessages() {
        log.info("🚀 Sending messages to Kafka...");

        kafkaTemplate.send("topic-1", "key1", "Message to topic-1");
        log.info("✅ Sent message to topic-1: key=key1, value=Message to topic-1");

        kafkaTemplate.send("topic-2", "key2", "Message to topic-2");
        log.info("✅ Sent message to topic-2: key=key2, value=Message to topic-2");
    }
}
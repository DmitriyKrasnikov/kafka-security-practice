package com.krasnikov.kafkasecuritypractice;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaMessageConsumer {

    @KafkaListener(topics = "topic-1", groupId = "spring-security-consumer")
    public void listenTopic1(ConsumerRecord<String, String> record) {
        log.info("✅ [topic-1] Received message: key={}, value={}, partition={}, offset={}",
                record.key(), record.value(), record.partition(), record.offset());
    }

    @KafkaListener(topics = "topic-2", groupId = "spring-security-consumer")
    public void listenTopic2(ConsumerRecord<String, String> record) {
        log.warn("⚠️ [topic-2] Received message: key={}, value={}, partition={}, offset={}",
                record.key(), record.value(), record.partition(), record.offset());
    }
}
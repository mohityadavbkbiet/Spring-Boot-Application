package net.engineeringdigest.ecommerce.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "test-topic", groupId = "ecommerce-group")
    public void listen(String message) {
        log.info("Received message from Kafka: {}", message);
    }
}

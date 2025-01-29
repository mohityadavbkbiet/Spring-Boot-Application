package net.engineeringdigest.ecommerce.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TestController(RedisTemplate<String, String> redisTemplate,
                         @Qualifier("stringKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/kafka")
    public String testKafka(@RequestBody String message) {
        kafkaTemplate.send("test-topic", message);
        return "Message sent to Kafka: " + message;
    }

    @PostMapping("/redis/set")
    public String setRedisValue(@RequestParam String key, @RequestParam String value) {
        redisTemplate.opsForValue().set(key, value);
        return "Value set in Redis for key: " + key;
    }

    @GetMapping("/redis/get")
    public String getRedisValue(@RequestParam String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? value : "Key not found";
    }
}

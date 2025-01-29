package net.engineeringdigest.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private static final Logger log = LoggerFactory.getLogger(RedisService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long DEFAULT_EXPIRE_TIME = 3600L; // 1 hour

    public <T> T get(String key, Class<T> type) {
        try {
            log.debug("Getting value from Redis for key: {}", key);
            return type.cast(redisTemplate.opsForValue().get(key));
        } catch (Exception e) {
            log.error("Error getting value from Redis for key: {}", key, e);
            return null;
        }
    }

    public void set(String key, Object value) {
        try {
            log.debug("Setting value in Redis for key: {}", key);
            redisTemplate.opsForValue().set(key, value, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error setting value in Redis for key: {}", key, e);
        }
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            log.debug("Setting value in Redis for key: {} with timeout: {} {}", key, timeout, unit);
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Error setting value in Redis for key: {}", key, e);
        }
    }

    public void delete(String key) {
        try {
            log.debug("Deleting value from Redis for key: {}", key);
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error deleting value from Redis for key: {}", key, e);
        }
    }
}

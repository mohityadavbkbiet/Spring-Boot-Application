package net.engineeringdigest.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Properties;

@Component
public class RedisConnectionTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void testRedisConnection() {
        System.out.println("\n=== Testing Redis Connection ===");
        try {
            RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
            
            // Test connection with PING command
            String pong = connection.ping();
            
            // Get Redis server information
            Properties info = connection.info();
            String version = info.getProperty("redis_version");
            String connectedClients = info.getProperty("connected_clients");
            String usedMemory = info.getProperty("used_memory_human");
            
            System.out.println("✅ Redis connection test successful!");
            System.out.println("📊 Redis Details:");
            System.out.println("   • Response: " + pong);
            System.out.println("   • Version: " + version);
            System.out.println("   • Connected clients: " + connectedClients);
            System.out.println("   • Memory usage: " + usedMemory);
            
            connection.close();
        } catch (Exception e) {
            System.out.println("❌ Failed to connect to Redis!");
            System.out.println("Error details: " + e.getMessage());
        }
        System.out.println("===============================\n");
    }
}

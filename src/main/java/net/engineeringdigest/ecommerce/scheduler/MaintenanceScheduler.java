package net.engineeringdigest.ecommerce.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class MaintenanceScheduler {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceScheduler.class);
    
    private final MongoTemplate mongoTemplate;
    private final CacheManager cacheManager;

    public MaintenanceScheduler(MongoTemplate mongoTemplate, CacheManager cacheManager) {
        this.mongoTemplate = mongoTemplate;
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    public void cleanupExpiredTokens() {
        logger.info("Starting cleanup of expired tokens");
        try {
            mongoTemplate.remove(
                query(where("expirationDate").lt(new Date())),
                "refresh_tokens"
            );
            logger.info("Completed cleanup of expired tokens");
        } catch (Exception e) {
            logger.error("Error during token cleanup: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 */4 * * *") // Run every 4 hours
    public void clearAllCaches() {
        logger.info("Starting cache cleanup");
        try {
            cacheManager.getCacheNames()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
            logger.info("Completed cache cleanup");
        } catch (Exception e) {
            logger.error("Error during cache cleanup: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void healthCheck() {
        logger.info("Performing health check");
        // Add your health check logic here
    }
}

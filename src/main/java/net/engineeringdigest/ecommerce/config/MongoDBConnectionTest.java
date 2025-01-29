package net.engineeringdigest.ecommerce.config;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoDBConnectionTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void testMongoDBConnection() {
        System.out.println("\n=== Testing MongoDB Connection ===");
        try {
            // Test the connection by executing a ping command
            Document result = mongoTemplate.getDb().runCommand(new Document("ping", 1));
            
            // Get MongoDB server information
            Document serverStatus = mongoTemplate.getDb().runCommand(new Document("serverStatus", 1));
            String version = serverStatus.get("version", String.class);
            Document connections = serverStatus.get("connections", Document.class);
            
            System.out.println("✅ MongoDB connection test successful!");
            System.out.println("📊 MongoDB Details:");
            System.out.println("   • Version: " + version);
            System.out.println("   • Current connections: " + connections.get("current"));
            System.out.println("   • Available connections: " + connections.get("available"));
            
            // List all collections in the database
            System.out.println("\n📚 Collections in database:");
            mongoTemplate.getDb().listCollectionNames()
                .into(new java.util.ArrayList<>())
                .forEach(collectionName -> 
                    System.out.println("   • " + collectionName));
                    
        } catch (Exception e) {
            System.out.println("❌ Failed to connect to MongoDB!");
            System.out.println("Error details: " + e.getMessage());
            System.out.println("\nMongoDB connection properties:");
            System.out.println("Host: " + mongoTemplate.getDb().getName());
            System.out.println("Database: " + mongoTemplate.getDb().getName());
        }
        System.out.println("===============================\n");
    }
}

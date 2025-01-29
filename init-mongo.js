db.auth('root', 'password')

db = db.getSiblingDB('ecommerce')

// Initialize config collection
db.configEcommerce.insertMany([
    {
        key: "PAYMENT_GATEWAY_URL",
        value: "https://api.stripe.com/v1",
        description: "Stripe API endpoint for payment processing",
        active: true
    },
    {
        key: "ORDER_CLEANUP_DAYS",
        value: "30",
        description: "Number of days after which abandoned orders are cleaned up",
        active: true
    },
    {
        key: "PRODUCT_CACHE_TTL",
        value: "3600",
        description: "Product cache time-to-live in seconds",
        active: true
    }
]);

// Create indexes
db.products.createIndex({ "name": "text", "description": "text" });
db.products.createIndex({ "category": 1 });
db.products.createIndex({ "active": 1 });
db.products.createIndex({ "category": 1, "active": 1 });
db.products.createIndex({ "stockQuantity": 1 });

db.reviews.createIndex({ "productId": 1 });
db.reviews.createIndex({ "userId": 1 });
db.reviews.createIndex({ "productId": 1, "userId": 1 });
db.reviews.createIndex({ "rating": 1 });

db.orders.createIndex({ "userId": 1, "createdAt": -1 });
db.orders.createIndex({ "status": 1 });

db.users.createIndex({ "username": 1 }, { unique: true });
db.users.createIndex({ "email": 1 }, { unique: true });

db.carts.createIndex({ "userId": 1 }, { unique: true });
db.carts.createIndex({ "updatedAt": 1 });

// Create admin user
db.users.insertOne({
    username: "admin",
    password: "$2a$10$XQFXjY2HY.4uAv3pg1bQh.Q.DuM1vZ7JAXP6pqD5yYmzhw/6VJYji", // hashed 'admin123'
    email: "admin@example.com",
    roles: ["ADMIN"],
    active: true,
    createdAt: new Date()
});

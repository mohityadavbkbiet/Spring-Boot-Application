package net.engineeringdigest.ecommerce.constant;

public final class AppConstants {
    private AppConstants() {}

    // Cache Constants
    public static final String PRODUCT_CACHE = "productCache";
    public static final String USER_CACHE = "userCache";
    public static final long CACHE_TTL = 3600; // 1 hour

    // Kafka Topics
    public static final String ORDERS_TOPIC = "orders";
    public static final String AUDIT_LOGS_TOPIC = "audit-logs";

    // Security Constants
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long TOKEN_EXPIRATION = 864_000_000; // 10 days

    // Pagination Constants
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    // API Endpoints
    public static final String API_BASE_PATH = "/api/v1";
    public static final String PRODUCTS_API = API_BASE_PATH + "/products";
    public static final String USERS_API = API_BASE_PATH + "/users";
    public static final String ORDERS_API = API_BASE_PATH + "/orders";
}

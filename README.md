# E-commerce Application

A modern e-commerce application built with Spring Boot, MongoDB, Redis, and Kafka.

## Prerequisites

- Docker
- Docker Compose
- Java 17 (for local development)
- Maven (for local development)

## Running with Docker

The application is containerized using Docker and can be easily run using Docker Compose.

### Quick Start

1. Build the application:
```bash
./build.sh
```

2. Start the application:
```bash
./start.sh
```

3. Stop the application:
```bash
./stop.sh
```

### Available Services

- **Application**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **MongoDB**: localhost:27017
- **Redis**: localhost:6379
- **Kafka**: localhost:9092
- **Zookeeper**: localhost:2181

### Monitoring

View application logs:
```bash
docker-compose logs -f
```

View logs for a specific service:
```bash
docker-compose logs -f [service-name]
```

Available service names:
- app
- mongodb
- redis
- kafka
- zookeeper

### Health Checks

All services have health checks configured. Check the status of services:
```bash
docker-compose ps
```

## Development

### Local Development

1. Start the required services:
```bash
docker-compose up -d mongodb redis kafka zookeeper
```

2. Run the application locally:
```bash
./mvnw spring-boot:run
```

### Configuration

- Application configuration: `src/main/resources/application.properties`
- Docker-specific configuration: `src/main/resources/application-docker.properties`

### Architecture

The application follows a microservices-based architecture:

- **MongoDB**: Primary database for storing product, user, and order information
- **Redis**: Caching layer for improved performance
- **Kafka**: Event streaming for order processing and notifications
- **Spring Security**: Authentication and authorization
- **Swagger/OpenAPI**: API documentation

## API Documentation

Access the API documentation at http://localhost:8080/swagger-ui.html when the application is running.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

# LibriStream Codebase Instructions for AI Agents

## Architecture Overview
LibriStream is a microservices platform for borrowing and reading books. Three independent Spring Boot services communicate via **gRPC** (synchronous) and **Kafka** (async events).

### Services & Responsibilities
- **UserProfileService** (port 4000, gRPC 9000): User registration, authentication (JWT), profile management. Master-slave database replication (userdb).
- **BillingService** (port 4001, gRPC 9001): Handles billing account creation via gRPC. Uses PostgreSQL (billingdb).
- **NotificationService** (port 4002): Consumes user events from Kafka, sends notifications.

### Data Flow
1. UserProfileService creates users → publishes `UserEvent` to Kafka topic "user"
2. NotificationService consumes events and sends notifications
3. BillingService exposes gRPC endpoint `CreateBillingAccount` (called externally)

## Tech Stack & Key Patterns

### Core Technologies
- **Java 21**, Spring Boot 3.5.9, Maven
- **gRPC** with Spring gRPC starter for service-to-service RPC calls
- **Kafka** for async event publishing (protobuf-serialized messages)
- **Protocol Buffers** (proto3) for contract-driven schemas
- **JWT (io.jsonwebtoken)** for stateless auth in UserProfileService
- **JPA/Hibernate** for ORM (UserProfileService and BillingService)
- **PostgreSQL** with master-slave read replicas (UserProfileService uses `userdb` with `ReplicationRoutingDataSource`, BillingService uses `billingdb`)

### Proto Definitions
Proto files live in `src/main/proto/` and are compiled to Java classes during `mvn clean install`:
- `billing_service.proto`: BillingService gRPC interface
- `user_event.proto`: Kafka event schema for user lifecycle events

### Custom Annotations
- `@StrongPassword`: Validates password strength (see `CustomAnnotation/StrongPasswordValidator.java`)
- Applied in `RegisterRequestDto` for registration validation

## Build & Test Workflows

### Build Each Service
```bash
cd BillingService && mvn clean install
cd ../UserProfileService && mvn clean install
cd ../NotificationService && mvn clean install
```

The protobuf Maven plugin regenerates Java classes from `.proto` files. Use `os-maven-plugin` extension (handles Linux/macOS/Windows protoc compilation).

### Testing
- Unit tests in `src/test/java/` 
- Run with `mvn test`
- Surefire reports in `target/surefire-reports/`

### Configuration Files
- **UserProfileService**: `application.properties` (master-slave DB config for `userdb`, ports, Kafka, Redis)
- **BillingService**: `application.properties` (single DB config for `billingdb`, ports)
- **NotificationService**: `application.yaml` (Kafka bootstrap server)
- **Environment Variables**: `.env` file stores credentials (excluded from git via `.gitignore`)

### Environment Variables
Credentials and service addresses are stored in `.env` file (see `.env.example` for template):
- `POSTGRES_HOST`: PostgreSQL server hostname/IP
- `POSTGRES_USER`: PostgreSQL username
- `POSTGRES_PASSWORD`: PostgreSQL password
- `KAFKA_HOST`: Kafka server hostname/IP
- `KAFKA_PORT`: Kafka port (default: 9094)
- `REDIS_HOST`: Redis server hostname/IP
- `REDIS_PORT`: Redis port (default: 6379)
- `REDIS_PASSWORD`: Redis password

Spring Boot applications use these via `${VAR_NAME}` syntax. All variables must be set in the environment.

## gRPC Communication Details

### gRPC Service Calls
**UserProfileService → BillingService** (during user registration):
- Client: [BillingServiceGrpcClient](UserProfileService/src/main/java/info/mayankag/UserProfileService/grpc/BillingServiceGrpcClient.java)
- Endpoint: `CreateBillingAccount(BillingRequest) → BillingResponse`
- Usage: Called in [UserService.registerUser()](UserProfileService/src/main/java/info/mayankag/UserProfileService/service/UserService.java#L108) after user saved
- Config: Uses `${billing.service.address}` and `${billing.service.grpc.port}` from application.properties
- ManagedChannel established with `.usePlaintext()` for local development

### gRPC Service Implementation
Extend `*ServiceImplBase` (generated from proto). See [BillingGrpcService](BillingService/src/main/java/info/mayankag/billingservice/grpc/BillingGrpcService.java) for example.

## Integration Points & Conventions

### Kafka Messaging
- Producer: `KafkaTemplate<String, byte[]>` with protobuf `.toByteArray()`
- Consumer: Implement `KafkaListener` with `ByteArrayDeserializer`
- Topic naming: lowercase (e.g., "user")
- See [KafkaProducer](UserProfileService/src/main/java/info/mayankag/UserProfileService/kafka/KafkaProducer.java)

### REST Endpoints (UserProfileService)
Use Swagger annotations (`@Tag`, `@Operation`) for API docs. DTOs in `dto/` folder with validation annotations.

### Database Replication
`ReplicationRoutingDataSource` routes reads to replica, writes to master. Configured via split datasource properties (`spring.datasource.write.*` and `spring.datasource.read.*`).

## Docker & Local Development

### Environment Setup
Copy `.env.example` to `.env` and update with your credentials:
```bash
cp .env.example .env
# Edit .env with your actual credentials
```

### Running Infrastructure Services
Use `docker-compose.yml` to run PostgreSQL (master + 2 replicas), Kafka, Zookeeper, and Redis:
```bash
docker-compose up -d
```

Access endpoints:
- PostgreSQL Master: `localhost:5432` (writes)
- PostgreSQL Replicas: `localhost:5433`, `localhost:5434` (reads)
- Kafka: `localhost:9094`
- Redis: `localhost:6379`

Update `application.properties` for your services to connect to services via environment variables:

**UserProfileService:**
```properties
spring.datasource.write.jdbc-url=jdbc:postgresql://${POSTGRES_HOST}:5432/${USER_DB_NAME}
spring.datasource.read.jdbc-url=jdbc:postgresql://${POSTGRES_HOST}:5433,${POSTGRES_HOST}:5434/${USER_DB_NAME}
spring.kafka.bootstrap-servers=${KAFKA_HOST}:${KAFKA_PORT}
spring.data.redis.host=${REDIS_HOST}
```

**BillingService:**
```properties
spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST}:5432/${BILLING_DB_NAME}
```

### Building & Running Service Containers
Each service has a multi-stage Dockerfile (builds JAR with Maven, runs on JDK 21):
```bash
docker build -t libristream/user-profile-service UserProfileService/
docker build -t libristream/billing-service BillingService/
docker build -t libristream/notification-service NotificationService/
```

Run a service container (expose ports):
```bash
docker run -p 4000:4000 -p 9000:9000 libristream/user-profile-service
```

See [DOCKER_SETUP.md](DOCKER_SETUP.md) for detailed container setup and environment variable configuration.

## Performance Testing

### JMeter Load Test
File: `Connection Pool Test.jmx` (located in root)
- **Test Config**: 1000 concurrent threads, 1 second ramp-up
- **Target**: UserProfileService pagination endpoint (`GET /api/user?page=1`)
- **Purpose**: Tests connection pool behavior under load
- **Run locally**: Open in JMeter GUI or CLI:
  ```bash
  jmeter -n -t "Connection Pool Test.jmx" -l results.jtl -j jmeter.log
  ```
- **Metrics**: Check `target/surefire-reports/` for details and monitor connection pool usage via logs

## Common Developer Tasks

- **Add gRPC method**: Update `.proto` file, run Maven, implement in `*GrpcService.java`
- **Publish Kafka event**: Use `KafkaProducer.notifyUserCreated()` pattern
- **Add REST endpoint**: Create controller method, add DTOs, use validation annotations
- **Run locally**: Start services in order (UserProfileService, then BillingService, NotificationService), ensure Kafka/PostgreSQL running
- **Check test results**: Look in `target/surefire-reports/` after `mvn test`

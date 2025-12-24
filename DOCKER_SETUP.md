# Docker setup instructions

## Environment Setup

Before running any services, set up your environment variables:

1. Copy the example environment file:
```bash
cp .env.example .env
```

2. Edit `.env` with your actual configuration:
```bash
# .env file
POSTGRES_HOST=your_postgres_server_url      # PostgreSQL server address
POSTGRES_USER=your_username
POSTGRES_PASSWORD=your_secure_password
USER_DB_NAME=your_user_db_name              # UserProfileService database
BILLING_DB_NAME=your_billing_db_name        # BillingService database
KAFKA_HOST=your_kafka_server_url            # Kafka server address
KAFKA_PORT=your_kafka_server_port           # Kafka port
REDIS_HOST=your_redis_password              # Redis server address
REDIS_PORT=your_redis_server_port           # Redis port
REDIS_PASSWORD=your_redis_password
```

**IMPORTANT**: The `.env` file is excluded from git to protect your credentials and configuration. Never commit it to version control.

## Running Infrastructure with Docker Compose

Use the provided `docker-compose.yml` to run all infrastructure services:

```bash
docker-compose up -d
```

This starts:
- **PostgreSQL Master** on `localhost:5432` (write operations, databases: `userdb`, `billingdb`)
- **PostgreSQL Slave 1** on `localhost:5433` (read replica)
- **PostgreSQL Slave 2** on `localhost:5434` (read replica)
- **Kafka** on `localhost:9094` (with Zookeeper on port 2181)
- **Redis** on `localhost:6379` (for caching)

Stop services:
```bash
docker-compose down
```

Remove volumes (WARNING: data loss):
```bash
docker-compose down -v
```

## Building Service Docker Images

Each service has a multi-stage Dockerfile that:
1. Builds the Spring Boot JAR using Maven
2. Runs the JAR on OpenJDK 21

Build all services:
```bash
docker build -t libristream/user-profile-service UserProfileService/
docker build -t libristream/billing-service BillingService/
docker build -t libristream/notification-service NotificationService/
```

Run services:

**UserProfileService:**
```bash
docker run -p 4000:4000 -p 9000:9000 \
  -e POSTGRES_HOST=${POSTGRES_HOST} \
  -e POSTGRES_USER=${POSTGRES_USER} \
  -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
  -e USER_DB_NAME=${USER_DB_NAME} \
  -e KAFKA_HOST=${KAFKA_HOST} \
  -e KAFKA_PORT=${KAFKA_PORT} \
  -e REDIS_HOST=${REDIS_HOST} \
  -e REDIS_PORT=${REDIS_PORT} \
  -e REDIS_PASSWORD=${REDIS_PASSWORD} \
  libristream/user-profile-service
```

**BillingService:**
```bash
docker run -p 4001:4001 -p 9001:9001 \
  -e POSTGRES_HOST=${POSTGRES_HOST} \
  -e POSTGRES_USER=${POSTGRES_USER} \
  -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
  -e BILLING_DB_NAME=${BILLING_DB_NAME} \
  libristream/billing-service
```

**Note**: Environment variables from your `.env` file are automatically loaded by docker-compose, but must be explicitly passed when using `docker run`.

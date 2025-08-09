# Rental API Reactive

[![Java Version](https://img.shields.io/badge/Java-21-blue.svg)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A fully reactive, multi-tenant API for vehicle rental management, built with Spring Boot WebFlux, Project Reactor, and ScyllaDB/Cassandra. This project provides a comprehensive backend solution for managing organizations, agencies, users, roles, permissions, and subscriptions.

## Features

-   **Reactive Stack:** Built from the ground up with a non-blocking, reactive architecture using Spring WebFlux.
-   **Multi-Tenancy:** Data isolation for organizations and agencies.
-   **Security:** JWT-based authentication and role-based access control (RBAC).
-   **Onboarding:** A complete, multi-step onboarding process for new organizations.
-   **Database:** High-performance, scalable data storage with ScyllaDB (Cassandra-compatible).
-   **Containerized:** Fully containerized with Docker and Docker Compose for easy setup and deployment.
-   **API Documentation:** Integrated OpenAPI 3 (Swagger UI) for clear and interactive API documentation.

---

## Prerequisites

Before you begin, ensure you have the following installed on your system:

-   **Java JDK 21**
-   **Maven 3.8+**
-   **Make**
-   **Docker**
-   **Docker Compose**
-   A Git client

---

## Quick Start (Docker)

This is the recommended way to run the application for development and testing. It will start the application, the ScyllaDB database, and the database initialization service.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/AdministrationReseau/rental-api-reactfi.git rental-api
    cd rental-api
    ```

2.  **Build and Run with Docker Compose:**
    This command will build the Java application's Docker image and start all the services defined in `docker-compose.yml` in detached mode.

    ```bash
    docker-compose up -d --build
    ```

3.  **Check the application status:**
    You can view the logs to ensure everything started correctly.

    ```bash
    # View logs for all services
    docker-compose logs -f

    # View logs for only the application
    docker-compose logs -f rental-api
    ```
    Wait for the log message indicating that the Spring application has started.

4.  **Access the Application:**
    Once started, the application will be available at:
    -   **API Base URL:** `http://localhost:8080`
    -   **Swagger UI (API Docs):** `http://localhost:8080/swagger-ui.html`
    -   **Health Check:** `http://localhost:8080/api/v1/health`

5.  **Stop the Application:**
    To stop all running services and remove the containers:
    ```bash
    docker-compose down
    ```

---

## Local Development (Without Docker for the App)

If you prefer to run the Spring Boot application directly on your host machine (e.g., from your IDE), you can still use Docker to run the database.

1.  **Clone the repository** (if you haven't already).

2.  **Start the Database:**
    Use the `Makefile` shortcut or `docker-compose` to start only ScyllaDB and its initialization container.

    ```bash
    # Using Makefile
    make docker-start

    # Or using docker-compose directly
    docker-compose up -d scylladb scylla-init
    ```
    This ensures the `rental` keyspace is created and ready.

    If you encounter any issues with the database, you can use the `docker-compose logs -f scylladb` command to check the logs. If there's any difficult issues with understanding the logs, you can directly delete the volume `rental-api_scylla_data` while doing :
    
    ```bash
    docker-compose down
    # delete volume
    docker volume rm rental-api_scylla_data
    docker-compose up -d scylladb scylla-init
    ```

3.  **Run the Spring Boot Application:**
    Use the Maven wrapper to compile and run the application. It will connect to the ScyllaDB instance running in Docker.

    ```bash
    ./mvnw spring-boot:run
    ```

4.  The application will be accessible at `http://localhost:8080`.

---

## API Documentation

The project uses SpringDoc to automatically generate OpenAPI 3 documentation. You can access the interactive Swagger UI here:

-   **URL:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Project Structure

The project follows a clean, layered architecture:

```
src
├── main
│   ├── java
│   │   └── inc/yowyob/rental_api_reactive
│   │       ├── application       # Core business logic, services, DTOs
│   │       ├── infrastructure    # Framework-specific code (web controllers, security)
│   │       └── persistence       # Data access layer (entities, repositories, mappers)
│   └── resources
│       └── application.properties # Main configuration
├── test                        # Unit and integration tests
...
```

---

## Environment Variables

The application can be configured using environment variables, which is especially useful when running with Docker.

| Variable                               | Description                                      | Default Value      |
| -------------------------------------- | ------------------------------------------------ | ------------------ |
| `SPRING_CASSANDRA_CONTACT_POINTS`      | The hostname of the ScyllaDB/Cassandra instance. | `scylladb`         |
| `SPRING_CASSANDRA_PORT`                | The port for the database.                       | `9042`             |
| `SPRING_CASSANDRA_KEYSPACE_NAME`       | The keyspace to use.                             | `rental`           |
| `SPRING_CASSANDRA_LOCAL_DATACENTER`    | The local datacenter name.                       | `datacenter1`      |
| `SPRING_CASSANDRA_USERNAME`            | Database username.                               | `cassandra`        |
| `SPRING_CASSANDRA_PASSWORD`            | Database password.                               | `cassandra`        |
| `APP_JWT_SECRET`                       | Secret key for signing JWTs (must be long).      | (A default is set) |
| `APP_JWT_EXPIRATION`                   | JWT access token expiration time in ms.          | `86400000` (24h)   |
| `APP_JWT_REFRESHEXPIRATION`            | JWT refresh token expiration time in ms.         | `604800000` (7d)   |
| `JAVA_OPTS`                            | JVM options (e.g., memory settings).             | `-Xms256m -Xmx512m` |

---

## Makefile Commands

A `Makefile` is included with shortcuts for common operations.

-   `make help`: Shows all available commands.
-   `make docker-start`: Starts the ScyllaDB database only.
-   `make docker-app`: Builds and starts the complete application stack (app + db).
-   `make docker-stop`: Stops all running services.
-   `make docker-build`: Rebuilds the application's Docker image.
-   `make docker-logs`: Tails the logs from all services.
-   `make docker-clean`: Stops and removes all containers, volumes, and networks. **(Warning: Deletes all data)**
-   `make test`: Runs the unit tests using Maven.
-   `make package`: Packages the application into a JAR file.
-   `make run`: Runs the application locally using Maven.

---

## Testing

The project is configured to run both unit and integration tests.

-   **Run Unit Tests:**
    ```bash
    ./mvnw test
    ```
    This will execute tests matching `*Test.java` and `*Tests.java`.

-   **Run Integration Tests:**
    ```bash
    ./mvnw verify
    ```
    This will execute tests matching `*IntegrationTest.java` and `*IT.java`. The integration tests use Testcontainers to spin up a dedicated database instance.

---

## Technology Stack

-   **Framework:** Spring Boot 3.3.4 (WebFlux)
-   **Language:** Java 21
-   **Reactive Programming:** Project Reactor
-   **Database:** ScyllaDB 5.2 (Cassandra-compatible)
-   **Security:** Spring Security, JWT
-   **API Documentation:** SpringDoc (OpenAPI 3)
-   **Mapping:** MapStruct
-   **Testing:** JUnit 5, Mockito, AssertJ, Testcontainers
-   **Build Tool:** Maven
-   **Containerization:** Docker, Docker Compose
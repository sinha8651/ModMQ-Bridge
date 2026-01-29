# ModMQ-Bridge  

ModMQ-Bridge Service is a Spring Boot–based backend application that transforms **Modbus polling data into real-time MQTT streams**, enabling device data to be accessed anytime and anywhere.

The application manages bridge executors, establishes TCP connections to slave devices, and periodically executes polling operations using a scheduler.

---

## Key Features

* Scheduled bridge execution (every **3 minutes**)
* TCP/Modbus-based device connectivity
* Real-time data publishing to MQTT
* REST APIs for managing bridge executors
* Swagger UI for interactive API documentation
* Robust exception handling and structured logging

---

## Tech Stack

* **Java 17**
* **Spring Boot**
* Spring Web (REST APIs)
* JPA / Hibernate
* MQTT Client
* MODBUS Slave
* Maven

---

## API Documentation

Once the application is running, APIs can be explored using Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

## Database

The application uses **PostgreSQL** for persistent storage.

### Current State (v1.0.0)

* PostgreSQL is integrated using JPA / Hibernate
* Used for persisting:

  * Bridge executor configurations
  * TCP connection metadata
  * Application-related configuration data

## Scheduling

Bridge execution is handled using Spring’s scheduling mechanism.

* Execution interval: **every 3 minutes**
* Ensures periodic polling of Modbus devices
* Designed to avoid overlapping executions

---

## Getting Started

### Prerequisites

* Java 17+
* Maven
* MQTT Broker (e.g., Mosquitto)
* MODBUS Slave

## PostgreSQL Configuration

The application uses PostgreSQL as its database.

Update the following properties before running the application:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/mod_gateway
spring.datasource.username=p_mod_gateway
spring.datasource.password=postgres
```

Make sure PostgreSQL is running and the database exists.

> Note: The above database configuration is intended for local development only.
---

### Build

```bash
mvn clean package
```

### Run

```bash
java -jar target/modbus-mqtt-gateway-1.0.0.jar
```

---

## Versioning & Releases

* Current Release: **v1.0.0**
* Versioning follows semantic versioning:

  * `v1.0.x` – Bug fixes
  * `v1.x.0` – New features
  * `v2.0.0` – Breaking changes

---

## License

Internal / Private Project (update as applicable)

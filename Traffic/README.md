# Distributed Smart Urban Traffic Management System

A mini distributed system that simulates traffic monitoring for one intersection in a smart city.

## Overview

This project implements a distributed traffic management system with:
- Camera simulator for traffic metadata generation
- Kafka-based message streaming
- Central analysis service with rule-based traffic analysis
- MySQL database for persistence
- JAX-RS REST APIs for dashboard integration
- Java RMI for camera control

## Architecture

```
Camera Simulator (RMI) → Kafka → Central Analysis Service (JAX-RS) → MySQL → Web Dashboard
```

## Technologies

- **Java 11+**
- **Apache Kafka** - Message streaming
- **JAX-RS (Jersey)** - REST API
- **Java RMI** - Distributed camera control
- **MySQL** - Data persistence
- **Jackson** - JSON processing
- **Maven** - Build tool

## Prerequisites

- Java 11 or higher
- Apache Kafka 3.x
- MySQL 8.x
- Maven 3.6+

## Setup

### 1. Database Setup

```bash
mysql -u root -p < database/schema.sql
```

### 2. Kafka Setup

Start Zookeeper:
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

Start Kafka:
```bash
bin/kafka-server-start.sh config/server.properties
```

Create required topics:
```bash
bin/kafka-topics.sh --create --topic camera-data --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic traffic-alerts --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic traffic-recommendations --bootstrap-server localhost:9092
```

### 3. Build Project

```bash
mvn clean package
```

## Running the System

### 1. Start Central Analysis Service

```bash
java -cp target/traffic-management-1.0-SNAPSHOT.jar com.smartcity.traffic.CentralAnalysisMain
```

This starts:
- Kafka consumer listening for camera events
- JAX-RS REST API server on http://localhost:8080
- Traffic analysis engine

### 2. Start Camera Simulator

```bash
java -cp target/traffic-management-1.0-SNAPSHOT.jar com.smartcity.traffic.CameraSimulatorMain
```

This starts:
- RMI server for camera control
- Periodic traffic metadata generation (every 5 seconds)
- Kafka producer publishing to `camera-data` topic

## REST API Endpoints

### Get Latest Traffic State
```http
GET /api/traffic/latest
```

### Get Traffic History
```http
GET /api/traffic/history?from=2026-03-15T00:00:00&to=2026-03-15T23:59:59
```

### Get Active Alerts
```http
GET /api/alerts
```

### Get Recommendations
```http
GET /api/recommendations
```

### Get Camera Status
```http
GET /api/cameras/{id}/status
```

## RMI Camera Control

Connect to RMI registry on `localhost:1099`:

```java
CameraRemoteService camera = (CameraRemoteService) 
    Naming.lookup("rmi://localhost:1099/CameraService");

camera.startSimulation();
camera.stopSimulation();
String status = camera.getStatus();
```

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database
db.url=jdbc:mysql://localhost:3306/traffic_management
db.username=root
db.password=yourpassword

# Kafka
kafka.bootstrap.servers=localhost:9092
kafka.group.id=traffic-analysis-group
kafka.topic.camera.data=camera-data

# RMI
rmi.port=1099
rmi.service.name=CameraService

# API Server
api.host=localhost
api.port=8080
```

## Project Structure

```
src/main/java/com/smartcity/traffic/
├── model/              # Domain entities
├── dto/                # Data Transfer Objects
├── kafka/              # Kafka producers/consumers
├── service/            # Business logic services
├── repository/         # Database repositories
├── api/                # JAX-RS REST resources
├── rmi/                # RMI interfaces and implementations
└── util/               # Utilities and configuration
```

## Traffic Analysis Rules

The system uses simple rule-based logic:

- **NORMAL**: vehicleCount < 20 && averageSpeed >= 30
- **BUSY**: vehicleCount 20-35 && averageSpeed 15-30
- **CONGESTED**: vehicleCount > 35 && averageSpeed < 15
- **CRITICAL**: vehicleCount > 45 && averageSpeed < 8
- **INCIDENT**: suspectedIncident flag is true

## License

Educational project for distributed systems course.

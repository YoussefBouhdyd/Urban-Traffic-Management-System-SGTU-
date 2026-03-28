# Traffic Camera & Analysis Service

## Overview

The Traffic Camera & Analysis Service is a distributed system for real-time traffic monitoring and analysis using camera-based data. It consists of two main components: a camera simulator that generates traffic events, and a central analysis service that processes these events, performs traffic analysis, generates alerts and recommendations, and exposes REST APIs for dashboard consumption.

## Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                    Traffic Camera Service                       │
└────────────────────────────────────────────────────────────────┘

┌──────────────────────┐                 ┌──────────────────────┐
│  Camera Simulator    │                 │ Central Analysis     │
│                      │                 │     Service          │
│  ┌────────────────┐  │                 │  ┌────────────────┐  │
│  │                │  │                 │  │ REST API       │  │
│  │ Event Generator│  │    Camera Data  │  │ (Port 8083)    │  │
│  │                │  │    ────────────►│  │                │  │
│  └────────────────┘  │                 │  └────────────────┘  │
│          │           │                 │          │           │
│          ▼           │                 │          ▼           │
│  ┌────────────────┐  │                 │  ┌────────────────┐  │
│  │ RMI Server     │  │                 │  │ Event Consumer │  │
│  │ (Port 1099)    │  │                 │  │ (Kafka)        │  │
│  └────────────────┘  │                 │  └────────────────┘  │
│          │           │                 │          │           │
│          ▼           │                 │          ▼           │
│  ┌────────────────┐  │                 │  ┌────────────────┐  │
│  │ Kafka Producer │  │                 │  │ Analysis Engine│  │
│  └────────────────┘  │                 │  └────────────────┘  │
└──────────────────────┘                 │          │           │
                                         │          ▼           │
                                         │  ┌────────────────┐  │
                                         │  │ MySQL Database │  │
                                         │  └────────────────┘  │
                                         └──────────────────────┘
```

## Components

### 1. Camera Simulator

**Location:** `Traffic/src/main/java/com/smartcity/traffic/CameraSimulatorMain.java`

**Purpose:** Simulates traffic camera data generation for testing and demonstration purposes.

**Responsibilities:**
- Generate realistic traffic metadata every 5 seconds
- Publish camera events to Kafka topic `camera-data`
- Expose RMI interface for remote control and configuration
- Update camera status in MySQL database
- Simulate various traffic conditions (light, moderate, heavy, critical)

**Key Classes:**
- `CameraSimulatorMain` - Main entry point
- `CameraSimulationService` - Core simulation logic
- `CameraRemoteServiceImpl` - RMI interface implementation
- `CameraEventProducer` - Kafka producer for events

**Configuration Properties:**
```properties
# Camera Configuration
camera.id=CAM-01
intersection.id=INT-01
camera.event.interval.seconds=5

# RMI Configuration
rmi.port=1099
rmi.service.name=CameraService
```

**Running the Simulator:**
```bash
cd Traffic
java -cp "target/traffic-management-1.0-SNAPSHOT.jar:target/lib/*" \
  com.smartcity.traffic.CameraSimulatorMain
```

### 2. Central Analysis Service

**Location:** `Traffic/src/main/java/com/smartcity/traffic/CentralAnalysisMain.java`

**Purpose:** Consumes camera events, performs traffic analysis, generates alerts and recommendations, and provides REST APIs.

**Responsibilities:**
- Consume camera events from Kafka
- Analyze traffic conditions using rule-based algorithms
- Generate traffic alerts based on thresholds
- Create AI-powered recommendations for traffic optimization
- Store all data in MySQL database
- Expose RESTful APIs for dashboard consumption

**Key Classes:**
- `CentralAnalysisMain` - Main entry point and HTTP server setup
- `CameraEventConsumer` - Kafka consumer
- `TrafficAnalysisService` - Traffic analysis engine
- REST Resources:
  - `TrafficResource` - Traffic state endpoints
  - `AlertResource` - Alert management endpoints
  - `RecommendationResource` - Recommendation endpoints
  - `CameraResource` - Camera status endpoints

**Configuration Properties:**
```properties
# API Configuration
api.host=localhost
api.port=8083

# Kafka Configuration
kafka.bootstrap.servers=localhost:9092
kafka.group.id=traffic-analysis-group
kafka.topic.camera.data=camera-data
kafka.auto.offset.reset=latest

# Database Configuration
db.url=jdbc:mysql://localhost:3306/integrated_traffic_system
db.username=root
db.password=
```

**Running the Service:**
```bash
cd Traffic
java -jar target/traffic-management-1.0-SNAPSHOT.jar
```

## Traffic Analysis Algorithm

The system uses a rule-based algorithm to analyze traffic conditions:

### Vehicle Count Thresholds

| Condition | Vehicle Count | Description |
|-----------|--------------|-------------|
| Light | 0 - 20 | Normal traffic flow |
| Moderate | 21 - 50 | Increased traffic |
| Heavy | 51 - 80 | Significant congestion |
| Critical | 81+ | Severe congestion |

### Alert Generation

Alerts are automatically generated when:
- Vehicle count exceeds 80 (critical congestion)
- Vehicle count exceeds 50 (heavy congestion)
- Average speed drops below 20 km/h
- Unusual traffic patterns detected

### Recommendation Engine

The system generates recommendations based on:
- Current traffic density
- Historical traffic patterns
- Traffic light timing optimization
- Route suggestions for drivers

## REST API Endpoints

Base URL: `http://localhost:8083/api`

### 1. Get Latest Traffic State

**Endpoint:** `GET /traffic/latest`

**Description:** Returns the current traffic analysis for all cameras

**Response Example:**
```json
{
  "cameraId": "CAM-01",
  "intersectionId": "INT-01",
  "timestamp": "2026-03-28T10:30:00",
  "vehicleCount": 45,
  "averageSpeed": 35.5,
  "congestionLevel": "MODERATE",
  "analysis": "Moderate traffic conditions detected",
  "recommendations": ["Adjust traffic light timing", "Monitor situation"]
}
```

### 2. Get Traffic History

**Endpoint:** `GET /traffic/history`

**Query Parameters:**
- `hours` (optional) - Number of hours of history (default: 24)

**Response:** Array of traffic analysis records

### 3. Get Active Alerts

**Endpoint:** `GET /alerts`

**Query Parameters:**
- `status` (optional) - Filter by status (ACTIVE, RESOLVED)
- `severity` (optional) - Filter by severity (HIGH, MEDIUM, LOW)

**Response Example:**
```json
[
  {
    "id": 123,
    "type": "CONGESTION",
    "severity": "HIGH",
    "message": "Heavy congestion detected at intersection INT-01",
    "cameraId": "CAM-01",
    "timestamp": "2026-03-28T10:25:00",
    "status": "ACTIVE"
  }
]
```

### 4. Get Recommendations

**Endpoint:** `GET /recommendations`

**Query Parameters:**
- `priority` (optional) - Filter by priority (HIGH, MEDIUM, LOW)

**Response Example:**
```json
[
  {
    "id": 456,
    "recommendation": "Increase green light duration for north-south route",
    "reason": "Heavy traffic detected on north approach",
    "priority": "HIGH",
    "cameraId": "CAM-01",
    "timestamp": "2026-03-28T10:30:00"
  }
]
```

### 5. Get Camera Status

**Endpoint:** `GET /cameras/{cameraId}/status`

**Path Parameters:**
- `cameraId` - Camera identifier (e.g., CAM-01)

**Response Example:**
```json
{
  "cameraId": "CAM-01",
  "intersectionId": "INT-01",
  "status": "RUNNING",
  "lastUpdate": "2026-03-28T10:30:00",
  "eventsGenerated": 12540
}
```

### 6. Get All Cameras

**Endpoint:** `GET /cameras`

**Response:** Array of all camera status records

## RMI Remote Interface

The camera simulator exposes an RMI interface for remote control.

**Interface:** `CameraRemoteService`

**RMI URL:** `rmi://localhost:1099/CameraService`

**Available Methods:**
- `startSimulation()` - Start generating events
- `stopSimulation()` - Stop generating events
- `setEventInterval(int seconds)` - Change generation interval
- `getStatus()` - Get current simulator status
- `generateEvent()` - Manually trigger a single event

**Example Usage:**
```java
Registry registry = LocateRegistry.getRegistry("localhost", 1099);
CameraRemoteService service = (CameraRemoteService) registry.lookup("CameraService");
service.startSimulation();
```

## Database Schema

### Table: camera_events
Stores all camera events generated by the simulator.

```sql
CREATE TABLE camera_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    camera_id VARCHAR(50) NOT NULL,
    intersection_id VARCHAR(50),
    timestamp TIMESTAMP,
    vehicle_count INT,
    metadata TEXT,
    INDEX idx_camera_timestamp (camera_id, timestamp)
);
```

### Table: traffic_analysis
Stores traffic analysis results.

```sql
CREATE TABLE traffic_analysis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    camera_id VARCHAR(50) NOT NULL,
    intersection_id VARCHAR(50),
    timestamp TIMESTAMP,
    vehicle_count INT,
    average_speed DOUBLE,
    congestion_level VARCHAR(20),
    analysis TEXT,
    INDEX idx_timestamp (timestamp)
);
```

### Table: alerts
Stores traffic alerts.

```sql
CREATE TABLE alerts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL,
    severity VARCHAR(20),
    message TEXT,
    camera_id VARCHAR(50),
    timestamp TIMESTAMP,
    status VARCHAR(20),
    INDEX idx_status_timestamp (status, timestamp)
);
```

### Table: recommendations
Stores system recommendations.

```sql
CREATE TABLE recommendations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recommendation TEXT,
    reason TEXT,
    priority VARCHAR(20),
    camera_id VARCHAR(50),
    timestamp TIMESTAMP,
    INDEX idx_priority_timestamp (priority, timestamp)
);
```

### Table: camera_status
Tracks camera operational status.

```sql
CREATE TABLE camera_status (
    camera_id VARCHAR(50) PRIMARY KEY,
    intersection_id VARCHAR(50),
    status VARCHAR(20),
    last_update TIMESTAMP,
    events_generated BIGINT
);
```

## Kafka Integration

### Topics

**camera-data**
- Purpose: Camera event streaming
- Partitions: 1
- Replication Factor: 1
- Message Format: JSON

**Example Message:**
```json
{
  "cameraId": "CAM-01",
  "intersectionId": "INT-01",
  "timestamp": "2026-03-28T10:30:00Z",
  "vehicleCount": 45,
  "metadata": {
    "averageSpeed": 35.5,
    "weather": "clear",
    "visibility": "good"
  }
}
```

### Producer Configuration

```properties
bootstrap.servers=localhost:9092
key.serializer=org.apache.kafka.common.serialization.StringSerializer
value.serializer=org.apache.kafka.common.serialization.StringSerializer
acks=all
retries=3
```

### Consumer Configuration

```properties
bootstrap.servers=localhost:9092
group.id=traffic-analysis-group
key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
auto.offset.reset=latest
enable.auto.commit=true
```

## Development

### Build Requirements

- Java JDK 21
- Apache Maven 3.6+
- Access to MySQL database
- Apache Kafka running

### Building

```bash
cd Traffic
mvn clean package
```

This generates:
- `target/traffic-management-1.0-SNAPSHOT.jar` - Main JAR file
- `target/lib/` - All dependencies

### Running Tests

```bash
mvn test
```

### Debugging

Enable debug logging by setting:
```properties
logging.level=DEBUG
```

View logs in real-time:
```bash
tail -f logs/traffic-camera.log
```

## Monitoring

### Health Check

The service exposes a health endpoint:
```bash
curl http://localhost:8083/api/health
```

### Metrics

Key metrics to monitor:
- Events processed per minute
- Analysis latency
- Alert generation rate
- Database connection pool status
- Kafka consumer lag

## Troubleshooting

### Common Issues

**Problem:** Service fails to start
**Solution:** Check if port 8083 is available and MySQL/Kafka are running

**Problem:** No camera events being generated
**Solution:** Verify the simulator is running and RMI service is accessible

**Problem:** Kafka connection errors
**Solution:** Ensure Kafka broker is running on port 9092

**Problem:** Database connection failures
**Solution:** Check MySQL credentials and database existence

### Logs

Logs are written to:
- `logs/traffic-camera.log` - Central Analysis Service logs
- `logs/camera-simulator.log` - Camera Simulator logs

## Performance Considerations

### Scalability

The system can be scaled by:
- Running multiple camera simulators with different camera IDs
- Increasing Kafka partitions for parallel processing
- Using database read replicas for API queries
- Adding caching layer (Redis) for frequently accessed data

### Optimization Tips

1. Adjust event generation interval based on system load
2. Use database connection pooling (default: 10 connections)
3. Implement batch processing for database inserts
4. Use asynchronous processing for non-critical operations
5. Monitor and tune Kafka consumer threads

## Security Considerations

1. Enable authentication for RMI interface in production
2. Use HTTPS for REST API endpoints
3. Implement API rate limiting
4. Secure database credentials using environment variables
5. Enable Kafka SSL/SASL for production environments

## Future Enhancements

Potential improvements for the service:

1. Machine learning-based traffic prediction
2. Integration with real camera hardware
3. Video analytics capabilities
4. Multi-intersection coordination
5. Real-time traffic routing optimization
6. Mobile app integration
7. Weather data integration
8. Incident detection algorithms

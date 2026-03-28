# System Architecture

## Overview

The Distributed Smart Urban Traffic Management System is composed of two main services and several supporting components:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Distributed Traffic System                       │
└─────────────────────────────────────────────────────────────────────┘

┌────────────────────┐          ┌────────────────────┐
│ Camera Simulator   │          │  Central Analysis  │
│    Service         │          │     Service        │
│                    │          │                    │
│ ┌────────────────┐ │          │ ┌────────────────┐ │
│ │ RMI Server     │ │          │ │ REST API       │ │
│ │ (Port 1099)    │ │          │ │ (Port 8080)    │ │
│ └────────────────┘ │          │ └────────────────┘ │
│                    │          │                    │
│ ┌────────────────┐ │          │ ┌────────────────┐ │
│ │ Event Generator│ │   Kafka  │ │ Event Consumer │ │
│ │                ├─┼──────────┼─►│                │ │
│ └────────────────┘ │          │ └────────────────┘ │
│                    │          │                    │
│ ┌────────────────┐ │          │ ┌────────────────┐ │
│ │ Kafka Producer │ │          │ │ Analysis Engine│ │
│ └────────────────┘ │          │ └────────────────┘ │
│                    │          │          │         │
└────────────────────┘          │          ▼         │
                                │ ┌────────────────┐ │
                                │ │ MySQL Database │ │
                                │ └────────────────┘ │
                                │          │         │
                                │          ▼         │
                                │ ┌────────────────┐ │
                                │ │ REST Endpoints │ │
                                │ └────────────────┘ │
                                └────────────────────┘
                                          │
                                          ▼
                                ┌────────────────────┐
                                │  Web Dashboard     │
                                │   (Frontend)       │
                                └────────────────────┘
```

## Components

### 1. Camera Simulator Service

**Main Class:** `CameraSimulatorMain`

**Responsibilities:**
- Generate simulated traffic metadata every 5 seconds
- Publish events to Kafka topic `camera-data`
- Expose RMI interface for remote control
- Update camera status in database

**Key Classes:**
- `CameraSimulationService` - Core simulation logic
- `CameraRemoteServiceImpl` - RMI implementation
- `CameraEventProducer` - Kafka producer

**Configuration:**
```properties
camera.id=CAM-01
intersection.id=INT-01
camera.event.interval.seconds=5
rmi.port=1099
rmi.service.name=CameraService
```

---

### 2. Central Analysis Service

**Main Class:** `CentralAnalysisMain`

**Responsibilities:**
- Consume camera events from Kafka
- Analyze traffic conditions using rule-based logic
- Generate alerts and recommendations
- Store all data in MySQL
- Expose REST APIs for dashboard

**Key Classes:**
- `CameraEventConsumer` - Kafka consumer
- `TrafficAnalysisService` - Analysis engine
- REST Resources (Traffic, Alert, Recommendation, Camera)
- Repositories for database access

**Configuration:**
```properties
api.host=localhost
api.port=8080
kafka.bootstrap.servers=localhost:9092
kafka.group.id=traffic-analysis-group
```

---

### 3. Apache Kafka

**Purpose:** Message broker for event streaming

**Topics:**
- `camera-data` - Raw camera events (used)
- `traffic-alerts` - Generated alerts (optional, for future use)
- `traffic-recommendations` - Generated recommendations (optional, for future use)

**Current Implementation:**
Only `camera-data` topic is actively used. Alerts and recommendations are stored directly in MySQL.

---

### 4. MySQL Database

**Database:** `traffic_management`

**Tables:**

#### camera_events
Stores raw camera metadata.
```sql
- id (PK)
- camera_id
- intersection_id
- timestamp
- vehicle_count
- average_speed
- stopped_vehicles
- pedestrian_crossing
- suspected_incident
- congestion_hint
```

#### traffic_analysis
Stores analyzed traffic states.
```sql
- id (PK)
- intersection_id
- camera_id
- timestamp
- traffic_state (NORMAL, BUSY, CONGESTED, INCIDENT)
- severity (LOW, MEDIUM, HIGH, CRITICAL)
- vehicle_count
- average_speed
- recommendation
```

#### alerts
Stores generated alerts.
```sql
- id (PK)
- type (CONGESTION, INCIDENT, PEDESTRIAN_WARNING)
- severity
- message
- timestamp
- status (ACTIVE, RESOLVED, DISMISSED)
- intersection_id
- camera_id
```

#### recommendations
Stores traffic recommendations.
```sql
- id (PK)
- intersection_id
- camera_id
- timestamp
- recommendation
- reason
- priority
```

#### camera_status
Stores camera simulator status.
```sql
- camera_id (PK)
- intersection_id
- status (RUNNING, STOPPED, ERROR)
- last_update
```

---

## Data Flow

### Event Processing Pipeline

```
1. Camera Simulator generates event
   └─> CameraEvent object created with simulated metadata

2. Event sent to Kafka
   └─> Serialized to JSON
   └─> Published to "camera-data" topic

3. Central Service consumes event
   └─> Deserialized from JSON
   └─> Stored in camera_events table

4. Analysis performed
   └─> Rules applied based on:
       - Vehicle count
       - Average speed
       - Stopped vehicles
       - Incident flags
       - Pedestrian crossing
   
5. Results generated
   └─> TrafficAnalysis created and stored
   └─> Alerts created if needed
   └─> Recommendations created if needed

6. Data available via REST API
   └─> Dashboard queries endpoints
   └─> JSON responses returned
```

---

## Traffic Analysis Rules

The system uses the following rule-based logic:

### Rule Priority (Highest to Lowest)

1. **INCIDENT** (Critical)
   - Condition: `suspectedIncident == true`
   - Action: Dispatch verification team

2. **HEAVY CONGESTION** (Critical)
   - Condition: `vehicleCount > 45 AND averageSpeed < 8 AND stoppedVehicles > 6`
   - Action: Apply traffic diversion

3. **CONGESTION** (High)
   - Condition: `vehicleCount > 35 AND averageSpeed < 15`
   - Action: Increase green light duration

4. **BUSY** (Medium)
   - Condition: `vehicleCount 20-35 AND averageSpeed 15-30`
   - Action: Monitor density

5. **NORMAL** (Low)
   - Condition: `vehicleCount < 20 AND averageSpeed >= 30`
   - Action: No action needed

6. **PEDESTRIAN WARNING** (Medium)
   - Condition: `pedestrianCrossing == true AND vehicleCount > 30`
   - Action: Monitor crossing zone

---

## REST API Endpoints

### Traffic Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/traffic/latest` | Latest traffic state |
| GET | `/api/traffic/history` | Historical traffic data |

### Alert Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/alerts` | Active alerts |

### Recommendation Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/recommendations` | Latest recommendations |

### Camera Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/cameras/{id}/status` | Camera status |

---

## RMI Interface

**Service Name:** `CameraService`  
**Port:** `1099`  
**Location:** `rmi://localhost:1099/CameraService`

### Methods

```java
void startSimulation()
void stopSimulation()
String getStatus()
CameraEvent getLastGeneratedEvent()
```

### Usage Example

```java
import java.rmi.Naming;
import com.smartcity.traffic.rmi.CameraRemoteService;

public class RMIClient {
    public static void main(String[] args) throws Exception {
        CameraRemoteService camera = (CameraRemoteService) 
            Naming.lookup("rmi://localhost:1099/CameraService");
        
        // Control camera
        camera.startSimulation();
        System.out.println("Status: " + camera.getStatus());
        camera.stopSimulation();
    }
}
```

---

## Package Structure

```
com.smartcity.traffic/
├── model/              # Domain entities
│   ├── CameraEvent
│   ├── TrafficAnalysis
│   ├── Alert
│   ├── Recommendation
│   └── CameraStatus
│
├── dto/                # Data Transfer Objects
│   ├── TrafficLatestResponse
│   ├── TrafficHistoryItemResponse
│   ├── AlertResponse
│   ├── RecommendationResponse
│   └── CameraStatusResponse
│
├── kafka/              # Kafka integration
│   ├── CameraEventProducer
│   └── CameraEventConsumer
│
├── service/            # Business logic
│   ├── CameraSimulationService
│   └── TrafficAnalysisService
│
├── repository/         # Database access
│   ├── CameraEventRepository
│   ├── TrafficAnalysisRepository
│   ├── AlertRepository
│   ├── RecommendationRepository
│   └── CameraStatusRepository
│
├── api/                # JAX-RS REST resources
│   ├── TrafficResource
│   ├── AlertResource
│   ├── RecommendationResource
│   └── CameraResource
│
├── rmi/                # RMI interface
│   ├── CameraRemoteService (interface)
│   └── CameraRemoteServiceImpl
│
├── util/               # Utilities
│   ├── Config
│   └── DatabaseConnection
│
├── CentralAnalysisMain      # Main entry point for analysis service
├── CameraSimulatorMain      # Main entry point for camera simulator
└── ObjectMapperContextResolver # Jackson configuration
```

---

## Technology Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| Language | Java 11 | Core programming language |
| Build Tool | Maven | Dependency management & build |
| REST API | JAX-RS (Jersey) | RESTful web services |
| HTTP Server | Grizzly | Embedded HTTP server |
| RMI | Java RMI | Remote method invocation |
| Messaging | Apache Kafka | Event streaming |
| Database | MySQL | Data persistence |
| JDBC Driver | MySQL Connector/J | Database connectivity |
| JSON | Jackson | JSON serialization |
| Logging | SLF4J + Simple | Logging framework |

---

## Scalability Considerations

### Current Implementation (Single Intersection)
- 1 camera simulator
- 1 intersection
- Simple rule-based analysis

### Future Enhancements

1. **Multiple Cameras**
   - Multiple CameraSimulatorMain instances
   - Different camera IDs in configuration
   - Same Kafka topic or separate topics

2. **Multiple Intersections**
   - Partition Kafka topics by intersection ID
   - Separate analysis per intersection
   - Aggregate view for city-wide monitoring

3. **Distributed Analysis**
   - Multiple Central Analysis instances
   - Load balancing with reverse proxy
   - Shared database or database clustering

4. **Advanced Features**
   - Machine learning models for prediction
   - Real-time traffic light control integration
   - Historical trend analysis
   - Incident prediction
   - Route optimization suggestions

---

## Performance Metrics

### Camera Simulator
- Event generation rate: Configurable (default 5 seconds)
- Kafka producer throughput: ~200 events/second (if needed)

### Central Analysis
- Event processing latency: < 100ms
- Database write latency: < 50ms
- REST API response time: < 50ms

### Database
- Tables are indexed for optimal query performance
- Timestamps indexed for time-range queries
- Foreign key relationships maintained

---

## Monitoring & Observability

### Logs
- SLF4J logging throughout the application
- Configurable log levels
- Console output for development

### Metrics to Monitor
- Kafka lag (consumer behind producer)
- Database connection pool usage
- API response times
- Event processing rate
- Alert generation frequency

### Health Checks
- Database connectivity
- Kafka connectivity
- REST API availability
- RMI service availability

---

## Security Considerations

### Current Implementation
- No authentication (suitable for development)
- No authorization
- No encryption
- Direct database access

### Production Recommendations
- Add JWT or OAuth2 authentication
- Implement role-based access control (RBAC)
- Use HTTPS for REST API
- Encrypt database connections (SSL/TLS)
- Secure RMI with SSL
- Use Kafka SASL/SSL for secure messaging
- Implement rate limiting on APIs
- Add input validation and sanitization

---

## Testing Strategy

### Unit Testing
- Repository layer tests
- Service layer tests
- Analysis rule tests

### Integration Testing
- Kafka producer/consumer tests
- Database integration tests
- REST API endpoint tests

### End-to-End Testing
1. Start all services
2. Generate camera events
3. Verify Kafka message delivery
4. Verify database persistence
5. Verify REST API responses
6. Test RMI operations

---

## Deployment Architecture

### Development
```
Laptop/Workstation
├── MySQL (localhost:3306)
├── Kafka (localhost:9092)
├── Zookeeper (localhost:2181)
├── Camera Simulator (RMI :1099)
└── Central Analysis (HTTP :8080)
```

### Production (Example)
```
Cloud Infrastructure
├── Database Tier
│   └── MySQL Cluster (e.g., AWS RDS, Azure Database)
├── Messaging Tier
│   └── Kafka Cluster (e.g., Confluent Cloud, AWS MSK)
├── Application Tier
│   ├── Camera Simulators (multiple instances)
│   └── Analysis Services (load balanced)
└── API Gateway
    └── Reverse Proxy (NGINX, API Gateway)
```

---

## Conclusion

This architecture provides a solid foundation for a distributed traffic management system with:
- Clear separation of concerns
- Scalable messaging with Kafka
- Persistent data storage with MySQL
- RESTful API for integration
- RMI for distributed control

The system can be extended with additional features, more complex analysis rules, or integration with actual traffic cameras and control systems.

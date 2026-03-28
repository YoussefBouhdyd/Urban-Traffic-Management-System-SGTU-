# Traffic Management Services

## Overview

The Traffic Management Services provide comprehensive traffic flow control through SOAP-based traffic light management and REST-based traffic data centralization. These services enable real-time traffic light control, traffic flow monitoring, route management, and integration with the dashboard and other subsystems.

## Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                 Traffic Management Services                     │
└────────────────────────────────────────────────────────────────┘

┌──────────────────────┐         ┌──────────────────────┐
│  SOAP Services       │         │  Centrale Service    │
│  (Port 8080)         │         │  (REST - Port 9999)  │
│                      │         │                      │
│  ┌────────────────┐  │         │  ┌────────────────┐  │
│  │ Service Feux   │  │         │  │ Traffic Flow   │  │
│  │ (Traffic Light)│  │         │  │ Aggregation    │  │
│  │                │  │         │  │                │  │
│  └────────────────┘  │         │  └────────────────┘  │
│  ┌────────────────┐  │         │  ┌────────────────┐  │
│  │ Service Trafic │  │         │  │ Route Manager  │  │
│  │ (Traffic Flow) │  │         │  │                │  │
│  │                │  │         │  │                │  │
│  └────────────────┘  │         │  └────────────────┘  │
│  ┌────────────────┐  │         │  ┌────────────────┐  │
│  │ Service Client │  │         │  │ Real-time API  │  │
│  │ (Client Mgmt)  │  │         │  │                │  │
│  │                │  │         │  │                │  │
│  └────────────────┘  │         │  └────────────────┘  │
└──────────────────────┘         └──────────────────────┘
           │                                  │
           └──────────────┬───────────────────┘
                          ▼
                ┌──────────────────┐
                │ MySQL Database   │
                │  (Port 3306)     │
                └──────────────────┘
```

## Components

### 1. SOAP Services (Port 8080)

**Location:** `javaproject/services/`

**Purpose:** Provide SOAP-based web services for traffic light control, traffic flow management, and client operations.

#### Service: Feux (Traffic Light Control)

**WSDL:** `http://localhost:8080/Services/ServiceFeux?wsdl`

**Operations:**

**setFeu(String routeId, String color)**
- Description: Set traffic light color for a specific route
- Parameters:
  - `routeId`: Route identifier (nord, sud, est, ouest)
  - `color`: Light color (VERT, ROUGE, ORANGE)
- Returns: Success confirmation
- Example:
  ```xml
  <routeId>nord</routeId>
  <color>VERT</color>
  ```

**getFeu(String routeId)**
- Description: Get current traffic light status for a route
- Parameters:
  - `routeId`: Route identifier
- Returns: Current light color and timing information
- Response Example:
  ```xml
  <currentColor>VERT</currentColor>
  <timeRemaining>45</timeRemaining>
  <routeId>nord</routeId>
  ```

**getAllFeux()**
- Description: Get status of all traffic lights
- Parameters: None
- Returns: Array of all traffic light states

**setAutoMode(boolean enabled)**
- Description: Enable or disable automatic traffic light control
- Parameters:
  - `enabled`: true for automatic, false for manual
- Returns: Success confirmation

#### Service: Trafic (Traffic Flow Management)

**WSDL:** `http://localhost:8080/Services/ServiceTrafic?wsdl`

**Operations:**

**getTrafficFlow(String routeId)**
- Description: Get real-time traffic flow data for a route
- Parameters:
  - `routeId`: Route identifier
- Returns: Vehicle count, average speed, congestion level
- Response Example:
  ```xml
  <routeId>nord</routeId>
  <vehicleCount>35</vehicleCount>
  <averageSpeed>42.5</averageSpeed>
  <congestionLevel>MODERATE</congestionLevel>
  ```

**getAllTrafficFlows()**
- Description: Get traffic flow for all routes
- Parameters: None
- Returns: Array of traffic flow data for all routes

**getHistoricalData(String routeId, String startTime, String endTime)**
- Description: Retrieve historical traffic data
- Parameters:
  - `routeId`: Route identifier
  - `startTime`: ISO 8601 timestamp
  - `endTime`: ISO 8601 timestamp
- Returns: Array of historical traffic records

**reportIncident(String routeId, String incidentType, String description)**
- Description: Report a traffic incident
- Parameters:
  - `routeId`: Affected route
  - `incidentType`: ACCIDENT, BREAKDOWN, ROADWORK, OTHER
  - `description`: Incident details
- Returns: Incident ID

#### Service: Client (Client Management)

**WSDL:** `http://localhost:8080/Services/ServiceClient?wsdl`

**Operations:**

**registerClient(String clientId, String clientType)**
- Description: Register a new client (dashboard, mobile app, etc.)
- Parameters:
  - `clientId`: Unique client identifier
  - `clientType`: DASHBOARD, MOBILE, API
- Returns: Registration confirmation and API key

**subscribeToUpdates(String clientId, String[] routeIds)**
- Description: Subscribe to traffic updates for specific routes
- Parameters:
  - `clientId`: Client identifier
  - `routeIds`: Array of route IDs to monitor
- Returns: Subscription ID

**unsubscribeFromUpdates(String subscriptionId)**
- Description: Cancel a subscription
- Parameters:
  - `subscriptionId`: Subscription to cancel
- Returns: Confirmation

### 2. Centrale Service (REST API - Port 9999)

**Location:** `javaproject/centraleservice/`

**Base URL:** `http://localhost:9999/centrale/api`

**Purpose:** Centralize traffic data from all sources and provide unified REST APIs for dashboard consumption.

#### Endpoints

**1. Get Traffic Flow Data**

**Endpoint:** `GET /Flux`

**Query Parameters:**
- `routeId` (optional): Filter by specific route (nord, sud, est, ouest)

**Response Example:**
```json
{
  "timestamp": "2026-03-28T10:30:00Z",
  "routes": [
    {
      "routeId": "nord",
      "vehicleCount": 45,
      "averageSpeed": 38.5,
      "congestionLevel": "MODERATE",
      "trafficLight": {
        "currentColor": "VERT",
        "timeRemaining": 35
      }
    },
    {
      "routeId": "sud",
      "vehicleCount": 28,
      "averageSpeed": 52.0,
      "congestionLevel": "LIGHT",
      "trafficLight": {
        "currentColor": "ROUGE",
        "timeRemaining": 20
      }
    }
  ]
}
```

**2. Get Route Status**

**Endpoint:** `GET /Routes/{routeId}`

**Path Parameters:**
- `routeId`: Route identifier (nord, sud, est, ouest)

**Response Example:**
```json
{
  "routeId": "nord",
  "status": "ACTIVE",
  "vehicleCount": 45,
  "averageSpeed": 38.5,
  "congestionLevel": "MODERATE",
  "trafficLight": {
    "currentColor": "VERT",
    "timeRemaining": 35,
    "mode": "AUTO"
  },
  "incidents": [],
  "lastUpdate": "2026-03-28T10:30:00Z"
}
```

**3. Control Traffic Light**

**Endpoint:** `POST /TrafficLights/{routeId}`

**Path Parameters:**
- `routeId`: Route identifier

**Request Body:**
```json
{
  "color": "VERT",
  "duration": 60
}
```

**Response:**
```json
{
  "success": true,
  "routeId": "nord",
  "newColor": "VERT",
  "duration": 60
}
```

**4. Get All Routes Summary**

**Endpoint:** `GET /Routes`

**Response Example:**
```json
[
  {
    "routeId": "nord",
    "congestionLevel": "MODERATE",
    "vehicleCount": 45
  },
  {
    "routeId": "sud",
    "congestionLevel": "LIGHT",
    "vehicleCount": 28
  },
  {
    "routeId": "est",
    "congestionLevel": "HEAVY",
    "vehicleCount": 67
  },
  {
    "routeId": "ouest",
    "congestionLevel": "LIGHT",
    "vehicleCount": 22
  }
}
]
```

**5. Get Traffic Statistics**

**Endpoint:** `GET /Statistics`

**Query Parameters:**
- `period` (optional): DAY, WEEK, MONTH (default: DAY)
- `routeId` (optional): Filter by route

**Response Example:**
```json
{
  "period": "DAY",
  "startTime": "2026-03-28T00:00:00Z",
  "endTime": "2026-03-28T23:59:59Z",
  "statistics": {
    "totalVehicles": 12540,
    "averageSpeed": 42.3,
    "peakHour": "08:00",
    "peakVehicleCount": 145,
    "incidentCount": 3
  }
}
```

**6. Report Traffic Incident**

**Endpoint:** `POST /Incidents`

**Request Body:**
```json
{
  "routeId": "nord",
  "type": "ACCIDENT",
  "severity": "HIGH",
  "description": "Vehicle accident blocking two lanes",
  "location": "Intersection A"
}
```

**Response:**
```json
{
  "incidentId": "INC-2026-03-28-001",
  "status": "REPORTED",
  "timestamp": "2026-03-28T10:30:00Z"
}
```

**7. Get Active Incidents**

**Endpoint:** `GET /Incidents`

**Query Parameters:**
- `status` (optional): ACTIVE, RESOLVED
- `routeId` (optional): Filter by route

**Response Example:**
```json
[
  {
    "incidentId": "INC-2026-03-28-001",
    "routeId": "nord",
    "type": "ACCIDENT",
    "severity": "HIGH",
    "description": "Vehicle accident blocking two lanes",
    "status": "ACTIVE",
    "timestamp": "2026-03-28T10:30:00Z"
  }
]
```

## Database Schema

### Table: traffic_lights

```sql
CREATE TABLE traffic_lights (
    route_id VARCHAR(20) PRIMARY KEY,
    current_color VARCHAR(10) NOT NULL,
    time_remaining INT,
    mode VARCHAR(10) DEFAULT 'AUTO',
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Table: traffic_flow

```sql
CREATE TABLE traffic_flow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    route_id VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vehicle_count INT,
    average_speed DOUBLE,
    congestion_level VARCHAR(20),
    INDEX idx_route_timestamp (route_id, timestamp)
);
```

### Table: incidents

```sql
CREATE TABLE incidents (
    incident_id VARCHAR(50) PRIMARY KEY,
    route_id VARCHAR(20),
    type VARCHAR(50),
    severity VARCHAR(20),
    description TEXT,
    location VARCHAR(200),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    INDEX idx_status (status),
    INDEX idx_route (route_id)
);
```

### Table: clients

```sql
CREATE TABLE clients (
    client_id VARCHAR(50) PRIMARY KEY,
    client_type VARCHAR(20),
    api_key VARCHAR(100),
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_active TIMESTAMP
);
```

### Table: subscriptions

```sql
CREATE TABLE subscriptions (
    subscription_id VARCHAR(50) PRIMARY KEY,
    client_id VARCHAR(50),
    route_ids TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(client_id)
);
```

## Configuration

### SOAP Services Configuration

**File:** `javaproject/services/src/main/resources/application.properties`

```properties
# Server Configuration
server.port=8080
server.host=localhost

# SOAP Configuration
soap.path=/Services
soap.namespace=http://services.smartcity.com/

# Database Configuration
db.url=jdbc:mysql://localhost:3306/integrated_traffic_system
db.username=root
db.password=
db.pool.size=10

# Traffic Light Timing (seconds)
traffic.light.green.duration=60
traffic.light.yellow.duration=5
traffic.light.red.duration=55
```

### Centrale Service Configuration

**File:** `javaproject/centraleservice/src/main/resources/config.properties`

```properties
# API Configuration
api.port=9999
api.host=localhost
api.base.path=/centrale/api

# Database Configuration
db.url=jdbc:mysql://localhost:3306/integrated_traffic_system
db.username=root
db.password=
db.connection.pool.size=20

# Routes Configuration
routes=nord,sud,est,ouest

# CORS Configuration (for dashboard)
cors.allowed.origins=http://localhost:3000
cors.allowed.methods=GET,POST,PUT,DELETE
cors.allowed.headers=*
```

## Running the Services

### Start SOAP Services

```bash
cd javaproject/services
mvn clean package
java -jar target/services.jar
```

Verify at: `http://localhost:8080/Services`

### Start Centrale Service

```bash
cd javaproject/centraleservice
mvn clean package
java -jar target/centrale.jar
```

Verify at: `http://localhost:9999/centrale/api/Routes`

### Using start-all.sh Script

The integrated startup script handles both services:

```bash
cd integrated-traffic-system/scripts
./start-all.sh
```

This starts:
1. SOAP Services on port 8080
2. Centrale Service on port 9999

## Testing the APIs

### Test SOAP Services

Using SoapUI or curl:

```bash
# Get traffic flow for route "nord"
curl -X POST http://localhost:8080/Services/ServiceTrafic \
  -H "Content-Type: text/xml" \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.smartcity.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:getTrafficFlow>
         <routeId>nord</routeId>
      </ser:getTrafficFlow>
   </soapenv:Body>
</soapenv:Envelope>'
```

### Test REST Centrale API

```bash
# Get all traffic flows
curl http://localhost:9999/centrale/api/Flux

# Get specific route
curl http://localhost:9999/centrale/api/Routes/nord

# Control traffic light
curl -X POST http://localhost:9999/centrale/api/TrafficLights/nord \
  -H "Content-Type: application/json" \
  -d '{"color": "VERT", "duration": 60}'

# Get statistics
curl http://localhost:9999/centrale/api/Statistics?period=DAY

# Report incident
curl -X POST http://localhost:9999/centrale/api/Incidents \
  -H "Content-Type: application/json" \
  -d '{
    "routeId": "nord",
    "type": "ACCIDENT",
    "severity": "HIGH",
    "description": "Test incident"
  }'
```

### Using API Tester Web App

A built-in web-based API tester is available:

**Location:** `javaproject/api-tester-webapp/`

**Usage:**
```bash
cd javaproject/api-tester-webapp
# Open index.html in browser
# Or serve with simple HTTP server:
python3 -m http.server 8000
# Then visit: http://localhost:8000
```

The tester provides:
- Pre-configured API endpoints
- Request templates
- Response visualization
- Error handling

## Client Integration

### SOAP Client Example (Java)

```java
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import java.net.URL;

// Connect to ServiceTrafic
URL wsdlUrl = new URL("http://localhost:8080/Services/ServiceTrafic?wsdl");
QName qname = new QName("http://services.smartcity.com/", "ServiceTraficService");
Service service = Service.create(wsdlUrl, qname);

ServiceTrafic traficService = service.getPort(ServiceTrafic.class);

// Get traffic flow
TrafficFlow flow = traficService.getTrafficFlow("nord");
System.out.println("Vehicle count: " + flow.getVehicleCount());
```

### REST Client Example (JavaScript)

```javascript
// Fetch traffic flow data
async function getTrafficFlow() {
  const response = await fetch('http://localhost:9999/centrale/api/Flux');
  const data = await response.json();
  console.log('Traffic data:', data);
  return data;
}

// Control traffic light
async function setTrafficLight(routeId, color, duration) {
  const response = await fetch(
    `http://localhost:9999/centrale/api/TrafficLights/${routeId}`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ color, duration })
    }
  );
  return await response.json();
}
```

## Traffic Light Control Algorithm

The system implements an intelligent traffic light control algorithm:

### Auto Mode

When `mode=AUTO`, the system:
1. Monitors traffic flow on all routes
2. Calculates congestion levels
3. Adjusts green light duration based on demand
4. Prioritizes routes with higher congestion
5. Maintains minimum green time (30s) and maximum (120s)

### Manual Override

Operators can manually control lights via:
- Dashboard interface
- REST API calls
- SOAP service calls

Manual mode remains active until auto mode is re-enabled.

## Monitoring and Logging

### Log Files

- `logs/soap-services.log` - SOAP service logs
- `logs/centrale-service.log` - REST API logs
- `logs/traffic-control.log` - Traffic light control actions

### Metrics

Monitor these key metrics:
- API request rate
- Response times
- Traffic light state changes
- Incident reports
- Database query performance

### Health Checks

```bash
# SOAP services health
curl http://localhost:8080/health

# Centrale service health
curl http://localhost:9999/centrale/api/health
```

## Troubleshooting

### Common Issues

**Problem:** SOAP service returns 404
**Solution:** Verify service is running and WSDL is accessible at `/Services/ServiceName?wsdl`

**Problem:** Centrale API CORS errors
**Solution:** Check `cors.allowed.origins` includes your dashboard URL

**Problem:** Traffic light commands not working
**Solution:** Ensure route ID is valid (nord, sud, est, ouest) and service has database access

**Problem:** Database connection errors
**Solution:** Verify MySQL is running and credentials in config files are correct

### Debug Mode

Enable debug logging:

```properties
logging.level=DEBUG
logging.sql.enabled=true
```

## Performance Optimization

### Database Optimization

1. Add indexes on frequently queried columns
2. Use connection pooling (default: 10-20 connections)
3. Enable query caching for read-heavy operations
4. Archive old traffic flow data periodically

### API Optimization

1. Implement response caching for static data
2. Use HTTP/2 for REST APIs
3. Enable gzip compression
4. Implement rate limiting to prevent abuse

### Scalability

For high-traffic scenarios:
1. Deploy multiple Centrale service instances with load balancer
2. Use database replication for read scaling
3. Implement Redis cache for hot data
4. Consider message queue for async operations

## Security

### Authentication

Implement API key authentication:

```bash
curl http://localhost:9999/centrale/api/Flux \
  -H "X-API-Key: your-api-key"
```

### HTTPS

In production, enable HTTPS:

```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
```

### Input Validation

All APIs validate:
- Route IDs against allowed values
- Color values against VERT, ROUGE, ORANGE
- Timestamps in ISO 8601 format
- Numeric values within reasonable ranges

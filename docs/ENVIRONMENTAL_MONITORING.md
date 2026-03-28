# Environmental Monitoring Services

## Overview

The Environmental Monitoring Services provide real-time air quality and noise pollution monitoring for the smart traffic management system. These services collect environmental data, generate alerts when pollution levels exceed thresholds, and integrate with Kafka for event streaming and the dashboard for visualization.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│              Environmental Monitoring Services                   │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────────────┐       ┌──────────────────────────┐
│  Pollution Service       │       │  Noise Service           │
│  (REST - Port 8080)      │       │  (TCP - Port 9999)       │
│                          │       │                          │
│  ┌────────────────────┐  │       │  ┌────────────────────┐  │
│  │ Air Quality API    │  │       │  │ TCP Server         │  │
│  │                    │  │       │  │                    │  │
│  └────────────────────┘  │       │  └────────────────────┘  │
│          │               │       │          │               │
│          ▼               │       │          ▼               │
│  ┌────────────────────┐  │       │  ┌────────────────────┐  │
│  │ Alert Generator    │  │       │  │ Noise Analyzer     │  │
│  │                    │  │       │  │                    │  │
│  └────────────────────┘  │       │  └────────────────────┘  │
│          │               │       │          │               │
│          ▼               │       │          ▼               │
│  ┌────────────────────┐  │       │  ┌────────────────────┐  │
│  │ Kafka Producer     │  │       │  │ Kafka Producer     │  │
│  └────────────────────┘  │       │  └────────────────────┘  │
└──────────────────────────┘       └──────────────────────────┘
           │                                    │
           └────────────┬───────────────────────┘
                        ▼
              ┌──────────────────┐
              │ Kafka Broker     │
              │  localhost:9092  │
              └──────────────────┘
                        │
                        ▼
                ┌──────────────────┐
                │ MySQL Database   │
                │  Port 3306       │
                └──────────────────┘
```

## Components

### 1. Pollution Service (REST API)

**Location:** `sgtu-backend/service-pollution-rest/`

**Port:** 8080

**Purpose:** Monitor air quality including PM2.5, PM10, NO2, CO, O3 levels and generate pollution alerts.

#### API Endpoints

**Base URL:** `http://localhost:8080/api/pollution`

**1. Get Current Air Quality**

**Endpoint:** `GET /current`

**Query Parameters:**
- `location` (optional): Specific location/zone identifier

**Response Example:**
```json
{
  "timestamp": "2026-03-28T10:30:00Z",
  "location": "ZONE_CENTRE",
  "measurements": {
    "pm25": 35.5,
    "pm10": 52.3,
    "no2": 42.1,
    "co": 1.2,
    "o3": 68.4
  },
  "aqi": 72,
  "quality": "MODERATE",
  "healthAdvice": "Sensitive groups should reduce prolonged outdoor exertion"
}
```

**2. Get Pollution History**

**Endpoint:** `GET /history`

**Query Parameters:**
- `location` (optional): Location/zone identifier
- `hours` (optional): Number of hours of history (default: 24)
- `pollutant` (optional): Specific pollutant (PM25, PM10, NO2, CO, O3)

**Response Example:**
```json
{
  "location": "ZONE_CENTRE",
  "period": {
    "start": "2026-03-27T10:30:00Z",
    "end": "2026-03-28T10:30:00Z"
  },
  "measurements": [
    {
      "timestamp": "2026-03-28T10:00:00Z",
      "pm25": 35.5,
      "aqi": 72
    },
    {
      "timestamp": "2026-03-28T09:00:00Z",
      "pm25": 32.1,
      "aqi": 68
    }
  ]
}
```

**3. Get Active Alerts**

**Endpoint:** `GET /alerts`

**Query Parameters:**
- `severity` (optional): Filter by severity (HIGH, MEDIUM, LOW)
- `status` (optional): Filter by status (ACTIVE, RESOLVED)
- `location` (optional): Filter by location

**Response Example:**
```json
[
  {
    "alertId": "POLL-2026-03-28-001",
    "type": "HIGH_PM25",
    "severity": "HIGH",
    "location": "ZONE_CENTRE",
    "message": "PM2.5 levels exceed safe threshold (50 µg/m³)",
    "currentValue": 78.5,
    "threshold": 50.0,
    "timestamp": "2026-03-28T10:25:00Z",
    "status": "ACTIVE",
    "recommendations": [
      "Avoid outdoor activities",
      "Use air purifiers indoors",
      "Close windows"
    ]
  }
]
```

**4. Submit Measurement**

**Endpoint:** `POST /measurements`

**Request Body:**
```json
{
  "location": "ZONE_CENTRE",
  "timestamp": "2026-03-28T10:30:00Z",
  "pm25": 35.5,
  "pm10": 52.3,
  "no2": 42.1,
  "co": 1.2,
  "o3": 68.4,
  "temperature": 22.5,
  "humidity": 65.0
}
```

**Response:**
```json
{
  "success": true,
  "measurementId": 12345,
  "aqi": 72,
  "alertsGenerated": []
}
```

**5. Get AQI Statistics**

**Endpoint:** `GET /statistics`

**Query Parameters:**
- `period` (optional): DAY, WEEK, MONTH
- `location` (optional): Location filter

**Response Example:**
```json
{
  "period": "DAY",
  "location": "ZONE_CENTRE",
  "statistics": {
    "averageAQI": 68.5,
    "maxAQI": 92,
    "minAQI": 45,
    "exceedanceHours": 3,
    "dominantPollutant": "PM25"
  }
}
```

#### Air Quality Index (AQI) Calculation

The service calculates AQI based on EPA standards:

| AQI Range | Category | Health Impact | Color |
|-----------|----------|---------------|-------|
| 0 - 50 | Good | Air quality is satisfactory | Green |
| 51 - 100 | Moderate | Acceptable for most people | Yellow |
| 101 - 150 | Unhealthy for Sensitive Groups | Sensitive groups may experience effects | Orange |
| 151 - 200 | Unhealthy | Everyone may begin to experience effects | Red |
| 201 - 300 | Very Unhealthy | Health alert | Purple |
| 301+ | Hazardous | Emergency conditions | Maroon |

#### Pollution Thresholds

Alerts are generated when pollutant levels exceed:

| Pollutant | Threshold | Unit |
|-----------|-----------|------|
| PM2.5 | 50 | µg/m³ |
| PM10 | 100 | µg/m³ |
| NO2 | 80 | µg/m³ |
| CO | 4.0 | mg/m³ |
| O3 | 120 | µg/m³ |

### 2. Noise Service (TCP Socket)

**Location:** `sgtu-backend/service-bruit-tcp/`

**Port:** 9999

**Purpose:** Monitor noise pollution levels via TCP socket communication and generate noise alerts.

#### TCP Protocol

The service uses a simple text-based protocol over TCP.

**Connection:**
```bash
telnet localhost 9999
```

#### Commands

**1. MEASURE**

Submit a noise measurement

**Format:**
```
MEASURE location decibel_level timestamp
```

**Example:**
```
MEASURE ZONE_CENTRE 75.5 2026-03-28T10:30:00Z
```

**Response:**
```
OK measurement_id=12345 alert_generated=false
```

**2. CURRENT**

Get current noise level

**Format:**
```
CURRENT location
```

**Example:**
```
CURRENT ZONE_CENTRE
```

**Response:**
```
OK location=ZONE_CENTRE decibels=75.5 timestamp=2026-03-28T10:30:00Z status=MODERATE
```

**3. ALERTS**

Get active noise alerts

**Format:**
```
ALERTS [location]
```

**Example:**
```
ALERTS ZONE_CENTRE
```

**Response:**
```
OK count=2
ALERT alert_id=NOISE-001 location=ZONE_CENTRE decibels=88.5 severity=HIGH timestamp=2026-03-28T10:25:00Z
ALERT alert_id=NOISE-002 location=ZONE_EST decibels=82.3 severity=MEDIUM timestamp=2026-03-28T10:20:00Z
```

**4. HISTORY**

Get noise history

**Format:**
```
HISTORY location hours
```

**Example:**
```
HISTORY ZONE_CENTRE 24
```

**Response:**
```
OK count=24
2026-03-28T10:00:00Z 75.5
2026-03-28T09:00:00Z 72.1
...
```

**5. STATUS**

Get service status

**Format:**
```
STATUS
```

**Response:**
```
OK service=NoiseMonitoring version=1.0 uptime=3600 measurements_processed=1234
```

#### Noise Level Categories

| Decibel Range | Category | Description | Color |
|---------------|----------|-------------|-------|
| 0 - 50 | Quiet | Normal ambient noise | Green |
| 51 - 70 | Moderate | Acceptable noise levels | Yellow |
| 71 - 85 | Loud | Potentially disturbing | Orange |
| 86 - 100 | Very Loud | Harmful with prolonged exposure | Red |
| 101+ | Dangerous | Immediate hearing damage risk | Purple |

#### Noise Thresholds

Alerts generated at:
- 70 dB: MEDIUM severity (disturbing)
- 85 dB: HIGH severity (potentially harmful)
- 100 dB: CRITICAL severity (dangerous)

### 3. Simulators

For testing and demonstration, simulators generate realistic environmental data.

#### Pollution Simulator

**Location:** `sgtu-backend/simulateur-pollution/`

**Purpose:** Generate realistic air quality measurements

**Running:**
```bash
cd sgtu-backend/simulateur-pollution
mvn clean package
java -jar target/simulateur-pollution.jar
```

**Configuration:**
```properties
# Simulation interval (seconds)
simulation.interval=60

# Target service
pollution.service.url=http://localhost:8080/api/pollution

# Simulation patterns
simulation.pattern=NORMAL
# Options: NORMAL, HIGH_TRAFFIC, INDUSTRIAL, CLEAN
```

#### Noise Simulator

**Location:** `sgtu-backend/simulateur-bruit/`

**Purpose:** Generate realistic noise measurements

**Running:**
```bash
cd sgtu-backend/simulateur-bruit
mvn clean package
java -jar target/simulateur-bruit.jar
```

**Configuration:**
```properties
# Simulation interval (seconds)
simulation.interval=30

# Target service
noise.service.host=localhost
noise.service.port=9999

# Simulation patterns
simulation.pattern=NORMAL
# Options: NORMAL, RUSH_HOUR, NIGHT, EVENT
```

## Kafka Integration

### Topics

**pollution-alerts**
- Purpose: Stream pollution alert events
- Partitions: 1
- Producer: Pollution Service

**noise-alerts**
- Purpose: Stream noise alert events
- Partitions: 1
- Producer: Noise Service

**pollution-measurements**
- Purpose: Stream all pollution measurements
- Partitions: 1
- Producer: Pollution Service

**noise-measurements**
- Purpose: Stream all noise measurements
- Partitions: 1
- Producer: Noise Service

### Message Formats

**Pollution Alert Message:**
```json
{
  "eventType": "POLLUTION_ALERT",
  "alertId": "POLL-2026-03-28-001",
  "timestamp": "2026-03-28T10:30:00Z",
  "location": "ZONE_CENTRE",
  "pollutant": "PM25",
  "value": 78.5,
  "threshold": 50.0,
  "severity": "HIGH",
  "aqi": 135,
  "message": "PM2.5 levels exceed safe threshold"
}
```

**Noise Alert Message:**
```json
{
  "eventType": "NOISE_ALERT",
  "alertId": "NOISE-2026-03-28-001",
  "timestamp": "2026-03-28T10:30:00Z",
  "location": "ZONE_CENTRE",
  "decibels": 88.5,
  "threshold": 85.0,
  "severity": "HIGH",
  "message": "Noise levels potentially harmful"
}
```

## Database Schema

### Table: pollution_measurements

```sql
CREATE TABLE pollution_measurements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    location VARCHAR(100) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pm25 DOUBLE,
    pm10 DOUBLE,
    no2 DOUBLE,
    co DOUBLE,
    o3 DOUBLE,
    aqi INT,
    temperature DOUBLE,
    humidity DOUBLE,
    INDEX idx_location_timestamp (location, timestamp)
);
```

### Table: noise_measurements

```sql
CREATE TABLE noise_measurements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    location VARCHAR(100) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    decibels DOUBLE NOT NULL,
    category VARCHAR(20),
    INDEX idx_location_timestamp (location, timestamp)
);
```

### Table: pollution_alerts

```sql
CREATE TABLE pollution_alerts (
    alert_id VARCHAR(50) PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    severity VARCHAR(20),
    location VARCHAR(100),
    pollutant VARCHAR(20),
    current_value DOUBLE,
    threshold_value DOUBLE,
    aqi INT,
    message TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    resolved_at TIMESTAMP NULL,
    INDEX idx_status_timestamp (status, timestamp),
    INDEX idx_location (location)
);
```

### Table: noise_alerts

```sql
CREATE TABLE noise_alerts (
    alert_id VARCHAR(50) PRIMARY KEY,
    location VARCHAR(100),
    decibels DOUBLE,
    threshold_value DOUBLE,
    severity VARCHAR(20),
    message TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    resolved_at TIMESTAMP NULL,
    INDEX idx_status_timestamp (status, timestamp),
    INDEX idx_location (location)
);
```

## Configuration

### Pollution Service

**File:** `sgtu-backend/service-pollution-rest/src/main/resources/application.properties`

```properties
# Server Configuration
server.port=8080
server.host=localhost

# Database Configuration
db.url=jdbc:mysql://localhost:3306/integrated_traffic_system
db.username=root
db.password=
db.pool.size=10

# Kafka Configuration
kafka.bootstrap.servers=localhost:9092
kafka.topic.alerts=pollution-alerts
kafka.topic.measurements=pollution-measurements

# Alert Thresholds
threshold.pm25=50.0
threshold.pm10=100.0
threshold.no2=80.0
threshold.co=4.0
threshold.o3=120.0

# Measurement Interval (seconds)
measurement.retention.days=90
alert.retention.days=365
```

### Noise Service

**File:** `sgtu-backend/service-bruit-tcp/src/main/resources/config.properties`

```properties
# TCP Server Configuration
tcp.port=9999
tcp.host=0.0.0.0
tcp.max.connections=50

# Database Configuration
db.url=jdbc:mysql://localhost:3306/integrated_traffic_system
db.username=root
db.password=

# Kafka Configuration
kafka.bootstrap.servers=localhost:9092
kafka.topic.alerts=noise-alerts
kafka.topic.measurements=noise-measurements

# Alert Thresholds (decibels)
threshold.moderate=70.0
threshold.high=85.0
threshold.critical=100.0

# Measurement Settings
measurement.retention.days=90
measurement.aggregation.minutes=15
```

## Running the Services

### Start Pollution Service

```bash
cd sgtu-backend/service-pollution-rest
mvn clean package
java -jar target/service-pollution-rest.jar
```

Verify at: `http://localhost:8080/api/pollution/current`

### Start Noise Service

```bash
cd sgtu-backend/service-bruit-tcp
mvn clean package
java -jar target/service-bruit-tcp.jar
```

Verify with:
```bash
telnet localhost 9999
STATUS
```

### Using start-all.sh

Both services are started automatically:

```bash
cd integrated-traffic-system/scripts
./start-all.sh
```

## Testing

### Test Pollution API

```bash
# Get current air quality
curl http://localhost:8080/api/pollution/current

# Get alerts
curl http://localhost:8080/api/pollution/alerts

# Submit measurement
curl -X POST http://localhost:8080/api/pollution/measurements \
  -H "Content-Type: application/json" \
  -d '{
    "location": "ZONE_CENTRE",
    "pm25": 75.5,
    "pm10": 90.0,
    "no2": 45.0,
    "co": 1.5,
    "o3": 70.0
  }'

# Get history
curl "http://localhost:8080/api/pollution/history?hours=24&location=ZONE_CENTRE"
```

### Test Noise Service

```bash
# Connect with telnet
telnet localhost 9999

# Send commands
MEASURE ZONE_CENTRE 85.5 2026-03-28T10:30:00Z
CURRENT ZONE_CENTRE
ALERTS
HISTORY ZONE_CENTRE 24
STATUS
```

### Python Test Client

```python
import socket

def send_noise_command(command):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(('localhost', 9999))
    sock.send(f"{command}\n".encode())
    response = sock.recv(4096).decode()
    sock.close()
    return response

# Test commands
print(send_noise_command("STATUS"))
print(send_noise_command("CURRENT ZONE_CENTRE"))
print(send_noise_command("MEASURE ZONE_CENTRE 75.5 2026-03-28T10:30:00Z"))
```

## Client Integration

### JavaScript Example (Pollution API)

```javascript
// Fetch current air quality
async function getAirQuality(location) {
  const response = await fetch(
    `http://localhost:8080/api/pollution/current?location=${location}`
  );
  const data = await response.json();
  return data;
}

// Submit measurement
async function submitPollutionData(measurement) {
  const response = await fetch(
    'http://localhost:8080/api/pollution/measurements',
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(measurement)
    }
  );
  return await response.json();
}

// Get active alerts
async function getPollutionAlerts() {
  const response = await fetch(
    'http://localhost:8080/api/pollution/alerts?status=ACTIVE'
  );
  return await response.json();
}
```

## Monitoring and Alerts

### Alert Workflow

1. Service receives measurement
2. Compares value against thresholds
3. If exceeded:
   - Generates alert with unique ID
   - Stores in database
   - Publishes to Kafka
   - Returns alert in API response
4. Dashboard consumes from Kafka or polls API
5. Displays alert to operators
6. Alert auto-resolves when values return to normal

### Alert Escalation

- MEDIUM severity: Log and notify
- HIGH severity: Log, notify, and send to dashboard
- CRITICAL severity: Log, notify, dashboard, and trigger automated responses

## Performance Considerations

### Pollution Service

- Handle up to 1000 requests/minute
- Measurement retention: 90 days
- Alert retention: 365 days
- Database connection pool: 10 connections
- Response time target: < 100ms

### Noise Service

- Handle up to 50 concurrent TCP connections
- Process measurements every 30 seconds
- Maintain connection keep-alive
- Timeout inactive connections after 5 minutes

## Troubleshooting

### Common Issues

**Problem:** Pollution service returns 500 error
**Solution:** Check MySQL connection and ensure database schema exists

**Problem:** Noise service connection refused
**Solution:** Verify service is running on port 9999 and firewall allows connections

**Problem:** No alerts being generated
**Solution:** Check threshold configuration and ensure measurements exceed thresholds

**Problem:** Kafka publishing failures
**Solution:** Verify Kafka broker is running on port 9092

### Debug Mode

Enable debug logging:

```properties
logging.level=DEBUG
logging.kafka.enabled=true
logging.sql.enabled=true
```

View logs:
```bash
tail -f logs/pollution-service.log
tail -f logs/noise-service.log
```

## Security

### API Security

Implement in production:
- API key authentication
- Rate limiting (100 requests/minute per client)
- Input validation and sanitization
- HTTPS/TLS encryption

### TCP Socket Security

For production:
- Implement authentication protocol
- Use TLS for encrypted communication
- IP whitelist for allowed clients
- Connection rate limiting

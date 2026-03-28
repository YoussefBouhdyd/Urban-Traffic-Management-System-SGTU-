# API Testing Guide

This document provides examples for testing all REST API endpoints.

## Base URL
```
http://localhost:8080
```

## Endpoints

### 1. Get Latest Traffic State

**Request:**
```bash
curl -X GET http://localhost:8080/api/traffic/latest
```

**Response (200 OK):**
```json
{
  "intersectionId": "INT-01",
  "timestamp": "2026-03-15T17:25:00",
  "trafficState": "CONGESTED",
  "severity": "HIGH",
  "recommendation": "Increase green light duration by 20 seconds"
}
```

**Response (404 Not Found):**
```json
{
  "error": "No traffic data available"
}
```

---

### 2. Get Traffic History

**Request (with date range):**
```bash
curl -X GET "http://localhost:8080/api/traffic/history?from=2026-03-15T00:00:00&to=2026-03-15T23:59:59"
```

**Request (default - last 24 hours):**
```bash
curl -X GET http://localhost:8080/api/traffic/history
```

**Response (200 OK):**
```json
[
  {
    "id": 101,
    "intersectionId": "INT-01",
    "timestamp": "2026-03-15T17:25:00",
    "trafficState": "CONGESTED",
    "severity": "HIGH",
    "vehicleCount": 42,
    "averageSpeed": 12.5,
    "recommendation": "Increase green light duration by 20 seconds"
  },
  {
    "id": 100,
    "intersectionId": "INT-01",
    "timestamp": "2026-03-15T17:20:00",
    "trafficState": "BUSY",
    "severity": "MEDIUM",
    "vehicleCount": 28,
    "averageSpeed": 22.3,
    "recommendation": "Monitor traffic density closely"
  }
]
```

**Response (400 Bad Request):**
```json
{
  "error": "Invalid date format. Use ISO 8601 format: yyyy-MM-ddTHH:mm:ss"
}
```

---

### 3. Get Active Alerts

**Request:**
```bash
curl -X GET http://localhost:8080/api/alerts
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "type": "CONGESTION",
    "severity": "HIGH",
    "message": "Heavy congestion detected at intersection INT-01",
    "timestamp": "2026-03-15T17:25:00",
    "status": "ACTIVE"
  },
  {
    "id": 2,
    "type": "PEDESTRIAN_WARNING",
    "severity": "MEDIUM",
    "message": "High pedestrian activity detected with heavy traffic at intersection INT-01",
    "timestamp": "2026-03-15T17:20:00",
    "status": "ACTIVE"
  }
]
```

---

### 4. Get Recommendations

**Request:**
```bash
curl -X GET http://localhost:8080/api/recommendations
```

**Response (200 OK):**
```json
[
  {
    "id": 11,
    "intersectionId": "INT-01",
    "timestamp": "2026-03-15T17:25:00",
    "recommendation": "Increase green light duration by 20 seconds",
    "reason": "Vehicle density is high (42 vehicles). Average speed is low (12.5 km/h).",
    "priority": "HIGH"
  },
  {
    "id": 10,
    "intersectionId": "INT-01",
    "timestamp": "2026-03-15T17:20:00",
    "recommendation": "Monitor traffic density closely",
    "reason": "Vehicle density is moderate. Traffic flow is slowing.",
    "priority": "MEDIUM"
  }
]
```

---

### 5. Get Camera Status

**Request:**
```bash
curl -X GET http://localhost:8080/api/cameras/CAM-01/status
```

**Response (200 OK):**
```json
{
  "cameraId": "CAM-01",
  "intersectionId": "INT-01",
  "status": "RUNNING",
  "lastUpdate": "2026-03-15T17:25:00"
}
```

**Response (404 Not Found):**
```json
{
  "error": "Camera not found"
}
```

---

## Using Postman

### Import Collection

Create a Postman collection with these requests:

1. **Collection Name:** Traffic Management API
2. **Variables:**
   - `base_url`: `http://localhost:8080`

### Requests:

#### 1. Latest Traffic
- Method: GET
- URL: `{{base_url}}/api/traffic/latest`

#### 2. Traffic History (Default)
- Method: GET
- URL: `{{base_url}}/api/traffic/history`

#### 3. Traffic History (Custom Range)
- Method: GET
- URL: `{{base_url}}/api/traffic/history`
- Query Params:
  - `from`: `2026-03-15T00:00:00`
  - `to`: `2026-03-15T23:59:59`

#### 4. Alerts
- Method: GET
- URL: `{{base_url}}/api/alerts`

#### 5. Recommendations
- Method: GET
- URL: `{{base_url}}/api/recommendations`

#### 6. Camera Status
- Method: GET
- URL: `{{base_url}}/api/cameras/CAM-01/status`

---

## Testing Scenarios

### Scenario 1: Normal Traffic Flow
Wait for camera to generate events with low vehicle count. Then check:
```bash
curl http://localhost:8080/api/traffic/latest
# Expected: trafficState = "NORMAL", severity = "LOW"
```

### Scenario 2: Congestion Detection
Wait for high vehicle count events. Then check:
```bash
curl http://localhost:8080/api/traffic/latest
curl http://localhost:8080/api/alerts
# Expected: trafficState = "CONGESTED", new alerts generated
```

### Scenario 3: Historical Analysis
Collect data for several minutes, then query history:
```bash
FROM=$(date -u -d '1 hour ago' '+%Y-%m-%dT%H:%M:%S')
TO=$(date -u '+%Y-%m-%dT%H:%M:%S')
curl "http://localhost:8080/api/traffic/history?from=$FROM&to=$TO"
```

---

## Response Status Codes

- `200 OK`: Request successful
- `400 Bad Request`: Invalid parameters
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

---

## JSON Formatting

For pretty-printed JSON responses, pipe curl output through `jq`:

```bash
curl http://localhost:8080/api/traffic/latest | jq .
```

Or use Postman which automatically formats JSON.

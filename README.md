# Integrated Smart City Traffic Management System

A comprehensive distributed system for real-time traffic monitoring, environmental quality tracking, and intelligent traffic control. This system integrates camera-based traffic analysis, SOAP-based traffic management services, environmental monitoring (air quality and noise levels), and a modern web dashboard for visualization and control.

## System Overview

The system consists of five main components working together to provide a complete smart city traffic management solution:

1. **Traffic Camera & Analysis Service** - Real-time traffic monitoring using camera data
2. **Traffic Management Services** - SOAP and REST services for traffic flow and traffic light control
3. **Environmental Monitoring Services** - Air quality (pollution) and noise level tracking
4. **Dashboard Application** - Web-based interface for visualization and control
5. **System Integration** - Unified deployment and management infrastructure

## Architecture

The system uses a microservices architecture with event-driven communication through Apache Kafka:

```
┌────────────────────────────────────────────────────────────────────┐
│                          Apache Kafka                              │
│                     (Message Streaming Platform)                   │
└────────────────────────────────────────────────────────────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
        ▼                         ▼                         ▼
┌───────────────┐        ┌───────────────┐        ┌───────────────┐
│ Traffic Camera│        │   Traffic     │        │ Environmental │
│   & Analysis  │        │  Management   │        │  Monitoring   │
│   Service     │        │   Services    │        │   Services    │
│               │        │               │        │               │
│ - REST API    │        │ - SOAP API    │        │ - REST API    │
│ - RMI Server  │        │ - REST API    │        │ - TCP Server  │
│ - Kafka Prod. │        │ - Kafka Cons. │        │ - Kafka Prod. │
└───────────────┘        └───────────────┘        └───────────────┘
        │                         │                         │
        └─────────────────────────┼─────────────────────────┘
                                  │
                                  ▼
                          ┌───────────────┐
                          │  MySQL        │
                          │  Database     │
                          └───────────────┘
                                  │
                                  ▼
                          ┌───────────────┐
                          │  Next.js      │
                          │  Dashboard    │
                          └───────────────┘
```

## Technology Stack

### Backend Services
- **Java 21** - Primary programming language for all backend services
- **Apache Maven** - Build and dependency management
- **Apache Kafka 2.13-3.8.0** - Event streaming platform
- **MySQL 8.0** - Relational database for persistent storage

### Service Frameworks
- **JAX-RS (Jersey 3.1.3)** - RESTful web services
- **JAX-WS** - SOAP web services
- **Grizzly HTTP Server** - Lightweight HTTP server
- **Jetty 11** - Servlet container

### Frontend
- **Next.js 14** - React framework for the dashboard
- **TypeScript** - Type-safe JavaScript
- **Tailwind CSS** - Utility-first CSS framework
- **Zustand** - State management

## Quick Start

### Prerequisites

- Java JDK 21 or higher
- Apache Maven 3.6+
- Node.js 18+ and npm
- MySQL 8.0
- Apache Kafka 2.13-3.8.0

### Installation

1. Start infrastructure services:
```bash
cd integrated-traffic-system

# Start Kafka and Zookeeper
./scripts/start-kafka.sh

# Create Kafka topics
./scripts/setup-kafka.sh

# Initialize MySQL database
./scripts/setup-database.sh
```

2. Build and start all services:
```bash
./scripts/start-all.sh
```

3. Access the dashboard:
```
http://localhost:3000
```

### Service Ports

| Service | Port | Protocol |
|---------|------|----------|
| Dashboard | 3000 | HTTP |
| Traffic Camera API | 8083 | HTTP/REST |
| SOAP ServiceFlux | 8080 | HTTP/SOAP |
| SOAP ServiceFeux | 8081 | HTTP/SOAP |
| Centrale REST API | 9999 | HTTP/REST |
| Pollution Service | 8082 | HTTP/REST |
| Noise Service | 5000 | TCP |
| Kafka Broker | 9092 | TCP |
| Zookeeper | 2181 | TCP |
| RMI Registry | 1099 | TCP |
| MySQL | 3306 | TCP |

## Documentation

Detailed documentation for each component:

### 1. Traffic Camera & Analysis Service
**File:** [docs/CAMERA_SERVICE.md](docs/CAMERA_SERVICE.md)

Covers the camera-based traffic monitoring system including:
- Real-time traffic analysis
- AI-powered recommendations
- Alert generation
- REST API endpoints
- RMI remote control interface

### 2. Traffic Management Services
**File:** [docs/TRAFFIC_MANAGEMENT.md](docs/TRAFFIC_MANAGEMENT.md)

Describes the SOAP and REST services for traffic control:
- SOAP ServiceFlux (traffic flow monitoring)
- SOAP ServiceFeux (traffic light control)
- Centrale REST API (unified interface)
- Traffic optimization algorithms

### 3. Environmental Monitoring Services
**File:** [docs/ENVIRONMENTAL_MONITORING.md](docs/ENVIRONMENTAL_MONITORING.md)

Details the pollution and noise monitoring capabilities:
- Air quality monitoring (CO2, PM2.5, PM10, NO2, O3)
- Noise level tracking
- Alert threshold management
- Data collection and storage

### 4. Dashboard Application
**File:** [docs/DASHBOARD.md](docs/DASHBOARD.md)

Explains the web-based user interface:
- Real-time visualization
- Traffic light control interface
- Environmental data display
- Alert management
- Historical analytics

### 5. System Deployment & Integration
**File:** [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md)

Provides comprehensive deployment information:
- System architecture
- Installation procedures
- Configuration management
- Monitoring and maintenance
- Troubleshooting guide

## Key Features

### Traffic Monitoring
- Real-time camera-based traffic analysis
- Vehicle count and congestion detection
- Traffic flow measurement across four routes (North, South, East, West)
- Historical traffic data visualization

### Traffic Control
- Intelligent traffic light management
- Dynamic timing adjustments based on traffic conditions
- Manual override capabilities
- Traffic optimization recommendations

### Environmental Monitoring
- Continuous air quality measurement
- Noise level tracking by zone
- Alert generation for threshold violations
- Historical trend analysis

### Dashboard Interface
- Real-time data visualization
- Interactive traffic light control
- Alert management system
- Multi-zone environmental monitoring
- Historical charts and analytics

## Development

### Building Individual Modules

```bash
# Build all services
cd integrated-traffic-system
mvn clean install -DskipTests

# Build specific module
cd Traffic
mvn clean package

# Build dashboard
cd sgtu-dashboard
npm install
npm run build
```

### Running Services Individually

Refer to the specific service documentation for detailed instructions on running services in development mode.

## Testing

### API Testing

Each service provides REST or SOAP endpoints that can be tested using curl or API testing tools. Example:

```bash
# Test Camera API
curl http://localhost:8083/api/traffic/latest

# Test Centrale API
curl http://localhost:9999/centrale/api/Flux/latest

# Test Pollution API
curl http://localhost:8082/api/pollution/latest
```

### System Health Check

```bash
cd integrated-traffic-system
./scripts/health-check.sh
```

## Monitoring

The system provides several monitoring capabilities:

- Service health endpoints
- Kafka topic monitoring
- Database connection status
- Log aggregation in `integrated-traffic-system/logs/`
- Process ID tracking in `integrated-traffic-system/pids/`

## Stopping the System

```bash
cd integrated-traffic-system

# Stop all services
./scripts/stop-all.sh

# Stop Kafka infrastructure
./scripts/stop-kafka.sh
```

## Project Structure

```
.
├── Traffic/                    # Camera-based traffic monitoring
├── javaproject/               # SOAP services and Centrale REST API
│   ├── services/              # SOAP traffic flow and lights services
│   ├── client/                # SOAP client simulator
│   └── centraleservice/       # Central REST API
├── sgtu-backend/              # Environmental monitoring services
│   ├── service-pollution-rest/
│   ├── service-bruit-tcp/
│   ├── service-central/
│   ├── simulateur-pollution/
│   └── simulateur-bruit/
├── sgtu-dashboard/            # Next.js dashboard application
├── integrated-traffic-system/ # Integration scripts and docs
└── docs/                      # Comprehensive documentation
```

## Contributing

This is an integrated academic/demonstration project. For contributions or modifications, please ensure:

1. All services build successfully
2. Integration tests pass
3. Documentation is updated
4. Code follows existing patterns

## License

This project is for educational and demonstration purposes.

## Support

For detailed information about specific components, please refer to the documentation files in the `docs/` directory. Each file provides in-depth technical details, API specifications, and usage examples for its respective component.

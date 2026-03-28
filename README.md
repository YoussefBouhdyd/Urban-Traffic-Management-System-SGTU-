# Urban Traffic Management System (SGTU)

A comprehensive smart city traffic management system integrating multiple services for real-time traffic analysis, environmental monitoring, and intelligent control.

## Overview

This integrated system combines four previously separate projects into a unified platform:

- **Traffic Camera Analysis Service** - Real-time traffic monitoring with AI-powered analysis
- **SOAP Traffic Services** - Traffic flow and traffic light management
- **Environmental Monitoring** - Air quality (pollution) and noise level tracking
- **Real-time Dashboard** - Next.js web interface for visualization and control

## Architecture

```
├── Traffic/                    # Camera-based traffic monitoring (REST + RMI + Kafka)
├── javaproject/               # SOAP services and centrale REST API
│   ├── services/              # SOAP ServiceFlux & ServiceFeux
│   ├── client/                # SOAP client simulator
│   └── centraleservice/       # Central REST API (Flux, Feux, Alerts)
├── sgtu-backend/              # Environmental monitoring services
│   ├── service-pollution-rest/ # Air quality monitoring (REST + Kafka)
│   ├── service-bruit-tcp/     # Noise monitoring (TCP + Kafka)
│   ├── service-central/       # Central Kafka consumer & MySQL storage
│   ├── simulateur-pollution/  # Pollution data simulator
│   └── simulateur-bruit/      # Noise data simulator
├── sgtu-dashboard/            # Next.js 14 dashboard (React + TypeScript)
└── integrated-traffic-system/ # Integration scripts and documentation
```

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Apache Maven 3.6+**
- **Node.js 18+** and npm
- **MySQL 8.x**
- **Apache Kafka 2.13-3.8.0**

### Installation

1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd "Java Project Traffic"
   ```

2. **Set up infrastructure:**
   ```bash
   cd integrated-traffic-system
   
   # Start Kafka & Zookeeper
   ./scripts/start-kafka.sh
   
   # Create Kafka topics
   ./scripts/setup-kafka.sh
   
   # Initialize MySQL database
   ./scripts/setup-database.sh
   ```

3. **Start all services:**
   ```bash
   ./scripts/start-all.sh
   ```

4. **Access the dashboard:**
   Open your browser to `http://localhost:3000`

## 📊 System Ports

| Service | Port | Protocol | Description |
|---------|------|----------|-------------|
| Dashboard | 3000 | HTTP | Next.js web interface |
| Traffic Camera API | 8083 | HTTP/REST | Camera analysis & recommendations |
| SOAP ServiceFlux | 8080 | HTTP/SOAP | Traffic flow service |
| SOAP ServiceFeux | 8081 | HTTP/SOAP | Traffic lights service |
| Centrale REST API | 9999 | HTTP/REST | Central API (Flux, Feux, Alerts) |
| Pollution Service | 8082 | HTTP/REST | Air quality monitoring |
| Noise Service | 5000 | TCP | Noise level monitoring |
| Kafka Broker | 9092 | TCP | Message streaming |
| Zookeeper | 2181 | TCP | Kafka coordination |
| RMI Registry | 1099 | TCP | Camera remote control |
| MySQL | 3306 | TCP | Database |

## Documentation

Detailed documentation is available in the `integrated-traffic-system/` directory:

- **[QUICKSTART.md](integrated-traffic-system/QUICKSTART.md)** - Complete setup and testing guide
- **[ARCHITECTURE.md](integrated-traffic-system/ARCHITECTURE.md)** - System architecture (if exists)
- **[API_TESTING.md](Traffic/API_TESTING.md)** - API endpoint testing guide

## 🛠️ Development

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

```bash
# Camera Service
cd Traffic
java -jar target/traffic-management-1.0-SNAPSHOT.jar

# Camera Simulator
cd Traffic
java -cp "target/traffic-management-1.0-SNAPSHOT.jar:target/lib/*" \
  com.smartcity.traffic.CameraSimulatorMain

# SOAP Services
cd javaproject/services
java -jar target/services-1.0-SNAPSHOT.jar

# Centrale REST Service
cd javaproject/centraleservice
java -jar target/centrale.jar

# Dashboard
cd sgtu-dashboard
npm run dev
```

## 🧪 Testing

### API Endpoints

**Traffic Camera API:**
```bash
curl http://localhost:8083/api/traffic/latest
curl http://localhost:8083/api/alerts
curl http://localhost:8083/api/recommendations
```

**Centrale REST API:**
```bash
curl http://localhost:9999/centrale/api/Flux/latest
curl http://localhost:9999/centrale/api/Feux/etat
curl http://localhost:9999/centrale/api/Alert
```

**Pollution Service:**
```bash
curl http://localhost:8082/api/pollution/latest
```

## 🛑 Stopping Services

```bash
cd integrated-traffic-system

# Stop all services
./scripts/stop-all.sh

# Stop Kafka & Zookeeper
./scripts/stop-kafka.sh

# Stop MySQL
sudo systemctl stop mysql
```

## 📂 Key Features

### ✨ Traffic Management
- Real-time camera-based traffic analysis
- AI-powered recommendations
- Traffic flow monitoring (4 routes: Nord, Sud, Est, Ouest)
- Intelligent traffic light control
- Congestion detection and alerts

### 🌍 Environmental Monitoring
- Air quality monitoring (CO2, PM2.5, PM10, NO2, O3)
- Noise level tracking
- Real-time alerts for threshold violations
- Historical data visualization

### 📊 Dashboard Features
- Live traffic visualization
- Interactive traffic light control
- Air quality heat maps
- Alert management system
- Historical charts and analytics
- Camera status monitoring

## 🔧 Configuration

Key configuration files:
- `integrated-traffic-system/config/system.properties` - System-wide settings
- `Traffic/database/schema.sql` - Database schema
- `sgtu-dashboard/.env.local` - Dashboard environment variables (create from .env.example)

## 🤝 Contributing

This is an integrated academic/demonstration project. For contributions:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📝 License

This project is for educational/demonstration purposes.

## 👥 Authors

- Integrated system architecture and implementation
- Original modules from separate academic projects

## 🐛 Known Issues

- None currently reported

## 📧 Support

For issues or questions, please open a GitHub issue.

---

**⚡ Powered by:** Java 21, Spring, Apache Kafka, MySQL, Next.js 14, React, TypeScript, Tailwind CSS

# Integrated Smart City Traffic Management System

> A unified platform combining traffic monitoring, environmental sensing, and intelligent analysis

## 🌟 Features

### Traffic Management
- **Real-time Camera Monitoring** - Live traffic analysis from multiple intersections
- **RMI-based Communication** - Distributed camera service access
- **Traffic Flow Analysis** - SOAP-based traffic flow monitoring (4 routes: Nord, Sud, Est, Ouest)
- **Intelligent Traffic Light Control** - Automated and manual traffic light management
- **Congestion Detection** - Real-time congestion alerts and recommendations

### Environmental Monitoring
- **Air Quality Tracking** - CO₂ level monitoring across multiple zones
- **Noise Level Monitoring** - Real-time noise pollution tracking
- **Multi-zone Coverage** - Zone_Centre, Zone_Nord, Zone_Sud

### Data Processing
- **Kafka Event Streaming** - High-throughput event processing (Kafka 2.13)
- **Multi-protocol Integration** - REST, SOAP, RMI, and TCP
- **MySQL Storage** - Persistent data storage with optimized schema
- **Real-time Analytics** - Automated alert generation and recommendations

### User Interface
- **Modern Dashboard** - Next.js-based real-time visualization
- **RESTful APIs** - Comprehensive API access to all data
- **Multiple Simulators** - Realistic data generation for testing

## 🏗️ Architecture

```
┌─────────────────┐
│   Dashboard     │ (Next.js)
│   Port: 3000    │
└────────┬────────┘
         │
    ┌────┴────┐
    │  APIs   │
    └────┬────┘
         │
┌────────┴──────────────────────────┐
│                                   │
│  ┌──────────┐    ┌─────────────┐ │
│  │  Kafka   │───▶│   Central   │ │
│  │ Broker   │    │  Analysis   │ │
│  └─────┬────┘    └──────┬──────┘ │
│        │                │         │
│   ┌────┴────┐      ┌────┴────┐   │
│   │ Topics  │      │  MySQL  │   │
│   └─────────┘      └─────────┘   │
└───────────────────────────────────┘
         ▲
         │
┌────────┴─────────────────────────┐
│        Services                  │
├──────────────────────────────────┤
│ • Camera Service (REST/RMI)      │
│ • SOAP Services (Flow/Lights)    │
│ • Pollution Service (REST)       │
│ • Noise Service (TCP)            │
└─────────────┬────────────────────┘
              │
    ┌─────────┴──────────┐
    │    Simulators      │
    └────────────────────┘
```

## 📦 Components

| Component | Technology | Port | Description |
|-----------|-----------|------|-------------|
| Traffic Camera Service | Java (REST/RMI) | 8080 | Camera monitoring and analysis |
| SOAP Services | JAX-WS | 8080, 8081 | Traffic flow and light control |
| Central WebApp | JAX-RS (Tomcat) | 9999 | Unified REST API |
| Pollution Service | JAX-RS | 8082 | Air quality monitoring |
| Noise Service | TCP Sockets | 5000 | Noise level monitoring |
| Central Analysis | Kafka Consumer | - | Data processing and alerts |
| Dashboard | Next.js | 3000 | Web UI |
| Kafka | Apache Kafka 2.13 | 9092 | Event streaming |
| MySQL | MySQL 8.x | 3306 | Data persistence |

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- MySQL 8.x
- Kafka 2.13-3.8.0
- Node.js 18+ (for dashboard)
- Tomcat 10 (for central webapp)

### Installation

1. **Set up environment**
   ```bash
   export KAFKA_HOME=/opt/kafka
   ```

2. **Clone and navigate**
   ```bash
   cd integrated-traffic-system
   ```

3. **Make scripts executable**
   ```bash
   chmod +x scripts/*.sh
   ```

4. **Start infrastructure**
   ```bash
   ./scripts/start-kafka.sh
   ./scripts/setup-kafka.sh
   ./scripts/setup-database.sh
   ```

5. **Start all services**
   ```bash
   ./scripts/start-all.sh
   ```

6. **Access the dashboard**
   ```
   http://localhost:3000
   ```

**See [QUICKSTART.md](QUICKSTART.md) for detailed instructions.**

## 🧪 Testing

### API Examples

**Traffic Camera:**
```bash
curl http://localhost:8080/api/traffic/cameras/latest
```

**Traffic Flow:**
```bash
curl http://localhost:9999/centrale/api/Flux/latest
```

**Traffic Lights:**
```bash
curl http://localhost:9999/centrale/api/Feux/etat
```

**Control Lights:**
```bash
curl -X POST http://localhost:9999/centrale/api/Feux/force/nord \
  -H "Content-Type: application/json" \
  -d '{"duration":20,"green":true}'
```

### Kafka Monitoring

```bash
# Monitor camera events
$KAFKA_HOME/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic camera-data

# Monitor traffic flow
$KAFKA_HOME/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic service-flux
```

## 📊 Database

**Database Name:** `integrated_traffic_system`

**Main Tables:**
- `camera_events` - Raw camera data
- `traffic_analysis` - Analyzed traffic patterns
- `flux` - Traffic flow measurements
- `pollution` - Air quality data
- `bruit` - Noise level data
- `alerts` - System alerts
- `recommendations` - Traffic recommendations

**Views:**
- `v_latest_traffic_state`
- `v_active_alerts_summary`
- `v_latest_pollution`
- `v_latest_noise`

## 🔧 Configuration

**Main config file:** `config/system.properties`

**Key settings:**
```properties
# Database
db.url=jdbc:mysql://localhost:3306/integrated_traffic_system
db.username=root

# Kafka
kafka.bootstrap.servers=localhost:9092

# Simulation intervals
camera.event.interval.seconds=5
flux.update.interval.seconds=5
pollution.update.interval.seconds=10

# Alert thresholds
alert.traffic.congestion.threshold=80
alert.pollution.co2.high=100.0
alert.noise.high=90.0
```

## 📁 Project Structure

```
integrated-traffic-system/
├── pom.xml                         # Parent POM
├── QUICKSTART.md                   # Quick start guide
├── README.md                       # This file
├── MIGRATION.md                    # Migration from old projects
├── config/
│   └── system.properties           # System configuration
├── database/
│   └── schema.sql                  # Database schema
├── scripts/
│   ├── start-kafka.sh
│   ├── setup-kafka.sh
│   ├── setup-database.sh
│   ├── start-all.sh
│   ├── stop-all.sh
│   └── stop-kafka.sh
├── traffic-camera-service/         # Module: Camera monitoring
├── soap-services/                   # Module: SOAP services
├── soap-client-simulator/           # Module: SOAP client
├── central-webapp/                  # Module: Central API (WAR)
├── pollution-service-rest/          # Module: Pollution monitoring
├── noise-service-tcp/               # Module: Noise monitoring
├── central-analysis-service/        # Module: Data processing
├── pollution-simulator/             # Module: Pollution simulator
└── noise-simulator/                 # Module: Noise simulator
```

## 🛠️ Development

### Build Project
```bash
mvn clean install
```

### Build Specific Module
```bash
cd traffic-camera-service
mvn clean package
```

### Run Tests
```bash
mvn test
```

### View Logs
```bash
tail -f logs/*.log
```

## 🛑 Stopping Services

```bash
# Stop all application services
./scripts/stop-all.sh

# Stop Kafka infrastructure
./scripts/stop-kafka.sh

# Stop MySQL
sudo systemctl stop mysql
```

## 📈 Monitoring

### Check Service Status
```bash
# Check running services
ps aux | grep java

# Check specific port
lsof -i :8080

# View process IDs
cat pids/*.pid
```

### Database Monitoring
```bash
mysql -u root integrated_traffic_system

# View live data
SELECT * FROM camera_events ORDER BY timestamp DESC LIMIT 10;
SELECT * FROM alerts WHERE status='ACTIVE';
SELECT type, COUNT(*) FROM alerts GROUP BY type;
```

## 🔐 Security

**Important:** Default configuration is for development only.

For production:
- Set MySQL passwords
- Enable Kafka authentication
- Configure CORS properly  
- Use HTTPS
- Implement API authentication

## 📄 Documentation

- [QUICKSTART.md](QUICKSTART.md) - Complete setup guide
- [MIGRATION.md](MIGRATION.md) - Migration from old projects
- [config/system.properties](config/system.properties) - Configuration reference
- [database/schema.sql](database/schema.sql) - Database documentation

## 🤝 Contributing

This is an integrated academic project combining multiple systems:
- Traffic monitoring system
- SOAP-based traffic management
- Environmental monitoring platform

## 📞 Support

For issues:
1. Check logs in `logs/` directory
2. Review [QUICKSTART.md](QUICKSTART.md) troubleshooting section
3. Verify all prerequisites
4. Ensure all ports are available

## 🎯 Use Cases

1. **Smart City Traffic Management** - Monitor and control traffic in real-time
2. **Environmental Monitoring** - Track air quality and noise pollution
3. **Incident Detection** - Automatic detection of traffic incidents
4. **Data Analysis** - Historical traffic and environmental data analysis
5. **Educational Platform** - Learn distributed systems, microservices, and integration patterns

## 🌍 Technologies

- **Java 21** - Modern Java features
- **Apache Kafka 2.13** - Event streaming
- **MySQL 8** - Relational database
- **JAX-RS (Jersey)** - REST APIs
- **JAX-WS** - SOAP services
- **RMI** - Remote method invocation
- **TCP Sockets** - Low-level networking
- **Next.js** - Modern web framework
- **Maven** - Build tool

## 📝 License

Educational project - Please refer to individual module licenses.

---

**Ready to start?** See [QUICKSTART.md](QUICKSTART.md)

**Need help migrating?** See [MIGRATION.md](MIGRATION.md)

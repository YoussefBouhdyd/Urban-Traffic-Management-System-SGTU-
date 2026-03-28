# Quick Reference Card

## 🚀 Essential Commands

### First Time Setup
```bash
# 1. Verify prerequisites
make verify
# or: ./scripts/verify-system.sh

# 2. Start infrastructure
make kafka-start
make kafka-setup
make db-setup

# 3. Start everything
make start
# or: ./scripts/start-all.sh
```

### Daily Operations
```bash
# Start system
make start

# Stop system
make stop

# Restart system
make restart

# View logs
make logs
```

---

## 🌐 Access Points

| Service | URL | Description |
|---------|-----|-------------|
| **Dashboard** | http://localhost:3000 | Main web interface |
| **Camera API** | http://localhost:8083/api/traffic | Traffic camera data |
| **Central API** | http://localhost:9999/centrale/api | Unified REST API |
| **Pollution API** | http://localhost:8082/api/pollution | Air quality data |
| **SOAP ServiceFlux** | http://localhost:8080/ServiceFlux?wsdl | Traffic flow SOAP |
| **SOAP ServiceFeux** | http://localhost:8081/ServiceFeux?wsdl | Traffic lights SOAP |

---

## 📊 API Quick Reference

### Traffic Camera API

```bash
# Get latest camera events
curl http://localhost:8083/api/traffic/latest

# Get traffic analysis history
curl http://localhost:8083/api/traffic/history

# Get alerts
curl http://localhost:8083/api/alerts

# Get recommendations
curl http://localhost:8083/api/recommendations

# Get camera status
curl http://localhost:8083/api/cameras/CAM-01/status
```

### Central API (SOAP Integration)

```bash
# Latest traffic flow
curl http://localhost:9999/centrale/api/Flux/latest

# Traffic flow by route (nord, sud, est, ouest)
curl http://localhost:9999/centrale/api/Flux/route/nord

# Congestion alerts
curl http://localhost:9999/centrale/api/Alert

# Traffic light status
curl http://localhost:9999/centrale/api/Feux/etat

# Traffic light configuration
curl http://localhost:9999/centrale/api/Feux/config

# Change light configuration
curl -X POST http://localhost:9999/centrale/api/Feux/config \
  -H "Content-Type: application/json" \
  -d '{"duration":15,"segmentGreen":true}'

# Force route to green
curl -X POST http://localhost:9999/centrale/api/Feux/force/nord \
  -H "Content-Type: application/json" \
  -d '{"duration":20,"green":true}'
```

---

## 🔍 Monitoring Commands

### Check Services
```bash
# Check running Java services
ps aux | grep java

# Check specific ports
lsof -i :8080  # Camera API
lsof -i :9092  # Kafka
lsof -i :3306  # MySQL
lsof -i :3000  # Dashboard

# View process IDs
cat pids/*.pid
```

### Monitor Kafka
```bash
# List topics
$KAFKA_HOME/bin/kafka-topics.sh --list \
  --bootstrap-server localhost:9092

# Monitor camera events
$KAFKA_HOME/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic camera-data \
  --from-beginning

# Monitor traffic flow
$KAFKA_HOME/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic service-flux

# Monitor pollution
$KAFKA_HOME/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic pollution-topic

# Monitor noise
$KAFKA_HOME/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic bruit-topic
```

### Database Queries
```bash
# Connect to database
mysql -u root integrated_traffic_system

# Latest camera events
SELECT * FROM camera_events ORDER BY timestamp DESC LIMIT 10;

# Latest traffic analysis
SELECT * FROM traffic_analysis ORDER BY timestamp DESC LIMIT 10;

# Active alerts
SELECT * FROM alerts WHERE status='ACTIVE';

# Alert statistics
SELECT type, severity, COUNT(*) 
FROM alerts 
GROUP BY type, severity;

# Latest pollution by zone
SELECT zone_id, niveau_co2, timestamp 
FROM pollution 
ORDER BY date_insertion DESC 
LIMIT 10;

# Latest noise by zone
SELECT zone_id, niveau_decibels, timestamp 
FROM bruit 
ORDER BY date_insertion DESC 
LIMIT 10;

# Traffic flow statistics
SELECT name, AVG(flux) as avg_flow, COUNT(*) as count 
FROM flux 
GROUP BY name;
```

---

## 🛠️ Troubleshooting

### Service Won't Start
```bash
# Check logs
tail -f logs/SERVICE_NAME.log

# Check if port is available
lsof -i :PORT

# Kill process on port
kill -9 $(lsof -t -i:PORT)
```

### Kafka Issues
```bash
# Check Kafka status
lsof -i :9092

# Restart Kafka
./scripts/stop-kafka.sh
./scripts/start-kafka.sh

# Recreate topics
./scripts/setup-kafka.sh
```

### Database Issues
```bash
# Check MySQL status
sudo systemctl status mysql

# Restart MySQL
sudo systemctl restart mysql

# Recreate database
mysql -u root -e "DROP DATABASE IF EXISTS integrated_traffic_system;"
./scripts/setup-database.sh
```

### Build Issues
```bash
# Clean and rebuild
mvn clean install -U

# Skip tests
mvn clean install -DskipTests

# Build specific module
cd MODULE_NAME
mvn clean package
```

---

## 📁 Important Files

| File | Purpose |
|------|---------|
| `config/system.properties` | System configuration |
| `database/schema.sql` | Database schema |
| `logs/*.log` | Service logs |
| `pids/*.pid` | Process IDs |
| `QUICKSTART.md` | Detailed setup guide |
| `README.md` | Project overview |
| `MIGRATION.md` | Migration guide |

---

## 🔧 Configuration

### Database Connection
```properties
# config/system.properties
db.url=jdbc:mysql://localhost:3306/integrated_traffic_system
db.username=root
db.password=
```

### Kafka Settings
```properties
kafka.bootstrap.servers=localhost:9092
kafka.topic.camera.data=camera-data
kafka.topic.service.flux=service-flux
kafka.topic.pollution=pollution-topic
kafka.topic.noise=bruit-topic
```

### Simulation Intervals
```properties
camera.event.interval.seconds=5
flux.update.interval.seconds=5
pollution.update.interval.seconds=10
noise.update.interval.seconds=10
```

### Alert Thresholds
```properties
alert.traffic.congestion.threshold=80
alert.pollution.co2.high=100.0
alert.noise.high=90.0
```

---

## 📦 Ports Reference

| Port | Service | Protocol |
|------|---------|----------|
| 2181 | Zookeeper | TCP |
| 9092 | Kafka | TCP |
| 3306 | MySQL | TCP |
| 8083 | Camera API | HTTP |
| 8080 | SOAP ServiceFlux | HTTP |
| 8081 | SOAP ServiceFeux | HTTP |
| 8082 | Pollution Service | HTTP |
| 9999 | Central WebApp | HTTP |
| 5000 | Noise Service | TCP |
| 3000 | Dashboard | HTTP |
| 1099 | RMI Registry | RMI |

---

## 🚦 System Health Check

Run this to verify everything is working:

```bash
#!/bin/bash
echo "=== System Health Check ==="

# 1. Infrastructure
echo "1. Checking Kafka..."
lsof -i :9092 >/dev/null && echo "  ✓ Kafka running" || echo "  ✗ Kafka not running"

echo "2. Checking MySQL..."
systemctl is-active --quiet mysql && echo "  ✓ MySQL running" || echo "  ✗ MySQL not running"

# 2. Services
echo "3. Checking Camera API..."
curl -s http://localhost:8080/api/traffic/cameras/latest >/dev/null && echo "  ✓ Camera API responding" || echo "  ✗ Camera API not responding"

echo "4. Checking Central API..."
curl -s http://localhost:9999/centrale/api/Flux/latest >/dev/null && echo "  ✓ Central API responding" || echo "  ✗ Central API not responding"

echo "5. Checking Dashboard..."
curl -s http://localhost:3000 >/dev/null && echo "  ✓ Dashboard accessible" || echo "  ✗ Dashboard not accessible"

# 3. Database
echo "6. Checking Database..."
mysql -u root integrated_traffic_system -e "SELECT 1" >/dev/null 2>&1 && echo "  ✓ Database accessible" || echo "  ✗ Database not accessible"

echo ""
echo "Health check complete!"
```

Save as `scripts/health-check.sh`, make executable, and run:
```bash
chmod +x scripts/health-check.sh
./scripts/health-check.sh
```

---

## 📚 Documentation Links

- [QUICKSTART.md](QUICKSTART.md) - Complete setup guide
- [README.md](README.md) - Project overview
- [MIGRATION.md](MIGRATION.md) - Migration from old projects
- [INTEGRATION_SUMMARY.md](INTEGRATION_SUMMARY.md) - Integration details

---

## 🆘 Getting Help

1. **Check logs:** `tail -f logs/*.log`
2. **Verify prerequisites:** `./scripts/verify-system.sh`
3. **Review configuration:** `cat config/system.properties`
4. **Check QUICKSTART:** See troubleshooting section
5. **Database issues:** See database troubleshooting in QUICKSTART

---

**Quick Start:** `make verify && make kafka-start && make kafka-setup && make db-setup && make start`

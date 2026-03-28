# Integrated Smart City Traffic Management System - QUICKSTART

> **Complete guide for running the integrated traffic management system on Linux with Kafka 2.13**

## 🎯 System Overview

This integrated system combines multiple traffic and environmental monitoring components into a unified platform:

- **Traffic Camera Monitoring** (Kafka + RMI + REST)
- **Traffic Flow Management** (SOAP Services)
- **Traffic Light Control** (SOAP Services)
- **Air Pollution Monitoring** (REST + Kafka)
- **Noise Level Monitoring** (TCP + Kafka)
- **Central Analysis Service** (Kafka Consumer + MySQL)
- **Real-time Dashboard** (Next.js)

## 📋 Prerequisites

### Required Software

1. **Java Development Kit (JDK) 21**
   ```bash
   # Check Java version
   java -version
   # Required output: openjdk version "21" or higher
   ```
   
   **Install on Ubuntu/Debian:**
   ```bash
   sudo apt update
   sudo apt install openjdk-21-jdk
   ```
   
   **Install on Fedora/RHEL:**
   ```bash
   sudo dnf install java-21-openjdk-devel
   ```

2. **Apache Maven 3.6+**
   ```bash
   # Check Maven version
   mvn --version
   ```
   
   **Install:**
   ```bash
   sudo apt install maven  # Ubuntu/Debian
   sudo dnf install maven  # Fedora/RHEL
   ```

3. **MySQL Server 8.x**
   ```bash
   # Check MySQL version
   mysql --version
   ```
   
   **Install on Ubuntu/Debian:**
   ```bash
   sudo apt install mysql-server
   sudo systemctl start mysql
   sudo systemctl enable mysql
   ```
   
   **Install on Fedora/RHEL:**
   ```bash
   sudo dnf install mysql-server
   sudo systemctl start mysqld
   sudo systemctl enable mysqld
   ```

4. **Apache Kafka 2.13-3.8.0**
   ```bash
   # Download Kafka
   cd /opt
   sudo wget https://downloads.apache.org/kafka/3.8.0/kafka_2.13-3.8.0.tgz
   sudo tar -xzf kafka_2.13-3.8.0.tgz
   sudo mv kafka_2.13-3.8.0 kafka
   
   # Set KAFKA_HOME environment variable
   echo 'export KAFKA_HOME=/opt/kafka' >> ~/.bashrc
   echo 'export PATH=$PATH:$KAFKA_HOME/bin' >> ~/.bashrc
   source ~/.bashrc
   
   # Verify installation
   echo $KAFKA_HOME
   ```

5. **Node.js 18+ and npm** (for Dashboard)
   ```bash
   # Check Node.js version
   node --version
   ```
   
   **Install on Ubuntu/Debian:**
   ```bash
   curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
   sudo apt install -y nodejs
   ```
   
   **Install on Fedora/RHEL:**
   ```bash
   sudo dnf install nodejs npm
   ```

6. **Apache Tomcat 10** (for Central WebApp)
   ```bash
   # Download and install Tomcat
   cd /opt
   sudo wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.20/bin/apache-tomcat-10.1.20.tar.gz
   sudo tar -xzf apache-tomcat-10.1.20.tar.gz
   sudo mv apache-tomcat-10.1.20 tomcat10
   
   # Create directory for webapps
   sudo mkdir -p /var/lib/tomcat10/webapps
   sudo ln -s /opt/tomcat10/webapps /var/lib/tomcat10/webapps
   ```

### System Requirements

- **RAM:** 8GB minimum, 16GB recommended
- **Disk:** 10GB free space
- **OS:** Ubuntu 20.04+, Debian 11+, Fedora 35+, or RHEL 8+
- **Ports Required:**
  - 2181 (Zookeeper)
  - 9092 (Kafka)
  - 3306 (MySQL)
  - 8080 (Traffic Camera API & SOAP ServiceFlux)
  - 8081 (SOAP ServiceFeux)
  - 8082 (Pollution Service)
  - 8083 (Central Analysis Service)
  - 9999 (Central WebApp/Tomcat)
  - 5000 (Noise Service TCP)
  - 3000 (Dashboard)
  - 1099 (RMI Registry)

---

## 🚀 Quick Start (5 Minutes)

### Step 1: Clone/Download the Project

```bash
cd /home/youssefbouhdyd/WorkSpace/Java\ Project\ Traffic/
cd integrated-traffic-system
```

### Step 2: Make Scripts Executable

```bash
chmod +x scripts/*.sh
```

### Step 3: Set Up Infrastructure

```bash
# Start Kafka and Zookeeper
./scripts/start-kafka.sh

# Create Kafka topics
./scripts/setup-kafka.sh

# Set up MySQL database
./scripts/setup-database.sh
```

### Step 4: Start All Services

```bash
# This will build and start everything automatically
./scripts/start-all.sh
```

The startup script will:
1. ✅ Check all prerequisites
2. ✅ Build all Java modules with Maven
3. ✅ Start all backend services
4. ✅ Start simulators
5. ✅ Deploy web applications
6. ✅ Start the dashboard

**⏱️ Total startup time:** ~2-3 minutes

### Step 5: Verify System

Once started, you should see:
```
========================================
✓ All services started successfully!
========================================

Service Status:
  ✓ Kafka:                   localhost:9092
  ✓ MySQL:                   localhost:3306
  ✓ Traffic Camera API:      http://localhost:8083
  ✓ SOAP Services:           http://localhost:8080 & :8081
  ✓ Pollution Service:       http://localhost:8082
  ✓ Noise Service:           TCP :5000
  ✓ Central Analysis:        Running (Kafka consumer)
  ✓ Centrale REST API:       http://localhost:9999/centrale/api
  ✓ Dashboard:               http://localhost:3000
```

### Step 6: Access the System

**Dashboard:**
```bash
# Open in your browser
http://localhost:3000
```

**API Endpoints:**
- Traffic Camera: http://localhost:8083/api/traffic
- Central API: http://localhost:9999/centrale/api
- Pollution API: http://localhost:8082/api/pollution

---

## 📊 Testing the System

### 1. Test Traffic Camera API

```bash
# Get latest traffic analysis
curl http://localhost:8083/api/traffic/latest

# Get camera status (replace CAM-01 with your camera ID)
curl http://localhost:8083/api/cameras/CAM-01/status

# Get all active alerts
curl http://localhost:8083/api/alerts

# Get all recommendations
curl http://localhost:8083/api/recommendations
```

### 2. Test Central API (SOAP Integration)

```bash
# Get latest traffic flow by route
curl http://localhost:9999/centrale/api/Flux/latest

# Get traffic flow history for a specific route
curl http://localhost:9999/centrale/api/Flux/route/nord

# Get congestion alerts
curl http://localhost:9999/centrale/api/Alert

# Get traffic light status
curl http://localhost:9999/centrale/api/Feux/etat

# Get traffic light configuration
curl http://localhost:9999/centrale/api/Feux/config
```

### 3. Test Traffic Light Control

```bash
# Change traffic light configuration
curl -X POST http://localhost:9999/centrale/api/Feux/config \
  -H "Content-Type: application/json" \
  -d '{"duration":15,"segmentGreen":true}'

# Force a specific route to green
curl -X POST http://localhost:9999/centrale/api/Feux/force/nord \
  -H "Content-Type: application/json" \
  -d '{"duration":20,"green":true}'
```

### 4. Test Pollution Monitoring

```bash
# Get pollution data (through Central Analysis Service)
# Data is automatically consumed from Kafka and stored in MySQL

# Query database directly
mysql -u root integrated_traffic_system -e "SELECT * FROM pollution ORDER BY date_insertion DESC LIMIT 10;"
```

### 5. Test Noise Monitoring

```bash
# Get noise data
mysql -u root integrated_traffic_system -e "SELECT * FROM bruit ORDER BY date_insertion DESC LIMIT 10;"
```

### 6. Monitor Kafka Topics

```bash
# Monitor camera data
$KAFKA_HOME/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic camera-data \
  --from-beginning

# Monitor traffic flow
$KAFKA_HOME/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic service-flux \
  --from-beginning

# Monitor pollution data
$KAFKA_HOME/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic pollution-topic \
  --from-beginning

# Monitor noise data
$KAFKA_HOME/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic bruit-topic \
  --from-beginning
```

---

## 🛠️ Manual Setup (Detailed)

If you prefer manual setup or troubleshooting:

### 1. Start Infrastructure Manually

#### Start MySQL
```bash
sudo systemctl start mysql
sudo systemctl status mysql
```

#### Start Zookeeper
```bash
cd $KAFKA_HOME
bin/zookeeper-server-start.sh config/zookeeper.properties &
```

#### Start Kafka
```bash
cd $KAFKA_HOME
bin/kafka-server-start.sh config/server.properties &
```

#### Create Kafka Topics
```bash
# Camera data topic
kafka-topics.sh --create --topic camera-data \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# Traffic alerts topic
kafka-topics.sh --create --topic traffic-alerts \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# Traffic recommendations topic
kafka-topics.sh --create --topic traffic-recommendations \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# Service flux topic
kafka-topics.sh --create --topic service-flux \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# Pollution topic
kafka-topics.sh --create --topic pollution-topic \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# Noise topic
kafka-topics.sh --create --topic bruit-topic \
  --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

#### Initialize Database
```bash
mysql -u root -e "CREATE DATABASE IF NOT EXISTS integrated_traffic_system;"
mysql -u root integrated_traffic_system < database/schema.sql
```

### 2. Build All Modules

```bash
cd integrated-traffic-system

# Build parent and all modules
mvn clean install -DskipTests
```

### 3. Start Services Manually

#### Start Traffic Camera Service
```bash
cd traffic-camera-service
java -jar target/traffic-camera-service-1.0.0.jar --camera-mode &
```

#### Start SOAP Services
```bash
cd soap-services
java -jar target/soap-services-1.0.0.jar &
```

#### Start Pollution Service
```bash
cd pollution-service-rest
java -jar target/pollution-service-rest-1.0.0.jar &
```

#### Start Noise Service
```bash
cd noise-service-tcp
java -jar target/noise-service-tcp-1.0.0.jar &
```

#### Start Central Analysis Service
```bash
cd central-analysis-service
java -jar target/central-analysis-service-1.0.0.jar &
```

#### Start Simulators
```bash
# SOAP Client Simulator
cd soap-client-simulator
java -jar target/soap-client-simulator-1.0.0.jar &

# Camera Simulator
cd traffic-camera-service
java -jar target/traffic-camera-service-1.0.0.jar --simulator-mode &

# Pollution Simulator
cd pollution-simulator
java -jar target/pollution-simulator-1.0.0.jar &

# Noise Simulator
cd noise-simulator
java -jar target/noise-simulator-1.0.0.jar &
```

#### Deploy Central WebApp to Tomcat
```bash
cd central-webapp
mvn clean package
sudo cp target/centrale.war /var/lib/tomcat10/webapps/

# Start Tomcat
/opt/tomcat10/bin/startup.sh
```

#### Start Dashboard
```bash
cd ../sgtu-dashboard
npm install
npm run dev &
```

---

## 🔧 Configuration

### Database Configuration

Edit connection settings in each module's `application.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/integrated_traffic_system
db.username=root
db.password=YOUR_PASSWORD
```

Or modify the central configuration:
```bash
nano config/system.properties
```

### Kafka Configuration

All Kafka settings are centralized in `config/system.properties`:

```properties
kafka.bootstrap.servers=localhost:9092
kafka.topic.camera.data=camera-data
kafka.topic.service.flux=service-flux
kafka.topic.pollution=pollution-topic
kafka.topic.noise=bruit-topic
```

### Simulation Settings

Adjust simulation intervals and thresholds:

```properties
camera.event.interval.seconds=5
flux.update.interval.seconds=5
pollution.update.interval.seconds=10
noise.update.interval.seconds=10

alert.traffic.congestion.threshold=80
alert.pollution.co2.high=100.0
alert.noise.high=90.0
```

---

## 🛑 Stopping the System

### Stop All Services
```bash
./scripts/stop-all.sh
```

### Stop Kafka Infrastructure
```bash
./scripts/stop-kafka.sh
```

### Stop MySQL
```bash
sudo systemctl stop mysql
```

### Stop Tomcat
```bash
/opt/tomcat10/bin/shutdown.sh
```

---

## 📁 Project Structure

```
integrated-traffic-system/
├── pom.xml                          # Parent POM
├── README.md                        # This file
├── config/
│   └── system.properties            # Centralized configuration
├── database/
│   └── schema.sql                   # Database schema
├── scripts/
│   ├── start-kafka.sh              # Start Kafka & Zookeeper
│   ├── setup-kafka.sh              # Create Kafka topics
│   ├── setup-database.sh           # Initialize MySQL
│   ├── start-all.sh                # Start all services
│   ├── stop-all.sh                 # Stop all services
│   └── stop-kafka.sh               # Stop Kafka & Zookeeper
├── logs/                            # Log files (created at runtime)
├── pids/                            # Process IDs (created at runtime)
├── traffic-camera-service/         # Camera monitoring + RMI
├── soap-services/                   # SOAP services (Flow & Lights)
├── soap-client-simulator/           # SOAP client simulator
├── central-webapp/                  # Central WebApp (WAR for Tomcat)
├── pollution-service-rest/          # Pollution REST service
├── noise-service-tcp/               # Noise TCP service
├── central-analysis-service/        # Central Kafka consumer
├── pollution-simulator/             # Pollution data simulator
└── noise-simulator/                 # Noise data simulator
```

---

## 📝 Logs and Monitoring

### View Logs

```bash
# View all logs in real-time
tail -f logs/*.log

# View specific service log
tail -f logs/traffic-camera.log
tail -f logs/central-analysis.log
tail -f logs/pollution-service.log
```

### Check Service Status

```bash
# Check running processes
ps aux | grep java

# Check specific port
lsof -i :8080
lsof -i :9092

# View PIDs
cat pids/*.pid
```

### Monitor Database

```bash
# Connect to MySQL
mysql -u root integrated_traffic_system

# View recent data
SELECT * FROM camera_events ORDER BY timestamp DESC LIMIT 10;
SELECT * FROM traffic_analysis ORDER BY timestamp DESC LIMIT 10;
SELECT * FROM alerts WHERE status='ACTIVE';
SELECT * FROM pollution ORDER BY date_insertion DESC LIMIT 10;
SELECT * FROM bruit ORDER BY date_insertion DESC LIMIT 10;

# View statistics
SELECT type, COUNT(*) FROM alerts GROUP BY type;
SELECT zone_id, AVG(niveau_co2) FROM pollution GROUP BY zone_id;
```

---

## 🐛 Troubleshooting

### Kafka Issues

**Problem:** Kafka fails to start
```bash
# Check if Zookeeper is running
lsof -i :2181

# Check Kafka logs
tail -f $KAFKA_HOME/logs/server.log

# Clean Kafka data and restart
rm -rf /tmp/kafka-logs
./scripts/start-kafka.sh
```

**Problem:** Topics not created
```bash
# List existing topics
$KAFKA_HOME/bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# Recreate topics
./scripts/setup-kafka.sh
```

### MySQL Issues

**Problem:** Cannot connect to database
```bash
# Check MySQL status
sudo systemctl status mysql

# Reset root password
sudo mysql
ALTER USER 'root'@'localhost' IDENTIFIED BY '';
FLUSH PRIVILEGES;
```

**Problem:** Database not created
```bash
# Recreate database
mysql -u root -e "DROP DATABASE IF EXISTS integrated_traffic_system;"
./scripts/setup-database.sh
```

### Port Conflicts

**Problem:** Port already in use
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 PID
```

### Build Failures

**Problem:** Maven build fails
```bash
# Clean and rebuild
mvn clean install -U -DskipTests

# Check Java version
java -version
# Must be 21 or higher
```

### Service Not Starting

**Problem:** Service fails to start
```bash
# Check logs
tail -f logs/SERVICE_NAME.log

# Check if dependencies are running
lsof -i :9092  # Kafka
lsof -i :3306  # MySQL

# Restart service manually
cd SERVICE_MODULE
java -jar target/SERVICE_JAR.jar
```

---

## 🎓 Architecture Overview

### Components

1. **Traffic Camera System**
   - Camera Simulator (generates events)
   - Camera Service (REST API + RMI)
   - Kafka Producer (publishes to camera-data topic)

2. **SOAP Services**
   - ServiceFlux (traffic flow management)
   - ServiceFeux (traffic light control)
   - Kafka Producer (publishes to service-flux topic)

3. **Environmental Monitoring**
   - Pollution Service (REST API + Kafka)
   - Noise Service (TCP Server + Kafka)
   - Simulators for both services

4. **Central Analysis**
   - Kafka Consumers (all topics)
   - MySQL Storage
   - Alert Generation
   - Recommendation Engine

5. **Web Applications**
   - Central WebApp (JAX-RS REST API on Tomcat)
   - Dashboard (Next.js)

### Data Flow

```
[Simulators] → [Services] → [Kafka Topics] → [Central Analysis] → [MySQL]
                     ↓                              ↓
                [REST APIs]                   [Dashboard]
```

---

## 🔒 Security Notes

- Default configuration uses no passwords (development only)
- Configure MySQL passwords in production
- Enable Kafka authentication for production
- Configure CORS properly for production dashboard
- Use HTTPS for production deployments

---

## 📞 Support

For issues or questions:
1. Check the logs in `logs/` directory
2. Review the troubleshooting section above
3. Verify all prerequisites are met
4. Ensure all ports are available

---

## 📄 License

This integrated system combines multiple projects. Please refer to individual module licenses.

---

## ✅ Checklist

Before running the system:

- [ ] Java 21+ installed
- [ ] Maven 3.6+ installed
- [ ] MySQL 8.x installed and running
- [ ] Kafka 2.13-3.8.0 installed
- [ ] KAFKA_HOME environment variable set
- [ ] Node.js 18+ installed (for dashboard)
- [ ] Tomcat 10 installed (for central webapp)
- [ ] All ports available (check with `lsof -i :PORT`)
- [ ] Scripts made executable (`chmod +x scripts/*.sh`)

After running setup:

- [ ] Kafka topics created (6 topics)
- [ ] Database created with all tables
- [ ] All services started successfully
- [ ] Dashboard accessible at http://localhost:3000
- [ ] APIs responding to requests

---

**Ready to start? Run: `./scripts/start-all.sh`**

🎉 **Enjoy your integrated smart city traffic management system!**

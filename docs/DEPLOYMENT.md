# System Deployment and Integration

## Overview

This guide provides comprehensive instructions for deploying, configuring, and integrating the complete Integrated Traffic Management System. The system consists of multiple services (Java backend services, Next.js dashboard) that must be properly configured, started in the correct order, and monitored for successful operation.

## System Requirements

### Hardware Requirements

**Minimum:**
- CPU: 4 cores
- RAM: 8 GB
- Storage: 20 GB free space
- Network: 100 Mbps

**Recommended:**
- CPU: 8 cores
- RAM: 16 GB
- Storage: 50 GB SSD
- Network: 1 Gbps

### Software Requirements

**Core Dependencies:**
- Java JDK 21 or higher
- Apache Maven 3.6+
- MySQL 8.0+
- Apache Kafka 3.0+
- Node.js 18+ (for dashboard)
- npm / yarn / pnpm

**Operating System:**
- Linux (Ubuntu 20.04+, CentOS 8+)
- macOS 11+
- Windows 10+ (with WSL2 recommended)

## Pre-Deployment Setup

### 1. Install Java JDK

**Linux (Ubuntu):**
```bash
sudo apt update
sudo apt install openjdk-21-jdk
java -version
```

**macOS:**
```bash
brew install openjdk@21
java -version
```

**Windows:**
Download from Oracle or use SDKMAN:
```bash
sdk install java 21.0.1-open
```

### 2. Install Maven

**Linux:**
```bash
sudo apt install maven
mvn -version
```

**macOS:**
```bash
brew install maven
```

**Windows:**
Download from https://maven.apache.org/download.cgi and add to PATH.

### 3. Install MySQL

**Linux:**
```bash
sudo apt install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
sudo mysql_secure_installation
```

**macOS:**
```bash
brew install mysql
brew services start mysql
```

**Windows:**
Download MySQL Installer from https://dev.mysql.com/downloads/installer/

### 4. Install Apache Kafka

**Download and Extract:**
```bash
cd /opt
wget https://downloads.apache.org/kafka/3.6.0/kafka_2.13-3.6.0.tgz
tar -xzf kafka_2.13-3.6.0.tgz
cd kafka_2.13-3.6.0
```

**Start Zookeeper:**
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

**Start Kafka Broker (in new terminal):**
```bash
bin/kafka-server-start.sh config/server.properties
```

**Create Required Topics:**
```bash
bin/kafka-topics.sh --create --topic camera-data --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic pollution-alerts --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic noise-alerts --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic pollution-measurements --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic noise-measurements --bootstrap-server localhost:9092
```

### 5. Install Node.js and npm

**Linux:**
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs
node -v
npm -v
```

**macOS:**
```bash
brew install node@18
```

**Windows:**
Download from https://nodejs.org/

### 6. Database Setup

**Create Database:**
```bash
mysql -u root -p
```

```sql
CREATE DATABASE integrated_traffic_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'traffic_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON integrated_traffic_system.* TO 'traffic_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

**Import Schema:**
```bash
cd integrated-traffic-system
mysql -u traffic_user -p integrated_traffic_system < database/schema.sql
```

## Building the System

### Quick Build Script

The project includes a setup script that builds all components:

```bash
cd integrated-traffic-system
chmod +x setup.sh
./setup.sh
```

This script:
1. Checks all prerequisites
2. Builds all Java projects with Maven
3. Installs dashboard dependencies
4. Creates necessary directories
5. Sets up log rotation

### Manual Build Process

If you prefer manual builds:

**1. Build Traffic Camera Service:**
```bash
cd Traffic
mvn clean package
```

**2. Build SOAP Services:**
```bash
cd javaproject/services
mvn clean package
```

**3. Build Centrale Service:**
```bash
cd javaproject/centraleservice
mvn clean package
```

**4. Build SOAP Client:**
```bash
cd javaproject/client
mvn clean package
```

**5. Build Pollution Service:**
```bash
cd sgtu-backend/service-pollution-rest
mvn clean package
```

**6. Build Noise Service:**
```bash
cd sgtu-backend/service-bruit-tcp
mvn clean package
```

**7. Build Simulators:**
```bash
cd sgtu-backend/simulateur-pollution
mvn clean package

cd ../simulateur-bruit
mvn clean package
```

**8. Install Dashboard Dependencies:**
```bash
cd sgtu-dashboard
npm install
npm run build
```

## Starting the System

### Using Automated Scripts

The recommended way to start the entire system:

```bash
cd integrated-traffic-system/scripts
./start-all.sh
```

This script starts services in the correct order:
1. MySQL database check
2. Kafka broker check
3. Pollution Service (port 8080)
4. Noise Service (port 9999)
5. SOAP Services (port 8080)
6. Centrale Service (port 9999)
7. Traffic Camera Simulator (RMI port 1099)
8. Central Analysis Service (port 8083)
9. Dashboard (port 3000)

**Verify Services:**
```bash
./check-services.sh
```

**Stop All Services:**
```bash
./stop-all.sh
```

### Manual Service Startup

If you need to start services individually:

**1. Start MySQL:**
```bash
sudo systemctl start mysql
```

**2. Start Kafka:**
```bash
# Terminal 1 - Zookeeper
cd /opt/kafka_2.13-3.6.0
bin/zookeeper-server-start.sh config/zookeeper.properties

# Terminal 2 - Kafka Broker
bin/kafka-server-start.sh config/server.properties
```

**3. Start Pollution Service:**
```bash
cd sgtu-backend/service-pollution-rest
java -jar target/service-pollution-rest.jar > logs/pollution.log 2>&1 &
echo $! > pollution.pid
```

**4. Start Noise Service:**
```bash
cd sgtu-backend/service-bruit-tcp
java -jar target/service-bruit-tcp.jar > logs/noise.log 2>&1 &
echo $! > noise.pid
```

**5. Start SOAP Services:**
```bash
cd javaproject/services
java -jar target/services.jar > logs/soap.log 2>&1 &
echo $! > soap.pid
```

**6. Start Centrale Service:**
```bash
cd javaproject/centraleservice
java -jar target/centrale.jar > logs/centrale.log 2>&1 &
echo $! > centrale.pid
```

**7. Start Camera Simulator:**
```bash
cd Traffic
java -cp "target/traffic-management-1.0-SNAPSHOT.jar:target/lib/*" \
  com.smartcity.traffic.CameraSimulatorMain > logs/camera-sim.log 2>&1 &
echo $! > camera-sim.pid
```

**8. Start Central Analysis:**
```bash
cd Traffic
java -jar target/traffic-management-1.0-SNAPSHOT.jar > logs/central-analysis.log 2>&1 &
echo $! > central-analysis.pid
```

**9. Start Dashboard:**
```bash
cd sgtu-dashboard
npm run start > logs/dashboard.log 2>&1 &
echo $! > dashboard.pid
```

## Service Ports Reference

| Service | Port | Protocol | Purpose |
|---------|------|----------|---------|
| MySQL Database | 3306 | TCP | Database server |
| Kafka Broker | 9092 | TCP | Message broker |
| Zookeeper | 2181 | TCP | Kafka coordination |
| Pollution Service | 8080 | HTTP | Air quality API |
| SOAP Services | 8080 | HTTP | Traffic SOAP APIs |
| Noise Service | 9999 | TCP | Noise monitoring socket |
| Centrale Service | 9999 | HTTP | Central traffic API |
| Camera Simulator | 1099 | RMI | Camera RMI service |
| Central Analysis | 8083 | HTTP | Traffic analysis API |
| Dashboard | 3000 | HTTP | Web interface |

**Note:** Some services share ports (8080, 9999). Ensure you understand which services can coexist and which require different configurations.

## Configuration Management

### Environment-Specific Configuration

Create configuration files for different environments:

**Development (config/dev.properties):**
```properties
# Database
db.url=jdbc:mysql://localhost:3306/integrated_traffic_system
db.username=traffic_user
db.password=dev_password

# Kafka
kafka.bootstrap.servers=localhost:9092

# API URLs
api.camera.url=http://localhost:8083/api
api.centrale.url=http://localhost:9999/centrale/api
api.pollution.url=http://localhost:8080/api/pollution
```

**Production (config/prod.properties):**
```properties
# Database
db.url=jdbc:mysql://prod-db-server:3306/integrated_traffic_system
db.username=traffic_user
db.password=${DB_PASSWORD}

# Kafka
kafka.bootstrap.servers=kafka1:9092,kafka2:9092,kafka3:9092

# API URLs
api.camera.url=https://api.production.com/camera
api.centrale.url=https://api.production.com/centrale
api.pollution.url=https://api.production.com/pollution
```

### Using Environment Variables

Configure sensitive data via environment variables:

```bash
export DB_PASSWORD=secure_password
export KAFKA_SERVERS=localhost:9092
export API_KEY=your_api_key
```

**For dashboard:**
```bash
# Create .env.local in sgtu-dashboard/
echo "NEXT_PUBLIC_API_CAMERA_URL=http://localhost:8083/api" > .env.local
echo "NEXT_PUBLIC_API_CENTRALE_URL=http://localhost:9999/centrale/api" >> .env.local
```

## Verification and Health Checks

### Service Health Check Script

```bash
#!/bin/bash
# check-services.sh

echo "Checking service health..."

# MySQL
if mysqladmin ping -h localhost -u root -p > /dev/null 2>&1; then
  echo "✓ MySQL is running"
else
  echo "✗ MySQL is not running"
fi

# Kafka
if nc -z localhost 9092; then
  echo "✓ Kafka is running"
else
  echo "✗ Kafka is not running"
fi

# Pollution Service
if curl -s http://localhost:8080/api/pollution/current > /dev/null; then
  echo "✓ Pollution Service is running"
else
  echo "✗ Pollution Service is not running"
fi

# Centrale Service
if curl -s http://localhost:9999/centrale/api/Routes > /dev/null; then
  echo "✓ Centrale Service is running"
else
  echo "✗ Centrale Service is not running"
fi

# Central Analysis
if curl -s http://localhost:8083/api/traffic/latest > /dev/null; then
  echo "✓ Central Analysis Service is running"
else
  echo "✗ Central Analysis Service is not running"
fi

# Dashboard
if curl -s http://localhost:3000 > /dev/null; then
  echo "✓ Dashboard is running"
else
  echo "✗ Dashboard is not running"
fi
```

### Manual Health Checks

**Test Each Service:**

```bash
# MySQL
mysql -u traffic_user -p -e "SELECT 1"

# Kafka
echo "test" | kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic

# Pollution API
curl http://localhost:8080/api/pollution/current

# Centrale API
curl http://localhost:9999/centrale/api/Flux

# Camera Analysis
curl http://localhost:8083/api/traffic/latest

# Dashboard
curl http://localhost:3000
```

## Monitoring and Logging

### Log File Locations

All logs are written to the `logs/` directory:

```
logs/
├── pollution-service.log
├── noise-service.log
├── soap-services.log
├── centrale-service.log
├── camera-simulator.log
├── central-analysis.log
└── dashboard.log
```

### Real-Time Log Monitoring

**Single Service:**
```bash
tail -f logs/centrale-service.log
```

**All Services:**
```bash
tail -f logs/*.log
```

**With Filtering:**
```bash
tail -f logs/*.log | grep ERROR
tail -f logs/*.log | grep -E "ERROR|WARN"
```

### Log Rotation

Configure logrotate for automatic log management:

**/etc/logrotate.d/traffic-system:**
```
/path/to/integrated-traffic-system/logs/*.log {
    daily
    rotate 7
    compress
    delaycompress
    notifempty
    create 0644 user group
    sharedscripts
    postrotate
        systemctl reload traffic-services > /dev/null 2>&1 || true
    endscript
}
```

### Performance Monitoring

**Monitor Java Processes:**
```bash
# CPU and Memory usage
ps aux | grep java

# Detailed process info
top -p $(pgrep -d',' java)

# Java-specific monitoring
jps -lvm
```

**Monitor Database:**
```bash
mysql -u root -p -e "SHOW PROCESSLIST;"
mysql -u root -p -e "SHOW STATUS LIKE 'Threads%';"
```

**Monitor Kafka:**
```bash
# Consumer lag
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group traffic-analysis-group

# Topic status
kafka-topics.sh --bootstrap-server localhost:9092 --describe
```

## Troubleshooting

### Common Issues and Solutions

**Problem:** Port already in use
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>
```

**Problem:** Database connection failed
```bash
# Check MySQL status
sudo systemctl status mysql

# Verify credentials
mysql -u traffic_user -p integrated_traffic_system -e "SELECT 1"

# Check database exists
mysql -u root -p -e "SHOW DATABASES LIKE 'integrated%'"
```

**Problem:** Kafka connection errors
```bash
# Check Kafka is running
nc -z localhost 9092

# Verify topics exist
kafka-topics.sh --bootstrap-server localhost:9092 --list

# Check consumer groups
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
```

**Problem:** Service won't start
```bash
# Check logs for errors
tail -100 logs/service-name.log

# Verify Java version
java -version

# Check dependencies
lsof -i :PORT_NUMBER
```

**Problem:** Dashboard not loading
```bash
# Check if Node.js is running
ps aux | grep node

# Verify build completed
ls -la sgtu-dashboard/.next/

# Check for port conflicts
lsof -i :3000
```

### Debug Mode

Enable debug logging for troubleshooting:

**Java Services:**
Add to startup commands:
```bash
java -Dlogging.level=DEBUG -jar service.jar
```

**Dashboard:**
Set in `.env.local`:
```bash
NEXT_PUBLIC_LOG_LEVEL=debug
```

## Backup and Recovery

### Database Backup

**Full Backup:**
```bash
mysqldump -u traffic_user -p integrated_traffic_system > backup-$(date +%Y%m%d).sql
```

**Automated Daily Backup:**
```bash
#!/bin/bash
# /opt/scripts/backup-db.sh
BACKUP_DIR=/opt/backups
DATE=$(date +%Y%m%d)
mysqldump -u traffic_user -pPASSWORD integrated_traffic_system | gzip > $BACKUP_DIR/db-$DATE.sql.gz
find $BACKUP_DIR -name "db-*.sql.gz" -mtime +7 -delete
```

Add to crontab:
```bash
0 2 * * * /opt/scripts/backup-db.sh
```

**Restore Database:**
```bash
mysql -u traffic_user -p integrated_traffic_system < backup.sql
```

### Configuration Backup

```bash
# Backup all configuration
tar -czf config-backup-$(date +%Y%m%d).tar.gz \
  javaproject/*/src/main/resources/*.properties \
  sgtu-backend/*/src/main/resources/*.properties \
  sgtu-dashboard/.env.local \
  database/schema.sql
```

## Production Deployment

### Server Setup

**1. Dedicated Server Deployment:**

```bash
# Create dedicated user
sudo useradd -m -s /bin/bash traffic-system
sudo passwd traffic-system

# Setup directories
sudo mkdir -p /opt/traffic-system
sudo chown traffic-system:traffic-system /opt/traffic-system

# Copy project files
scp -r integrated-traffic-system traffic-system@server:/opt/
```

**2. Systemd Service Configuration:**

**/etc/systemd/system/traffic-centrale.service:**
```ini
[Unit]
Description=Traffic Centrale Service
After=network.target mysql.service

[Service]
Type=simple
User=traffic-system
WorkingDirectory=/opt/traffic-system/javaproject/centraleservice
ExecStart=/usr/bin/java -jar target/centrale.jar
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Create similar files for all services, then:
```bash
sudo systemctl daemon-reload
sudo systemctl enable traffic-centrale
sudo systemctl start traffic-centrale
```

### Load Balancing

For high availability, deploy multiple instances behind a load balancer:

**Nginx Configuration:**
```nginx
upstream centrale_backend {
    server 10.0.1.10:9999;
    server 10.0.1.11:9999;
    server 10.0.1.12:9999;
}

server {
    listen 80;
    server_name traffic.example.com;

    location /centrale/api {
        proxy_pass http://centrale_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### SSL/TLS Configuration

**Generate SSL Certificate:**
```bash
sudo certbot --nginx -d traffic.example.com
```

**Nginx HTTPS:**
```nginx
server {
    listen 443 ssl;
    server_name traffic.example.com;

    ssl_certificate /etc/letsencrypt/live/traffic.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/traffic.example.com/privkey.pem;

    location / {
        proxy_pass http://localhost:3000;
    }
}
```

## Scaling Considerations

### Horizontal Scaling

1. **Database:** Use MySQL replication or clustering
2. **Kafka:** Deploy multi-broker Kafka cluster
3. **Services:** Run multiple instances with load balancer
4. **Dashboard:** Deploy multiple Next.js instances

### Vertical Scaling

Adjust JVM heap sizes based on load:

```bash
java -Xms2g -Xmx4g -jar service.jar
```

### Caching

Implement Redis for caching:

```bash
# Install Redis
sudo apt install redis-server

# Configure services to use Redis for caching API responses
```

## Security Best Practices

1. **Database:** Use strong passwords, limit network access
2. **API Security:** Implement API keys, rate limiting
3. **Firewall:** Only expose necessary ports
4. **Updates:** Keep Java, Node.js, MySQL, Kafka updated
5. **Secrets:** Use environment variables, never commit credentials
6. **SSL/TLS:** Enable HTTPS for all production services
7. **Access Control:** Implement authentication and authorization

## Maintenance

### Regular Tasks

**Daily:**
- Check service health
- Review error logs
- Monitor disk space

**Weekly:**
- Database backup verification
- Performance metrics review
- Update security patches

**Monthly:**
- Clean old logs
- Optimize database
- Review and update dependencies

### Update Process

```bash
# Stop services
./stop-all.sh

# Backup database
mysqldump -u traffic_user -p integrated_traffic_system > pre-update-backup.sql

# Pull updates
git pull origin main

# Rebuild
./setup.sh

# Restart services
./start-all.sh

# Verify
./check-services.sh
```

## Performance Tuning

### MySQL Optimization

**/etc/mysql/my.cnf:**
```ini
[mysqld]
innodb_buffer_pool_size = 4G
innodb_log_file_size = 512M
max_connections = 200
query_cache_size = 64M
```

### Kafka Optimization

**config/server.properties:**
```properties
num.network.threads=8
num.io.threads=16
log.retention.hours=168
log.segment.bytes=1073741824
```

### JVM Tuning

```bash
java -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:ParallelGCThreads=8 \
     -Xms4g -Xmx8g \
     -jar service.jar
```

## Disaster Recovery

### Recovery Plan

1. Restore database from latest backup
2. Rebuild services from source
3. Restore configuration files
4. Restart services in correct order
5. Verify all health checks pass
6. Resume normal operations

### Backup Locations

- Database: Daily backups in `/opt/backups/`
- Configuration: Version controlled in Git
- Logs: Archived to external storage
- Application code: Git repository

## Support and Resources

### Logs for Debugging

Always include these when reporting issues:
- Service logs from `logs/`
- MySQL error log: `/var/log/mysql/error.log`
- System logs: `journalctl -u traffic-*`

### Useful Commands Reference

```bash
# Check all service ports
netstat -tuln | grep -E '8080|8083|9092|9999|3000|3306'

# Monitor system resources
htop

# Check disk usage
df -h

# View active connections
ss -tunap

# Java process details
jps -lvm
jstat -gc <pid>
```

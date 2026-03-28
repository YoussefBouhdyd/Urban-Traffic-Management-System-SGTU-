# Traffic Management System - Quick Start Guide

## Prerequisites

Before you start, ensure you have the following installed:

1. **Java 11+**
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **MySQL 8.x**
   ```bash
   mysql --version
   ```

4. **Apache Kafka 3.x**
   - Download from: https://kafka.apache.org/downloads
   - Extract to a directory

## Step-by-Step Setup

### Step 1: Database Setup

1. Start MySQL server

2. Create the database and tables:
   ```bash
   mysql -u root -p < database/schema.sql
   ```

3. Update database credentials in `src/main/resources/application.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/traffic_management
   db.username=root
   db.password=YOUR_PASSWORD
   ```

### Step 2: Kafka Setup

1. Start Zookeeper:
   ```bash
   cd /path/to/kafka
   bin/zookeeper-server-start.sh config/zookeeper.properties
   ```

2. In a new terminal, start Kafka:
   ```bash
   cd /path/to/kafka
   bin/kafka-server-start.sh config/server.properties
   ```

3. Create required topics:
   ```bash
   bin/kafka-topics.sh --create --topic camera-data --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
   
   bin/kafka-topics.sh --create --topic traffic-alerts --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
   
   bin/kafka-topics.sh --create --topic traffic-recommendations --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
   ```

### Step 3: Build the Project

```bash
mvn clean package
```

This will:
- Compile all Java source files
- Download all dependencies
- Create JAR file in `target/` directory

### Step 4: Run the Services

You need to run TWO services in separate terminals:

**Terminal 1 - Central Analysis Service:**
```bash
chmod +x run-central-service.sh
./run-central-service.sh
```

Or manually:
```bash
java -cp target/traffic-management-1.0-SNAPSHOT.jar com.smartcity.traffic.CentralAnalysisMain
```

This starts:
- REST API server on http://localhost:8080
- Kafka consumer for camera events
- Traffic analysis engine

**Terminal 2 - Camera Simulator:**
```bash
chmod +x run-camera-simulator.sh
./run-camera-simulator.sh
```

Or manually:
```bash
java -cp target/traffic-management-1.0-SNAPSHOT.jar com.smartcity.traffic.CameraSimulatorMain
```

This starts:
- RMI server on port 1099
- Periodic camera event generation (every 5 seconds)
- Kafka producer

### Step 5: Test the System

Once both services are running, test the REST API endpoints:

1. **Get latest traffic state:**
   ```bash
   curl http://localhost:8080/api/traffic/latest
   ```

2. **Get traffic history:**
   ```bash
   curl "http://localhost:8080/api/traffic/history?from=2026-03-15T00:00:00&to=2026-03-15T23:59:59"
   ```

3. **Get active alerts:**
   ```bash
   curl http://localhost:8080/api/alerts
   ```

4. **Get recommendations:**
   ```bash
   curl http://localhost:8080/api/recommendations
   ```

5. **Get camera status:**
   ```bash
   curl http://localhost:8080/api/cameras/CAM-01/status
   ```

## Monitoring the System

### View Kafka Messages

To see camera events being published to Kafka:
```bash
cd /path/to/kafka
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic camera-data --from-beginning
```

### View Database Records

Connect to MySQL and check the data:
```bash
mysql -u root -p traffic_management
```

```sql
-- View recent camera events
SELECT * FROM camera_events ORDER BY timestamp DESC LIMIT 10;

-- View traffic analysis
SELECT * FROM traffic_analysis ORDER BY timestamp DESC LIMIT 10;

-- View alerts
SELECT * FROM alerts ORDER BY timestamp DESC LIMIT 10;

-- View recommendations
SELECT * FROM recommendations ORDER BY timestamp DESC LIMIT 10;
```

### Check Logs

Both services output logs to the console. Look for:
- `INFO` messages for normal operations
- `ERROR` messages for problems

## Troubleshooting

### Database Connection Error

If you see: `Unable to connect to database`
- Check MySQL is running
- Verify credentials in `application.properties`
- Ensure database `traffic_management` exists

### Kafka Connection Error

If you see: `Failed to connect to Kafka`
- Check Zookeeper is running on port 2181
- Check Kafka broker is running on port 9092
- Verify topics exist using `kafka-topics.sh --list`

### RMI Port Already in Use

If you see: `Port 1099 already in use`
- Change `rmi.port` in `application.properties`
- Kill the process using port 1099

### REST API Not Responding

If API calls fail:
- Check Central Analysis Service is running
- Verify it started on port 8080
- Check firewall settings

## Stopping the System

Press `Ctrl+C` in each terminal to stop the services gracefully.

Or if running in background:
```bash
# Find Java processes
ps aux | grep java

# Kill specific process
kill <PID>
```

## Configuration

All configuration is in `src/main/resources/application.properties`:

```properties
# Database
db.url=jdbc:mysql://localhost:3306/traffic_management
db.username=root
db.password=yourpassword

# Kafka
kafka.bootstrap.servers=localhost:9092

# Camera Simulator
camera.id=CAM-01
intersection.id=INT-01
camera.event.interval.seconds=5

# API Server
api.host=localhost
api.port=8080
```

## Next Steps

1. **Integrate with a Dashboard**: Use the REST APIs to build a web dashboard
2. **Add More Cameras**: Modify configuration to simulate multiple cameras
3. **Customize Rules**: Edit `TrafficAnalysisService.java` to change analysis logic
4. **Add Authentication**: Implement JWT or Basic Auth for API endpoints

## Support

For issues or questions:
- Check the logs in the console output
- Review the project documentation in README.md
- Verify all prerequisites are properly installed

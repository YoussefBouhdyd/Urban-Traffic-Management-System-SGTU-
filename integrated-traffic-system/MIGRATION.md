# Migration Guide - From Separate Projects to Integrated System

This guide helps you migrate from the original separate projects to the new integrated system.

## 📦 Original Projects

You had three separate projects:

1. **Traffic/** - Camera-based traffic monitoring
2. **javaproject/** - SOAP services for traffic flow and lights
3. **sgtu-backend/** - Noise and pollution monitoring
4. **sgtu-dashboard/** - Next.js dashboard

## 🎯 Integrated Structure

All projects are now unified under `integrated-traffic-system/` with:
- Shared parent POM
- Unified database schema
- Coordinated Kafka topics
- Centralized configuration
- Automated startup scripts

## 🔄 Module Mapping

### From Traffic → traffic-camera-service

**Old Structure:**
```
Traffic/
├── src/main/java/com/smartcity/traffic/
│   ├── CameraSimulatorMain.java
│   ├── CentralAnalysisMain.java
│   ├── api/
│   ├── kafka/
│   ├── rmi/
│   └── service/
```

**New Location:**
```
integrated-traffic-system/traffic-camera-service/
```

**Changes:**
- ✅ Updated Kafka version to 3.8.0 (Scala 2.13)
- ✅ Database name changed to `integrated_traffic_system`
- ✅ Configuration centralized in `config/system.properties`

**Migration Steps:**
```bash
# Copy your custom code modifications (if any)
cp -r Traffic/src/* integrated-traffic-system/traffic-camera-service/src/

# Update application.properties
# Old: db.url=jdbc:mysql://localhost:3306/traffic_management
# New: db.url=jdbc:mysql://localhost:3306/integrated_traffic_system
```

### From javaproject → Multiple Modules

**Old Structure:**
```
javaproject/
├── services/          → ServiceFlux & ServiceFeux
├── client/            → SOAP client simulator
└── centraleservice/   → Central REST API (WAR)
```

**New Modules:**
```
integrated-traffic-system/
├── soap-services/         # Merged services
├── soap-client-simulator/ # Client
└── central-webapp/        # Central API
```

**Changes:**
- ✅ Java version unified to Java 21
- ✅ Kafka client updated to 3.8.0
- ✅ Database unified to `integrated_traffic_system`
- ✅ Topic name: `service-flux` (unchanged)

**Migration Steps:**
```bash
# Copy SOAP services code
cp -r javaproject/services/src/* \
      integrated-traffic-system/soap-services/src/

# Copy client code  
cp -r javaproject/client/src/* \
      integrated-traffic-system/soap-client-simulator/src/

# Copy central webapp
cp -r javaproject/centraleservice/src/* \
      integrated-traffic-system/central-webapp/src/

# Update database connection in centraleservice
# Find: DBConnection.java
# Update: database name to integrated_traffic_system
```

### From sgtu-backend → Multiple Services

**Old Structure:**
```
sgtu-backend/
├── service-bruit-tcp/      → Noise TCP service
├── service-pollution-rest/ → Pollution REST service
├── service-central/        → Central analysis
├── simulateur-bruit/       → Noise simulator
└── simulateur-pollution/   → Pollution simulator
```

**New Modules:**
```
integrated-traffic-system/
├── noise-service-tcp/
├── pollution-service-rest/
├── central-analysis-service/
├── noise-simulator/
└── pollution-simulator/
```

**Changes:**
- ✅ Kafka updated to 3.8.0
- ✅ Database unified to `integrated_traffic_system`
- ✅ Tables: `pollution`, `bruit` (unchanged structure)

**Migration Steps:**
```bash
# Copy noise service
cp -r sgtu-backend/service-bruit-tcp/src/* \
      integrated-traffic-system/noise-service-tcp/src/

# Copy pollution service
cp -r sgtu-backend/service-pollution-rest/src/* \
      integrated-traffic-system/pollution-service-rest/src/

# Copy central analysis
cp -r sgtu-backend/service-central/src/* \
      integrated-traffic-system/central-analysis-service/src/

# Copy simulators
cp -r sgtu-backend/simulateur-bruit/src/* \
      integrated-traffic-system/noise-simulator/src/
      
cp -r sgtu-backend/simulateur-pollution/src/* \
      integrated-traffic-system/pollution-simulator/src/
```

### Dashboard - No Changes Needed

**Location:**
```
sgtu-dashboard/ (stays as is, outside integrated-traffic-system)
```

**Changes:**
- ✅ API endpoints updated in configuration
- ✅ Now connects to unified backend

**Update API Config:**
```typescript
// sgtu-dashboard/src/config/api-config.ts
const API_BASE_URL = 'http://localhost:9999/centrale/api';
const CAMERA_API_URL = 'http://localhost:8080/api/traffic';
const POLLUTION_API_URL = 'http://localhost:8082/api/pollution';
```

## 📊 Database Migration

### Old Databases

1. `traffic_management` (Traffic project)
2. `projetjava` (javaproject)
3. `traffic_pollution_bruit` (sgtu-backend)

### New Unified Database

**Name:** `integrated_traffic_system`

**Combined Tables:**
- `camera_events` (from traffic_management)
- `traffic_analysis` (from traffic_management)
- `camera_status` (from traffic_management)
- `flux` (from projetjava)
- `pollution` (from traffic_pollution_bruit)
- `bruit` (from traffic_pollution_bruit)
- `alerts` (unified from all projects)
- `recommendations` (unified from all projects)

### Migration SQL Script

```sql
-- 1. Create new database
CREATE DATABASE integrated_traffic_system;
USE integrated_traffic_system;

-- 2. Run new schema
SOURCE database/schema.sql;

-- 3. Migrate old data (if needed)

-- From traffic_management
INSERT INTO integrated_traffic_system.camera_events
SELECT * FROM traffic_management.camera_events;

INSERT INTO integrated_traffic_system.traffic_analysis
SELECT * FROM traffic_management.traffic_analysis;

-- From projetjava
INSERT INTO integrated_traffic_system.flux
SELECT * FROM projetjava.flux;

-- From traffic_pollution_bruit
INSERT INTO integrated_traffic_system.pollution
SELECT * FROM traffic_pollution_bruit.pollution;

INSERT INTO integrated_traffic_system.bruit
SELECT * FROM traffic_pollution_bruit.bruit;
```

**Automated Migration:**
```bash
# The setup script creates a fresh database
./scripts/setup-database.sh

# Optional: Import old data
mysql -u root integrated_traffic_system < migration/import-old-data.sql
```

## ⚙️ Configuration Updates

### Old: Scattered Configuration

Each project had its own configuration files:
- `Traffic/src/main/resources/application.properties`
- `javaproject/centraleservice/.../DBConnection.java`
- `sgtu-backend/service-central/.../DatabaseManager.java`

### New: Centralized Configuration

**Single source of truth:** `config/system.properties`

**Update Checklist:**

1. **Database URL**
   ```properties
   # Old (various)
   jdbc:mysql://localhost:3306/traffic_management
   jdbc:mysql://localhost:3306/projetjava
   jdbc:mysql://localhost:3306/traffic_pollution_bruit
   
   # New (unified)
   db.url=jdbc:mysql://localhost:3306/integrated_traffic_system
   ```

2. **Kafka Bootstrap Servers**
   ```properties
   # Unchanged
   kafka.bootstrap.servers=localhost:9092
   ```

3. **Kafka Topics**
   ```properties
   # Old (scattered)
   camera-data (Traffic)
   service-flux (javaproject)
   pollution-topic (sgtu-backend)
   bruit-topic (sgtu-backend)
   
   # New (documented in one place)
   kafka.topic.camera.data=camera-data
   kafka.topic.service.flux=service-flux
   kafka.topic.pollution=pollution-topic
   kafka.topic.noise=bruit-topic
   ```

## 🚀 Startup Differences

### Old: Manual Multi-Step Process

**Traffic Project:**
```bash
cd Traffic
mvn clean package
java -jar target/traffic-management-1.0-SNAPSHOT.jar --camera
java -jar target/traffic-management-1.0-SNAPSHOT.jar --simulator
```

**javaproject:**
```bash
cd javaproject/services
mvn clean package
# Run in IDE

cd ../client
mvn clean package
# Run in IDE

cd ../centraleservice
mvn clean package
sudo cp target/centrale.war /var/lib/tomcat10/webapps/
```

**sgtu-backend:**
```bash
cd sgtu-backend/service-pollution-rest
mvn clean package
java -jar target/*.jar

cd ../service-bruit-tcp
mvn clean package
java -jar target/*.jar

cd ../service-central
mvn clean package
java -jar target/*.jar

# ... and simulators
```

### New: One Command

```bash
cd integrated-traffic-system
./scripts/start-all.sh
```

**What it does:**
1. ✅ Checks prerequisites
2. ✅ Builds all modules
3. ✅ Starts all services in correct order
4. ✅ Starts simulators
5. ✅ Deploys webapp to Tomcat
6. ✅ Starts dashboard
7. ✅ Shows status summary

## 🔧 Kafka Version Update

### Old: Various Versions
- Traffic: Kafka 3.3.1
- javaproject: Kafka 4.1.0 (incorrect version)
- sgtu-backend: Kafka 3.8.0

### New: Unified Kafka 2.13-3.8.0

**What Changed:**
- ✅ All modules use `kafka-clients:3.8.0`
- ✅ Scala version: 2.13
- ✅ Compatible with Kafka 2.13-3.8.0 server

**Installation:**
```bash
# Download Kafka 2.13-3.8.0
cd /opt
sudo wget https://downloads.apache.org/kafka/3.8.0/kafka_2.13-3.8.0.tgz
sudo tar -xzf kafka_2.13-3.8.0.tgz
sudo mv kafka_2.13-3.8.0 kafka

# Set KAFKA_HOME
export KAFKA_HOME=/opt/kafka
```

**No Code Changes Needed** - The Kafka client API is compatible.

## 📝 Code Migration Checklist

### For Each Module:

- [ ] Copy source code to new module location
- [ ] Update `pom.xml` parent reference
- [ ] Update database connection strings
- [ ] Update Kafka topic names (if changed)
- [ ] Update package imports (if changed)
- [ ] Update configuration files
- [ ] Test build: `mvn clean package`
- [ ] Test run: `java -jar target/*.jar`

### Example: Migrating Custom Service

```bash
# 1. Create new module (copy from template)
cp -r traffic-camera-service/ my-custom-service/

# 2. Update pom.xml
cd my-custom-service
nano pom.xml
# Change artifactId, name, description

# 3. Copy your code
cp -r ~/old-project/src/* src/

# 4. Update database config
nano src/main/resources/application.properties
# db.url=jdbc:mysql://localhost:3306/integrated_traffic_system

# 5. Add to parent POM
cd ..
nano pom.xml
# Add <module>my-custom-service</module>

# 6. Build
mvn clean install
```

## 🧪 Testing Migration

### 1. Test Database
```bash
mysql -u root integrated_traffic_system

# Verify tables exist
SHOW TABLES;

# Check if data migrated
SELECT COUNT(*) FROM camera_events;
SELECT COUNT(*) FROM flux;
SELECT COUNT(*) FROM pollution;
```

### 2. Test Kafka
```bash
# List topics
$KAFKA_HOME/bin/kafka-topics.sh --list \
  --bootstrap-server localhost:9092

# Should see:
# - camera-data
# - traffic-alerts
# - traffic-recommendations
# - service-flux
# - pollution-topic
# - bruit-topic
```

### 3. Test Services
```bash
# Traffic Camera API
curl http://localhost:8080/api/traffic/cameras/latest

# SOAP Services
curl http://localhost:8080/ServiceFlux?wsdl
curl http://localhost:8081/ServiceFeux?wsdl

# Central API
curl http://localhost:9999/centrale/api/Flux/latest

# Pollution API
curl http://localhost:8082/api/pollution/status
```

### 4. Test Dashboard
```bash
# Open browser
http://localhost:3000

# Should see:
# - Traffic flow data
# - Camera status
# - Pollution levels
# - Noise levels
# - Active alerts
```

## 🔄 Rollback Plan

If you need to go back to the old structure:

```bash
# Keep old projects intact
# Don't delete:
# - Traffic/
# - javaproject/
# - sgtu-backend/
# - sgtu-dashboard/

# To rollback, just switch directories and run old process
```

**Recommendation:** Test the integrated system in parallel before migrating completely.

## 📊 Comparison Table

| Aspect | Old Projects | Integrated System |
|--------|--------------|-------------------|
| Databases | 3 separate DBs | 1 unified DB |
| Java Versions | 11, 17, 21 (mixed) | 21 (unified) |
| Kafka Versions | 3.3.1, 4.1.0, 3.8.0 | 3.8.0 (unified) |
| Build Commands | 9+ separate builds | 1 parent build |
| Startup | Manual, 15+ steps | Automated, 1 command |
| Configuration | Scattered in 10+ files | Centralized config |
| Logs | Various locations | One logs/ directory |
| Documentation | 4 separate READMEs | Unified docs |

## ✅ Post-Migration Checklist

After migration:

- [ ] All services build successfully
- [ ] Database schema applied
- [ ] Kafka topics created
- [ ] All services start without errors
- [ ] APIs respond to requests
- [ ] Simulators generate data
- [ ] Data flows through Kafka
- [ ] Data stored in MySQL
- [ ] Alerts generated correctly
- [ ] Dashboard displays data
- [ ] All original features working

## 🎯 Benefits of Migration

1. **Simplified Operations** - One command to rule them all
2. **Unified Configuration** - Single source of truth
3. **Consistent Versioning** - No version conflicts
4. **Better Documentation** - Everything in one place
5. **Easier Debugging** - Centralized logging
6. **Scalability** - Shared infrastructure
7. **Maintainability** - Single repository
8. **Standard Build Process** - Maven multi-module

## 💡 Tips

1. **Test incrementally** - Migrate one module at a time
2. **Keep backups** - Don't delete old projects until verified
3. **Document changes** - Note any custom modifications
4. **Use scripts** - Leverage provided automation
5. **Check logs** - Monitor `logs/` directory during testing

## 📞 Need Help?

If you encounter issues during migration:

1. Check the logs: `tail -f logs/*.log`
2. Verify configuration: `cat config/system.properties`
3. Test connectivity: `./scripts/start-kafka.sh`
4. Review database: `mysql -u root integrated_traffic_system`
5. Consult [QUICKSTART.md](QUICKSTART.md) troubleshooting section

---

**Ready to migrate?** Follow the steps above and run `./scripts/start-all.sh` to test!

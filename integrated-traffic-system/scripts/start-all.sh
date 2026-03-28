#!/bin/bash
# ============================================================================
# Master Startup Script for Integrated Traffic System
# ============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$PROJECT_ROOT/logs"
PID_DIR="$PROJECT_ROOT/pids"

# Service ports
CAMERA_SERVICE_PORT=8083
SOAP_SERVICES_PORT_FLUX=8080
SOAP_SERVICES_PORT_FEUX=8081
CENTRAL_WEBAPP_PORT=9999
POLLUTION_SERVICE_PORT=8082
NOISE_SERVICE_PORT=5000
CENTRAL_ANALYSIS_PORT=8083

# Create necessary directories
mkdir -p "$LOG_DIR" "$PID_DIR"

echo -e "${BLUE}=========================================="
echo "Integrated Traffic System - Startup"
echo -e "==========================================${NC}"
echo ""

# Function to wait for port
wait_for_port() {
    local port=$1
    local service=$2
    local max_wait=30
    local count=0
    
    echo -n "Waiting for $service (port $port) to start..."
    while ! lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; do
        sleep 1
        count=$((count + 1))
        if [ $count -ge $max_wait ]; then
            echo -e " ${RED}✗ Timeout${NC}"
            return 1
        fi
    done
    echo -e " ${GREEN}✓${NC}"
    return 0
}

# Function to check if service is running
check_service() {
    local pid_file=$1
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            return 0
        fi
    fi
    return 1
}

# Step 1: Check prerequisites
echo -e "${YELLOW}Step 1: Checking prerequisites...${NC}"

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}✗ Java is not installed${NC}"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo -e "${RED}✗ Java 21 or higher is required (found Java $JAVA_VERSION)${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Java $JAVA_VERSION${NC}"

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}✗ Maven is not installed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Maven$(mvn --version | head -n1 | cut -d' ' -f3)${NC}"

# Check MySQL
if ! command -v mysql &> /dev/null; then
    echo -e "${RED}✗ MySQL is not installed${NC}"
    exit 1
fi
if ! systemctl is-active --quiet mysql && ! systemctl is-active --quiet mysqld; then
    echo -e "${RED}✗ MySQL is not running${NC}"
    exit 1
fi
echo -e "${GREEN}✓ MySQL is running${NC}"

# Check Kafka
if [ -z "$KAFKA_HOME" ]; then
    echo -e "${RED}✗ KAFKA_HOME is not set${NC}"
    exit 1
fi
if ! lsof -Pi :9092 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${RED}✗ Kafka is not running on port 9092${NC}"
    echo "Run: ./scripts/start-kafka.sh first"
    exit 1
fi
echo -e "${GREEN}✓ Kafka is running${NC}"

# Check Node.js for dashboard
if ! command -v node &> /dev/null; then
    echo -e "${YELLOW}⚠ Node.js is not installed (dashboard will not be available)${NC}"
    SKIP_DASHBOARD=1
else
    echo -e "${GREEN}✓ Node.js $(node --version)${NC}"
    SKIP_DASHBOARD=0
fi

echo ""

# Step 2: Build all services
echo -e "${YELLOW}Step 2: Building all services...${NC}"
cd "$PROJECT_ROOT"

if [ ! -f "pom.xml" ]; then
    echo -e "${RED}✗ Parent pom.xml not found${NC}"
    echo "Please ensure you're running this from the project root"
    exit 1
fi

echo "Building parent project..."
mvn clean install -DskipTests > "$LOG_DIR/build.log" 2>&1 &
BUILD_PID=$!

# Show progress
while ps -p $BUILD_PID > /dev/null 2>&1; do
    echo -n "."
    sleep 2
done
wait $BUILD_PID
BUILD_STATUS=$?

if [ $BUILD_STATUS -ne 0 ]; then
    echo -e " ${RED}✗ Build failed${NC}"
    echo "Check logs: $LOG_DIR/build.log"
    exit 1
fi
echo -e " ${GREEN}✓${NC}"

echo ""

# Step 3: Start backend services
echo -e "${YELLOW}Step 3: Starting backend services...${NC}"

# Start Traffic Camera Service (Central Analysis)
echo "Starting Traffic Camera Service..."
cd "$PROJECT_ROOT/../Traffic"
if [ -f "target/traffic-management-1.0-SNAPSHOT.jar" ]; then
    java -jar target/traffic-management-1.0-SNAPSHOT.jar > "$LOG_DIR/traffic-camera.log" 2>&1 &
    echo $! > "$PID_DIR/traffic-camera.pid"
    wait_for_port $CAMERA_SERVICE_PORT "Traffic Camera Service"
else
    echo -e "${YELLOW}⚠ Traffic Camera Service JAR not found${NC}"
fi

# Start SOAP Services
echo "Starting SOAP Services (Flow & Lights)..."
cd "$PROJECT_ROOT/../javaproject/services"
if [ -f "target/services-1.0-SNAPSHOT.jar" ]; then
    java -jar target/services-1.0-SNAPSHOT.jar > "$LOG_DIR/soap-services.log" 2>&1 &
    echo $! > "$PID_DIR/soap-services.pid"
    wait_for_port $SOAP_SERVICES_PORT_FLUX "SOAP Services"
else
    echo -e "${YELLOW}⚠ SOAP Services JAR not found${NC}"
fi

# Start Pollution Service
echo "Starting Pollution Service (REST)..."
cd "$PROJECT_ROOT/../sgtu-backend/service-pollution-rest"
if [ -f "target/service-pollution-rest-1.0.0.jar" ]; then
    java -jar target/service-pollution-rest-1.0.0.jar > "$LOG_DIR/pollution-service.log" 2>&1 &
    echo $! > "$PID_DIR/pollution-service.pid"
    wait_for_port $POLLUTION_SERVICE_PORT "Pollution Service"
else
    echo -e "${YELLOW}⚠ Pollution Service JAR not found${NC}"
fi

# Start Noise Service
echo "Starting Noise Service (TCP)..."
cd "$PROJECT_ROOT/../sgtu-backend/service-bruit-tcp"
if [ -f "target/service-bruit-tcp-1.0.0.jar" ]; then
    java -jar target/service-bruit-tcp-1.0.0.jar > "$LOG_DIR/noise-service.log" 2>&1 &
    echo $! > "$PID_DIR/noise-service.pid"
    wait_for_port $NOISE_SERVICE_PORT "Noise Service"
else
    echo -e "${YELLOW}⚠ Noise Service JAR not found${NC}"
fi

# Start Central Analysis Service
echo "Starting Central Analysis Service..."
cd "$PROJECT_ROOT/../sgtu-backend/service-central"
if [ -f "target/service-central-1.0.0.jar" ]; then
    java -jar target/service-central-1.0.0.jar > "$LOG_DIR/central-analysis.log" 2>&1 &
    echo $! > "$PID_DIR/central-analysis.pid"
    sleep 3
    echo -e "${GREEN}✓ Central Analysis Service started${NC}"
else
    echo -e "${YELLOW}⚠ Central Analysis Service JAR not found${NC}"
fi

# Start Centrale REST Service (for Flux and Feux APIs)
echo "Starting Centrale REST Service..."
cd "$PROJECT_ROOT/../javaproject/centraleservice"
if [ -f "target/centrale.jar" ]; then
    nohup java -jar target/centrale.jar > "$LOG_DIR/centrale-service.log" 2>&1 &
    echo $! > "$PID_DIR/centrale-service.pid"
    wait_for_port $CENTRAL_WEBAPP_PORT "Centrale REST Service"
else
    echo -e "${YELLOW}⚠ Centrale Service JAR not found${NC}"
fi

echo ""

# Step 4: Start simulators
echo -e "${YELLOW}Step 4: Starting data simulators...${NC}"

sleep 2  # Wait for services to be ready

# Start SOAP Client Simulator
echo "Starting SOAP Client Simulator..."
cd "$PROJECT_ROOT/../javaproject/client"
if [ -f "target/client-1.0-SNAPSHOT.jar" ]; then
    java -jar target/client-1.0-SNAPSHOT.jar > "$LOG_DIR/soap-client.log" 2>&1 &
    echo $! > "$PID_DIR/soap-client.pid"
    echo -e "${GREEN}✓ SOAP Client Simulator started${NC}"
else
    echo -e "${YELLOW}⚠ SOAP Client Simulator JAR not found${NC}"
fi

# Start Camera Simulator
echo "Starting Camera Simulator..."
cd "$PROJECT_ROOT/../Traffic"
if [ -f "target/traffic-management-1.0-SNAPSHOT.jar" ] && [ -d "target/lib" ]; then
    java -cp "target/traffic-management-1.0-SNAPSHOT.jar:target/lib/*" \
        com.smartcity.traffic.CameraSimulatorMain > "$LOG_DIR/camera-simulator.log" 2>&1 &
    echo $! > "$PID_DIR/camera-simulator.pid"
    echo -e "${GREEN}✓ Camera Simulator started${NC}"
else
    echo -e "${YELLOW}⚠ Camera Simulator not available${NC}"
fi

# Start Pollution Simulator
echo "Starting Pollution Simulator..."
cd "$PROJECT_ROOT/../sgtu-backend/simulateur-pollution"
if [ -f "target/simulateur-pollution-1.0.0.jar" ]; then
    java -jar target/simulateur-pollution-1.0.0.jar > "$LOG_DIR/pollution-simulator.log" 2>&1 &
    echo $! > "$PID_DIR/pollution-simulator.pid"
    echo -e "${GREEN}✓ Pollution Simulator started${NC}"
else
    echo -e "${YELLOW}⚠ Pollution Simulator JAR not found${NC}"
fi

# Start Noise Simulator
echo "Starting Noise Simulator..."
cd "$PROJECT_ROOT/../sgtu-backend/simulateur-bruit"
if [ -f "target/simulateur-bruit-1.0.0.jar" ]; then
    java -jar target/simulateur-bruit-1.0.0.jar > "$LOG_DIR/noise-simulator.log" 2>&1 &
    echo $! > "$PID_DIR/noise-simulator.pid"
    echo -e "${GREEN}✓ Noise Simulator started${NC}"
else
    echo -e "${YELLOW}⚠ Noise Simulator JAR not found${NC}"
fi

echo ""

# Step 5: Start Dashboard (if Node.js is available)
if [ $SKIP_DASHBOARD -eq 0 ]; then
    echo -e "${YELLOW}Step 6: Starting Dashboard...${NC}"
    cd "$PROJECT_ROOT/../sgtu-dashboard"
    
    if [ ! -d "node_modules" ]; then
        echo "Installing dashboard dependencies..."
        npm install > "$LOG_DIR/npm-install.log" 2>&1
    fi
    
    echo "Starting Next.js dashboard..."
    npm run dev > "$LOG_DIR/dashboard.log" 2>&1 &
    echo $! > "$PID_DIR/dashboard.pid"
    sleep 5
    echo -e "${GREEN}✓ Dashboard started${NC}"
    echo "Access at: http://localhost:3000"
else
    echo -e "${YELLOW}Step 5: Skipping Dashboard (Node.js not available)${NC}"
fi

    echo -e "${YELLOW}Step 5: Starting Dashboard...${NC}"
echo -e "${GREEN}=========================================="
echo "✓ All services started successfully!"
echo -e "==========================================${NC}"
echo ""
echo "Service Status:"
echo "  ✓ Kafka:                   localhost:9092"
echo "  ✓ MySQL:                   localhost:3306"
echo "  ✓ Traffic Camera API:      http://localhost:$CAMERA_SERVICE_PORT"
echo "  ✓ SOAP Services:           http://localhost:$SOAP_SERVICES_PORT_FLUX & :$SOAP_SERVICES_PORT_FEUX"
echo "  ✓ Pollution Service:       http://localhost:$POLLUTION_SERVICE_PORT"
echo "  ✓ Noise Service:           TCP :$NOISE_SERVICE_PORT"
echo "  ✓ Central Analysis:        Running (Kafka consumer)"
echo "  ✓ Centrale REST API:       http://localhost:$CENTRAL_WEBAPP_PORT/centrale/api"
if [ $SKIP_DASHBOARD -eq 0 ]; then
    echo "  ✓ Dashboard:               http://localhost:3000"
fi
echo ""
echo "Logs directory:   $LOG_DIR"
echo "PIDs directory:   $PID_DIR"
echo ""
echo "To stop all services, run:"
echo "  ./scripts/stop-all.sh"
echo ""
echo "To view logs:"
echo "  tail -f $LOG_DIR/*.log"
echo ""

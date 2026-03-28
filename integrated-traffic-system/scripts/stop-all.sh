#!/bin/bash
# ============================================================================
# Stop All Services Script for Integrated Traffic System
# ============================================================================

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PID_DIR="$PROJECT_ROOT/pids"

echo -e "${BLUE}=========================================="
echo "Integrated Traffic System - Shutdown"
echo -e "==========================================${NC}"
echo ""

# Function to stop service
stop_service() {
    local service_name=$1
    local pid_file="$PID_DIR/$2.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            echo -n "Stopping $service_name (PID: $pid)..."
            kill $pid 2>/dev/null
            sleep 2
            
            # Force kill if still running
            if ps -p $pid > /dev/null 2>&1; then
                kill -9 $pid 2>/dev/null
            fi
            
            rm -f "$pid_file"
            echo -e " ${GREEN}✓${NC}"
        else
            echo -e "${YELLOW}✓ $service_name is not running${NC}"
            rm -f "$pid_file"
        fi
    else
        echo -e "${YELLOW}✓ $service_name (no PID file found)${NC}"
    fi
}

# Stop all services
echo "Stopping all services..."
echo ""

stop_service "Dashboard" "dashboard"
stop_service "Camera Simulator" "camera-simulator"
stop_service "SOAP Client Simulator" "soap-client"
stop_service "Pollution Simulator" "pollution-simulator"
stop_service "Noise Simulator" "noise-simulator"
stop_service "Central Analysis Service" "central-analysis"
stop_service "Centrale REST Service" "centrale-service"
stop_service "Noise Service" "noise-service"
stop_service "Pollution Service" "pollution-service"
stop_service "SOAP Services" "soap-services"
stop_service "Traffic Camera Service" "traffic-camera"

echo ""
echo -e "${YELLOW}Checking for remaining processes...${NC}"

# Kill any remaining Java processes related to the project
pkill -f "traffic-camera-service" 2>/dev/null && echo "  Killed remaining camera service processes"
pkill -f "soap-services" 2>/dev/null && echo "  Killed remaining SOAP service processes"
pkill -f "pollution-service" 2>/dev/null && echo "  Killed remaining pollution service processes"
pkill -f "noise-service" 2>/dev/null && echo "  Killed remaining noise service processes"
pkill -f "central-analysis" 2>/dev/null && echo "  Killed remaining central analysis processes"
pkill -f "centrale.jar" 2>/dev/null && echo "  Killed remaining centrale service processes"
pkill -f "simulator" 2>/dev/null && echo "  Killed remaining simulator processes"

echo ""
echo -e "${GREEN}=========================================="
echo "✓ All services stopped"
echo -e "==========================================${NC}"
echo ""
echo "Note: Kafka and MySQL are still running."
echo "To stop them:"
echo "  Kafka:  ./scripts/stop-kafka.sh"
echo "  MySQL:  sudo systemctl stop mysql"
echo ""

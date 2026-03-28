#!/bin/bash
# ============================================================================
# System Verification Script
# ============================================================================
# This script checks if all prerequisites are met before running the system

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================="
echo "System Verification Check"
echo -e "==========================================${NC}"
echo ""

ERRORS=0
WARNINGS=0

# Function to check command
check_command() {
    local cmd=$1
    local name=$2
    local install_hint=$3
    
    if command -v $cmd &> /dev/null; then
        local version=$($cmd --version 2>&1 | head -n1)
        echo -e "${GREEN}✓${NC} $name: $version"
        return 0
    else
        echo -e "${RED}✗${NC} $name: Not installed"
        if [ ! -z "$install_hint" ]; then
            echo "  Install: $install_hint"
        fi
        ERRORS=$((ERRORS + 1))
        return 1
    fi
}

# Function to check port
check_port() {
    local port=$1
    local service=$2
    local required=$3
    
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        if [ "$required" == "yes" ]; then
            echo -e "${GREEN}✓${NC} $service: Running on port $port"
        else
            echo -e "${YELLOW}⚠${NC}  $service: Port $port is already in use (may conflict)"
            WARNINGS=$((WARNINGS + 1))
        fi
    else
        if [ "$required" == "yes" ]; then
            echo -e "${RED}✗${NC} $service: Not running on port $port"
            ERRORS=$((ERRORS + 1))
        else
            echo -e "${GREEN}✓${NC} Port $port: Available"
        fi
    fi
}

# Check Java
echo -e "${YELLOW}1. Checking Java...${NC}"
if check_command "java" "Java" "sudo apt install openjdk-21-jdk"; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 21 ]; then
        echo -e "${RED}  ✗ Java 21 or higher required (found Java $JAVA_VERSION)${NC}"
        ERRORS=$((ERRORS + 1))
    fi
fi
echo ""

# Check Maven
echo -e "${YELLOW}2. Checking Maven...${NC}"
check_command "mvn" "Maven" "sudo apt install maven"
echo ""

# Check MySQL
echo -e "${YELLOW}3. Checking MySQL...${NC}"
if check_command "mysql" "MySQL" "sudo apt install mysql-server"; then
    if systemctl is-active --quiet mysql; then
        echo -e "${GREEN}✓${NC} MySQL service: Running"
    elif systemctl is-active --quiet mysqld; then
        echo -e "${GREEN}✓${NC} MySQL service: Running"
    else
        echo -e "${RED}✗${NC} MySQL service: Not running"
        echo "  Start: sudo systemctl start mysql"
        ERRORS=$((ERRORS + 1))
    fi
fi
echo ""

# Check Kafka
echo -e "${YELLOW}4. Checking Kafka...${NC}"
if [ -z "$KAFKA_HOME" ]; then
    echo -e "${RED}✗${NC} KAFKA_HOME: Not set"
    echo "  Set: export KAFKA_HOME=/opt/kafka"
    ERRORS=$((ERRORS + 1))
else
    echo -e "${GREEN}✓${NC} KAFKA_HOME: $KAFKA_HOME"
    
    if [ -f "$KAFKA_HOME/bin/kafka-server-start.sh" ]; then
        echo -e "${GREEN}✓${NC} Kafka installation: Found"
    else
        echo -e "${RED}✗${NC} Kafka installation: Invalid"
        ERRORS=$((ERRORS + 1))
    fi
fi
echo ""

# Check Node.js
echo -e "${YELLOW}5. Checking Node.js (optional for dashboard)...${NC}"
if check_command "node" "Node.js" "curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash - && sudo apt install -y nodejs"; then
    NODE_VERSION=$(node --version | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$NODE_VERSION" -lt 18 ]; then
        echo -e "${YELLOW}⚠${NC}  Node.js 18+ recommended (found v$NODE_VERSION)"
        WARNINGS=$((WARNINGS + 1))
    fi
else
    echo -e "${YELLOW}ℹ${NC}  Dashboard will not be available without Node.js"
fi
echo ""

# Check Tomcat
echo -e "${YELLOW}6. Checking Tomcat (optional for central webapp)...${NC}"
if [ -d "/var/lib/tomcat10/webapps" ] || [ -d "/opt/tomcat10/webapps" ]; then
    echo -e "${GREEN}✓${NC} Tomcat: Installation found"
else
    echo -e "${YELLOW}⚠${NC}  Tomcat: Not found"
    echo -e "${YELLOW}ℹ${NC}  Central webapp deployment will be skipped"
    WARNINGS=$((WARNINGS + 1))
fi
echo ""

# Check required ports
echo -e "${YELLOW}7. Checking required ports...${NC}"
check_port 2181 "Zookeeper" "no"
check_port 9092 "Kafka" "no"
check_port 3306 "MySQL" "yes"
check_port 8080 "API Port" "no"
check_port 8081 "SOAP Port" "no"
check_port 8082 "Pollution Service" "no"
check_port 9999 "Central WebApp" "no"
check_port 5000 "Noise Service" "no"
check_port 3000 "Dashboard" "no"
check_port 1099 "RMI Registry" "no"
echo ""

# Check disk space
echo -e "${YELLOW}8. Checking disk space...${NC}"
AVAILABLE=$(df -BG . | tail -1 | awk '{print $4}' | sed 's/G//')
if [ "$AVAILABLE" -ge 10 ]; then
    echo -e "${GREEN}✓${NC} Disk space: ${AVAILABLE}GB available"
else
    echo -e "${YELLOW}⚠${NC}  Disk space: Only ${AVAILABLE}GB available (10GB recommended)"
    WARNINGS=$((WARNINGS + 1))
fi
echo ""

# Check memory
echo -e "${YELLOW}9. Checking memory...${NC}"
TOTAL_MEM=$(free -g | awk '/^Mem:/{print $2}')
if [ "$TOTAL_MEM" -ge 8 ]; then
    echo -e "${GREEN}✓${NC} Memory: ${TOTAL_MEM}GB total"
else
    echo -e "${YELLOW}⚠${NC}  Memory: ${TOTAL_MEM}GB total (8GB recommended)"
    WARNINGS=$((WARNINGS + 1))
fi
echo ""

# Summary
echo -e "${BLUE}=========================================="
echo "Verification Summary"
echo -e "==========================================${NC}"
echo ""

if [ $ERRORS -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    echo -e "${GREEN}✓ All checks passed!${NC}"
    echo ""
    echo "You're ready to run the system:"
    echo "  1. ./scripts/start-kafka.sh"
    echo "  2. ./scripts/setup-kafka.sh"
    echo "  3. ./scripts/setup-database.sh"
    echo "  4. ./scripts/start-all.sh"
elif [ $ERRORS -eq 0 ]; then
    echo -e "${YELLOW}⚠ Verification completed with $WARNINGS warning(s)${NC}"
    echo ""
    echo "You can proceed, but some features may be limited."
    echo "Review warnings above for details."
else
    echo -e "${RED}✗ Verification failed with $ERRORS error(s) and $WARNINGS warning(s)${NC}"
    echo ""
    echo "Please fix the errors above before running the system."
    echo "See QUICKSTART.md for installation instructions."
    exit 1
fi

echo ""

#!/bin/bash
# ============================================================================
# System Health Check Script
# ============================================================================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================="
echo "System Health Check"
echo -e "==========================================${NC}"
echo ""

PASS=0
FAIL=0
WARN=0

# Infrastructure Checks
echo -e "${YELLOW}Infrastructure:${NC}"

# Kafka
echo -n "  Kafka (port 9092): "
if lsof -i :9092 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${GREEN}✓ Running${NC}"
    PASS=$((PASS + 1))
else
    echo -e "${RED}✗ Not running${NC}"
    FAIL=$((FAIL + 1))
fi

# MySQL
echo -n "  MySQL (port 3306): "
if systemctl is-active --quiet mysql || systemctl is-active --quiet mysqld; then
    echo -e "${GREEN}✓ Running${NC}"
    PASS=$((PASS + 1))
else
    echo -e "${RED}✗ Not running${NC}"
    FAIL=$((FAIL + 1))
fi

# Zookeeper
echo -n "  Zookeeper (port 2181): "
if lsof -i :2181 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${GREEN}✓ Running${NC}"
    PASS=$((PASS + 1))
else
    echo -e "${YELLOW}⚠ Not running${NC}"
    WARN=$((WARN + 1))
fi

echo ""

# Service Checks
echo -e "${YELLOW}Services:${NC}"

# Camera API
echo -n "  Camera API (port 8080): "
if lsof -i :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
    if curl -s http://localhost:8080/api/traffic/cameras/latest >/dev/null 2>&1; then
        echo -e "${GREEN}✓ Running and responding${NC}"
        PASS=$((PASS + 1))
    else
        echo -e "${YELLOW}⚠ Port open but not responding${NC}"
        WARN=$((WARN + 1))
    fi
else
    echo -e "${RED}✗ Not running${NC}"
    FAIL=$((FAIL + 1))
fi

# SOAP Services
echo -n "  SOAP ServiceFlux (port 8080): "
if curl -s http://localhost:8080/ServiceFlux?wsdl >/dev/null 2>&1; then
    echo -e "${GREEN}✓ WSDL accessible${NC}"
    PASS=$((PASS + 1))
else
    echo -e "${YELLOW}⚠ WSDL not accessible${NC}"
    WARN=$((WARN + 1))
fi

echo -n "  SOAP ServiceFeux (port 8081): "
if lsof -i :8081 -sTCP:LISTEN -t >/dev/null 2>&1; then
    if curl -s http://localhost:8081/ServiceFeux?wsdl >/dev/null 2>&1; then
        echo -e "${GREEN}✓ WSDL accessible${NC}"
        PASS=$((PASS + 1))
    else
        echo -e "${YELLOW}⚠ Port open but WSDL not accessible${NC}"
        WARN=$((WARN + 1))
    fi
else
    echo -e "${RED}✗ Not running${NC}"
    FAIL=$((FAIL + 1))
fi

# Central WebApp
echo -n "  Central WebApp (port 9999): "
if lsof -i :9999 -sTCP:LISTEN -t >/dev/null 2>&1; then
    if curl -s http://localhost:9999/centrale/api/Flux/latest >/dev/null 2>&1; then
        echo -e "${GREEN}✓ Running and responding${NC}"
        PASS=$((PASS + 1))
    else
        echo -e "${YELLOW}⚠ Port open but not responding${NC}"
        WARN=$((WARN + 1))
    fi
else
    echo -e "${RED}✗ Not running${NC}"
    FAIL=$((FAIL + 1))
fi

# Pollution Service
echo -n "  Pollution Service (port 8082): "
if lsof -i :8082 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${GREEN}✓ Running${NC}"
    PASS=$((PASS + 1))
else
    echo -e "${RED}✗ Not running${NC}"
    FAIL=$((FAIL + 1))
fi

# Noise Service
echo -n "  Noise Service (port 5000): "
if lsof -i :5000 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${GREEN}✓ Running${NC}"
    PASS=$((PASS + 1))
else
    echo -e "${RED}✗ Not running${NC}"
    FAIL=$((FAIL + 1))
fi

# Dashboard
echo -n "  Dashboard (port 3000): "
if lsof -i :3000 -sTCP:LISTEN -t >/dev/null 2>&1; then
    if curl -s http://localhost:3000 >/dev/null 2>&1; then
        echo -e "${GREEN}✓ Running and accessible${NC}"
        PASS=$((PASS + 1))
    else
        echo -e "${YELLOW}⚠ Port open but not accessible${NC}"
        WARN=$((WARN + 1))
    fi
else
    echo -e "${YELLOW}⚠ Not running${NC}"
    WARN=$((WARN + 1))
fi

echo ""

# Database Checks
echo -e "${YELLOW}Database:${NC}"

echo -n "  Database connection: "
if mysql -u root integrated_traffic_system -e "SELECT 1" >/dev/null 2>&1; then
    echo -e "${GREEN}✓ Connected${NC}"
    PASS=$((PASS + 1))
    
    # Check tables
    TABLE_COUNT=$(mysql -u root integrated_traffic_system -N -B -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='integrated_traffic_system';" 2>/dev/null)
    echo "  Tables: $TABLE_COUNT/8 expected"
    
    # Check recent data
    CAMERA_COUNT=$(mysql -u root integrated_traffic_system -N -B -e "SELECT COUNT(*) FROM camera_events;" 2>/dev/null)
    FLUX_COUNT=$(mysql -u root integrated_traffic_system -N -B -e "SELECT COUNT(*) FROM flux;" 2>/dev/null)
    POLLUTION_COUNT=$(mysql -u root integrated_traffic_system -N -B -e "SELECT COUNT(*) FROM pollution;" 2>/dev/null)
    
    echo "  Data: Camera=$CAMERA_COUNT, Flux=$FLUX_COUNT, Pollution=$POLLUTION_COUNT"
else
    echo -e "${RED}✗ Cannot connect${NC}"
    FAIL=$((FAIL + 1))
fi

echo ""

# Kafka Topics
echo -e "${YELLOW}Kafka Topics:${NC}"

if [ ! -z "$KAFKA_HOME" ]; then
    TOPICS=$($KAFKA_HOME/bin/kafka-topics.sh --list --bootstrap-server localhost:9092 2>/dev/null)
    
    for topic in camera-data service-flux pollution-topic bruit-topic traffic-alerts traffic-recommendations; do
        echo -n "  $topic: "
        if echo "$TOPICS" | grep -q "^$topic$"; then
            echo -e "${GREEN}✓ Exists${NC}"
            PASS=$((PASS + 1))
        else
            echo -e "${RED}✗ Missing${NC}"
            FAIL=$((FAIL + 1))
        fi
    done
else
    echo -e "${YELLOW}⚠ KAFKA_HOME not set, cannot check topics${NC}"
    WARN=$((WARN + 1))
fi

echo ""

# Summary
echo -e "${BLUE}=========================================="
echo "Summary"
echo -e "==========================================${NC}"
echo ""
echo -e "${GREEN}Passed: $PASS${NC}"
echo -e "${YELLOW}Warnings: $WARN${NC}"
echo -e "${RED}Failed: $FAIL${NC}"
echo ""

if [ $FAIL -eq 0 ] && [ $WARN -eq 0 ]; then
    echo -e "${GREEN}✓ System is fully operational!${NC}"
    exit 0
elif [ $FAIL -eq 0 ]; then
    echo -e "${YELLOW}⚠ System is operational with warnings${NC}"
    exit 0
else
    echo -e "${RED}✗ System has failures that need attention${NC}"
    echo ""
    echo "Troubleshooting:"
    echo "  1. Check logs: tail -f logs/*.log"
    echo "  2. Restart failed services: ./scripts/start-all.sh"
    echo "  3. See QUICKSTART.md for detailed troubleshooting"
    exit 1
fi

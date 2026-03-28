#!/bin/bash
# ============================================================================
# Stop Kafka and Zookeeper
# ============================================================================

set -e

echo "=========================================="
echo "Stopping Kafka Infrastructure"
echo "=========================================="

# Check if KAFKA_HOME is set
if [ -z "$KAFKA_HOME" ]; then
    echo "❌ KAFKA_HOME is not set."
    echo "Attempting to kill processes by port..."
    
    # Try to kill by port
    if lsof -Pi :9092 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo "Stopping Kafka (port 9092)..."
        kill $(lsof -t -i:9092) 2>/dev/null || true
        echo "✓ Kafka stopped"
    else
        echo "✓ Kafka is not running"
    fi
    
    if lsof -Pi :2181 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo "Stopping Zookeeper (port 2181)..."
        kill $(lsof -t -i:2181) 2>/dev/null || true
        echo "✓ Zookeeper stopped"
    else
        echo "✓ Zookeeper is not running"
    fi
    
    exit 0
fi

# Stop Kafka
if lsof -Pi :9092 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "Stopping Kafka broker..."
    $KAFKA_HOME/bin/kafka-server-stop.sh
    sleep 3
    echo "✓ Kafka broker stopped"
else
    echo "✓ Kafka broker is not running"
fi

# Stop Zookeeper
if lsof -Pi :2181 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "Stopping Zookeeper..."
    $KAFKA_HOME/bin/zookeeper-server-stop.sh
    sleep 3
    echo "✓ Zookeeper stopped"
else
    echo "✓ Zookeeper is not running"
fi

echo ""
echo "=========================================="
echo "✓ Kafka infrastructure stopped"
echo "=========================================="

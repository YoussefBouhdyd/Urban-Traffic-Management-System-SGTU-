#!/bin/bash
# ============================================================================
# Start Kafka and Zookeeper for Integrated Traffic System
# Kafka Version: 2.13-3.8.0
# ============================================================================

set -e

echo "=========================================="
echo "Starting Kafka Infrastructure"
echo "=========================================="

# Check if KAFKA_HOME is set
if [ -z "$KAFKA_HOME" ]; then
    echo "❌ KAFKA_HOME is not set. Please set it to your Kafka installation directory."
    echo "Example: export KAFKA_HOME=/opt/kafka"
    exit 1
fi

echo "✓ KAFKA_HOME: $KAFKA_HOME"

# Check if Zookeeper is already running
if lsof -Pi :2181 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "⚠️  Zookeeper is already running on port 2181"
else
    echo "Starting Zookeeper..."
    $KAFKA_HOME/bin/zookeeper-server-start.sh -daemon $KAFKA_HOME/config/zookeeper.properties
    echo "✓ Zookeeper started in background"
    sleep 5  # Wait for Zookeeper to fully start
fi

# Check if Kafka is already running
if lsof -Pi :9092 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "⚠️  Kafka is already running on port 9092"
else
    echo "Starting Kafka broker..."
    $KAFKA_HOME/bin/kafka-server-start.sh -daemon $KAFKA_HOME/config/server.properties
    echo "✓ Kafka broker started in background"
    sleep 10  # Wait for Kafka to fully start
fi

echo ""
echo "=========================================="
echo "Verifying Kafka status..."
echo "=========================================="

# Check if both services are running
if lsof -Pi :2181 -sTCP:LISTEN -t >/dev/null 2>&1 && lsof -Pi :9092 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "✓ Zookeeper: Running on port 2181"
    echo "✓ Kafka: Running on port 9092"
    echo ""
    echo "=========================================="
    echo "✓ Kafka infrastructure is ready!"
    echo "=========================================="
    echo ""
    echo "Next steps:"
    echo "1. Run: ./scripts/setup-kafka.sh (to create topics)"
    echo "2. Run: ./scripts/setup-database.sh (to setup MySQL)"
    echo "3. Run: ./scripts/start-all.sh (to start all services)"
else
    echo "❌ Failed to start Kafka infrastructure"
    exit 1
fi

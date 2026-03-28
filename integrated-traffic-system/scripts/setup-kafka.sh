#!/bin/bash
# ============================================================================
# Kafka Setup Script for Integrated Traffic System
# Kafka Version: 2.13-3.8.0
# ============================================================================

set -e  # Exit on error

echo "=========================================="
echo "Kafka Setup for Smart City Traffic System"
echo "=========================================="

# Check if KAFKA_HOME is set
if [ -z "$KAFKA_HOME" ]; then
    echo "❌ KAFKA_HOME is not set. Please set it to your Kafka installation directory."
    echo "Example: export KAFKA_HOME=/opt/kafka"
    exit 1
fi

echo "✓ KAFKA_HOME: $KAFKA_HOME"

# Kafka configuration
BOOTSTRAP_SERVER="localhost:9092"
ZOOKEEPER_CONNECT="localhost:2181"

# Topics configuration
declare -A TOPICS=(
    ["camera-data"]="1:1"
    ["traffic-alerts"]="1:1"
    ["traffic-recommendations"]="1:1"
    ["service-flux"]="1:1"
    ["pollution-topic"]="1:1"
    ["bruit-topic"]="1:1"
)

echo ""
echo "Starting Kafka topic creation..."
echo ""

# Create topics
for topic in "${!TOPICS[@]}"; do
    IFS=':' read -r partitions replication <<< "${TOPICS[$topic]}"
    
    echo "Creating topic: $topic (partitions=$partitions, replication=$replication)"
    
    $KAFKA_HOME/bin/kafka-topics.sh --create \
        --topic "$topic" \
        --bootstrap-server "$BOOTSTRAP_SERVER" \
        --partitions "$partitions" \
        --replication-factor "$replication" \
        --if-not-exists 2>/dev/null && echo "  ✓ Created" || echo "  ℹ Already exists"
done

echo ""
echo "=========================================="
echo "Listing all topics:"
echo "=========================================="
$KAFKA_HOME/bin/kafka-topics.sh --list \
    --bootstrap-server "$BOOTSTRAP_SERVER"

echo ""
echo "=========================================="
echo "Topic details:"
echo "=========================================="
for topic in "${!TOPICS[@]}"; do
    echo ""
    echo "Topic: $topic"
    $KAFKA_HOME/bin/kafka-topics.sh --describe \
        --topic "$topic" \
        --bootstrap-server "$BOOTSTRAP_SERVER"
done

echo ""
echo "=========================================="
echo "✓ Kafka setup completed successfully!"
echo "=========================================="
echo ""
echo "Consumer Groups:"
echo "  - traffic-analysis-group (camera-data)"
echo "  - centrale-flux-consumer (service-flux)"
echo "  - pollution-consumer-group (pollution-topic)"
echo "  - bruit-consumer-group (bruit-topic)"
echo ""
echo "To monitor a topic in real-time, use:"
echo "  $KAFKA_HOME/bin/kafka-console-consumer.sh \\"
echo "    --bootstrap-server $BOOTSTRAP_SERVER \\"
echo "    --topic <topic-name> \\"
echo "    --from-beginning"
echo ""

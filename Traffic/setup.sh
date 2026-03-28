#!/bin/bash
# Setup script for Traffic Management System

echo "=== Traffic Management System Setup ==="
echo ""

# 1. Check Java
echo "1. Checking Java installation..."
if command -v java &> /dev/null; then
    java -version
else
    echo "ERROR: Java is not installed. Please install Java 11 or higher."
    exit 1
fi
echo ""

# 2. Check Maven
echo "2. Checking Maven installation..."
if command -v mvn &> /dev/null; then
    mvn -version
else
    echo "ERROR: Maven is not installed. Please install Maven 3.6+."
    exit 1
fi
echo ""

# 3. Check MySQL
echo "3. Checking MySQL installation..."
if command -v mysql &> /dev/null; then
    mysql --version
else
    echo "WARNING: MySQL command not found. Make sure MySQL is installed and accessible."
fi
echo ""

# 4. Build the project
echo "4. Building the project..."
mvn clean package
if [ $? -ne 0 ]; then
    echo "ERROR: Build failed"
    exit 1
fi
echo "Build successful!"
echo ""

# 5. Setup database
echo "5. Setting up database..."
echo "Please run the following command manually with your MySQL credentials:"
echo "  mysql -u root -p < database/schema.sql"
echo ""

# 6. Kafka setup instructions
echo "6. Kafka Setup Required:"
echo "Please make sure Kafka is running and create the following topics:"
echo "  kafka-topics.sh --create --topic camera-data --bootstrap-server localhost:9092"
echo "  kafka-topics.sh --create --topic traffic-alerts --bootstrap-server localhost:9092"
echo "  kafka-topics.sh --create --topic traffic-recommendations --bootstrap-server localhost:9092"
echo ""

echo "=== Setup Complete ==="
echo "Next steps:"
echo "1. Configure database credentials in src/main/resources/application.properties"
echo "2. Start Central Analysis Service: ./run-central-service.sh"
echo "3. Start Camera Simulator: ./run-camera-simulator.sh"

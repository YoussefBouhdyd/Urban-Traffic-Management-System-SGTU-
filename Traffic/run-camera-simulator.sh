#!/bin/bash
# Run Camera Simulator

echo "=== Starting Camera Simulator Service ==="

# Set classpath
CP="target/traffic-management-1.0-SNAPSHOT.jar:target/lib/*"

# Run the application
java -cp $CP com.smartcity.traffic.CameraSimulatorMain

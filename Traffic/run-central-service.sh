#!/bin/bash
# Run Central Analysis Service

echo "=== Starting Central Traffic Analysis Service ==="

# Set classpath
CP="target/traffic-management-1.0-SNAPSHOT.jar:target/lib/*"

# Run the application
java -cp $CP com.smartcity.traffic.CentralAnalysisMain

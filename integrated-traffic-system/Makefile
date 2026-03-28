# Makefile for Integrated Traffic System
# Convenience wrapper for shell scripts

.PHONY: help verify start stop clean build test kafka-start kafka-stop db-setup

help:
	@echo "Integrated Smart City Traffic Management System"
	@echo ""
	@echo "Available targets:"
	@echo "  make verify       - Verify system prerequisites"
	@echo "  make kafka-start  - Start Kafka and Zookeeper"
	@echo "  make kafka-setup  - Create Kafka topics"
	@echo "  make db-setup     - Initialize MySQL database"
	@echo "  make build        - Build all modules"
	@echo "  make start        - Start all services"
	@echo "  make stop         - Stop all services"
	@echo "  make kafka-stop   - Stop Kafka infrastructure"
	@echo "  make clean        - Clean build artifacts and logs"
	@echo "  make test         - Run tests"
	@echo "  make full-start   - Complete setup and start (first time)"
	@echo "  make logs         - Tail all logs"
	@echo ""

verify:
	@./scripts/verify-system.sh

kafka-start:
	@./scripts/start-kafka.sh

kafka-setup:
	@./scripts/setup-kafka.sh

db-setup:
	@./scripts/setup-database.sh

build:
	@echo "Building all modules..."
	@mvn clean install -DskipTests

start:
	@./scripts/start-all.sh

stop:
	@./scripts/stop-all.sh

kafka-stop:
	@./scripts/stop-kafka.sh

clean:
	@echo "Cleaning build artifacts and logs..."
	@mvn clean
	@rm -rf logs/* pids/*
	@echo "Clean complete"

test:
	@echo "Running tests..."
	@mvn test

full-start: verify kafka-start kafka-setup db-setup build start
	@echo ""
	@echo "✓ System fully started!"
	@echo "Dashboard: http://localhost:3000"

logs:
	@if [ -d "logs" ]; then tail -f logs/*.log; else echo "No logs directory found"; fi

status:
	@echo "Checking service status..."
	@echo ""
	@echo "Kafka:"
	@lsof -i :9092 2>/dev/null || echo "  Not running"
	@echo ""
	@echo "MySQL:"
	@systemctl is-active --quiet mysql && echo "  Running" || echo "  Not running"
	@echo ""
	@echo "Active Java processes:"
	@ps aux | grep java | grep -v grep | wc -l

restart: stop start
	@echo "System restarted"

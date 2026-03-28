#!/bin/bash
# ============================================================================
# Database Setup Script for Integrated Traffic System
# MySQL Version: 8.x
# ============================================================================

set -e

echo "=========================================="
echo "MySQL Database Setup"
echo "=========================================="

# Configuration
DB_NAME="integrated_traffic_system"
DB_USER="root"
DB_PASS=""
SCHEMA_FILE="database/schema.sql"

echo "Database: $DB_NAME"
echo "User: $DB_USER"
echo ""

# Check if MySQL is installed
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL is not installed. Please install MySQL 8.x first."
    exit 1
fi

echo "✓ MySQL is installed"

# Check if MySQL is running
if ! systemctl is-active --quiet mysql && ! systemctl is-active --quiet mysqld; then
    echo "⚠️  MySQL is not running. Attempting to start..."
    sudo systemctl start mysql || sudo systemctl start mysqld
    sleep 3
fi

echo "✓ MySQL is running"

# Check if schema file exists
if [ ! -f "$SCHEMA_FILE" ]; then
    echo "❌ Schema file not found: $SCHEMA_FILE"
    exit 1
fi

echo "✓ Schema file found: $SCHEMA_FILE"
echo ""

# Execute schema
echo "Creating database and tables..."
if [ -z "$DB_PASS" ]; then
    mysql -u"$DB_USER" < "$SCHEMA_FILE"
else
    mysql -u"$DB_USER" -p"$DB_PASS" < "$SCHEMA_FILE"
fi

echo "✓ Database schema created successfully"
echo ""

# Verify database creation
echo "=========================================="
echo "Verifying database setup..."
echo "=========================================="

if [ -z "$DB_PASS" ]; then
    TABLE_COUNT=$(mysql -u"$DB_USER" -N -B -e "USE $DB_NAME; SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$DB_NAME';")
else
    TABLE_COUNT=$(mysql -u"$DB_USER" -p"$DB_PASS" -N -B -e "USE $DB_NAME; SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$DB_NAME';")
fi

echo "✓ Database: $DB_NAME"
echo "✓ Tables created: $TABLE_COUNT"
echo ""

# List tables
echo "Tables in database:"
if [ -z "$DB_PASS" ]; then
    mysql -u"$DB_USER" -N -B -e "USE $DB_NAME; SHOW TABLES;" | while read table; do
        echo "  - $table"
    done
else
    mysql -u"$DB_USER" -p"$DB_PASS" -N -B -e "USE $DB_NAME; SHOW TABLES;" | while read table; do
        echo "  - $table"
    done
fi

echo ""
echo "=========================================="
echo "✓ Database setup completed successfully!"
echo "=========================================="
echo ""
echo "Connection details:"
echo "  Host: localhost"
echo "  Port: 3306"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo ""

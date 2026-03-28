-- ============================================================================
-- INTEGRATED SMART CITY TRAFFIC MANAGEMENT SYSTEM - DATABASE SCHEMA
-- ============================================================================
-- 
-- Project: Unified Smart City Traffic, Pollution and Noise Monitoring System
-- Database: MySQL 8.x
-- Character Set: UTF8MB4
-- Date: 2026-03-19
-- 
-- This schema integrates all components:
-- 1. Camera-based traffic monitoring (RMI/Kafka)
-- 2. Traffic flow management (SOAP services)
-- 3. Traffic light control (SOAP services)
-- 4. Pollution monitoring (REST/Kafka)
-- 5. Noise monitoring (TCP/Kafka)
-- 
-- ============================================================================

-- Create database
CREATE DATABASE IF NOT EXISTS integrated_traffic_system
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE integrated_traffic_system;

-- ============================================================================
-- SECTION 1: CAMERA-BASED TRAFFIC MONITORING
-- ============================================================================

-- Table: camera_events
-- Stores raw camera event data from Kafka (camera-data topic)
CREATE TABLE IF NOT EXISTS camera_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    camera_id VARCHAR(50) NOT NULL,
    intersection_id VARCHAR(50) NOT NULL,
    timestamp DATETIME NOT NULL,
    vehicle_count INT NOT NULL,
    average_speed DOUBLE NOT NULL,
    stopped_vehicles INT NOT NULL,
    pedestrian_crossing BOOLEAN NOT NULL,
    suspected_incident BOOLEAN NOT NULL,
    congestion_hint VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_camera_timestamp (camera_id, timestamp),
    INDEX idx_intersection_timestamp (intersection_id, timestamp),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Raw camera event data';

-- Table: traffic_analysis
-- Stores analyzed traffic state history
CREATE TABLE IF NOT EXISTS traffic_analysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    intersection_id VARCHAR(50) NOT NULL,
    camera_id VARCHAR(50) NOT NULL,
    timestamp DATETIME NOT NULL,
    traffic_state VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    vehicle_count INT NOT NULL,
    average_speed DOUBLE NOT NULL,
    recommendation TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_intersection_timestamp (intersection_id, timestamp),
    INDEX idx_timestamp (timestamp),
    INDEX idx_traffic_state (traffic_state)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Analyzed traffic patterns';

-- Table: camera_status
-- Stores current status of camera simulators
CREATE TABLE IF NOT EXISTS camera_status (
    camera_id VARCHAR(50) PRIMARY KEY,
    intersection_id VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    last_update DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_intersection (intersection_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Camera simulator status';

-- ============================================================================
-- SECTION 2: TRAFFIC FLOW MANAGEMENT (SOAP Services)
-- ============================================================================

-- Table: flux
-- Stores traffic flow data from SOAP services
CREATE TABLE IF NOT EXISTS flux (
    id INT AUTO_INCREMENT PRIMARY KEY,
    flux INT NOT NULL COMMENT 'Traffic flow volume',
    name VARCHAR(50) NOT NULL COMMENT 'Route name (nord, sud, est, ouest)',
    timestamp DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name_timestamp (name, timestamp),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Traffic flow from SOAP services';

-- ============================================================================
-- SECTION 3: POLLUTION MONITORING
-- ============================================================================

-- Table: pollution
-- Stores pollution data (CO2 levels) from REST service via Kafka
CREATE TABLE IF NOT EXISTS pollution (
    id INT AUTO_INCREMENT PRIMARY KEY,
    zone_id VARCHAR(50) NOT NULL COMMENT 'Geographic zone (Zone_Centre, Zone_Nord, Zone_Sud)',
    niveau_co2 DECIMAL(10,2) NOT NULL COMMENT 'CO2 level in µg/m³',
    timestamp VARCHAR(50) NOT NULL COMMENT 'ISO 8601 timestamp',
    date_insertion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_zone_pollution (zone_id),
    INDEX idx_timestamp_pollution (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Air pollution data (CO2)';

-- ============================================================================
-- SECTION 4: NOISE MONITORING
-- ============================================================================

-- Table: bruit
-- Stores noise level data from TCP service via Kafka
CREATE TABLE IF NOT EXISTS bruit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    zone_id VARCHAR(50) NOT NULL COMMENT 'Geographic zone',
    niveau_decibels DECIMAL(10,2) NOT NULL COMMENT 'Noise level in dB',
    timestamp VARCHAR(50) NOT NULL COMMENT 'ISO 8601 timestamp',
    date_insertion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_zone_bruit (zone_id),
    INDEX idx_timestamp_bruit (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Noise level data (decibels)';

-- ============================================================================
-- SECTION 5: ALERTS & RECOMMENDATIONS
-- ============================================================================

-- Table: alerts
-- Stores all types of alerts (traffic, pollution, noise)
CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL COMMENT 'Alert type: TRAFFIC, POLLUTION, NOISE, CONGESTION',
    severity VARCHAR(20) NOT NULL COMMENT 'Severity: HIGH, MEDIUM, LOW',
    message TEXT NOT NULL,
    timestamp DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Status: ACTIVE, RESOLVED, IGNORED',
    intersection_id VARCHAR(50) DEFAULT NULL COMMENT 'For traffic alerts',
    camera_id VARCHAR(50) DEFAULT NULL COMMENT 'For camera-based alerts',
    zone_id VARCHAR(50) DEFAULT NULL COMMENT 'For pollution/noise alerts',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status_timestamp (status, timestamp),
    INDEX idx_type (type),
    INDEX idx_severity (severity),
    INDEX idx_intersection (intersection_id),
    INDEX idx_zone (zone_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Unified alerts from all subsystems';

-- Table: recommendations
-- Stores traffic management recommendations
CREATE TABLE IF NOT EXISTS recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    intersection_id VARCHAR(50) DEFAULT NULL,
    camera_id VARCHAR(50) DEFAULT NULL,
    zone_id VARCHAR(50) DEFAULT NULL,
    timestamp DATETIME NOT NULL,
    recommendation TEXT NOT NULL,
    reason TEXT,
    priority VARCHAR(20) NOT NULL COMMENT 'Priority: HIGH, MEDIUM, LOW',
    action_recommandee TEXT COMMENT 'Recommended action',
    alert_id BIGINT DEFAULT NULL COMMENT 'Related alert if any',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_intersection_timestamp (intersection_id, timestamp),
    INDEX idx_zone_timestamp (zone_id, timestamp),
    INDEX idx_priority (priority),
    INDEX idx_alert (alert_id),
    FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Traffic and environmental recommendations';

-- ============================================================================
-- SECTION 6: INITIAL DATA & CONFIGURATION
-- ============================================================================

-- Insert sample camera status
INSERT INTO camera_status (camera_id, intersection_id, status, last_update) VALUES
('CAM-01', 'INT-01', 'ACTIVE', NOW()),
('CAM-02', 'INT-02', 'ACTIVE', NOW()),
('CAM-03', 'INT-03', 'ACTIVE', NOW()),
('CAM-04', 'INT-04', 'ACTIVE', NOW())
ON DUPLICATE KEY UPDATE last_update = NOW();

-- ============================================================================
-- SECTION 7: VIEWS FOR DASHBOARD
-- ============================================================================

-- View: Latest traffic state per intersection
CREATE OR REPLACE VIEW v_latest_traffic_state AS
SELECT 
    ta.intersection_id,
    ta.camera_id,
    ta.traffic_state,
    ta.severity,
    ta.vehicle_count,
    ta.average_speed,
    ta.timestamp,
    cs.status as camera_status
FROM traffic_analysis ta
INNER JOIN (
    SELECT intersection_id, MAX(timestamp) as max_timestamp
    FROM traffic_analysis
    GROUP BY intersection_id
) latest ON ta.intersection_id = latest.intersection_id 
    AND ta.timestamp = latest.max_timestamp
LEFT JOIN camera_status cs ON ta.camera_id = cs.camera_id;

-- View: Active alerts summary
CREATE OR REPLACE VIEW v_active_alerts_summary AS
SELECT 
    type,
    severity,
    COUNT(*) as alert_count,
    MIN(timestamp) as earliest_alert,
    MAX(timestamp) as latest_alert
FROM alerts
WHERE status = 'ACTIVE'
GROUP BY type, severity;

-- View: Latest pollution by zone
CREATE OR REPLACE VIEW v_latest_pollution AS
SELECT 
    p.zone_id,
    p.niveau_co2,
    p.timestamp,
    CASE 
        WHEN p.niveau_co2 > 100 THEN 'HIGH'
        WHEN p.niveau_co2 > 80 THEN 'MEDIUM'
        ELSE 'LOW'
    END as pollution_level
FROM pollution p
INNER JOIN (
    SELECT zone_id, MAX(timestamp) as max_timestamp
    FROM pollution
    GROUP BY zone_id
) latest ON p.zone_id = latest.zone_id 
    AND p.timestamp = latest.max_timestamp;

-- View: Latest noise by zone
CREATE OR REPLACE VIEW v_latest_noise AS
SELECT 
    b.zone_id,
    b.niveau_decibels,
    b.timestamp,
    CASE 
        WHEN b.niveau_decibels > 90 THEN 'HIGH'
        WHEN b.niveau_decibels > 85 THEN 'MEDIUM'
        ELSE 'LOW'
    END as noise_level
FROM bruit b
INNER JOIN (
    SELECT zone_id, MAX(timestamp) as max_timestamp
    FROM bruit
    GROUP BY zone_id
) latest ON b.zone_id = latest.zone_id 
    AND b.timestamp = latest.max_timestamp;

-- ============================================================================
-- SECTION 8: CLEANUP PROCEDURES
-- ============================================================================

-- Procedure: Clean old data (keep last 30 days)
DELIMITER //
CREATE PROCEDURE cleanup_old_data()
BEGIN
    DELETE FROM camera_events WHERE timestamp < DATE_SUB(NOW(), INTERVAL 30 DAY);
    DELETE FROM traffic_analysis WHERE timestamp < DATE_SUB(NOW(), INTERVAL 30 DAY);
    DELETE FROM flux WHERE timestamp < DATE_SUB(NOW(), INTERVAL 30 DAY);
    DELETE FROM pollution WHERE date_insertion < DATE_SUB(NOW(), INTERVAL 30 DAY);
    DELETE FROM bruit WHERE date_insertion < DATE_SUB(NOW(), INTERVAL 30 DAY);
    DELETE FROM alerts WHERE timestamp < DATE_SUB(NOW(), INTERVAL 30 DAY) AND status = 'RESOLVED';
END //
DELIMITER ;

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================

-- Smart Traffic Management System Database Schema
-- MySQL Database Schema

-- Create database
CREATE DATABASE IF NOT EXISTS traffic_management;
USE traffic_management;

-- Table: camera_events
-- Stores raw simulated metadata received from Kafka
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
    INDEX idx_camera_timestamp (camera_id, timestamp),
    INDEX idx_intersection_timestamp (intersection_id, timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
    INDEX idx_intersection_timestamp (intersection_id, timestamp),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: alerts
-- Stores generated alerts
CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    timestamp DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    intersection_id VARCHAR(50) NOT NULL,
    camera_id VARCHAR(50) NOT NULL,
    INDEX idx_status_timestamp (status, timestamp),
    INDEX idx_intersection (intersection_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: recommendations
-- Stores generated recommendations
CREATE TABLE IF NOT EXISTS recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    intersection_id VARCHAR(50) NOT NULL,
    camera_id VARCHAR(50) NOT NULL,
    timestamp DATETIME NOT NULL,
    recommendation TEXT NOT NULL,
    reason TEXT,
    priority VARCHAR(20) NOT NULL,
    INDEX idx_intersection_timestamp (intersection_id, timestamp),
    INDEX idx_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: camera_status
-- Stores current simulator status
CREATE TABLE IF NOT EXISTS camera_status (
    camera_id VARCHAR(50) PRIMARY KEY,
    intersection_id VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    last_update DATETIME NOT NULL,
    INDEX idx_intersection (intersection_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

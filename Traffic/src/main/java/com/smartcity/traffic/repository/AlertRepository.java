package com.smartcity.traffic.repository;

import com.smartcity.traffic.model.Alert;
import com.smartcity.traffic.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertRepository {
    private static final Logger logger = LoggerFactory.getLogger(AlertRepository.class);

    public void save(Alert alert) throws SQLException {
        String sql = "INSERT INTO alerts (type, severity, message, timestamp, status, " +
                "intersection_id, camera_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, alert.getType());
            stmt.setString(2, alert.getSeverity());
            stmt.setString(3, alert.getMessage());
            stmt.setTimestamp(4, Timestamp.valueOf(alert.getTimestamp()));
            stmt.setString(5, alert.getStatus());
            stmt.setString(6, alert.getIntersectionId());
            stmt.setString(7, alert.getCameraId());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    alert.setId(generatedKeys.getLong(1));
                }
            }

            logger.debug("Alert saved: {}", alert.getId());
        }
    }

    public List<Alert> findActiveAlerts() throws SQLException {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM alerts WHERE status = 'ACTIVE' ORDER BY timestamp DESC LIMIT 50";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
        }

        return alerts;
    }

    public List<Alert> findAll() throws SQLException {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM alerts ORDER BY timestamp DESC LIMIT 100";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
        }

        return alerts;
    }

    private Alert mapResultSetToAlert(ResultSet rs) throws SQLException {
        Alert alert = new Alert();
        alert.setId(rs.getLong("id"));
        alert.setType(rs.getString("type"));
        alert.setSeverity(rs.getString("severity"));
        alert.setMessage(rs.getString("message"));
        alert.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        alert.setStatus(rs.getString("status"));
        alert.setIntersectionId(rs.getString("intersection_id"));
        alert.setCameraId(rs.getString("camera_id"));
        return alert;
    }
}

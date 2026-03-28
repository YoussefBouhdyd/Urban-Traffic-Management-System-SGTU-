package com.smartcity.traffic.repository;

import com.smartcity.traffic.model.CameraEvent;
import com.smartcity.traffic.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CameraEventRepository {
    private static final Logger logger = LoggerFactory.getLogger(CameraEventRepository.class);

    public void save(CameraEvent event) throws SQLException {
        String sql = "INSERT INTO camera_events (camera_id, intersection_id, timestamp, vehicle_count, " +
                "average_speed, stopped_vehicles, pedestrian_crossing, suspected_incident, congestion_hint) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, event.getCameraId());
            stmt.setString(2, event.getIntersectionId());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getTimestamp()));
            stmt.setInt(4, event.getVehicleCount());
            stmt.setDouble(5, event.getAverageSpeed());
            stmt.setInt(6, event.getStoppedVehicles());
            stmt.setBoolean(7, event.isPedestrianCrossing());
            stmt.setBoolean(8, event.isSuspectedIncident());
            stmt.setString(9, event.getCongestionHint());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getLong(1));
                }
            }

            logger.debug("Camera event saved: {}", event.getId());
        }
    }

    public List<CameraEvent> findAll() throws SQLException {
        List<CameraEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM camera_events ORDER BY timestamp DESC LIMIT 100";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }

        return events;
    }

    private CameraEvent mapResultSetToEvent(ResultSet rs) throws SQLException {
        CameraEvent event = new CameraEvent();
        event.setId(rs.getLong("id"));
        event.setCameraId(rs.getString("camera_id"));
        event.setIntersectionId(rs.getString("intersection_id"));
        event.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        event.setVehicleCount(rs.getInt("vehicle_count"));
        event.setAverageSpeed(rs.getDouble("average_speed"));
        event.setStoppedVehicles(rs.getInt("stopped_vehicles"));
        event.setPedestrianCrossing(rs.getBoolean("pedestrian_crossing"));
        event.setSuspectedIncident(rs.getBoolean("suspected_incident"));
        event.setCongestionHint(rs.getString("congestion_hint"));
        return event;
    }
}

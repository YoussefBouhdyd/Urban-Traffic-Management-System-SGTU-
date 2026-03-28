package com.smartcity.traffic.repository;

import com.smartcity.traffic.model.TrafficAnalysis;
import com.smartcity.traffic.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrafficAnalysisRepository {
    private static final Logger logger = LoggerFactory.getLogger(TrafficAnalysisRepository.class);

    public void save(TrafficAnalysis analysis) throws SQLException {
        String sql = "INSERT INTO traffic_analysis (intersection_id, camera_id, timestamp, traffic_state, " +
                "severity, vehicle_count, average_speed, recommendation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, analysis.getIntersectionId());
            stmt.setString(2, analysis.getCameraId());
            stmt.setTimestamp(3, Timestamp.valueOf(analysis.getTimestamp()));
            stmt.setString(4, analysis.getTrafficState());
            stmt.setString(5, analysis.getSeverity());
            stmt.setInt(6, analysis.getVehicleCount());
            stmt.setDouble(7, analysis.getAverageSpeed());
            stmt.setString(8, analysis.getRecommendation());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    analysis.setId(generatedKeys.getLong(1));
                }
            }

            logger.debug("Traffic analysis saved: {}", analysis.getId());
        }
    }

    public TrafficAnalysis findLatest() throws SQLException {
        String sql = "SELECT * FROM traffic_analysis ORDER BY timestamp DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return mapResultSetToAnalysis(rs);
            }
        }

        return null;
    }

    public List<TrafficAnalysis> findByTimeRange(LocalDateTime from, LocalDateTime to) throws SQLException {
        List<TrafficAnalysis> analyses = new ArrayList<>();
        String sql = "SELECT * FROM traffic_analysis WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(from));
            stmt.setTimestamp(2, Timestamp.valueOf(to));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    analyses.add(mapResultSetToAnalysis(rs));
                }
            }
        }

        return analyses;
    }

    private TrafficAnalysis mapResultSetToAnalysis(ResultSet rs) throws SQLException {
        TrafficAnalysis analysis = new TrafficAnalysis();
        analysis.setId(rs.getLong("id"));
        analysis.setIntersectionId(rs.getString("intersection_id"));
        analysis.setCameraId(rs.getString("camera_id"));
        analysis.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        analysis.setTrafficState(rs.getString("traffic_state"));
        analysis.setSeverity(rs.getString("severity"));
        analysis.setVehicleCount(rs.getInt("vehicle_count"));
        analysis.setAverageSpeed(rs.getDouble("average_speed"));
        analysis.setRecommendation(rs.getString("recommendation"));
        return analysis;
    }
}

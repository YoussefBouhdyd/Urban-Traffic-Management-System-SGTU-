package com.smartcity.traffic.repository;

import com.smartcity.traffic.model.Recommendation;
import com.smartcity.traffic.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecommendationRepository {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationRepository.class);

    public void save(Recommendation recommendation) throws SQLException {
        String sql = "INSERT INTO recommendations (intersection_id, camera_id, timestamp, " +
                "recommendation, reason, priority) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, recommendation.getIntersectionId());
            stmt.setString(2, recommendation.getCameraId());
            stmt.setTimestamp(3, Timestamp.valueOf(recommendation.getTimestamp()));
            stmt.setString(4, recommendation.getRecommendation());
            stmt.setString(5, recommendation.getReason());
            stmt.setString(6, recommendation.getPriority());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    recommendation.setId(generatedKeys.getLong(1));
                }
            }

            logger.debug("Recommendation saved: {}", recommendation.getId());
        }
    }

    public List<Recommendation> findLatest(int limit) throws SQLException {
        List<Recommendation> recommendations = new ArrayList<>();
        String sql = "SELECT * FROM recommendations ORDER BY timestamp DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recommendations.add(mapResultSetToRecommendation(rs));
                }
            }
        }

        return recommendations;
    }

    public List<Recommendation> findAll() throws SQLException {
        return findLatest(100);
    }

    private Recommendation mapResultSetToRecommendation(ResultSet rs) throws SQLException {
        Recommendation recommendation = new Recommendation();
        recommendation.setId(rs.getLong("id"));
        recommendation.setIntersectionId(rs.getString("intersection_id"));
        recommendation.setCameraId(rs.getString("camera_id"));
        recommendation.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        recommendation.setRecommendation(rs.getString("recommendation"));
        recommendation.setReason(rs.getString("reason"));
        recommendation.setPriority(rs.getString("priority"));
        return recommendation;
    }
}

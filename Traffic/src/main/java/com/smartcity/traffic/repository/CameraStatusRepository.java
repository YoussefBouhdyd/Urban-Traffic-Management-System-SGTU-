package com.smartcity.traffic.repository;

import com.smartcity.traffic.model.CameraStatus;
import com.smartcity.traffic.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class CameraStatusRepository {
    private static final Logger logger = LoggerFactory.getLogger(CameraStatusRepository.class);

    public void saveOrUpdate(CameraStatus cameraStatus) throws SQLException {
        String sql = "INSERT INTO camera_status (camera_id, intersection_id, status, last_update) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE intersection_id = ?, status = ?, last_update = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cameraStatus.getCameraId());
            stmt.setString(2, cameraStatus.getIntersectionId());
            stmt.setString(3, cameraStatus.getStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(cameraStatus.getLastUpdate()));
            stmt.setString(5, cameraStatus.getIntersectionId());
            stmt.setString(6, cameraStatus.getStatus());
            stmt.setTimestamp(7, Timestamp.valueOf(cameraStatus.getLastUpdate()));

            stmt.executeUpdate();
            logger.debug("Camera status saved/updated: {}", cameraStatus.getCameraId());
        }
    }

    public CameraStatus findById(String cameraId) throws SQLException {
        String sql = "SELECT * FROM camera_status WHERE camera_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cameraId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCameraStatus(rs);
                }
            }
        }

        return null;
    }

    private CameraStatus mapResultSetToCameraStatus(ResultSet rs) throws SQLException {
        CameraStatus status = new CameraStatus();
        status.setCameraId(rs.getString("camera_id"));
        status.setIntersectionId(rs.getString("intersection_id"));
        status.setStatus(rs.getString("status"));
        status.setLastUpdate(rs.getTimestamp("last_update").toLocalDateTime());
        return status;
    }
}

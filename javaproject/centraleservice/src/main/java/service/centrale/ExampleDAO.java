package service.centrale;

import java.sql.*;

import service.centrale.DataConnection.DBConnection;

public class ExampleDAO {
    public void fetchData() {
        String sql = "SELECT * FROM flux";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.println(rs.getString("column_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertData(String name,int flux) {
        String sql = "INSERT INTO flux (flux,name) VALUES (?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1 , flux);
            ps.setString(2 , name);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
package service.centrale.Services;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import service.centrale.DataConnection.DBConnection;
import service.centrale.Dtos.ReceptionFlux;

@Path("/Flux")
public class GestionFlux {
    private static int congestionlimit = 70;
    private static List<ReceptionFlux> list=new ArrayList<ReceptionFlux>();
    public static synchronized List<ReceptionFlux> getList() {
        return list;
    }
    public static synchronized void refresh()
    {
        list=new ArrayList<>();
    }
    public static void setCongestionlimit(int congestionlimit) {
        GestionFlux.congestionlimit = congestionlimit;
    }

    public static int getCongestionlimit() {
        return congestionlimit;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ReceptionFlux> getFlux() {
        return queryFlux("SELECT flux, name, timestamp FROM flux ORDER BY timestamp DESC LIMIT 50");
    }

    @GET
    @Path("/latest")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ReceptionFlux> getLatestFluxByRoute() {
        return queryFlux(
            "SELECT f.flux, f.name, f.timestamp "
                + "FROM flux f "
                + "INNER JOIN (SELECT name, MAX(timestamp) AS latest_timestamp FROM flux GROUP BY name) latest "
                + "ON f.name = latest.name AND f.timestamp = latest.latest_timestamp "
                + "ORDER BY f.name"
        );
    }

    @GET
    @Path("/route/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ReceptionFlux> getFluxByRoute(@PathParam("name") String name) {
        return queryFluxByRoute(
            "SELECT flux, name, timestamp FROM flux WHERE name = ? ORDER BY timestamp DESC LIMIT 50",
            name
        );
    }

    @GET
    @Path("/route/{name}/latest")
    @Produces(MediaType.APPLICATION_JSON)
    public ReceptionFlux getLatestFluxForRoute(@PathParam("name") String name) {
        ArrayList<ReceptionFlux> result = queryFluxByRoute(
            "SELECT flux, name, timestamp FROM flux WHERE name = ? ORDER BY timestamp DESC LIMIT 1",
            name
        );
        return result.isEmpty() ? null : result.get(0);
    }

    private ArrayList<ReceptionFlux> queryFlux(String sql) {
        ArrayList<ReceptionFlux> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new ReceptionFlux(
                    rs.getInt("flux"),
                    rs.getString("name"),
                    rs.getString("timestamp")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error: " + e.getMessage(), e);
        }

        return result;
    }

    private ArrayList<ReceptionFlux> queryFluxByRoute(String sql, String name) {
        ArrayList<ReceptionFlux> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new ReceptionFlux(
                        rs.getInt("flux"),
                        rs.getString("name"),
                        rs.getString("timestamp")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error: " + e.getMessage(), e);
        }

        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean addFlux(ReceptionFlux point) {
        insertData(point.getName(), point.getFlux(),point.getTimestamp());
        return true;
    }
    // on a utilise le mot synchronised car on a 3 choses,
    // on ajoute, on voit , on reinitialise la liste
    public static synchronized void insertData(String name, int flux, String timestamp) {
        String sql = "INSERT INTO flux (flux,name,timestamp) VALUES (?,?,?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flux);
            ps.setString(2, name);
            ps.setString(3, timestamp);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error: " + e.getMessage(), e);
        }
        if(flux>=congestionlimit)
        {
            list.add(new ReceptionFlux(flux,name, timestamp));
        }
    }
}

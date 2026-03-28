package com.smartcity.traffic.api;

import com.smartcity.traffic.dto.TrafficHistoryItemResponse;
import com.smartcity.traffic.dto.TrafficLatestResponse;
import com.smartcity.traffic.model.TrafficAnalysis;
import com.smartcity.traffic.repository.TrafficAnalysisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/traffic")
@Produces(MediaType.APPLICATION_JSON)
public class TrafficResource {
    private static final Logger logger = LoggerFactory.getLogger(TrafficResource.class);
    private final TrafficAnalysisRepository analysisRepository;

    public TrafficResource() {
        this.analysisRepository = new TrafficAnalysisRepository();
    }

    /**
     * GET /api/traffic/latest
     * Returns the latest analyzed traffic state for the intersection
     */
    @GET
    @Path("/latest")
    public Response getLatestTrafficState() {
        try {
            TrafficAnalysis latest = analysisRepository.findLatest();
            
            if (latest == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"No traffic data available\"}")
                        .build();
            }

            TrafficLatestResponse response = TrafficLatestResponse.fromTrafficAnalysis(latest);
            return Response.ok(response).build();
            
        } catch (SQLException e) {
            logger.error("Error fetching latest traffic state", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Internal server error\"}")
                    .build();
        }
    }

    /**
     * GET /api/traffic/history?from=...&to=...
     * Returns historical analyzed traffic events for charts and timeline
     */
    @GET
    @Path("/history")
    public Response getTrafficHistory(
            @QueryParam("from") String fromStr,
            @QueryParam("to") String toStr) {
        
        try {
            // Parse date parameters
            LocalDateTime from;
            LocalDateTime to;
            
            if (fromStr == null || toStr == null) {
                // Default: last 24 hours
                to = LocalDateTime.now();
                from = to.minusDays(1);
            } else {
                try {
                    from = LocalDateTime.parse(fromStr);
                    to = LocalDateTime.parse(toStr);
                } catch (DateTimeParseException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"Invalid date format. Use ISO 8601 format: yyyy-MM-ddTHH:mm:ss\"}")
                            .build();
                }
            }

            // Validate date range
            if (from.isAfter(to)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"'from' date must be before 'to' date\"}")
                        .build();
            }

            List<TrafficAnalysis> history = analysisRepository.findByTimeRange(from, to);
            
            List<TrafficHistoryItemResponse> response = history.stream()
                    .map(TrafficHistoryItemResponse::fromTrafficAnalysis)
                    .collect(Collectors.toList());

            return Response.ok(response).build();
            
        } catch (SQLException e) {
            logger.error("Error fetching traffic history", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Internal server error\"}")
                    .build();
        }
    }
}

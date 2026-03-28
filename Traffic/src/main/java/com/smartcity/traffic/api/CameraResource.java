package com.smartcity.traffic.api;

import com.smartcity.traffic.dto.CameraStatusResponse;
import com.smartcity.traffic.model.CameraStatus;
import com.smartcity.traffic.repository.CameraStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/api/cameras")
@Produces(MediaType.APPLICATION_JSON)
public class CameraResource {
    private static final Logger logger = LoggerFactory.getLogger(CameraResource.class);
    private final CameraStatusRepository statusRepository;

    public CameraResource() {
        this.statusRepository = new CameraStatusRepository();
    }

    /**
     * GET /api/cameras/{id}/status
     * Returns current camera simulator status
     */
    @GET
    @Path("/{id}/status")
    public Response getCameraStatus(@PathParam("id") String cameraId) {
        try {
            CameraStatus status = statusRepository.findById(cameraId);
            
            if (status == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Camera not found\"}")
                        .build();
            }

            CameraStatusResponse response = CameraStatusResponse.fromCameraStatus(status);
            return Response.ok(response).build();
            
        } catch (SQLException e) {
            logger.error("Error fetching camera status for camera {}", cameraId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Internal server error\"}")
                    .build();
        }
    }
}

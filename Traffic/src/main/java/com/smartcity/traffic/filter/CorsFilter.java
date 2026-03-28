package com.smartcity.traffic.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * CORS Filter to allow requests from the Next.js dashboard
 * 
 * This filter adds CORS (Cross-Origin Resource Sharing) headers to all responses
 * so that the browser allows requests from http://localhost:3000 to http://localhost:8083
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        
        // Allow requests from dashboard (port 3000, 3001, etc.)
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        
        // Allow common HTTP methods
        responseContext.getHeaders().add("Access-Control-Allow-Methods", 
            "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        
        // Allow common headers
        responseContext.getHeaders().add("Access-Control-Allow-Headers", 
            "origin, content-type, accept, authorization, x-requested-with");
        
        // Allow credentials (cookies, auth headers)
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        
        // Cache preflight response for 1 hour
        responseContext.getHeaders().add("Access-Control-Max-Age", "3600");
    }
}

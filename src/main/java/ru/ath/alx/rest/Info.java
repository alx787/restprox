package ru.ath.alx.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/info")
public class Info {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/test")
    public Response info() {

        return Response.ok("{\"status\":\"OK\", \"description\":\"info test message\"}").build();

    }
}

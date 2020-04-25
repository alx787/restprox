package ru.ath.alx.rest;

import org.apache.log4j.Logger;
import ru.ath.alx.dao.AuthService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/info")
public class Info {

    private static final Logger log = Logger.getLogger(Info.class);

    private AuthService authService = new AuthService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/test")
    public Response info() {

        log.warn("token: " + authService.getToken());

        return Response.ok("{\"status\":\"OK\", \"description\":\"info test message\"}").build();


    }
}

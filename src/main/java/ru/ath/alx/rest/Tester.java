package ru.ath.alx.rest;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/hello")
public class Tester {

    private static final Logger log = Logger.getLogger(Tester.class);


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hw")
    public String getMessage() {
        log.warn(" ===== 123 =====");

        return "Hello world!";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/js/{id0}/{id1}")
    public Response getJsonObj(@PathParam("id0") String id0, @PathParam("id1") String id1) {

        String sId0 = "";
        String sId1 = "";

        if (id0 != null) {
            sId0 = id0;
        }

        if (id1 != null) {
            sId1 = id1;
        }


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("objId", "всем привет");
        jsonObject.addProperty("msg1", sId0);
        jsonObject.addProperty("msg2", sId1);

        Gson gson = new Gson();

        log.warn(gson.toJson(jsonObject).toString());


        return Response.ok(gson.toJson(jsonObject)).build();
    }

}

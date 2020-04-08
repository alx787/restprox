package ru.ath.alx.rest;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/hello")
public class Tester {

    private static final Logger log = Logger.getLogger(Tester.class);


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMessage() {
        log.warn(" ===== 123 =====");

        return "Hello world!";
    }
}

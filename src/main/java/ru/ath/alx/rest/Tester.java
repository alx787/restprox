package ru.ath.alx.rest;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;


@Path("/hello")
public class Tester {

    private static final Logger log = Logger.getLogger(Tester.class);


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hw")
    public String getMessage() {
        log.warn(" ===== 123 =====");

        String token = "";

        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://wialon.kiravto.ru/wialon/ajax.html?svc=token/login&params={\"token\": \"" + token + "\"}");
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");

        HttpResponse response;
        try {
            response = client.execute(request);

        } catch (IOException e) {
            e.printStackTrace();
            return "empty 1";
        }

        String jsonString = "[]";

        try {
            jsonString = IOUtils.toString(response.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
            return "empty 2";
        }

        return jsonString;


//        HTTPОтвет = ВыполнитьHTTPЗапрос(ДанныеАвторизации.Получить("СерверВиалон") + "/wialon/ajax.html?svc=token/login&params={""token"": """ + ДанныеАвторизации.Получить("Токен") + """}",ТекстОшибки);
//
//
//        Если ЗначениеЗаполнено(ТекстОшибки) Тогда
//        Возврат "";
//        КонецЕсли;
//
//        КодСостояния = HTTPОтвет.КодСостояния;
//
//        Если КодСостояния = 200 Тогда
//                responseText = HTTPОтвет.ПолучитьТелоКакСтроку();
//        ЧтениеJSON = Новый ЧтениеJSON;
//        ЧтениеJSON.УстановитьСтроку(responseText);
//        response = ПрочитатьJSON(ЧтениеJSON, Истина);
//        ЧтениеJSON.Закрыть();
//
//        Возврат response.Получить("eid");
//        КонецЕсли;
//
//        Возврат "";



//        return "Hello world!";
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


    // получение информации за период
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/info/{id}/{datebeg}/{dateend}")
    public Response getTrack(@PathParam("id") String invnom,
                             @PathParam("datebeg") String datebeg,
                             @PathParam("dateend") String dateend) {


//        return Response.ok("{\"status\":\"error\", \"description\":\"exception in query\"}").build();

        JsonObject jsonRestAnswer = new JsonObject();
        jsonRestAnswer.addProperty("status", "ok");
        jsonRestAnswer.addProperty("total", 2);

        JsonArray jsonArray = new JsonArray();

        JsonObject oneRec = new JsonObject();
        oneRec.addProperty("datebeg", 12121212);
        oneRec.addProperty("dateend", 23232323);
        oneRec.addProperty("placebeg", "место 1");
        oneRec.addProperty("placendg", "место 2");
        oneRec.addProperty("mileage", "29 km");
        oneRec.addProperty("motohour", "1:16:10");

        oneRec.addProperty("duration", "6:59:34");

        jsonArray.add(oneRec);

        oneRec = new JsonObject();
        oneRec.addProperty("datebeg", 12121212);
        oneRec.addProperty("dateend", 23232323);
        oneRec.addProperty("placebeg", "место 1");
        oneRec.addProperty("placendg", "место 2");
        oneRec.addProperty("mileage", "29 km");
        oneRec.addProperty("motohour", "1:16:10");

        oneRec.addProperty("duration", "6:59:34");

        jsonArray.add(oneRec);


        jsonRestAnswer.add("records", jsonArray);


        Gson gson = new Gson();

//        log.warn(gson.toJson(jsonRestAnswer).toString());


        return Response.ok(gson.toJson(jsonRestAnswer)).build();


    }


}

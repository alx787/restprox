package ru.ath.alx.rest;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.google.gson.JsonParser;
import org.apache.log4j.Logger;



import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.charset.StandardCharsets;


@Path("/hello")
public class Tester {

    private static final Logger log = Logger.getLogger(Tester.class);

    private String token = "";

    private static final String URL_GET_SID = "https://wialon.kiravto.ru/wialon/ajax.html?svc=token/login&params={\"token\": \"%s\"}";
    private static final String URL_GET_OBJS = "https://wialon.kiravto.ru/wialon/ajax.html?svc=core/search_items&params={\"spec\":{\"itemsType\":\"avl_unit\",\"propName\":\"sys_name\",\"propValueMask\":\"*\",\"sortType\":\"sys_name\"},\"force\":1,\"flags\":\"0x00800109\",\"from\":0,\"to\":0}&sid=%s";



    private String sendRequest(String url) {
        HttpsURLConnection connection = null;
        StringBuilder result = new StringBuilder();
        boolean wasError = false;

        try {
            connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            log.warn(connection.toString());

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }

            } else {
                log.warn("error code " + String.valueOf(responseCode) + " - " + connection.getResponseMessage());
                wasError = true;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            log.warn("ошибка - неверно сформированный url");
            wasError = true;

        } catch (IOException e) {
            e.printStackTrace();
            log.warn("ошибка - ошибка ввода вывода");
            wasError = true;

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }


        if (wasError) {
            return null;
        }

        return result.toString();
    }


    private String getSID() {
        String res = sendRequest(String.format(URL_GET_SID, this.token));

        // распарсим ответ
        JsonObject jsonObject = new JsonParser().parse(res).getAsJsonObject();

        // произошла ошибка
        if (jsonObject.has("error")) {
            log.warn("ошибка при получении sid");
            log.warn(res);
            return null;
        }

        // ищем sid - не найден
        if (!jsonObject.has("eid")) {
            log.warn("ошибка при получении sid");
            log.warn("в структуре ответа не найден элемент eid");
            return null;
        }

        return jsonObject.get("eid").getAsString();
    }


//    private String sendRequest1111(String url, boolean getSID) {
//
//        HttpsURLConnection connection = null;
//        StringBuilder result = new StringBuilder();
//
//        String fullUrl;
//
//        if (getSID) {
//            fullUrl = String.format(url, this.token);
//        } else {
//            fullUrl = String.format(url, this.sid);
//        }
//
//
//        try {
//            connection = (HttpsURLConnection) new URL(fullUrl).openConnection();
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//            log.warn(connection.toString());
//
//            int responseCode = connection.getResponseCode();
//
//            if (responseCode == HttpsURLConnection.HTTP_OK) {
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
//                BufferedReader reader = new BufferedReader(inputStreamReader);
//                String line = reader.readLine();
//                while (line != null) {
//                    result.append(line);
//                    line = reader.readLine();
//                }
//
//            } else {
//                log.warn("error code " + String.valueOf(responseCode) + " - " + connection.getResponseMessage());
//                return "";
//            }
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            log.warn("ошибка - неверно сформированный url");
//            return "";
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            log.warn("ошибка - ошибка ввода вывода");
//            return "";
//
//        } finally {
//            if (connection != null) {
//                connection.disconnect();
//            }
//        }
//
//
//        log.warn(result.toString());
//
//        // распарсим ответ
//        JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
//
//
//        // получим идентификатор сессии
//        if (getSID) {
//
//            // произошла ошибка
//            if (jsonObject.has("error")) {
//                log.warn("ошибка при получении sid");
//                log.warn(result.toString());
//                return "";
//            }
//
//            // ищем sid - не найден
//            if (!jsonObject.has("eid")) {
//                log.warn("ошибка при получении sid");
//                log.warn("в структуре ответа не найден элемент eid");
//                return "";
//            }
//
//            return jsonObject.get("eid").getAsString();
//
//        }
//
//
//        // остальные запросы
//        this.sid = sendRequest(URL_GET_SID, true);
//
//
//
//        if (this.sid.equals("")) {
//            log.warn("не получен sid запрос выполнить невозможно");
//            return "";
//        }
//
//
//
//
//
//
//
//
//
//        return "";
//    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hw")
    public String getMessage() {
        log.warn(" ===== 123 =====");

//        String token = "";
//        String url = "";

//        log.warn(url);


//        return sendRequest(URL_GET_OBJS, false);




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

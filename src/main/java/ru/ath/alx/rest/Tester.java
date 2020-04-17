package ru.ath.alx.rest;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


import com.google.gson.*;

import org.apache.log4j.Logger;
import ru.ath.alx.dao.TransportService;
import ru.ath.alx.model.Transport;
import ru.ath.alx.util.ConverterUtil;


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
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;


@Path("/wl")
public class Tester {

    private static final Logger log = Logger.getLogger(Tester.class);

    private String token = "";
    private String sid = "";

    // получить сид
    private static final String URL_GET_SID = "https://wialon.kiravto.ru/wialon/ajax.html?svc=token/login&params={\"token\":\"%s\"}";
    // получить все объекты
    private static final String URL_GET_ALLOBJS = "https://wialon.kiravto.ru/wialon/ajax.html?svc=core/search_items&params={\"spec\":{\"itemsType\":\"avl_unit\",\"propName\":\"sys_name\",\"propValueMask\":\"*\",\"sortType\":\"sys_name\"},\"force\":1,\"flags\":\"0x00800109\",\"from\":0,\"to\":0}&sid=%s";
    // получить данные по одному объекту
    private static final String URL_GET_ONEOBJ = "https://wialon.kiravto.ru/wialon/ajax.html?svc=core/search_items&params={\"spec\":{\"itemsType\":\"avl_unit\",\"propName\":\"sys_id\",\"propValueMask\":\"__search_value__\",\"sortType\":\"sys_name\"},\"force\":1,\"flags\":\"0x00800109\",\"from\":0,\"to\":0}&sid=%s";
    // получить общие данные о пробеге
    private static final String URL_GET_TRACK = "https://wialon.kiravto.ru/wialon/ajax.html?svc=report/exec_report&params={\"reportResourceId\":__report_id__,\"reportTemplateId\":__template_id__,\"reportObjectId\":__object_id__,\"reportObjectSecId\":0,\"reportObjectIdList\":[],\"interval\":{\"from\":__date_begin__,\"to\":__date_end__,\"flags\":0}}&sid=%s";


    private TransportService trService = new TransportService();


//    private TransportService trService;
//
//    public Tester() {
//        this.trService = new TransportService();
//    }

    private String sendRequest(String url) {
        HttpsURLConnection connection = null;
        StringBuilder result = new StringBuilder();
        boolean wasError = false;

        try {
            connection = (HttpsURLConnection) new URL(url).openConnection();
//            connection.setRequestProperty("charset", StandardCharsets.UTF_8.displayName());
            connection.setRequestProperty("charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            log.warn(connection.toString());

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
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


    private String getDataFromWln(String url, boolean useSid) {

        if (useSid) {
            this.sid = getSID();
        }

        return sendRequest(String.format(url, this.sid));
    }


    // этот сервис используется для получения информации об объекте, в приложении он будет задействован
    // для наполнения/обновления таблицы, используемой при запросах с мобильных устройств
    // чтобы уменьшить частоту запросов к исходному серверу
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getobject/{id}")
    public Response getObjectsInfo(@PathParam("id") String id) {

        if ((id == null) || (id.equals(""))) {
            return Response.ok("{}").build();
        }

        String result = "";
        if (id.equals("all")) {
            result = getDataFromWln(URL_GET_ALLOBJS, true);
        } else {
            result = getDataFromWln(URL_GET_ONEOBJ.replace("__search_value__", id), true);
        }

        // преобразуем ответ в json
        JsonObject resJsonObj = new JsonParser().parse(result).getAsJsonObject();
        if (resJsonObj.has("items")) {
            JsonArray resItemsJsonArr = resJsonObj.getAsJsonArray("items");

            int itemsSize = resItemsJsonArr.size();
            for (int i = 0; i < itemsSize; i++) {


                Transport tr = ConverterUtil.getTransportFromJson(resItemsJsonArr.get(i).getAsJsonObject());
                log.warn(tr.getModel() + " " + tr.getRegistrationplate());
                log.warn("инвентарный номер: " + tr.getAtinvnom());

                Transport searchTr = trService.findTransportByInvnom(tr.getAtinvnom());



                if (searchTr != null) {
                    tr.setId(searchTr.getId());
                    trService.update(tr);
                } else {
                    //trService.create(tr);
                }

            }


        }


        return Response.ok(result).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/gettrack/{id}/{datebegin}/{dateend}")
    public Response getObjectTrack(@PathParam("id") String id, @PathParam("datebegin") String datebegin, @PathParam("dateend") String dateend) {
        if ((id == null) || (id.equals(""))) {
            return Response.ok("{\"status\":\"error\", \"description\":\"object id is empty\"}").build();
        }

        String reportId = "2352";
        String templateId = "2"; // 1САТХ МРСК

        String url = URL_GET_TRACK.replace("__report_id__", reportId);
        url = url.replace("__template_id__", templateId);
        url = url.replace("__object_id__", id);
        url = url.replace("__date_begin__", datebegin);
        url = url.replace("__date_end__", dateend);

        String result = getDataFromWln(url, true);

        return Response.ok(result).build();

    }


//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/js/{id0}/{id1}")
//    public Response getJsonObj(@PathParam("id0") String id0, @PathParam("id1") String id1) {
//
//        String sId0 = "";
//        String sId1 = "";
//
//        if (id0 != null) {
//            sId0 = id0;
//        }
//
//        if (id1 != null) {
//            sId1 = id1;
//        }
//
//
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("objId", "всем привет");
//        jsonObject.addProperty("msg1", sId0);
//        jsonObject.addProperty("msg2", sId1);
//
//        Gson gson = new Gson();
//
//        log.warn(gson.toJson(jsonObject).toString());
//
//
//        return Response.ok(gson.toJson(jsonObject)).build();
//    }
//
//
//    // получение информации за период
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/info/{id}/{datebeg}/{dateend}")
//    public Response getTrack(@PathParam("id") String invnom,
//                             @PathParam("datebeg") String datebeg,
//                             @PathParam("dateend") String dateend) {
//
//
////        return Response.ok("{\"status\":\"error\", \"description\":\"exception in query\"}").build();
//
//        JsonObject jsonRestAnswer = new JsonObject();
//        jsonRestAnswer.addProperty("status", "ok");
//        jsonRestAnswer.addProperty("total", 2);
//
//        JsonArray jsonArray = new JsonArray();
//
//        JsonObject oneRec = new JsonObject();
//        oneRec.addProperty("datebeg", 12121212);
//        oneRec.addProperty("dateend", 23232323);
//        oneRec.addProperty("placebeg", "место 1");
//        oneRec.addProperty("placendg", "место 2");
//        oneRec.addProperty("mileage", "29 km");
//        oneRec.addProperty("motohour", "1:16:10");
//
//        oneRec.addProperty("duration", "6:59:34");
//
//        jsonArray.add(oneRec);
//
//        oneRec = new JsonObject();
//        oneRec.addProperty("datebeg", 12121212);
//        oneRec.addProperty("dateend", 23232323);
//        oneRec.addProperty("placebeg", "место 1");
//        oneRec.addProperty("placendg", "место 2");
//        oneRec.addProperty("mileage", "29 km");
//        oneRec.addProperty("motohour", "1:16:10");
//
//        oneRec.addProperty("duration", "6:59:34");
//
//        jsonArray.add(oneRec);
//
//
//        jsonRestAnswer.add("records", jsonArray);
//
//
//        Gson gson = new Gson();
//
////        log.warn(gson.toJson(jsonRestAnswer).toString());
//
//
//        return Response.ok(gson.toJson(jsonRestAnswer)).build();
//
//
//    }


}

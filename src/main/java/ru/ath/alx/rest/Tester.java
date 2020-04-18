package ru.ath.alx.rest;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


import com.google.gson.*;

import org.apache.log4j.Logger;
import ru.ath.alx.dao.TransportService;
import ru.ath.alx.model.Transport;
import ru.ath.alx.util.ConverterUtil;
import ru.ath.alx.util.WebRequestUtil;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/wl")
public class Tester {

    private static final Logger log = Logger.getLogger(Tester.class);

    private String token = "a2146922f0785ff8de16107355d6a993EC8CB5E823A23AB32ABBCF31000D551C96C22E29";
    private String sid = "";


    // получить сид
//    private static final String URL_GET_SID = "https://wialon.kiravto.ru/wialon/ajax.html?svc=token/login&params={\"token\":\"%s\"}";
    // получить все объекты
    private static final String URL_GET_ALLOBJS = "https://wialon.kiravto.ru/wialon/ajax.html?svc=core/search_items&params={\"spec\":{\"itemsType\":\"avl_unit\",\"propName\":\"sys_name\",\"propValueMask\":\"*\",\"sortType\":\"sys_name\"},\"force\":1,\"flags\":\"0x00800109\",\"from\":0,\"to\":0}&sid=%s";
    // получить данные по одному объекту
    private static final String URL_GET_ONEOBJ = "https://wialon.kiravto.ru/wialon/ajax.html?svc=core/search_items&params={\"spec\":{\"itemsType\":\"avl_unit\",\"propName\":\"sys_id\",\"propValueMask\":\"__search_value__\",\"sortType\":\"sys_name\"},\"force\":1,\"flags\":\"0x00800109\",\"from\":0,\"to\":0}&sid=%s";
    // получить общие данные о пробеге
    private static final String URL_GET_TRACK = "https://wialon.kiravto.ru/wialon/ajax.html?svc=report/exec_report&params={\"reportResourceId\":__report_id__,\"reportTemplateId\":__template_id__,\"reportObjectId\":__object_id__,\"reportObjectSecId\":0,\"reportObjectIdList\":[],\"interval\":{\"from\":__date_begin__,\"to\":__date_end__,\"flags\":0}}&sid=%s";


    private TransportService trService = new TransportService();





    // этот сервис используется для обновления информации об объектах в базе данных из виалона
    // для наполнения/обновления таблицы, используемой при запросах с мобильных устройств
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/refreshobj/{invnom}")
    public Response refreshObjectsInDatabase(@PathParam("invnom") String invnom) {

        if ((invnom == null) || (invnom.equals(""))) {
            return Response.ok("{\"status\":\"error\", \"description\":\"object id (invnom) is empty\"}").build();
        }

        Transport searchTr = null; // переменная пригодится


        String result = "";
        if (invnom.equals("all")) {
            result = WebRequestUtil.getDataFromWln(URL_GET_ALLOBJS, true);
        } else {
            // тут придется найти наш элемент в базе и узнать его wlnid если он есть
            searchTr = trService.findTransportByInvnom(invnom);

            if (searchTr != null) {
                result = WebRequestUtil.getDataFromWln(URL_GET_ONEOBJ.replace("__search_value__", searchTr.getWlnid()), true);
            } else {
                return Response.ok("{\"status\":\"error\", \"description\":\"object with invnom = " + invnom + " not found in database\"}").build();
            }

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

                searchTr = trService.findTransportByInvnom(tr.getAtinvnom());

                if (searchTr != null) {
                    tr.setId(searchTr.getId());
                    trService.update(tr);
                } else {
                    trService.create(tr);
                }

            }

        }

        return Response.ok("{\"status\":\"OK\", \"description\":\"object with invnom = " + invnom + " updated\"}").build();
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

        String result = WebRequestUtil.getDataFromWln(url, true);

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

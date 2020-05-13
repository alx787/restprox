package ru.ath.alx.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import ru.ath.alx.dao.AuthService;
import ru.ath.alx.dao.TransportService;
import ru.ath.alx.dao.UserService;
import ru.ath.alx.model.Transport;
import ru.ath.alx.model.User;
import ru.ath.alx.util.AuthUtil;
import ru.ath.alx.util.ConverterUtil;
import ru.ath.alx.util.WebRequestUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("/info")
public class Info {

    private static final Logger log = Logger.getLogger(Info.class);

    private TransportService trService = new TransportService();

    private AuthService authService = new AuthService();
    private UserService userService = new UserService();

    private static final String URL_GET_MARS = "https://wialon.kiravto.ru/wialon/ajax.html?svc=messages/load_interval&params={\"itemId\":__object_id__,\"timeFrom\":__date_beg__,\"timeTo\":__date_end__,\"flags\":1,\"flagsMask\":65281,\"loadCount\":50000}&sid=%s";
    private static final String URL_GET_CLEAR_MARS = "https://wialon.kiravto.ru/wialon/ajax.html?svc=messages/unload&params={}&sid=%s";


//    @GET
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/test")
    public Response info() {

//        log.warn("token: " + authService.getToken());

        return Response.ok("{\"status\":\"OK\", \"description\":\"info test message\"}").build();

    }


    // сервис - заглушка возвращающий сообщение об ошибке авторизации
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/errorauth")
    public Response errorAuth() {

        log.warn("from errorauth");

        return Response.ok("{\"status\":\"error\", \"description\":\"error auth\"}").build();

    }


    // принимает логин пароль и формирует новый токен
    // проверять валидность токена или получать свежий токен можно при начале работы приложения

    // test
    // http://localhost:8080/restprox/info/newauth
    //    {
    //        "login":"alx",
    //        "pass":"345"
    //    }

    // в теле запроса должны быть post данные в формате json
    // {"login":"...", "pass":"..."} - реавторизация
    // возвращает {"stasus":"ok", content:{"userid":"...","token":"..."}}
    // {"userid":"...", "token":"..."} - просто авторизация
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/newauth")
//    public Response newAuth(InputStream incomingData) {
      public Response newAuth(String incomingData) {


//        StringBuffer stringBuffer = new StringBuffer();
//        String oneLine = null;
//
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(incomingData));
//            while ((oneLine = reader.readLine()) != null)
//                stringBuffer.append(oneLine);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.warn("ошибка при чтении post данных при реавторизации");
//            return Response.ok("{\"status\":\"error\", \"description\":\"error reauth\"}").build();
//        }

        JsonParser parser = new JsonParser();
//        JsonObject jsonPostData = parser.parse(stringBuffer.toString()).getAsJsonObject();
        JsonObject jsonPostData = parser.parse(incomingData).getAsJsonObject();

        String login = null;
        String passhash = null;

        login = jsonPostData.get("login").getAsString();
        passhash = jsonPostData.get("pass").getAsString();

//        log.warn("login: " + login);
//        log.warn("passhash: " + passhash);

        if (login == null || passhash == null) {
            log.warn("ошибка при получении логина или пароля при реавторизации");
            return Response.ok("{\"status\":\"error\", \"description\":\"error reauth\"}").build();
        }

        if (!AuthUtil.checkLoginPass(login, passhash)) {
            log.warn("ошибка при получении данных пользователя, неверное логин или пароль");
            return Response.ok("{\"status\":\"error\", \"description\":\"error reauth\"}").build();
        }


        User user = userService.findUserByLoginAndHash(login, passhash);
        if (user == null) {
            log.warn("ошибка при получении данных пользователя при реавторизации");
            return Response.ok("{\"status\":\"error\", \"description\":\"error reauth\"}").build();
        }

        JsonObject answObj = new JsonObject();
        answObj.addProperty("status", "ok");

        JsonObject contentObj = new JsonObject();
        contentObj.addProperty("id", String.valueOf(user.getId()));
        contentObj.addProperty("token", user.getPasstoken());

        answObj.add("content", contentObj);

        Gson gson = new Gson();

        return Response.ok(gson.toJson(answObj)).build();

    }








    // получение точек для построения трека - маршрута движения тс
    // пока лежит тут в тестовой части
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/mars/{invnom}/{datebeg}/{dateend}")
    public Response mars(@PathParam("invnom") String invnom, @PathParam("datebeg") String datebeg, @PathParam("dateend") String dateend) {

        ///////////////////////////////////////////////////
        // тут всякие разные проверки переменных
        if ((invnom == null) || (invnom.equals(""))) {
            return Response.ok("{\"status\":\"error\", \"description\":\"object invnom is empty\"}").build();
        }

        if ((datebeg == null) || (datebeg.equals(""))) {
            return Response.ok("{\"status\":\"error\", \"description\":\"datebeg is empty\"}").build();
        }

        if ((dateend == null) || (dateend.equals(""))) {
            return Response.ok("{\"status\":\"error\", \"description\":\"dateend is empty\"}").build();
        }


        Transport searchTrn = trService.findTransportByInvnom(invnom);

        if (searchTrn == null) {
            return Response.ok("{\"status\":\"error\", \"description\":\"объект с инв номером " + invnom + " не найден в базе данных\"}").build();
        }

        String wlnId = null;


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        // дату начала и окончания необходимо преобразовать в unix формат
        Date pDateBeg = null;
        Date pDateEnd = null;

        try {
            pDateBeg = format.parse(datebeg);
            pDateEnd = format.parse(dateend);
        } catch (ParseException e) {
            log.warn("ошибка получения периода. дата начала: " + datebeg + ", дата окончания: " + dateend);
            e.printStackTrace();
            return Response.ok("{\"status\":\"error\", \"description\":\"ошибка получения периода, дата начала: " + datebeg + ", дата окончания: " + dateend + "\"}").build();
        }

        // часовой пояс
        int localtimeZone = 3;
        // преобразование дат
        long lBegTime = pDateBeg.getTime() / 1000 - 3600 * localtimeZone;
        long lEndTime = pDateEnd.getTime() / 1000 - 3600 * localtimeZone + 86399;


        String sid = WebRequestUtil.getSID();

        String url = URL_GET_MARS.replace("__object_id__", searchTrn.getWlnid());
        url = url.replace("__date_beg__", String.valueOf(lBegTime));
        url = url.replace("__date_end__", String.valueOf(lEndTime));

        String resultAnsw = WebRequestUtil.getDataFromWln(url, sid);

        //
        if (resultAnsw == null) {
            Response resp = Response.ok("{\"status\":\"error\", \"description\":\"сервер вернул ошибку при попытке получения результатов отчета\"}").build();
            return resp;
        }

        // прочитаем ответ, если не ошибка то продолжим далее
        JsonObject resJsonObjAnsw = new JsonParser().parse(resultAnsw).getAsJsonObject();
        // проверка на ошибку
        if (resJsonObjAnsw.has("error")) {
            log.warn("ошибка при получении данных таблиц пробегов");
            log.warn("код ошибки " + resJsonObjAnsw.get("error").getAsString());
            return Response.ok("{\"status\":\"error\", \"description\":\"ошибка при получении данных сообщений смт, код ошибки " + resJsonObjAnsw.get("error").getAsString() + "\"}").build();
        }


        // для сообщений с координатами
        JsonArray answMarsJsonArr = new JsonArray();

        // читаем данные ответа
        JsonArray messagesJsonArr = resJsonObjAnsw.get("messages").getAsJsonArray();
        int cnt = messagesJsonArr.size();

        // для отбора
        String sX = "", sY = "", sCourse = "", sSpeed = "";

        for (int i = 0; i < cnt; i++) {

            JsonObject messageJsonObject = messagesJsonArr.get(i).getAsJsonObject();
            JsonObject messagePos = messageJsonObject.get("pos").getAsJsonObject();


            // условие для непринятия сообщений, проверяем одно за другим если срабатывает что то то сообщение не принимаем
            // 1 - координаты не меняются
            // 2 - направление не меняется - спорно, пока уберу
            // 3 - скорость не меняется и равна нулю

            if (sX.equals(messagePos.get("x").getAsString()) && sY.equals(messagePos.get("y").getAsString())) {
                continue;
            }

//            if (sCourse.equals(messagePos.get("c").getAsString())) {
//                continue;
//            }

            if (sSpeed.equals(messagePos.get("s").getAsString()) && sSpeed.equals("0")) {
                continue;
            }

            JsonObject oneMarsCoord = new JsonObject();

            oneMarsCoord.addProperty("x", messagePos.get("x").getAsString());
            oneMarsCoord.addProperty("y", messagePos.get("y").getAsString());
            oneMarsCoord.addProperty("course", messagePos.get("c").getAsString());
            oneMarsCoord.addProperty("speed", messagePos.get("s").getAsString());
            oneMarsCoord.addProperty("time", ConverterUtil.convertUnixToHuman(messageJsonObject.get("t").getAsString(), 3));

            answMarsJsonArr.add(oneMarsCoord);

            sX = messagePos.get("x").getAsString();
            sY = messagePos.get("y").getAsString();
            // sCourse = messagePos.get("c").getAsString();
            sSpeed = messagePos.get("s").getAsString();
        }


        ///////////////////////////////////////////////////////
        // здесь начнем формирование JSON объекта ответа
        ///////////////////////////////////////////////////////

        JsonObject answJson = new JsonObject();
        answJson.addProperty("status", "ok");
        answJson.addProperty("count", String.valueOf(answMarsJsonArr.size()));
        answJson.add("content", answMarsJsonArr);


//        log.warn(url);
//        log.warn(resultAnsw);

        Gson gson = new Gson();

        return Response.ok(gson.toJson(answJson)).build();
    }



}

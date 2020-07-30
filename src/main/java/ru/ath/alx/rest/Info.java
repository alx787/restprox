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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("/info")
public class Info {

    private static final Logger log = Logger.getLogger(Info.class);

    private UserService userService = new UserService();


//    @GET
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/test")
    public Response info() {

        // вобщем все запросы к рест стали через /rest

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
        passhash = jsonPostData.get("password").getAsString();

        log.warn("login: " + login);
        log.warn("passhash: " + passhash);

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
        contentObj.addProperty("userid", String.valueOf(user.getId()));
        contentObj.addProperty("token", user.getPasstoken());

        answObj.add("content", contentObj);

        Gson gson = new Gson();

        return Response.ok(gson.toJson(answObj)).build();

    }


}

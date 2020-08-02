package ru.ath.alx.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import ru.ath.alx.dao.TransportService;
import ru.ath.alx.model.Transport;
import ru.ath.alx.util.ConverterUtil;
import ru.ath.alx.util.WebRequestUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Path("/track")
public class ObjectsTracks {

    private static final Logger log = Logger.getLogger(ObjectsTracks.class);

    // очистка сессии
    private static final String URL_CLEAR_SESSION = "https://wialon.kiravto.ru/wialon/ajax.html&svc=report/cleanup_result&params={}&sid=%s";

    // получить общие данные о пробеге
    private static final String URL_GET_TRACK = "https://wialon.kiravto.ru/wialon/ajax.html?svc=report/exec_report&params={\"reportResourceId\":__report_id__,\"reportTemplateId\":__template_id__,\"reportObjectId\":__object_id__,\"reportObjectSecId\":0,\"reportObjectIdList\":[],\"interval\":{\"from\":__date_begin__,\"to\":__date_end__,\"flags\":0}}&sid=%s";
    // получить таблицу пробегов
    private static final String URL_GET_TABLE = "https://wialon.kiravto.ru/wialon/ajax.html?&svc=report/get_result_rows&params={\"tableIndex\":__table_index__,\"indexFrom\":__first_row__,\"indexTo\":__last_row__}&sid=%s";
    // получить расшифровку пробега
    private static final String URL_GET_TABLEROW = "https://wialon.kiravto.ru/wialon/ajax.html?svc=report/get_result_subrows&params={\"tableIndex\":__table_index__,\"rowIndex\":__row_index__}&sid=%s";
    // получить последнее месторасположение
    private static final String URL_GET_LASTPOS = "https://wialon.kiravto.ru/wialon/ajax.html?svc=core/search_item&params={\"id\":__object_id__,\"flags\":\"0x00000401\"}&sid=%s";
    // получить координаты точек в пробегах
    private static final String URL_GET_MARS = "https://wialon.kiravto.ru/wialon/ajax.html?svc=messages/load_interval&params={\"itemId\":__object_id__,\"timeFrom\":__date_beg__,\"timeTo\":__date_end__,\"flags\":1,\"flagsMask\":65281,\"loadCount\":50000}&sid=%s";
    private static final String URL_GET_CLEAR_MARS = "https://wialon.kiravto.ru/wialon/ajax.html?svc=messages/unload&params={}&sid=%s";


    private TransportService trService = new TransportService();



    // получаем данные о пробеге тс
    // invnom - инвентарный номер
    // datebegin - дата начала в формате гггг-мм-дд
    // dateend - дата окончания в формате гггг-мм-дд
    // sid - идентификатор сессии, используется если для получения данных используется существующая сессия, если не задан то будет создана новая
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/gettrack/{invnom}/{datebegin}/{dateend}{sid:(/sid/[^/]+?)?}")
    public Response getObjectTrack(@PathParam("invnom") String invnom,
                                   @PathParam("datebegin") String datebegin,
                                   @PathParam("dateend") String dateend,
                                   @PathParam("sid") String sid) {

        ///////////////////////////////////////////////////
        // тут всякие разные проверки переменных
        if ((invnom == null) || (invnom.equals(""))) {
            return Response.ok("{\"status\":\"error\", \"description\":\"object invnom is empty\"}").build();
        }

        if ((datebegin == null) || (datebegin.equals(""))) {
            return Response.ok("{\"status\":\"error\", \"description\":\"datebegin is empty\"}").build();
        }

        if ((dateend == null) || (dateend.equals(""))) {
            return Response.ok("{\"status\":\"error\", \"description\":\"dateend is empty\"}").build();
        }

        String wlnId = null;

        Transport searchTr = trService.findTransportByInvnom(invnom);

        if (searchTr == null) {
            return Response.ok("{\"status\":\"error\", \"description\":\"объект с инв номером " + invnom + " не найден в базе данных\"}").build();
        }

        // дату начала и окончания необходимо преобразовать в unix формат
        Date pDateBegin = null;
        Date pDateEnd = null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatFull = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");



//        format.setTimeZone(java.util.TimeZone.getTimeZone("GMT+3"));
        format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

//        log.warn("time zone");
//        log.warn(format.getTimeZone().toString());

//      для получения значений даты будем использовать временную зону UTC
//      и корректировать на минус три часа


        try {
            if (datebegin.length() == 10) {
                pDateBegin = format.parse(datebegin);
            } else {
                pDateBegin = formatFull.parse(datebegin);
            };

            if (dateend.length() == 10) {
                pDateEnd = format.parse(dateend);
            } else {
                pDateEnd = formatFull.parse(dateend);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            log.warn("ошибка получения периода. дата начала: " + datebegin + ", дата окончания: " + dateend);
            return Response.ok("{\"status\":\"error\", \"description\":\"ошибка получения периода, дата начала: " + datebegin + ", дата окончания: " + dateend + "\"}").build();
        }

        // часовой пояс
        int timeZone = 3;
        // преобразуем даты в юникс формат и корректируем с учетом часового пояса в UTC
        long lBeginTime = pDateBegin.getTime() / 1000 - 3600 * timeZone;
        long lEndTime = pDateEnd.getTime() / 1000 - 3600 * timeZone + 86399;

//        log.warn("time convert");
//        log.warn(String.valueOf(lBeginTime));
//        log.warn(String.valueOf(lEndTime));


        String reportId = "2352";
        String templateId = "2"; // 1САТХ МРСК


        //////////////////////////////////////////////////////
        // получим ид сессии
        //////////////////////////////////////////////////////
        // если значение sid явно передано то оно передается в переменную
        // со значением в виде /sid/25b134f557133e6bcc5943fd4bd4df01
        // поэтому его надо вытаскивать через split

        //log.warn("sid " + sid);

        if ((sid == null) || (sid.equals("")))  {
            sid = WebRequestUtil.getSID();
        } else {
            // попытаемся очистить сессию
            String[] sidArr = sid.split("/");
            if (sidArr.length == 3) {
//                log.warn("sid " + sidArr[2]);
                sid = sidArr[2];
                WebRequestUtil.getDataFromWln(URL_CLEAR_SESSION, sid);
            } else {
                sid = WebRequestUtil.getSID();
            }

        }

        // очистим сессию
        //WebRequestUtil.getDataFromWln(URL_CLEAR_SESSION, sid);

        //////////////////////////////////////////////////////
        // получим данные таблиц
        //////////////////////////////////////////////////////
        String url = URL_GET_TRACK.replace("__report_id__", reportId);
        url = url.replace("__template_id__", templateId);
        url = url.replace("__object_id__", searchTr.getWlnid());
        url = url.replace("__date_begin__", String.valueOf(lBeginTime));
        url = url.replace("__date_end__", String.valueOf(lEndTime));

        //log.warn(url);

        String result = WebRequestUtil.getDataFromWln(url, sid);
        if (result == null) {
            return Response.ok("{\"status\":\"error\", \"description\":\"сервер вернул ошибку при попытке получения результатов отчета\"}").build();
        }

        // прочитаем ответ, если не ошибка то продолжим далее
        JsonObject resJsonObj = new JsonParser().parse(result).getAsJsonObject();
        // проверка на ошибку
        // произошла ошибка
        if (resJsonObj.has("error")) {
            log.warn("ошибка при получении данных таблиц пробегов, код ошибки " + resJsonObj.get("error").getAsString());
            return Response.ok("{\"status\":\"error\", \"description\":\"ошибка при получении данных таблиц пробегов, код ошибки " + resJsonObj.get("error").getAsString() + "\"}").build();
        }

        // читаем данные таблицы ответа
        JsonObject repResultJsonObj = resJsonObj.getAsJsonObject("reportResult");
        JsonArray tablesJsonArr = repResultJsonObj.getAsJsonArray("tables");

        String tableIndex = null; // номер таблицы
        String cntRows = null;  // количество строк

        for (int i = 0; i < tablesJsonArr.size(); i++) {

            JsonObject tableJsonObj = tablesJsonArr.get(i).getAsJsonObject();
            if (tableJsonObj.get("name").getAsString().equals("unit_trips")) {
                tableIndex = String.valueOf(i);
                cntRows = tableJsonObj.get("rows").getAsString();
            }

        }

        // проверка
        if ((tableIndex == null) || (cntRows == null)) {
            log.warn("при получении данных таблиц пробегов не найдена таблица с именем unit_trips");
            return Response.ok("{\"status\":\"error\", \"description\":\"при получении данных таблиц пробегов не найдена таблица с именем unit_trips\"}").build();
        }

        //////////////////////////////////////////////////////
        // прочитаем таблицу пробегов
        //////////////////////////////////////////////////////
        url = URL_GET_TABLE.replace("__table_index__", tableIndex);
        url = url.replace("__first_row__", "0");
        url = url.replace("__last_row__", cntRows);

//        log.warn(url);

        result = WebRequestUtil.getDataFromWln(url, sid);
        if (result == null) {
            return Response.ok("{\"status\":\"error\", \"description\":\"сервер вернул ошибку при попытке получения таблиц отчета\"}").build();
        }

        // прочитаем ответ, если не ошибка то продолжим далее
        JsonArray resArr = new JsonParser().parse(result).getAsJsonArray();


        //////////////////////////////////////////////////
        // настроим форматы
        //////////////////////////////////////////////////
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
        simpleDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

        // для преобразования float
        Locale locale = new Locale("en", "US");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        symbols.setDecimalSeparator('.');

        DecimalFormat df = new DecimalFormat("0.00", symbols);
        //////////////////////////////////////////////////


        // итоговые значения
        float fTotalDuration = 0;
        float fTotalMotohours = 0;
        float fTotalProbeg = 0;
        float fTotalFuelRate = 0;

        float fDuration = 0;
        float fMotohours = 0;
        float fProbeg = 0;
        float fFuelRate = 0;





        //////////////////////////////////
        // массив json для детальных данных
        JsonArray detailArr = new JsonArray();

        for (int i = 0; i < resArr.size(); i++) {
            JsonObject oneTrack = resArr.get(i).getAsJsonObject();
//            JsonArray oneTrackPropArr = oneTrack.get("c").getAsJsonArray();

            // тут количество записей в расшифровке
//            String detailRowsCnt = oneTrack.get("d").getAsString();

            //////////////////////////////////////////////////////
            // получим записи расшифровки
            //////////////////////////////////////////////////////
            url = URL_GET_TABLEROW.replace("__table_index__", tableIndex);
            url = url.replace("__row_index__", String.valueOf(i));

            result = WebRequestUtil.getDataFromWln(url, sid);
            if (result == null) {
                return Response.ok("{\"status\":\"error\", \"description\":\"сервер вернул ошибку при попытке получения строк таблицы отчета\"}").build();
            }

//            log.warn("ответ сервера");
//            log.warn(result);

            // прочитаем ответ, если не ошибка то продолжим далее
            JsonArray resRowArr = new JsonParser().parse(result).getAsJsonArray();
            for (int ii = 0; ii < resRowArr.size(); ii++) {

                JsonObject oneResRow = resRowArr.get(ii).getAsJsonObject();

                JsonArray oneResRowArr = oneResRow.get("c").getAsJsonArray();

                // дата сообщения в формате 2020-05-06 03:40:57
                String sTrackTime = oneResRowArr.get(0).getAsString();

                // получаем значения координат и времени начала трека
                JsonObject placeJson = oneResRowArr.get(1).getAsJsonObject();
                String sTrackBegTime = placeJson.get("v").getAsString();
                String sTrackBegX = placeJson.get("x").getAsString();
                String sTrackBegY = placeJson.get("y").getAsString();

                // значения координат и времени окончания трека
                placeJson = oneResRowArr.get(3).getAsJsonObject();
                String sTrackEndTime = placeJson.get("v").getAsString();
                String sTrackEndX = placeJson.get("x").getAsString();
                String sTrackEndY = placeJson.get("y").getAsString();


                // значения названия и координат замечательного места начала
                placeJson = oneResRowArr.get(2).getAsJsonObject();
                String sPlaceBeg = placeJson.get("t").getAsString();
                String sPlaceBegX = placeJson.get("x").getAsString();
                String sPlaceBegY = placeJson.get("y").getAsString();

                // значения названия и координат замечательного места окончания
                placeJson = oneResRowArr.get(4).getAsJsonObject();
                String sPlaceEnd = placeJson.get("t").getAsString();
                String sPlaceEndX = placeJson.get("x").getAsString();
                String sPlaceEndY = placeJson.get("y").getAsString();

                // длительность
                String dDuration = oneResRowArr.get(5).getAsString();
                // моточасы
                String dMotohours = oneResRowArr.get(6).getAsString();
                // пробеги
                String dPprobeg = oneResRowArr.get(7).getAsString();

                // максимальная скорость, координаты фиксации
                String sMaxSpeed = "0";
                String sMaxSpeedX = "-";
                String sMaxSpeedY = "-";

                if (oneResRowArr.get(8).isJsonObject()) {
                    placeJson = oneResRowArr.get(8).getAsJsonObject();
                    sMaxSpeed = placeJson.get("t").getAsString();
                    sMaxSpeedX = placeJson.get("x").getAsString();
                    sMaxSpeedY = placeJson.get("y").getAsString();
                }

                // потрачено по нормам
                String sFuelRate = oneResRowArr.get(9).getAsString();

                // средний расход
                String sFuelAvgRate = oneResRowArr.get(10).getAsString();


                // дата начала периода в юниксе
                String dBeg = oneResRow.get("t1").getAsString();
                // дата окончания периода в юниксе
                String dEnd = oneResRow.get("t2").getAsString();

//                // преобразуем дату в тип Date
//                Date dateBegin = new Date(Long.valueOf(dBeg) * 1000 + timeZone * 3600);
//                Date dateEnd = new Date(Long.valueOf(dEnd) * 1000 + timeZone * 3600);

//                String outPutStr = "время нач " + simpleDateFormat.format(dateBegin) + " время кон " + simpleDateFormat.format(dateEnd);
//                outPutStr = outPutStr + " длит " + dDuration + " моточ " + dMotohours + " пробег " + dPprobeg;
//
//                log.warn(outPutStr);


                // получим значения часов и пробегов в числовом виде
                fDuration = convertTime(dDuration);
                fMotohours = convertTime(dMotohours);
                fProbeg = 0;
                fFuelRate = 0;

                try {
                    fProbeg = Float.valueOf(dPprobeg.replace(" km", ""));
                } catch (Exception e) {
                    log.warn("ошибка преобразования пробега в тип float: " + dPprobeg);
                }

                try {
                    fFuelRate = Float.valueOf(sFuelRate.replace(" lt", ""));
                } catch (Exception e) {
                    log.warn("ошибка преобразования расхода топлива в тип float: " + sFuelRate);
                }


                // суммируем значения
                fTotalDuration = fTotalDuration + fDuration;
                fTotalMotohours = fTotalMotohours + fMotohours;
                fTotalProbeg = fTotalProbeg + fProbeg;
                fTotalFuelRate = fTotalFuelRate + fFuelRate;

                //////////////////////////////////////////////////////
                // элемент детализации
                //////////////////////////////////////////////////////
                JsonObject detailElem = new JsonObject();

                detailElem.addProperty("tracktime", sTrackTime);

                detailElem.addProperty("trackbegtime", simpleDateFormat.format(ConverterUtil.convertUnixToDate(sTrackBegTime, timeZone)));
                detailElem.addProperty("trackbegx", sTrackBegX);
                detailElem.addProperty("trackbegy", sTrackBegY);

                detailElem.addProperty("trackendtime", simpleDateFormat.format(ConverterUtil.convertUnixToDate(sTrackEndTime, timeZone)));
                detailElem.addProperty("trackendx", sTrackEndX);
                detailElem.addProperty("trackendy", sTrackEndY);


                detailElem.addProperty("datebeg", simpleDateFormat.format(ConverterUtil.convertUnixToDate(dBeg, timeZone)));
                detailElem.addProperty("dateend", simpleDateFormat.format(ConverterUtil.convertUnixToDate(dEnd, timeZone)));

                detailElem.addProperty("placebeg", sPlaceBeg);
                detailElem.addProperty("placebegx", sPlaceBegX);
                detailElem.addProperty("placebegy", sPlaceBegY);

                detailElem.addProperty("placeend", sPlaceEnd);
                detailElem.addProperty("placeendx", sPlaceEndX);
                detailElem.addProperty("placeendy", sPlaceEndY);


                detailElem.addProperty("duration", df.format(fDuration));
                detailElem.addProperty("motohours", df.format(fMotohours));
//                detailElem.addProperty("probeg", df.format(fProbeg));
                detailElem.addProperty("probeg", df.format(fProbeg));

                detailElem.addProperty("maxspeed", sMaxSpeed);
                detailElem.addProperty("maxspeedx", sMaxSpeedX);
                detailElem.addProperty("maxspeedy", sMaxSpeedY);

                detailElem.addProperty("fuelrate", sFuelRate);
                detailElem.addProperty("fuelavgrate", sFuelAvgRate);


                detailArr.add(detailElem);

//                log.warn(df.format(fProbeg));
//                log.warn(String.format("%.2f", fProbeg));

            }


            // формат ответа
            // {
            //      status: OK
            //      content : {
//                        regnom :
//                        wlnid :
//                        invnom :
//                        diration :
//                        motohours :
//                        probeg :
//                        detail : [
//                              {
//                                  DateBeg:
//                                  DateEnd:
//                                  diration :
//                                  motohours :
//                                  probeg :
//                                  placebeg :
//                                  placeend :

//                              },
//                              {},{},{}

//                        ]
//
            //      }
            //
            // }


        }


        ///////////////////////////////////////////////////////
        // здесь начнем формирование JSON объекта ответа
        ///////////////////////////////////////////////////////

        JsonObject contentObj = new JsonObject();

        contentObj.addProperty("regnom", searchTr.getRegistrationplate());
        contentObj.addProperty("wlnid", searchTr.getWlnid());
        contentObj.addProperty("invnom", searchTr.getAtinvnom());
        contentObj.addProperty("duration", df.format(fTotalDuration));
        contentObj.addProperty("motohours", df.format(fTotalMotohours));
        contentObj.addProperty("probeg", df.format(fTotalProbeg));
        contentObj.addProperty("fuelrate", df.format(fTotalFuelRate));

        contentObj.add("detail", detailArr);

        JsonObject answerObj = new JsonObject();
        answerObj.addProperty("status", "OK");

        if (sid != null) {
            answerObj.addProperty("sid", sid);
        } else {
            answerObj.addProperty("sid", "");
        }

        answerObj.add("content", contentObj);


        Gson gson = new Gson();

        return Response.ok(gson.toJson(answerObj)).build();

        // запрос
        // track/gettrack/76442/2020-04-19/2020-04-19
        // wl/refreshobjbywlnid/1187
    }


    // получаем данные о месторасположении тс
    // invnom - инвентарный номер
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getlastpos/{invnom}")
    public Response getObjectLastPosition(@PathParam("invnom") String invnom) {


        ///////////////////////////////////////////////////
        // тут всякие разные проверки переменных
        if ((invnom == null) || (invnom.equals(""))) {
            return Response.ok("{\"status\":\"error\", \"description\":\"object invnom is empty\"}").build();
        }

        Transport searchTr = trService.findTransportByInvnom(invnom);

        if (searchTr == null) {
            return Response.ok("{\"status\":\"error\", \"description\":\"объект с инв номером " + invnom + " не найден в базе данных\"}").build();
        }

        String url = URL_GET_LASTPOS.replace("__object_id__", searchTr.getWlnid());

        String result = WebRequestUtil.getDataFromWln(url, null);
        if (result == null) {
            return Response.ok("{\"status\":\"error\", \"description\":\"сервер вернул ошибку при попытке получения таблиц отчета\"}").build();
        }

//        log.warn("last position");
//        log.warn(result);

        // track/getlastpos/30700
        // {"item":{"nm":"0072КХ 43 RUS","cls":2,"id":2693,"mu":0,"pos":{"t":1587731070,"f":1073741831,"lc":0,"y":57.567595,"x":49.9365633333,"c":58,"z":97.3,"s":3,"sc":14},"lmsg":{"t":1587731070,"f":1073741831,"tp":"ud","pos":{"y":57.567595,"x":49.9365633333,"c":58,"z":97.3,"s":3,"sc":14},"i":128,"o":0,"lc":0,"rt":0,"p":{"msg_type":"A","proto":"FLEX1.0","msg_number":26633,"event_code":4656,"status":0,"modules_st":169,"modules_st2":0,"gsm":16,"last_valid_time":1587731069,"nav_rcvr_state":1,"valid_nav":1,"sats":14,"mileage":948.234619141,"pwr_ext":8.655,"pwr_int":3.454,"engine_hours":66.1447222222}},"uacl":880333093887},"flags":1025}
        // https://sdk.wialon.com/wiki/ru/sidebar/remoteapi/apiref/format/unit#poslednee_soobschenie_i_mestopolozhenie

        // прочитаем ответ, если не ошибка то продолжим далее
        JsonObject resJsonObj = new JsonParser().parse(result).getAsJsonObject();
        // проверим что вернул серверу
        if (resJsonObj.has("error")) {
            log.warn("ошибка при получении данных таблиц пробегов, код ошибки " + resJsonObj.get("error").getAsString());
            return Response.ok("{\"status\":\"error\", \"description\":\"ошибка при получении данных таблиц пробегов, код ошибки " + resJsonObj.get("error").getAsString() + "\"}").build();
        }

        // читаем данные таблицы ответа
        JsonObject itemJsonObj = resJsonObj.getAsJsonObject("item");
        JsonObject itemPosJsonObj = itemJsonObj.getAsJsonObject("pos");

//        String objName = itemJsonObj.get("nm").getAsString();

        // готовим ответ
        JsonObject answJson = new JsonObject();
        JsonObject contentJson = new JsonObject();

        answJson.addProperty("status", "ok");

        contentJson.addProperty("name", itemJsonObj.get("nm").getAsString());


        String sTime = ConverterUtil.convertUnixToHuman(itemPosJsonObj.get("t").getAsString(), 3);

        // тут время местное !!!
        contentJson.addProperty("time", sTime);

        contentJson.addProperty("x", itemPosJsonObj.get("x").getAsString());
        contentJson.addProperty("y", itemPosJsonObj.get("y").getAsString());
        contentJson.addProperty("z", itemPosJsonObj.get("z").getAsString());
        contentJson.addProperty("sat", itemPosJsonObj.get("sc").getAsString());
        contentJson.addProperty("speed", itemPosJsonObj.get("s").getAsString());
        contentJson.addProperty("course", itemPosJsonObj.get("c").getAsString());

//        "t":<uint>,		/* время UTC */
//        "y":<double>,		/* широта */
//        "x":<double>,		/* долгота */
//        "z":<double>,		/* высота над уровнем моря */
//        "s":<int>,		/* скорость */
//        "c":<int>,		/* курс */
//        "sc":<int>		/* спутники */

        answJson.add("content", contentJson);

        Gson gson = new Gson();

        return Response.ok(gson.toJson(answJson)).build();
    }

    // получает строковое представление количество часов вида "0.00" из строки вида "00:00:00"
    private float convertTime(String sDlit) {

//        DecimalFormat df = new DecimalFormat("0.00");

        if (sDlit.isEmpty()) {
            return 0;
        }

        String[] arrStr = sDlit.split(":");

        if (arrStr.length != 3) {
            return 0;
        }

        float timeInHours = 0;

        try {
            float hour = 0;
            if (!arrStr[0].isEmpty()) {
                hour = Float.valueOf(arrStr[0]);
            }

            float min = 0;
            if (!arrStr[1].isEmpty()) {
                min = Float.valueOf(arrStr[1]);
            }

            float sec = 0;
            if (!arrStr[2].isEmpty()) {
                sec = Float.valueOf(arrStr[2]);
            }

            timeInHours = hour + min / 60; // на секунды забьем
        } catch (Exception e) {

        }
//        return df.format(timeInHours);
        return timeInHours;
    }




    // получение точек для построения трека - маршрута движения тс
    // пока лежит тут в тестовой части
    // нужно продумать передачу параметров даты начала и даты окончания со временем, чтобы получать точки по поездкам
    // возможно нужен будет параметр, позволяющий уменьшать количество передаваемых точек, например количество пропусков
    @POST
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


        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
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
//        long lEndTime = pDateEnd.getTime() / 1000 - 3600 * localtimeZone + 86399;
        long lEndTime = pDateEnd.getTime() / 1000 - 3600 * localtimeZone;


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

        // {
        //  "status":"ok",
        //  "count": 1505151,
        //  "content":[{"x":"", "y":"", "course":"", "speed":"", "time":""}]
        // }

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

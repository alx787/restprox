package ru.ath.alx.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import ru.ath.alx.dao.TransportService;
import ru.ath.alx.model.Transport;
import ru.ath.alx.util.WebRequestUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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


    private TransportService trService = new TransportService();



    // получаем данные о пробеге тс
    // invnom - инвентарный номер
    // datebegin - дата начала в формате гггг-мм-дд
    // dateend - дата окончания в формате гггг-мм-дд
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/gettrack/{invnom}/{datebegin}/{dateend}")
    public Response getObjectTrack(@PathParam("invnom") String invnom, @PathParam("datebegin") String datebegin, @PathParam("dateend") String dateend) {

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
//        format.setTimeZone(java.util.TimeZone.getTimeZone("GMT+3"));
        format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

//        log.warn("time zone");
//        log.warn(format.getTimeZone().toString());

//      для получения значений даты будем использовать временную зону UTC
//      и корректировать на минус три часа


        try {
            pDateBegin = format.parse(datebegin);
            pDateEnd = format.parse(dateend);
        } catch (ParseException e) {
            e.printStackTrace();
            log.warn("ошибка получения периода. дата начала: " + datebegin + ", дата окончания: " + dateend);
            return Response.ok("{\"status\":\"error\", \"description\":\"ошибка получения периода, дата начала: " + datebegin + ", дата окончания: " + dateend + "\"}").build();
        }

        // часовой пояс
        int timeZone = 3;

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
        String sid = WebRequestUtil.getSID();

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

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");


        float fTotalDuration = 0;
        float fTotalMotohours = 0;
        float fTotalProbeg = 0;

        float fDuration = 0;
        float fMotohours = 0;
        float fProbeg = 0;


        // для преобразования float
        Locale locale = new Locale("en", "US");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        symbols.setDecimalSeparator('.');

        DecimalFormat df = new DecimalFormat("0.00", symbols);



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


            // прочитаем ответ, если не ошибка то продолжим далее
            JsonArray resRowArr = new JsonParser().parse(result).getAsJsonArray();
            for (int ii = 0; ii < resRowArr.size(); ii++) {

                JsonObject oneResRow = resRowArr.get(ii).getAsJsonObject();

                JsonArray oneResRowArr = oneResRow.get("c").getAsJsonArray();

                // получаем значения
                // длительность
                String dDuration = oneResRowArr.get(5).getAsString();
                // моточасы
                String dMotohours = oneResRowArr.get(6).getAsString();
                // пробеги
                String dPprobeg = oneResRowArr.get(7).getAsString();


                // дата начала периода в юниксе
                String dBeg = oneResRow.get("t1").getAsString();
                // дата окончания периода в юниксе
                String dEnd = oneResRow.get("t2").getAsString();

                // преобразуем дату в тип Date
                Date dateBegin = new Date(Long.valueOf(dBeg) * 1000 + timeZone * 3600);
                Date dateEnd = new Date(Long.valueOf(dEnd) * 1000 + timeZone * 3600);

//                String outPutStr = "время нач " + simpleDateFormat.format(dateBegin) + " время кон " + simpleDateFormat.format(dateEnd);
//                outPutStr = outPutStr + " длит " + dDuration + " моточ " + dMotohours + " пробег " + dPprobeg;
//
//                log.warn(outPutStr);


                // получим значения часов и пробегов в числовом виде
                fDuration = convertTime(dDuration);
                fMotohours = convertTime(dMotohours);
                fProbeg = 0;
                try {
                    fProbeg = Float.valueOf(dPprobeg.replace(" km", ""));
                } catch (Exception e) {

                }

                // суммируем значения
                fTotalDuration = fTotalDuration + fDuration;
                fTotalMotohours = fTotalMotohours + fMotohours;
                fTotalProbeg = fTotalProbeg + fProbeg;

                //////////////////////////////////////////////////////
                // элемент детализации
                //////////////////////////////////////////////////////
                JsonObject detailElem = new JsonObject();
                detailElem.addProperty("datebeg", simpleDateFormat.format(dateBegin));
                detailElem.addProperty("dateend", simpleDateFormat.format(dateEnd));
                detailElem.addProperty("duration", df.format(fDuration));
                detailElem.addProperty("motohours", df.format(fMotohours));
//                detailElem.addProperty("probeg", df.format(fProbeg));
                detailElem.addProperty("probeg", df.format(fProbeg));

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
        contentObj.add("detail", detailArr);

        JsonObject answerObj = new JsonObject();
        answerObj.addProperty("status", "OK");
        answerObj.add("content", contentObj);


        Gson gson = new Gson();

        return Response.ok(gson.toJson(answerObj)).build();

        // запрос
        // track/gettrack/76442/2020-04-19/2020-04-19
        // wl/refreshobjbywlnid/1187
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

}

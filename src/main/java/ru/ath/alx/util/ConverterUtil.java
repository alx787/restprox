package ru.ath.alx.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import ru.ath.alx.model.Transport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ConverterUtil {

    private static final Logger log = Logger.getLogger(ConverterUtil.class);

    public static Transport getTransportFromJson(JsonObject jsonDataObj) {

        Transport newTr = new Transport();

        // инициализируем все

        // реквизиты
        newTr.setVehicletype("");
        newTr.setVin("");
        newTr.setBrand("");
        newTr.setModel("");
        newTr.setProdyear("");
        newTr.setColor("");
        newTr.setEnginemodel("");
        newTr.setPrimaryfueltype("");
        newTr.setEnginepower("");
        newTr.setGrossvehicleweight("");
        newTr.setRegistrationplate("");

        // дополнительные поля
        newTr.setAtinvnom("");
        newTr.setAtinstalldate("");
        newTr.setAtwheelformula("");
        newTr.setAtdepartment("");
        newTr.setAtautocol("");
        newTr.setAtlocation("");
        newTr.setAtbase("");
        newTr.setAtres("");


        // wln id
        newTr.setWlnid(jsonDataObj.get("id").getAsString());
        // гос номер nm
        newTr.setWlnnm(jsonDataObj.get("nm").getAsString());


        // реквизиты
        JsonObject pfldsJsonObj = jsonDataObj.get("pflds").getAsJsonObject();

        Set<Map.Entry<String, JsonElement>> entrySet = pfldsJsonObj.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            JsonObject onePfldsObj = pfldsJsonObj.get(entry.getKey()).getAsJsonObject();

//            String pid = onePfldsObj.get("id").getAsString();
            String pn = onePfldsObj.get("n").getAsString();
            String pv = onePfldsObj.get("v").getAsString();

            switch (pn) {
                case "vehicle_type":
                    newTr.setVehicletype(pv);
                    break;
                case "vin":
                    newTr.setVin(pv);
                    break;
                case "brand":
                    newTr.setBrand(pv);
                    break;
                case "model":
                    newTr.setModel(pv);
                    break;
                case "year":
                    newTr.setProdyear(pv);
                    break;
                case "color":
                    newTr.setColor(pv);
                    break;
                case "engine_model":
                    newTr.setEnginemodel(pv);
                    break;
                case "primary_fuel_type":
                    newTr.setPrimaryfueltype(pv);
                    break;
                case "engine_power":
                    newTr.setEnginepower(pv);
                    break;
                case "gross_vehicle_weight":
                    newTr.setGrossvehicleweight(pv);
                    break;
                case "registration_plate":
                    newTr.setRegistrationplate(pv);
                    break;
                default:

            }

        }

        // произвольные поля
        pfldsJsonObj = jsonDataObj.get("flds").getAsJsonObject();

        entrySet = pfldsJsonObj.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            JsonObject onePfldsObj = pfldsJsonObj.get(entry.getKey()).getAsJsonObject();

//            String pid = onePfldsObj.get("id").getAsString();
            String pn = onePfldsObj.get("n").getAsString().toUpperCase();
            String pv = onePfldsObj.get("v").getAsString();

            switch (pn) {
                case "ИНВ.НОМЕР":
                    newTr.setAtinvnom(pv);
                    break;
                case "ИНВ. НОМЕР":
                    newTr.setAtinvnom(pv);
                    break;
                case "ДАТА УСТАНОВКИ":
                    newTr.setAtinstalldate(pv);
                    break;
                case "КОЛЕСНАЯ ФОРМУЛА":
                    newTr.setAtwheelformula(pv);
                    break;
                case "ПОДРАЗДЕЛЕНИЕ":
                    newTr.setAtdepartment(pv);
                    break;
                case "АВТОКОЛОННА":
                    newTr.setAtautocol(pv);
                    break;
                case "ЗАКРЕПЛЕНИЕ":
                    newTr.setAtlocation(pv);
                    break;
                case "МЕСТО БАЗИРОВАНИЯ":
                    newTr.setAtbase(pv);
                    break;
                case "РЭС":
                    newTr.setAtres(pv);
                    break;

                default:

            }

        }

        return newTr;
    }

    public static JsonObject getJsonObjFromTransport(Transport tr) {
        JsonObject trJson = new JsonObject();

        trJson.addProperty("id", tr.getId());

         // реквизиты
        trJson.addProperty("vehicletype", tr.getVehicletype());
        trJson.addProperty("vin", tr.getVin());
        trJson.addProperty("brand", tr.getBrand());
        trJson.addProperty("model", tr.getModel());
        trJson.addProperty("prodyear", tr.getProdyear());
        trJson.addProperty("color", tr.getColor());
        trJson.addProperty("enginemodel", tr.getEnginemodel());
        trJson.addProperty("primaryfueltype", tr.getPrimaryfueltype());
        trJson.addProperty("enginepower", tr.getEnginepower());
        trJson.addProperty("grossvehicleweight", tr.getGrossvehicleweight());
        trJson.addProperty("registrationplate", tr.getRegistrationplate());

        // дополнительные поля
        trJson.addProperty("atinvnom", tr.getAtinvnom());
        trJson.addProperty("atinstalldate", tr.getAtinstalldate());
        trJson.addProperty("atwheelformula", tr.getAtwheelformula());
        trJson.addProperty("atdepartment", tr.getAtdepartment());
        trJson.addProperty("atautocol", tr.getAtautocol());
        trJson.addProperty("atlocation", tr.getAtlocation());
        trJson.addProperty("atbase", tr.getAtbase());
        trJson.addProperty("atres", tr.getAtres());

        return trJson;
    }

    // преобразуем дату из Unix timestamp в тип Date
    // sDate - строка даты в юникс формате
    // timeZone - временная зона
    // вобщем получаем дату типа Date из даты формата виалона
    public static Date convertUnixToDate(String sDate, int timeZone) {
        return new Date(Long.valueOf(sDate) * 1000 + timeZone * 3600 * 1000);
    }

    // преобразование времени виалона в дату формата yyyy.MM.dd - HH:mm:ss
    // sDate - время из виалона в юникс формате
    // timeZone - временная зона
    public static String convertUnixToHuman(String sDate, int timeZone) {
        String sTime = "-";
        try {
            // получаем значение времени в UTC
            long unixTime = Long.valueOf(sDate);
//            log.warn("time from request " + String.valueOf(unixTime));

            // часовой пояс
//            int timeZone = 3;
            // преобразуем в юникс тайм формат, сразу же корректируем время с учетом временной зоны
            // не умножаем на 1000 время и корректировку
            unixTime = unixTime * 1000 + 3600 * timeZone * 1000;
//            log.warn("time transform " + String.valueOf(unixTime));

            // получаем дату
            Date dTime = new Date(unixTime);
//            log.warn("date to string " + dTime.toString());

            // шаблон форматирования
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss");
            // настроим шаблон чтобы он не донастроил формат с учетом временных зон
            format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            // строковое представление
            sTime = format.format(dTime);
        } catch (Exception e) {
            log.warn("ошибка при преобразовании времени юникс формата из string в long");
        }

        return sTime;
    }
}

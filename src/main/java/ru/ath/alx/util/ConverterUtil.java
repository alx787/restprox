package ru.ath.alx.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import ru.ath.alx.model.Transport;

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
}

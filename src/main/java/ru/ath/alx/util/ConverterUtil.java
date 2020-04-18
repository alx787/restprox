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

        // проверить на заполненность реквизиты
        if (newTr.getVehicletype() == null) {
            newTr.setVehicletype("");
        }

        if (newTr.getVin() == null) {
            newTr.setVin("");
        }

        if (newTr.getBrand() == null) {
            newTr.setBrand("");
        }

        if (newTr.getModel() == null) {
            newTr.setModel("");
        }

        if (newTr.getProdyear() == null) {
            newTr.setProdyear("");
        }

        if (newTr.getColor() == null) {
            newTr.setColor("");
        }

        if (newTr.getEnginemodel() == null) {
            newTr.setEnginemodel("");
        }

        if (newTr.getPrimaryfueltype() == null) {
            newTr.setPrimaryfueltype("");
        }

        if (newTr.getEnginepower() == null) {
            newTr.setEnginepower("");
        }

        if (newTr.getGrossvehicleweight() == null) {
            newTr.setGrossvehicleweight("");
        }

        if (newTr.getRegistrationplate() == null) {
            newTr.setRegistrationplate("");
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

        // проверить на заполненность дополнительные поля
        if (newTr.getAtinvnom() == null) {
            newTr.setAtinvnom("");
        }

        if (newTr.getAtinstalldate() == null) {
            newTr.setAtinstalldate("");
        }

        if (newTr.getAtwheelformula() == null) {
            newTr.setAtwheelformula("");
        }

        if (newTr.getAtdepartment() == null) {
            newTr.setAtdepartment("");
        }

        if (newTr.getAtautocol() == null) {
            newTr.setAtautocol("");
        }

        if (newTr.getAtlocation() == null) {
            newTr.setAtlocation("");
        }

        if (newTr.getAtbase() == null) {
            newTr.setAtbase("");
        }

        if (newTr.getAtres() == null) {
            newTr.setAtres("");
        }

        return newTr;
    }
}
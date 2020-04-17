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

        // произвольные поля

        return newTr;
    }
}

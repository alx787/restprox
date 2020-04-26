package ru.ath.alx.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import ru.ath.alx.dao.AuthService;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class WebRequestUtil {

    private static final Logger log = Logger.getLogger(WebRequestUtil.class);

    private static AuthService authService = new AuthService();
    // получить сид
    private static final String URL_GET_SID = "https://wialon.kiravto.ru/wialon/ajax.html?svc=token/login&params={\"token\":\"%s\"}";


    private static String sendRequest(String url) {
        HttpsURLConnection connection = null;
        StringBuilder result = new StringBuilder();
        boolean wasError = false;

        try {
            connection = (HttpsURLConnection) new URL(url).openConnection();
//            connection.setRequestProperty("charset", StandardCharsets.UTF_8.displayName());
            connection.setRequestProperty("charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //log.warn(connection.toString());

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


    public static String getSID() {
        // получим токен
        String token = authService.getToken();
        // произошла ошибка
        if ((token == null) || (token.equals(""))) {
            log.warn("ошибка при получении токена - пустой или равен null");
            return null;
        }



        String res = sendRequest(String.format(URL_GET_SID, token));

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

    // получение данных из виалона
    // sid - идентификатор сессии, если он заполнен то используется именно он, если не заполнен то получаем новый и используем его
    public static String getDataFromWln(String url, String sid) {

        String pSid = "";

        if ((sid == null) || (sid.equals(""))) {
            pSid = getSID();
        } else {
            pSid = sid;
        }

        return sendRequest(String.format(url, pSid));
    }
}

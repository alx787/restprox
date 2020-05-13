package ru.ath.alx.util;

import ru.ath.alx.dao.UserService;
import ru.ath.alx.model.User;

import java.security.SecureRandom;

public class AuthUtil {

    private static UserService userService = new UserService();

    public static boolean checkToken(String userid, String token) {

        int intUserId = 0;
        try {
            intUserId = Integer.valueOf(userid);
        } catch (Exception e) {
            return false;
        }

        User user = userService.findUserByUserIdAndToken(intUserId, token);

        if (user != null) {
            return true;
        }

        return false;
    }


    public static boolean checkLoginPass(String login, String pass){
        User user = userService.findUserByLoginAndHash(login, pass);

        if (user == null) {
            return false;
        }

        // при проверке ставим новый токен
        user.setPasstoken(generateToken());
        userService.update(user);

        return true;
    }


    private static String generateToken() {

        SecureRandom random = new SecureRandom();

        String randomString = "";
        long longToken = 0;

        while (randomString.length() < 64) {
            longToken = Math.abs( random.nextLong() );
            randomString = randomString + Long.toString(longToken, 16);
        }

        if (randomString.length() > 64) {
            randomString = randomString.substring(0, 63);
        }

        return randomString;
    }
}

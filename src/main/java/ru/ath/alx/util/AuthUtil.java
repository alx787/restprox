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
        user.setPasshash(generateToken());
        userService.update(user);

        return true;
    }


    private static String generateToken() {
        SecureRandom random = new SecureRandom();

        long longTokenFirst = Math.abs( random.nextLong() );
        long longTokenMiddle = Math.abs( random.nextLong() );
        long longTokenLast = Math.abs( random.nextLong() );

        String randomString = Long.toString(longTokenFirst, 24) + Long.toString(longTokenMiddle, 24) + Long.toString(longTokenLast, 16);
        return randomString;
    }
}

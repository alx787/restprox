package ru.ath.alx.util;

import java.security.SecureRandom;

public class AuthUtil {
    public static String generateToken() {
        SecureRandom random = new SecureRandom();

        long longToken = Math.abs( random.nextLong() );
        String randomString = Long.toString( longToken, 24 );
        return randomString;
    }
}

package ru.ath.alx.dao;

public class AuthService {
    private static AuthDaoImpl authDao;

    public AuthService(AuthDaoImpl authDao) {
        authDao = new AuthDaoImpl();
    }


}

package ru.ath.alx.dao;

public class AuthService {

    private static AuthDaoImpl authDao;

    public AuthService() {
        authDao = new AuthDaoImpl();
    }

    public String getToken() {
        return authDao.getToken();
    }


}

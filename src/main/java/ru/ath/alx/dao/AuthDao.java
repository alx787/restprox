package ru.ath.alx.dao;

import ru.ath.alx.model.Auth;

public interface AuthDao {
    void create(Auth auth);
    void update(Auth auth);
    int countRow();
    String getToken();
}

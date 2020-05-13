package ru.ath.alx.dao;

import ru.ath.alx.model.User;

public interface UserDao {

    void update(User user);

    User findUserByLoginAndHash(String login, String hash);
    User findUserByUserIdAndToken(int userId, String token);
}

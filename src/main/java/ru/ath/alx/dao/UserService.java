package ru.ath.alx.dao;

import ru.ath.alx.model.User;

public class UserService {
    private static UserDaoImpl userDao;

    public UserService() {
        userDao = new UserDaoImpl();
    }

    public void update(User user){
        userDao.update(user);
    };

    public User findUserByLoginAndHash(String login, String hash){
        return userDao.findUserByLoginAndHash(login, hash);
    };

    public User findUserByUserIdAndToken(int userId, String token){
        return userDao.findUserByUserIdAndToken(userId, token);
    };
}

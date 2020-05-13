package ru.ath.alx.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.ath.alx.model.User;
import ru.ath.alx.util.HibernateUtil;

import java.util.List;

public class UserDaoImpl implements UserDao {
    @Override
    public void update(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();
    }

    @Override
    public User findUserByLoginAndHash(String login, String hash) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("FROM User U WHERE U.name = :login AND U.passhash = :passhash");
        query.setParameter("login", login);
        query.setParameter("passhash", hash);
        List<User> users = query.list();
        session.close();

        if (users.size() == 1) {
            return users.get(0);
        }

        return null;
    }

    @Override
    public User findUserByUserIdAndToken(int userId, String token) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("FROM User U WHERE U.id = :userid AND U.passtoken = :token");
        query.setParameter("userid", userId);
        query.setParameter("token", token);
        List<User> users = query.list();
        session.close();

        if (users.size() == 1) {
            return users.get(0);
        }

        return null;
    }
}

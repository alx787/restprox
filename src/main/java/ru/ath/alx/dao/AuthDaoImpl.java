package ru.ath.alx.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.ath.alx.model.Auth;
import ru.ath.alx.util.HibernateUtil;

import java.util.List;

public class AuthDaoImpl implements AuthDao {
    @Override
    public void create(Auth auth) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(auth);
        session.getTransaction().commit();
    }

    @Override
    public void update(Auth auth) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(auth);
        session.getTransaction().commit();
    }

    @Override
    public int countRow() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        String sql = "select count(*) from auth";
        List<Object[]> objList = session.createSQLQuery(sql).list();
        session.close();

        int rowcnt = (Integer) objList.get(0)[0];

        return rowcnt;
    }


    @Override
    public String getToken() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Auth> auths = (List<Auth>) session.createQuery("FROM Auth ").setMaxResults(1).list();
        session.close();
        if (auths.size() != 1) {
            return null;
        }

        return auths.get(0).getToken();
    }
}

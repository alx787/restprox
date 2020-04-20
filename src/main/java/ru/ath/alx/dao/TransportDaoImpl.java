package ru.ath.alx.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.StringType;
import ru.ath.alx.model.Transport;
import ru.ath.alx.util.HibernateUtil;

import java.util.List;


public class TransportDaoImpl implements TransportDao {

    @Override
    public void create(Transport transport) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(transport);
        session.getTransaction().commit();
    }

    @Override
    public void update(Transport transport) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(transport);
        session.getTransaction().commit();
    }

    @Override
    public void delete(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Transport transport = findTransportById(id);
        session.delete(transport);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public List<Transport> findTransportList() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Transport> transports = (List<Transport>) session.createQuery("FROM Transport").list();
        session.close();
        return transports;
    }

    @Override
    public Transport findTransportById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transport transport = (Transport) session.get(Transport.class, id);
        session.close();
        return transport;
    }

    @Override
    public Transport findTransportByInvnom(String invnom) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("FROM Transport T WHERE T.atinvnom = :paraminv");
        query.setParameter("paraminv", invnom);
        List<Transport> transports = query.list();
        session.close();

        if (transports.size() == 1) {
            return transports.get(0);
        }

        return null;
    }

    @Override
    public Transport findTransportByWlnid(String wlnid) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("FROM Transport T WHERE T.wlnid = :paramwlnid");
        query.setParameter("paramwlnid", wlnid);
        List<Transport> transports = query.list();
        session.close();

        if (transports.size() == 1) {
            return transports.get(0);
        }

        return null;
    }
}

package ru.ath.alx.dao;

import ru.ath.alx.model.Transport;

import java.util.List;

public class TransportService {

    private static TransportDaoImpl transportDao;

    public TransportService() {
        transportDao = new TransportDaoImpl();
    }

    public void create(Transport transport) {
        transportDao.create(transport);
    }

    public void update(Transport transport) {
        transportDao.update(transport);
    }

    public void delete(int id) {
        transportDao.delete(id);
    }

    public List<Transport> findTransportList() {
        return transportDao.findTransportList();
    }

    public Transport findTransportById(int id) {
        return transportDao.findTransportById(id);
    }

    public Transport findTransportByInvnom(String invnom) {
        return transportDao.findTransportByInvnom(invnom);
    }

    public Transport findTransportByInWlnid(String wlnid) {
        return transportDao.findTransportByWlnid(wlnid);
    }

}

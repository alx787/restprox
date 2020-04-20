package ru.ath.alx.dao;

import ru.ath.alx.model.Transport;

import java.util.List;

public interface TransportDao {
    void create(Transport transport);

    void update(Transport transport);

    void delete(int id);

    List<Transport> findTransportList();

    Transport findTransportById(int id);

    Transport findTransportByInvnom(String invnom);

    Transport findTransportByWlnid(String wlnid);
}

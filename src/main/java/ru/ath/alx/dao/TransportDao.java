package ru.ath.alx.dao;

import ru.ath.alx.model.Transport;

import java.util.List;

public interface TransportDao {
    void add(Transport transport);

    void update(Transport transport);

    void delete(Transport transport);

    List<Transport> getTransportList(String search);

    Transport getTransportById(int id);

    Transport getTransportByInvnom(String invnom);
}

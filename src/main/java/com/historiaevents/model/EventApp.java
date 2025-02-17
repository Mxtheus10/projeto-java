package com.historiaevents.model;

import com.historiaevents.dao.EventDAO;

import java.util.List;

public class EventApp {
    private EventDAO eventDAO;

    public EventApp() {
        this.eventDAO = new EventDAO();
    }

    public void addEvent(EventBase event) {
        eventDAO.addEvent(event);
    }

    public void removeEvent(int id) {
        eventDAO.removeEvent(id);
    }

    public void updateEvent(EventBase event) {
        eventDAO.updateEvent(event);
    }

    public List<EventBase> getAllEvents() {
        return eventDAO.getAllEvents();
    }

    // Método para obter eventos
    public List<EventBase> getEvents() {
        return getAllEvents(); // Chama o método existente
    }
}

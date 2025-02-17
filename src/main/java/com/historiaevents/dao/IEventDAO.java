package com.historiaevents.dao;

import java.util.List;

import com.historiaevents.model.EventBase;

public interface IEventDAO {
    List<EventBase> getAllEvents();
    void addEvent(EventBase event);
    void removeEvent(int id);
    void updateEvent(EventBase event);
}

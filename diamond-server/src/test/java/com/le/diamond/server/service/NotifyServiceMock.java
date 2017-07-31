package com.le.diamond.server.service;

import java.util.ArrayList;
import java.util.List;

import com.le.diamond.server.service.notify.NotifyService;
import com.le.diamond.server.utils.event.EventDispatcher;


public class NotifyServiceMock extends NotifyService {

    final public List<EventDispatcher.Event> events = new ArrayList<EventDispatcher.Event>();

    public NotifyServiceMock() {
        super(new ServerListService());
    }
    
    @Override
    public void onEvent(EventDispatcher.Event event) {
        events.add(event);
    }
}

package com.le.diamond.server.service;

import com.le.diamond.server.utils.event.EventDispatcher.Event;

/**
 * �������ݷ���������¼���
 */
public class LocalDataChangeEvent implements Event {
    final public String groupKey;
    
    public LocalDataChangeEvent(String groupKey) {
        this.groupKey = groupKey;
    }
}
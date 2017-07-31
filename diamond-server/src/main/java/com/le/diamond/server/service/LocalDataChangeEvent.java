package com.le.diamond.server.service;

import com.le.diamond.server.utils.event.EventDispatcher.Event;

/**
 * 本地数据发生变更的事件。
 */
public class LocalDataChangeEvent implements Event {
    final public String groupKey;
    
    public LocalDataChangeEvent(String groupKey) {
        this.groupKey = groupKey;
    }
}
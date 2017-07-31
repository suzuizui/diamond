package com.le.diamond.server.service;

import com.le.diamond.server.utils.event.EventDispatcher;


/**
 * ָ���ݷ����¼���
 */
public class ConfigDataChangeEvent implements EventDispatcher.Event {

    final public String dataId;
    final public String group;
    final public long lastModifiedTs;
    
    public ConfigDataChangeEvent(String dataId, String group, long gmtModified) {
        if (null == dataId || null == group) {
            throw new IllegalArgumentException();
        }
        this.dataId = dataId;
        this.group = group;
        this.lastModifiedTs = gmtModified;
    }
}

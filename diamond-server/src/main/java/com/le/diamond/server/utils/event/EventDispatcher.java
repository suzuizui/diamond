package com.le.diamond.server.utils.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class EventDispatcher {

    /**
     * add event listener
     */
    static public void addEventListener(EventListener listener) {
        for (Class<? extends Event> type : listener.interest()) {
            getEntry(type).listeners.addIfAbsent(listener);
        }
    }

    /**
     * fire event, notify listeners.
     */
    static public void fireEvent(Event event) {
        if (null == event) {
            throw new IllegalArgumentException();
        }

        for (EventListener listener : getEntry(event.getClass()).listeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
    }

    // For only test purpose
    static public void clear() {
        listenerHub.clear();
    }
    
    /**
     * get event listener for eventType. Add Entry if not exist.
     */
    static Entry getEntry(Class<? extends Event> eventType) {
        for (;;) {
            for (Entry entry : listenerHub) {
                if (entry.eventType == eventType) {
                    return entry;
                }
            }

            Entry tmp = new Entry(eventType);
            if (listenerHub.addIfAbsent(tmp)) { // false means already exists
                return tmp;
            }
        }
    }

    static private class Entry {
        final Class<? extends Event> eventType;
        final CopyOnWriteArrayList<EventListener> listeners;

        Entry(Class<? extends Event> type) {
            eventType = type;
            listeners = new CopyOnWriteArrayList<EventListener>();
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj || obj.getClass() != getClass()) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            return eventType == ((Entry) obj).eventType;
        }
    }

    // ========================

    static private final Logger log = LoggerFactory.getLogger(EventDispatcher.class);

    static final CopyOnWriteArrayList<Entry> listenerHub = new CopyOnWriteArrayList<Entry>();

    // ========================

    static public interface Event {
    }

    static public abstract class EventListener {

        public EventListener() {
            EventDispatcher.addEventListener(this); // automatic register
        }

        /**
         * 感兴趣的事件列表
         */
        abstract public List<Class<? extends Event>> interest();

        /**
         * 处理事件
         */
        abstract public void onEvent(Event event);
    }

}

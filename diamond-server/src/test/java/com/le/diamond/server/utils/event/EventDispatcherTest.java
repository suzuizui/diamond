package com.le.diamond.server.utils.event;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;



public class EventDispatcherTest {

    @After
    public void after() {
        EventDispatcher.clear();
    }

    @Ignore
    @Test
    public void testAddListener() throws Exception {
        final EventDispatcher.EventListener listener = new MockListener();
        
        int vusers = 1000;
        final CountDownLatch latch = new CountDownLatch(vusers);
        
        for (int i = 0; i < vusers; ++i) {
            new Thread(new Runnable() {
                public void run() {
                    latch.countDown();
                    EventDispatcher.addEventListener(listener);
                }
            }).start();
        }
        
        latch.await();
        assertEquals(1, EventDispatcher.listenerHub.size());
    }
    
    @Test
    public void testFireEvent() {
        EventDispatcher.fireEvent(new MockEvent());
        assertEquals(0, MockListener.count);
        
        EventDispatcher.addEventListener(new MockListener());
        
        EventDispatcher.fireEvent(new MockEvent());
        assertEquals(1, MockListener.count);
        
        EventDispatcher.fireEvent(new MockEvent());
        assertEquals(2, MockListener.count);
    }
}


class MockEvent implements EventDispatcher.Event {
}

class MockListener extends EventDispatcher.EventListener {
    static int count = 0;
    
    @Override
    public List<Class<? extends EventDispatcher.Event>> interest() {
        List<Class<? extends EventDispatcher.Event>> types = new ArrayList<Class<? extends EventDispatcher.Event>>();
        types.add(MockEvent.class);
        return types;
    }

    @Override
    public void onEvent(EventDispatcher.Event event) {
        ++count;
    }
}

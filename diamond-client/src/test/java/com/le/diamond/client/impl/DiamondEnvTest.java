package com.le.diamond.client.impl;

import java.util.Date;

import org.junit.Test;

import com.le.diamond.MockListener;
import com.le.diamond.client.Diamond;



public class DiamondEnvTest {

    @Test
    public void testAddListener() throws Exception {
        String dataId = "jiuren.test.addListener";
        String group = "DEFAULT_GROUP";
        String content = new Date().toString();
        MockListener listener = new MockListener();
        Diamond.addListener(dataId, group, listener);
        
        // publish and wait to receive
        Diamond.publishSingle(dataId, group, content);
        for (;;) {
            if (content.equals(listener.config)) {
                break;
            } else {
                Thread.sleep(100L);
            }
        }
        
        // add another listener and wait to receive
        MockListener listener2 = new MockListener();
        Diamond.addListener(dataId, group, listener2);
        for (;;) {
            if (content.equals(listener2.config)) {
                break;
            } else {
                Thread.sleep(100L);
            }
        }
    }
    
}

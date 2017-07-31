package com.le.diamond.manager;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.concurrent.Executor;

import org.junit.Test;

import com.le.diamond.client.Diamond;



public class SkipInitialCallbackListenerTest {

    @Test
    public void test() throws Exception {
        String dataId = "jiuren.test.SkipInitialCallbackListener";
        String group = "DEFAULT_GROUP";
        String config = new Date().toString();
        
        // publish and wait
        Diamond.publishSingle(dataId, group, config);
        while (!Diamond.getConfig(dataId, group, 2000L).equals(config)) {
            Thread.sleep(500L);
        }
        
        String initValue = Diamond.getConfig(dataId, group, 2000L);
        SkipInitialCallbackListenerMock listener2 = new SkipInitialCallbackListenerMock(initValue);
        Diamond.addListener(dataId, group, listener2);
        Thread.sleep(3000L);
        assertEquals(null, listener2.config);
    }
}


class SkipInitialCallbackListenerMock extends SkipInitialCallbackListener {

    SkipInitialCallbackListenerMock(String initValue) {
        super(initValue);
    }
    
    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void receiveConfigInfo0(String configInfo) {
        config = configInfo;
    }
    
    String config;
}

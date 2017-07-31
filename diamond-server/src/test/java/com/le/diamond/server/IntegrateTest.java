package com.le.diamond.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

import org.junit.Test;

import com.le.diamond.manager.ManagerListener;
import com.le.diamond.manager.impl.DefaultDiamondManager;
import com.le.diamond.md5.MD5;



public class IntegrateTest {

    
    @Test
    public void test() throws Exception {
        final String dataId = "abc";
        final String group = "abc";
        String content = "jiuren, " + UUID.randomUUID().toString();
        
        MD5.getInstance().getMD5String(content);
        
        DefaultDiamondManager sub = new DefaultDiamondManager(group, dataId, (List)null);
        sub.getAvailableConfigureInfomation(3000L);
        MyListener listener = new MyListener();
        sub.addListeners(Arrays.asList((ManagerListener)listener));
//        Assert.assertEquals(1, listener.data.size());
//        
//        DiamondPublisher pub = DiamondClientFactory.getSingletonBasestonePublisher();
//        pub.start();
//        pub.syncPublishAll(dataId, group, content, 3000L);
//        
//        Thread.sleep(10 * 1000L);
//        Assert.assertEquals(2, listener.data.size());
    }
}


class MyListener implements ManagerListener {

    @Override
    public Executor getExecutor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void receiveConfigInfo(String configInfo) {
        data.add(configInfo);
    }
    
    List<Object> data = new ArrayList<Object>();
}
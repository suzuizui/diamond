package com.le.diamond.client;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.le.diamond.MockListener;
import com.le.diamond.client.impl.DiamondEnvRepo;
import com.le.diamond.manager.ManagerListener;


public class DiamondTest {

    String dataId = "test1";
    String group = "le";
    
    
    @Test
    public void testAddListeners() {
        List<ManagerListener> listeners = new ArrayList<ManagerListener>();
        MockListener mockListener = new MockListener();
        listeners.add(mockListener);

        try {
            Diamond.addListeners(dataId, group, listeners);
            assertEquals(true, DiamondEnvRepo.defaultEnv.getSubscribeDataIds().contains(dataId));

            System.out.println("old config: " + Diamond.getConfig(dataId, group, 1000L));
            Thread.sleep(1 * 1000L);
            System.out.println("new config: " + mockListener.config);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (ManagerListener listener : listeners) {
                Diamond.removeListener(dataId, group, listener);
            }
        }
    }
    
//    @Test
//    public void testRemoveListener() {
//        List<ManagerListener> listeners = new ArrayList<ManagerListener>();
//        listeners.add(new MockListener());
//
//        try {
//            Diamond.addListeners(dataId, group, listeners);
//            assertEquals(true, DiamondEnvRepo.defaultEnv.getSubscribeDataIds().contains(dataId));
//
//            for (ManagerListener listener : listeners) {
//                Diamond.removeListener(dataId, group, listener);
//            }
//            assertEquals(false, DiamondEnvRepo.defaultEnv.getSubscribeDataIds().contains(dataId));
//        } finally {
//            for (ManagerListener listener : listeners) {
//                Diamond.removeListener(dataId, group, listener);
//            }
//        }
//    }
    
    
//    @Test
//    public void testPublishSingle_实际是聚合dataId_失败() {
//        String dataId = "NS_DIAMOND_SUBSCRIPTION_TOPIC_jiurenTest";
//        String group = Constants.GROUP;
//        String content = "111";
//
//        assertEquals(false, Diamond.publishSingle(dataId, group, content));
//    }
//
//    @Test
//    public void testPublishSingle_ok() throws Exception {
//        String dataId = "111";
//        String group = Constants.GROUP;
//        String content = new Date().toString();
//
//        assertEquals(true, Diamond.publishSingle(dataId, group, content));
//
//        for (;;) {
//            String config = Diamond.getConfig(dataId, group, 1000L);
//            if (null != config && config.equals(content)) {
//                break;
//            } else {
//                Thread.sleep(100L);
//            }
//        }
//    }
//
//    @Test
//    public void testPublishAggr_实际是普通dataId_失败() {
//        String dataId = "111";
//        String group = Constants.GROUP;
//        String content = "111";
//
//        assertEquals(false, Diamond.publishAggr(dataId, group, "datumId", content));
//    }
//
//    @Test
//    public void testPublishAggr_ok() throws Exception {
//        String dataId = "NS_DIAMOND_SUBSCRIPTION_TOPIC_jiurenTest";
//        String group = Constants.GROUP;
//        String datumId = "111";
//        String content = new Date().toString();
//
//        assertEquals(true, Diamond.publishAggr(dataId, group, datumId, content));
//
//        for (;;) {
//            String config = Diamond.getConfig(dataId, group, 3000L);
//            if (null != config && config.contains(content)) {
//                break;
//            } else {
//                System.out.println("sleep 100ms");
//                Thread.sleep(100L);
//            }
//        }
//    }
//
//    @Test
//    public void testAddListener_更新后收到通知() throws Exception {
//        String dataId = "111";
//        String group = Constants.GROUP;
//        String content = new Date().toString();
//        MockListener listener = new MockListener();
//
//        // 订阅
//        List<ManagerListener> listeners = new ArrayList<ManagerListener>();
//        listeners.add(listener);
//        Diamond.addListeners(dataId, group, listeners);
//
//        // 修改数据
//        assertEquals(true, Diamond.publishSingle(dataId, group, content));
//
//        // 等待通知
//        for (;;) {
//            if (null != listener.config && listener.config.contains(content)) {
//                break;
//            } else {
//                Thread.sleep(100L);
//            }
//        }
//    }
}

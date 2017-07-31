package com.le.diamond;

import java.util.concurrent.Executor;

import com.le.diamond.manager.ManagerListener;



public class MockListener implements ManagerListener {
    @Override
    public Executor getExecutor() {
        return null;
    }

    volatile public String config = null;
    
    @Override
    public void receiveConfigInfo(String configInfo) {
        config = configInfo;
    }
}

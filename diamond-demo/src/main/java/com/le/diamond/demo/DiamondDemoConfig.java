package com.le.diamond.demo;


import com.le.diamond.service.AbstractDiamondService;

/**
 * Created by gaobo3 on 2016/3/28.
 */
public class DiamondDemoConfig extends AbstractDiamondService {

    private String config = "";

    public String getConfig() {
        return config;
    }

    @Override
    protected void setConfigInfo(String configInfo) {
        config = configInfo;
    }

}

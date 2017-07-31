package com.le.diamond.service;

import com.le.diamond.client.Diamond;
import com.le.diamond.manager.ManagerListenerAdapter;

/**
 * Created by gaobo3 on 2016/3/28.
 */
public abstract class AbstractDiamondService {
    private String DIAMOND_DATAID = "";
    private String DIAMOND_GROUPID = "";

    public void setDataId(String dataId) {
        this.DIAMOND_DATAID = dataId;
    }

    public void setGroupId(String groupId) {
        this.DIAMOND_GROUPID = groupId;
    }

    public void init() throws Exception {
        //获取Config(防止spring上下文启动后config未初始化)
        String configInfo = Diamond.getConfig(DIAMOND_DATAID, DIAMOND_GROUPID, 5 * 1000);
        if (configInfo != null && !"".equals(configInfo)) {
            setConfigInfo(configInfo);
        }
        //监听Config变化
        Diamond.addListener(DIAMOND_DATAID, DIAMOND_GROUPID, new ManagerListenerAdapter() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                if (configInfo != null && !"".equals(configInfo)) {
                    setConfigInfo(configInfo);
                }
            }
        });
    }

    protected abstract void setConfigInfo(String configInfo);
}

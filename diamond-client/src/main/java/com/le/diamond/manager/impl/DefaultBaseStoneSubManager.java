package com.le.diamond.manager.impl;

import java.util.Arrays;
import java.util.List;

import com.le.diamond.manager.BaseStoneSubManager;
import com.le.diamond.manager.ManagerListener;


@Deprecated
public class DefaultBaseStoneSubManager extends DefaultDiamondManager implements
        BaseStoneSubManager {

    /**
     * �������ݵĹ��췽��, ָ��������
     * 
     * @param dataId
     * @param group
     * @param managerListener
     *            ������, �����н��յ����ݱ仯��ص�
     */
    public DefaultBaseStoneSubManager(String dataId, String group, ManagerListener managerListener) {
        super(group, dataId, Arrays.asList(managerListener));
    }

    /**
     * �������ݵĹ��췽��, ָ���������б�
     * 
     * @param dataId
     * @param group
     * @param managerListenerList
     *            �������б�, �����н��յ����ݱ仯�����λص��б��е�ÿһ��������
     */
    public DefaultBaseStoneSubManager(String dataId, String group,
            List<ManagerListener> managerListenerList) {
        super(group, dataId, managerListenerList);
    }

}

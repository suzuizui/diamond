package com.le.diamond.manager.impl;

import static com.le.diamond.client.impl.DiamondEnv.log;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.le.diamond.client.DiamondConfigure;
import com.le.diamond.client.impl.DiamondEnvRepo;
import com.le.diamond.manager.DiamondManager;
import com.le.diamond.manager.ManagerListener;
import com.le.diamond.client.Diamond;
import com.le.diamond.common.Constants;


/**
 * Diamond�����ߵ�ʵ��, һ��������ֻ�ܶ���һ��dataId��group��Ӧ������, ������ע�������ڽ������ݵĻص�������
 * 
 * ʹ��CopyOnWriteArraylist������������Ա�LinkedList�����Ա��Ⲣ��������ѭ����
 * 
 */
@Deprecated
public class DefaultDiamondManager implements DiamondManager {

    /**
	 * Ϊ�˼���r-checkcode�����ȼӻ���������캯��
	 */
    public DefaultDiamondManager(String dataId, List<ManagerListener> bizListeners) {
        this("DEFAULT_GROUP", dataId, bizListeners);
    }
    public DefaultDiamondManager(String dataId, ManagerListener bizListener) {
        this("DEFAULT_GROUP", dataId, Arrays.asList(bizListener));
    }
    public DefaultDiamondManager(String group, String dataId, ManagerListener bizListener) {
        this(group, dataId, Arrays.asList(bizListener));
    }
    public DefaultDiamondManager(String group, String dataId, List<ManagerListener> bizListeners) {
        if (null == group) {
            group = Constants.DEFAULT_GROUP;
        }
        this.dataId = dataId;
        this.group = group;

        selfListeners = new CopyOnWriteArrayList<ManagerListener>();
        addListeners(bizListeners);
    }
    
    @Override
    public List<ManagerListener> getManagerListeners() {
        return new ArrayList<ManagerListener>(selfListeners);
    }

    @Override
    public void addListeners(List<ManagerListener> newListeners) {
        newListeners = filterNull(newListeners);
        selfListeners.addAll(newListeners);
        Diamond.addListeners(dataId, group, newListeners);
    }

    @Override
    public void clearSelfListener() {
        for (ManagerListener listener : selfListeners) {
            Diamond.removeListener(dataId, group, listener);
        }
        selfListeners.clear();
    }
    
    @Override
    public void setManagerListener(ManagerListener newListener) {
        clearSelfListener();
        addListeners(Arrays.asList(newListener));
    }

    @Override
    public void setManagerListeners(List<ManagerListener> newListeners) {
        clearSelfListener();
        addListeners(newListeners);
    }
    
    @Override
    public String getConfigureInfomation(long timeoutMs) {
        try {
            return Diamond.getConfig(dataId, group, timeoutMs);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    @Override
    public String getAvailableConfigureInfomation(long timeout) {
        return getConfigureInfomation(timeout);
    }
    
    @Override
    public Properties getAvailablePropertiesConfigureInfomation(long timeoutMs) {
        String configInfo = getConfigureInfomation(timeoutMs);
        if (configInfo == null) {
            return null;
        }
        try {
            Properties prop = new Properties();
            prop.load(new StringReader(configInfo));
            return prop;
        } catch (IOException e) {
            log.error("load properties error��" + configInfo, e);
            throw new RuntimeException("load properties error��" + configInfo, e);
        }
    }


    @Override
    public void setDiamondConfigure(DiamondConfigure diamondConfigure) {
        // do nothing
    }

    @Override
    public DiamondConfigure getDiamondConfigure() {
        return DiamondConfigure.singleton;
    }

    @Override
    public void close() {
        clearSelfListener();
    }

    @Override
    public List<String> getServerAddress() {
        return DiamondEnvRepo.defaultEnv.getServerUrls();
    }
        
    @Override
    public Set<String> getAllDataId() {
        return DiamondEnvRepo.defaultEnv.getSubscribeDataIds();
    }

    /**
     * ȥ��NULLԪ�ء������µ�List��
     */
    private <T> List<T> filterNull(List<T> list) {
        if (null == list) {
            return Collections.emptyList();
        }
        
        List<T> copy = new ArrayList<T>(list.size());
        for (T element : list) {
            if (null != element) {
                copy.add(element);
            }
        }
        return copy;
    }
    
    @Deprecated
    public static class Builder {
        private final String dataId;
        private String group;
        private List<ManagerListener> managerListenerList = new ArrayList<ManagerListener>();

        public Builder(String dataId, ManagerListener managerListener) {
            this.dataId = dataId;
            if (managerListener == null) {
                log.info("manager listener is null, cannot receive runtime diamond config change");
            } else {
                managerListenerList.add(managerListener);
            }
        }

        public Builder(String dataId, List<ManagerListener> managerListenerList) {
            this.dataId = dataId;
            if (managerListenerList == null || managerListenerList.isEmpty()) {
                log.warn("manager listener list is null or empty, cannot receive runtime diamond config change");
            } else {
                managerListenerList.addAll(managerListenerList);
            }
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setDiamondConfigure(DiamondConfigure diamondConfigure) {
            return this;
        }

        public DiamondManager build() {
            return new DefaultDiamondManager(group, dataId, managerListenerList);
        }
    }
    
    // =========================
    
    private final String dataId;
    private final String group;
    private final CopyOnWriteArrayList<ManagerListener> selfListeners;

}

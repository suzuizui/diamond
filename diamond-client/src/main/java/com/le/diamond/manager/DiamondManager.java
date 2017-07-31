package com.le.diamond.manager;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.le.diamond.client.DiamondConfigure;


/**
 * DiamondManager���ڶ���һ���ҽ���һ��DataID��Ӧ��������Ϣ
 * 
 * @author aoqiong
 * 
 */
@Deprecated
public interface DiamondManager {

    /**
     * ����µ�ҵ�����ݼ�������
     */
    void addListeners(List<ManagerListener> newListeners);
    
    /**
     * ɾ���Լ��ļ�������
     */
    void clearSelfListener();
    
    
    /**
     * ����ҵ�����ݼ������� �����µļ������滻ԭ�е�ҵ�����ݼ�������
     */
    @Deprecated
    public void setManagerListener(ManagerListener managerListener);


    /**
     * ����ҵ�����ݼ������� �����µļ������滻ԭ�е�ҵ�����ݼ�������
     */
    @Deprecated
    public void setManagerListeners(List<ManagerListener> managerListenerList);


    /**
     * ���ظ�DiamondManager���õ�listener�б�
     */
    public List<ManagerListener> getManagerListeners();


    /**
     * ͬ����ȡ������Ϣ,���ȼ�����������Ŀ¼ -> server
     * 
     * @param timeoutMs
     *            �������ȡ������Ϣ�ĳ�ʱ����λ����
     * @return
     */
    public String getConfigureInfomation(long timeoutMs);


    /**
     * ͬ����ȡһ����Ч��������Ϣ�����ȼ�����������Ŀ¼ -> server -> ����snapshot��
     * �����Щ;������Ч���򷵻�null
     */
    public String     getAvailableConfigureInfomation(long timeoutMs);
    public Properties getAvailablePropertiesConfigureInfomation(long timeoutMs);
    
    
    public void setDiamondConfigure(DiamondConfigure diamondConfigure);
    public DiamondConfigure getDiamondConfigure();


    /**
     * �ر����DiamondManager
     */
    public void close();


    /**
     * ��ȡ��ǰ����ʹ�õķ������б�
     */
    public List<String> getServerAddress();


    /**
     * ��ȡע�����������dataId
     */
    public Set<String> getAllDataId();

}

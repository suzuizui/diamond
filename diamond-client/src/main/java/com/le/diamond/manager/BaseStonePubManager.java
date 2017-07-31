package com.le.diamond.manager;

import com.le.diamond.client.ContentIdentityPattern;
import com.le.diamond.client.DiamondConfigure;


/**
 * �����ӿ�
 * 
 * @author leiwen
 * 
 */
@Deprecated
public interface BaseStonePubManager {

    /**
     * ��������µ�������, �첽��ָ������Ψһ��ʶ��pattern
     */
    void publish(String dataId, String group, String configInfo, ContentIdentityPattern pattern);


    /**
     * ��������µ�������, ͬ��. ָ������Ψһ��ʶ��pattern.  method=syncUpdateConfig
     */
    boolean syncPublish(String dataId, String group, String configInfo, long timeout,
            ContentIdentityPattern pattern);
    
    /**
     * �滻���е�����������Ϣ
     */
    void publishAll(String dataId, String group, String configInfo);
    
    /**
     * �滻���е�����������Ϣ
     */
    boolean syncPublishAll(String dataId, String group, String configInfo, long timeout);


    /**
     * ɾ������������Ŀ
     */
    void removeAll(String dataId, String group);

    /**
     * ɾ������������Ŀ
     */
    boolean syncRemoveAll(String dataId, String group, long timeout);



    /**
     * ��ȡ������ص�����
     * 
     * @return
     */
    DiamondConfigure getDiamondConfigure();


    /**
     * ���÷�����ص�����
     * 
     * @param diamondConfigure
     */
    void setDiamondConfigure(DiamondConfigure diamondConfigure);


    /**
     * �رշ�����
     */
    void close();


    /**
     * ��������µ���������Ŀ
     * 
     * @param datumId
     *            �ۺ����ݵı�ʶ, datumId��ͬ����, ��ͬ׷��
     */
    boolean publish(String dataId, String group, String datumId, String configInfo, long timeout);

    /**
     * ɾ������������Ŀ
     */
    boolean unPublish(String dataId, String group, String datumId, long timeout);

    /**
     * ɾ�� dataId + group �������е�������Ŀ
     */
    boolean unPublishAll(String dataId, String group, long timeout);
}

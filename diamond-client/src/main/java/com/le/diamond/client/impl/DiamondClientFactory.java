package com.le.diamond.client.impl;

import com.le.diamond.client.DiamondPublisher;
import com.le.diamond.client.DiamondSubscriber;


/**
 * Diamond�ͻ��˹����࣬���Բ���һ��������DiamondSubscriber�������е�DiamondManager���� ��ͬ�ļ�Ⱥ��Ӧ��ͬ�ĵ���
 * 
 * @author aoqiong
 * 
 */
@Deprecated
public class DiamondClientFactory {

    /**
     * ��ȡ������diamond��Ⱥ������
     */
    public synchronized static DiamondSubscriber getSingletonDiamondSubscriber() {
        return DefaultDiamondSubscriber.singleton;
    }

    /**
     * ��ȡ������basestone��Ⱥ������
     */
    public synchronized static DiamondSubscriber getSingletonBasestoneSubscriber() {
        return DefaultDiamondSubscriber.singleton;
    }

    /**
     * ��ȡ������basestone��Ⱥ������
     */
    public synchronized static DiamondPublisher getSingletonBasestonePublisher() {
        return DefaultDiamondPublisher.singleton;
    }
}

package com.le.diamond.utils;

import java.util.concurrent.atomic.AtomicLong;


/**
 * ����ʱ�Ĺ�����, ���ڿͻ��˻�ȡ���ݵ����峬ʱ�� ÿ�δ������ȡ�����ݺ�, �ۼ�totalTime, ÿ�δ������ȡ����ǰ,
 * ���totalTime�Ƿ����totalTimeout, ����˵�����峬ʱ, totalTime��ʧЧʱ��, ÿ�δ������ȡ����ǰ, ����Ƿ�ʧЧ,
 * ʧЧ������totalTime, ���¿�ʼ�ۼ�
 * 
 * @author leiwen.zh
 * 
 */
public class TimeoutUtils {

    // �ۼƵĻ�ȡ�������ĵ�ʱ��, ��λms
    private final AtomicLong totalTime = new AtomicLong(0L);

    private volatile long lastResetTime;

    private volatile boolean initialized = false;

    // ��ȡ���ݵ����峬ʱ, ��λms
    private long totalTimeout;
    // �ۼƵĻ�ȡ�������ĵ�ʱ��Ĺ���ʱ��, ��λms
    private long invalidThreshold;


    public TimeoutUtils(long totalTimeout, long invalidThreshold) {
        this.totalTimeout = totalTimeout;
        this.invalidThreshold = invalidThreshold;
    }


    public synchronized void initLastResetTime() {
        if (initialized) {
            return;
        }
        lastResetTime = System.currentTimeMillis();
        initialized = true;
    }


    /**
     * �ۼ��ܵ�ʱ��
     * 
     * @param timeout
     */
    public void addTotalTime(long time) {
        totalTime.addAndGet(time);
    }


    /**
     * �ж��Ƿ�ʱ
     * 
     * @return
     */
    public boolean isTimeout() {
        return totalTime.get() > this.totalTimeout;
    }


    /**
     * �ܵ�ʱ������
     */
    public void resetTotalTime() {
        if (isTotalTimeExpired()) {
            totalTime.set(0L);
            lastResetTime = System.currentTimeMillis();
        }
    }


    public AtomicLong getTotalTime() {
        return totalTime;
    }


    private boolean isTotalTimeExpired() {
        return System.currentTimeMillis() - lastResetTime > this.invalidThreshold;
    }
}

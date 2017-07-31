package com.le.diamond.utils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


public class TimeoutUtilsUnitTest {

    private TimeoutUtils timeoutUtils;


    @Before
    public void init() {
        // �ܵĳ�ʱΪ2��
        long totalTimeout = 2000;
        // ��ʱ����ʱ��Ϊ1��
        long invalidThreshold = 1000;
        this.timeoutUtils = new TimeoutUtils(totalTimeout, invalidThreshold);
        this.timeoutUtils.initLastResetTime();
    }


    @Test
    public void testTimeout() {
        // ����1��ʱ��, û�г�ʱ
        this.timeoutUtils.addTotalTime(1000);
        Assert.assertFalse(this.timeoutUtils.isTimeout());
        // ������1.5��ʱ��, ��ʱ
        this.timeoutUtils.addTotalTime(1500);
        Assert.assertTrue(this.timeoutUtils.isTimeout());
    }


    @Test
    public void testTimeoutInvalid() {
        // ����1��ʱ��
        this.timeoutUtils.addTotalTime(1000);
        // ��ʱʱ��û�й���, ������Ч
        this.timeoutUtils.resetTotalTime();
        Assert.assertEquals(1000, this.timeoutUtils.getTotalTime().get());
        // ��ͣ1.5��, ʹ��ʱʱ�����
        try {
            Thread.sleep(1500);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // ��ʱʱ�����, ������Ч
        this.timeoutUtils.resetTotalTime();
        Assert.assertEquals(0, this.timeoutUtils.getTotalTime().get());
    }
}

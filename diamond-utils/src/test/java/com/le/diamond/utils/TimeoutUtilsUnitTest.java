package com.le.diamond.utils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


public class TimeoutUtilsUnitTest {

    private TimeoutUtils timeoutUtils;


    @Before
    public void init() {
        // 总的超时为2秒
        long totalTimeout = 2000;
        // 超时过期时间为1秒
        long invalidThreshold = 1000;
        this.timeoutUtils = new TimeoutUtils(totalTimeout, invalidThreshold);
        this.timeoutUtils.initLastResetTime();
    }


    @Test
    public void testTimeout() {
        // 增加1秒时间, 没有超时
        this.timeoutUtils.addTotalTime(1000);
        Assert.assertFalse(this.timeoutUtils.isTimeout());
        // 再增加1.5秒时间, 超时
        this.timeoutUtils.addTotalTime(1500);
        Assert.assertTrue(this.timeoutUtils.isTimeout());
    }


    @Test
    public void testTimeoutInvalid() {
        // 增加1秒时间
        this.timeoutUtils.addTotalTime(1000);
        // 超时时间没有过期, 重置无效
        this.timeoutUtils.resetTotalTime();
        Assert.assertEquals(1000, this.timeoutUtils.getTotalTime().get());
        // 暂停1.5秒, 使超时时间过期
        try {
            Thread.sleep(1500);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 超时时间过期, 重置有效
        this.timeoutUtils.resetTotalTime();
        Assert.assertEquals(0, this.timeoutUtils.getTotalTime().get());
    }
}

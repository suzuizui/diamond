package com.le.diamond.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SimpleFlowDataUnitTest {

    private SimpleFlowData flowData;

    @Before
    public void init() {
        this.flowData = new SimpleFlowData(2, 1000);
    }
    
    
    @Test
    public void test() {
        // ��index=0���ۻ�
        this.flowData.addAndGet(20);
        assertEquals(20, this.flowData.getCurrentCount());
        this.flowData.addAndGet(20);
        assertEquals(40, this.flowData.getCurrentCount());
        
        // ��ͣһ���, ��index=1���ۻ�
        try {
            Thread.sleep(1100);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertEquals(20, this.flowData.getAverageCount());
        
        this.flowData.addAndGet(20);
        assertEquals(20, this.flowData.getCurrentCount());
        this.flowData.addAndGet(20);
        assertEquals(40, this.flowData.getCurrentCount());
        
        
        // ��ͣһ���, ��index=0���ۻ�, �ۻ�ǰ��0
        try {
            Thread.sleep(1100);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        assertEquals(40, this.flowData.getAverageCount());
        
        assertEquals(0, this.flowData.getCurrentCount());
    }
}

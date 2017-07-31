package com.le.diamond.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class SimpleIPFlowDataUnitTest {

    private SimpleIPFlowData flowData;


    @Before
    public void init() {
        this.flowData = new SimpleIPFlowData(2, 1000);
    }


    @Test
    public void test() {
        String ip1 = "192.168.0.1";
        String ip2 = "192.168.0.2";

        for (int i = 0; i < 10; i++) {
            this.flowData.incrementAndGet(ip1);
            this.flowData.incrementAndGet(ip2);
        }

        assertEquals(10, this.flowData.getCurrentCount(ip1));
        assertEquals(10, this.flowData.getCurrentCount(ip2));
        assertEquals(0, this.flowData.getAverageCount());

        try {
            Thread.sleep(1100);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertEquals(0, this.flowData.getCurrentCount(ip1));
        assertEquals(0, this.flowData.getCurrentCount(ip2));
        assertEquals(10, this.flowData.getAverageCount());
    }
}

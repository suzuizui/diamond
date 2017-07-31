package com.le.diamond;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.le.diamond.client.Diamond;



public class PublishChineseTest {

    @Test
    public void test() throws Exception {
        String dataId = "test1";
        String group = "diamond";
        String content = System.currentTimeMillis() + "ÄãºÃ";
        
        Diamond.publishSingle(dataId, group, content);
        Thread.sleep(2000L);
        
        String configInfo = Diamond.getConfig(dataId, group, 1000L);
        assertEquals(content, configInfo);
    }
}

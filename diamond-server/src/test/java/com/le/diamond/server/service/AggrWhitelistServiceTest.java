package com.le.diamond.server.service;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;




public class AggrWhitelistServiceTest {

    AggrWhitelist service;
    
    @Before
    public void before() throws Exception {
        service = new AggrWhitelist();
    }
    
    @Test
    public void testIsAggrDataId() {
        List<String> list = new ArrayList<String>();
        list.add("com.le.jiuren.*");
        list.add("NS_DIAMOND_SUBSCRIPTION_TOPIC_*");
        list.add("com.le.tae.AppListOnGrid-*");
        service.compile(list);
        
        assertEquals(false, service.isAggrDataId("com.abc"));
        assertEquals(false, service.isAggrDataId("com.le.jiuren"));
        assertEquals(false, service.isAggrDataId("com.le.jiurenABC"));
        assertEquals(true, service.isAggrDataId("com.le.jiuren.abc"));
        assertEquals(true, service.isAggrDataId("NS_DIAMOND_SUBSCRIPTION_TOPIC_abc"));
        assertEquals(true, service.isAggrDataId("com.le.tae.AppListOnGrid-abc"));
    }
}

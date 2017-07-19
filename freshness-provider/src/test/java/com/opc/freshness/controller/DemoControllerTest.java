package com.opc.freshness.controller;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.opc.freshness.TestBase;

public class DemoControllerTest extends TestBase {
    private static final String GET_DEMO_BY_ID_EXPECT = "{\"id\":1,\"name\":\"demoName\"}";

    @Test
    public void testGetDemoById() throws Exception {
        MvcResult mvcResult = this.getMockMvc().perform(
                MockMvcRequestBuilders.get("/demo/getDemoById/v1").param("id", "1").accept(MediaType.APPLICATION_JSON))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        Assert.assertEquals(mvcResult.getResponse().getContentAsString(), GET_DEMO_BY_ID_EXPECT);
    }
}

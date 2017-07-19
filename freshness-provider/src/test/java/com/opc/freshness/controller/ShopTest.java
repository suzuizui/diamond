package com.opc.freshness.controller;

import com.opc.freshness.TestBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/19
 */
public class ShopTest extends TestBase {
    private static final String GET_DEMO_BY_ID_EXPECT = "{\"id\":1,\"name\":\"demoName\"}";

    @Test
    public void getStaff() throws Exception {
        MvcResult mvcResult = this.getMockMvc().perform(
                MockMvcRequestBuilders.get("/api/shop/staff/1/v1").accept(MediaType.APPLICATION_JSON))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        System.out.println(mvcResult.getResponse().getContentAsString());
        Assert.assertEquals(mvcResult.getResponse().getContentAsString(), GET_DEMO_BY_ID_EXPECT);
    }
}

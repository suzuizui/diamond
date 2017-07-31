package com.le.diamond.server.service;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import com.le.diamond.server.utils.GroupKey2;



public class ClientTrackServiceTest {

    @Before
    public void before() {
        ClientTrackService.clientRecords.clear();
    }

    @Test
    public void test_trackClientMd5() {
        String clientIp = "1.1.1.1";
        String dataId = "com.le.session.xml";
        String group = "online";
        String groupKey = GroupKey2.getKey(dataId, group);
        String md5 = "xxxxxxxxxxxxx";
        
        ConfigService.updateMd5(groupKey, md5, System.currentTimeMillis());

        ClientTrackService.trackClientMd5(clientIp, groupKey, md5);
        ClientTrackService.trackClientMd5(clientIp, groupKey, md5);

        Assert.assertEquals(true, ClientTrackService.isClientUptodate(clientIp).get(groupKey));
        Assert.assertEquals(1, ClientTrackService.subscribeClientCount());
        Assert.assertEquals(1, ClientTrackService.subscriberCount());
        
        //服务端数据更新
        ConfigService.updateMd5(groupKey, md5 + "111", System.currentTimeMillis());
        Assert.assertEquals(false, ClientTrackService.isClientUptodate(clientIp).get(groupKey));
    }

}

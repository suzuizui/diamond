package com.le.diamond.server.service;

import com.le.diamond.server.utils.GroupKey2;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class ConfigServiceTest {

    @Ignore
    @Test
    public void test_makeSure() {
        String dataId = "abcfff";
        String group = "abcfff";
        final String groupKey = GroupKey2.getKey(dataId, group);
        
        Assert.assertEquals(0, ConfigService.groupCount());
        
        ConfigService.makeSure(groupKey);
        Assert.assertEquals(1, ConfigService.groupCount());
    }
    
    
   
    /**
     * 基于日常发现的一个bug，不存在的数据，getContentMd5()应该返回零长度字符串，结果却返回null。导致NPE。
     * 原因是因为，在remove()方法内，从cache中删除CacheItem，但在finally内，clearModify(groupKey)方法导致CacheItem又被创建。
     */
//    @Test
//    public void test_getContentMd5_反复dump和remove() {
//        WebFilter.setWebRootPath("d:/test/");
//
//        String dataId = "abc";
//        String group = "abc";
//        String content = "fuck GFW";
//        String groupKey = GroupKey2.getKey(dataId, group);
//
//        ConfigService.dump(dataId, group, content, System.currentTimeMillis());
//        ConfigService.remove(dataId, group);
//        Assert.assertEquals("", ConfigService.getContentMd5(groupKey));
//    }
}

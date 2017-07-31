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
     * �����ճ����ֵ�һ��bug�������ڵ����ݣ�getContentMd5()Ӧ�÷����㳤���ַ��������ȴ����null������NPE��
     * ԭ������Ϊ����remove()�����ڣ���cache��ɾ��CacheItem������finally�ڣ�clearModify(groupKey)��������CacheItem�ֱ�������
     */
//    @Test
//    public void test_getContentMd5_����dump��remove() {
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

package com.le.diamond.client.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;


public class ClientWorkerTest {
    
    
    final String dataId = "jiuren";
    final String group = "group";
    
    LocalConfigInfoProcessor processor;
    File failover;
    CacheData cacheData;
    

    @Before
    public void startUp() {
        processor = new LocalConfigInfoProcessor();
        failover = LocalConfigInfoProcessor.getFailoverFile(DiamondEnvRepo.defaultEnv, dataId, group);
        failover.getParentFile().mkdirs();
        cacheData = new CacheData(dataId, group);
    }
    
    @After
    public void clearDown() throws Exception {
        IOUtils.delete(failover);
    }
    
    
//    @Test
//    public void test_容灾文件从无到有() throws Exception {
//        IOUtils.delete(failover);
//        failover.createNewFile();
//        String content = "xxxxxxxxxxxxx";
//        IOUtils.writeStringToFile(failover, content, Constants.ENCODE);
//        ClientWorker.checkLocalConfig(DiamondEnvRepo.defaultEnv, cacheData);
//
//        assertEquals(true, cacheData.isUseLocalConfigInfo());
//    }
//
//    @Test
//    public void test_容灾文件从有到无() throws Exception {
//        IOUtils.delete(failover);
//        failover.createNewFile();
//        String content = "xxxxxxxxxxxxx";
//        IOUtils.writeStringToFile(failover, content, Constants.ENCODE);
//        ClientWorker.checkLocalConfig(DiamondEnvRepo.defaultEnv, cacheData);
//        assertEquals(true, cacheData.isUseLocalConfigInfo());
//
//        IOUtils.delete(failover);
//        ClientWorker.checkLocalConfig(DiamondEnvRepo.defaultEnv, cacheData);
//        assertEquals(false, cacheData.isUseLocalConfigInfo());
//    }
//
//
//
//    @Test
//    public void test_容灾文件内容变化() throws Exception {
//        IOUtils.delete(failover);
//        failover.createNewFile();
//        String content = "xxxxxxxxxxxxx";
//        IOUtils.writeStringToFile(failover, content, Constants.ENCODE);
//        ClientWorker.checkLocalConfig(DiamondEnvRepo.defaultEnv, cacheData);
//        assertEquals(true, cacheData.isUseLocalConfigInfo());
//
//        content += "22222222222";
//        failover.delete();
//        Thread.sleep(1000);
//        failover.createNewFile();
//        IOUtils.writeStringToFile(failover, content, Constants.ENCODE);
//
//
//        ClientWorker.checkLocalConfig(DiamondEnvRepo.defaultEnv, cacheData);
//        assertEquals(true, cacheData.isUseLocalConfigInfo());
//        assertEquals(content, cacheData.getContent());
//    }
}

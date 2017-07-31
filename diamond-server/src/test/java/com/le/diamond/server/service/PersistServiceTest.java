package com.le.diamond.server.service;

import static org.junit.Assert.assertEquals;


public class PersistServiceTest {

    PersistService service;
    
//    @Before
//    public void setUp() throws IOException {
//        service = new PersistService();
//    }
//
//
//    @Test
//    public void test_configInfoCount() {
//        int rowCount = service.configInfoCount();
//        assertEquals(true, rowCount > 0);
//    }
//
//    @Test
//    public void test_findAllConfigInfo_正常分页() {
//        int rowCount = service.configInfoCount();
//        int pageSize = 1000;
//        int pageCount = (int) Math.ceil(rowCount * 1.0 / pageSize);
//
//        int fetchRowCount = 0;
//        for (int pageNo = 1; pageNo <= pageCount; ++pageNo) {
//            Page<ConfigInfo> page = service.findAllConfigInfo(pageNo, pageSize);
//            fetchRowCount += page.getPageItems().size();
//        }
//        assertEquals(rowCount, fetchRowCount);
//    }
//
//    @Test
//    public void test_findAllConfigInfo_pageNo越界() {
//        int rowCount = service.configInfoCount();
//        int pageSize = 1000;
//        int pageNo = rowCount / pageSize + 10;
//
//        assertEquals(null, service.findAllConfigInfo(pageSize, pageNo));
//    }
//
//    @Test
//    public void test_PaginationHelper_pageNo越界() {
//        int rowCount = service.configInfoCount();
//        int pageSize = 1000;
//        int pageNo = rowCount / pageSize + 10;
//
//        Page<ConfigInfo> page = new PaginationHelper<ConfigInfo>().fetchPage(
//                service.getJdbcTemplate(), "SELECT COUNT(ID) FROM config_info ",
//                "select * from config_info ", new Object[0], pageNo, pageSize,
//                PersistService.CONFIG_INFO_ROW_MAPPER);
//        assertEquals(null, page);
//    }
//
//
//    @Test
//    public void test_configAggrInfoCount(){
//        String dataid = "com.le.matrix.ring.auth.UCP";
//        String group = "ring_bbs";
//        service.addAggrConfigInfo(dataid, group, "datum1", "content");
//        service.addAggrConfigInfo(dataid, group, "datum2", "content");
//        int rowCount = service.aggrConfigInfoCount(dataid, group);
//        assertEquals(true, rowCount > 0);
//    }
//
//
//    @Test
//    public void test_findConfigInfoAggrByPage(){
//        String dataid = "com.le.matrix.ring.auth.UCP";
//        String group = "ring_bbs";
//        int PAGE_SIZE = 100;
//        int rowCount = service.aggrConfigInfoCount(dataid, group);
//        int pageCount = (int) Math.ceil(rowCount * 1.0 / PAGE_SIZE);
//
//        int actualRowCount = 0;
//        for (int pageNo = 1; pageNo <= pageCount; pageNo++) {
//            Page<ConfigInfoAggr> page = service.findConfigInfoAggrByPage(dataid, group, pageNo, PAGE_SIZE);
//            if (page != null) {
//                for (ConfigInfoAggr cf : page.getPageItems()) {
//                }
//                actualRowCount += page.getPageItems().size();
//            }
//        }
//
//        assertEquals(actualRowCount, rowCount);
//    }
//
//    @Test
//    public void testBatchQuery(){
//        String group = "DEFAULT_GROUP";
//        List<String> dataids = new ArrayList<String>();
//        String dataidbase = "com.le.diamond.test.batchquery.";
//
//        int bathQueryCnt = 59;
//        for(int i = 0; i < bathQueryCnt; i++){
//            String dataid = dataidbase + new Date().toString() + i;
//            service.addConfigInfo(dataid, group, "test" + i, "ip", "user", TimeUtils.getCurrentTime());
//            dataids.add(dataid);
//        }
//        List<ConfigInfo> configInfos = service.findConfigInfoByBatch(dataids,group,20);
//
//        for(int i = 0; i < bathQueryCnt; i++){
//            String dataid = dataidbase + i + new Date().toString();
//            service.removeConfigInfo(dataid, group);
//            dataids.add(dataid);
//        }
//
//        assertEquals(bathQueryCnt, configInfos.size());
//    }
//
//    @Test
//    public void testBatchQuery_返回为空(){
//        String group = "DEFAULT_GROUP";
//        List<String> dataids = new ArrayList<String>();
//        String dataidbase = "com.le.diamond.test.batchquery.";
//
//        int bathQueryCnt = 59;
//        for(int i = 0; i < bathQueryCnt; i++){
//            String dataid = dataidbase + i + new Date().toString();
//            service.removeConfigInfo(dataid, group);
//            dataids.add(dataid);
//        }
//        List<ConfigInfo> configInfos = service.findConfigInfoByBatch(dataids,group,20);
//
//        assertEquals(0, configInfos.size());
//    }


}

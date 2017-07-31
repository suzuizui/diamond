package com.le.diamond.client.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.le.diamond.client.BatchHttpResult;
import com.le.diamond.client.Diamond;
import com.le.diamond.domain.ConfigInfoEx;
import static org.junit.Assert.assertEquals;


public class BatchQueryTest {

    @Test
    public void test() {
        String dataId1 = "jiurenTest.111";
        String dataId2 = "jiurenTest.222";
        String nonExistDataId = "jiurenTest.nonExist";
        Diamond.publishSingle(dataId1, null, "111");
        Diamond.publishSingle(dataId2, null, "222");
        Diamond.remove(nonExistDataId, null);
        List<String> dataIds = Arrays.asList(dataId1, dataId2, nonExistDataId);

        BatchHttpResult<ConfigInfoEx> result = Diamond.batchQuery(dataIds, "DEFAULT_GROUP", 3000L);
        assertEquals(true, null != result);
        assertEquals(true, result.isSuccess());
        assertEquals(dataIds.size(), result.getResult().size());
        for (ConfigInfoEx cfg : result.getResult()) {
            if (cfg.getDataId().equals(nonExistDataId)) {
                assertEquals(2, cfg.getStatus());
            } else {
                assertEquals(1, cfg.getStatus());
            }
        }
    }
}

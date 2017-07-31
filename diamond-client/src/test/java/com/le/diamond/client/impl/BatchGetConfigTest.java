package com.le.diamond.client.impl;

import com.le.diamond.client.BatchHttpResult;
import com.le.diamond.client.Diamond;
import com.le.diamond.domain.ConfigInfoEx;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * <p/>
 * Project: diamond-client
 * User: qiaoyi.dingqy
 * Date: 13-11-12
 * Time: ÏÂÎç9:47
 */
public class BatchGetConfigTest {

    @Test
    public void test() {

        String dataId1 = "joeytest.111";
        String dataId2 = "joeytest.222";
        String dataId3 = "joeytest.333";
        String dataId4 = "joeytest.444";
        String nonExistDataId = "joeytest.nonExist";
        Diamond.publishSingle(dataId1, null, "111");
        Diamond.publishSingle(dataId2, null, "222");
        Diamond.publishSingle(dataId3, null, "333");
        Diamond.publishSingle(dataId4, null, "444");

        Diamond.remove(nonExistDataId, null);
        List<String> dataIds = Arrays.asList(dataId1, dataId2, dataId3, dataId4, nonExistDataId);

        BatchHttpResult<ConfigInfoEx> result = Diamond.batchGetConfig(dataIds, "DEFAULT_GROUP", 3000L);
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

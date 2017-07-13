package com.opc.freshness.service.job;

import com.opc.freshness.service.biz.BatchBiz;
import com.wormpex.wcommon.redis.JedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by qishang on 2017/7/12.
 * 鲜度管理 - 批次废弃任务
 */
@Component
public class AbortBatchJob {
    private static final Logger logger = LoggerFactory.getLogger(AbortBatchJob.class);
    private static final String JOBLOCK = "freshness_AbortBatchJob_lock";

    @Resource
    private BatchBiz batchBiz;
    @Resource
    private JedisClient jedisClient;

    public void abortBatch() {
        try {
            logger.info("AbortBatchJob 任务开始——————");

            if (jedisClient.setnx(JOBLOCK, "1") == 0) {
                logger.warn("AbortBatchJob锁获取失败，已有实例开始同步任务，本实例不执行");
                return;
            }
            jedisClient.expire(JOBLOCK, 120);

            jedisClient.del(JOBLOCK);
            logger.info("AbortBatchJob 任务结束——————");
        } catch (Exception e) {
            logger.error("批次废弃任务失败", e);
        }
    }

}

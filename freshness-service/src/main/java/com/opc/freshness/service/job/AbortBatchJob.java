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
    private static final String ABORT_LOCK = "freshness_abortBatch_lock";
    private static final String TOSALING_LOCK = "freshness_toSaling_lock";

    @Resource
    private BatchBiz batchBiz;
    @Resource
    private JedisClient jedisClient;

    public void abortBatch() {
        try {
            logger.info("AbortBatchJob abortBatch任务开始——————");

            if (jedisClient.setnx(ABORT_LOCK, "1") == 0) {
                logger.warn("AbortBatchJob-abortBatch 任务锁获取失败，已有实例开始同步任务，本实例不执行");
                return;
            }
            jedisClient.expire(ABORT_LOCK, 120);

            jedisClient.del(ABORT_LOCK);
            logger.info("AbortBatchJob abortBatch任务结束——————");
        } catch (Exception e) {
            logger.error("AbortBatchJob abortBatch失败", e);
        }
    }

    /**
     * 批次 准备中/回水中/制作中 -->售卖中
     */
    public void batchToSaling() {
        try {
            logger.info("batchToSaling 任务开始——————");

            if (jedisClient.setnx(TOSALING_LOCK, "1") == 0) {
                logger.warn("batchToSaling锁获取失败，已有实例开始同步任务，本实例不执行");
                return;
            }
            jedisClient.expire(TOSALING_LOCK, 120);

            jedisClient.del(TOSALING_LOCK);
            logger.info("batchToSaling 任务结束——————");
        } catch (Exception e) {
            logger.error("批次废弃任务失败", e);
        }
    }
}

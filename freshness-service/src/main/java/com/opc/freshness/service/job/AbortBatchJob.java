package com.opc.freshness.service.job;

import com.opc.freshness.common.util.DateUtils;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.KindPo;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.biz.KindBiz;
import com.wormpex.biz.BizException;
import com.wormpex.wcommon.redis.JedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by qishang on 2017/7/12.
 * 鲜度管理 - 批次任务
 */
@Component
public class AbortBatchJob {
    private static final Logger logger = LoggerFactory.getLogger(AbortBatchJob.class);
    private static final String ABORT_LOCK = "freshness_abortBatch_lock";
    private static final String TOSALING_LOCK = "freshness_toSaling_lock";

    @Resource
    private KindBiz kindBiz;
    @Resource
    private BatchBiz batchBiz;
    @Resource
    private JedisClient jedisClient;

    /**
     * 废弃任务 售卖中 TO 待废弃 每分钟一次
     */
    public void abortBatch() {
        try {
            logger.info("AbortBatchJob abortBatch任务开始——————");

            if (jedisClient.setnx(ABORT_LOCK, "1") == 0) {
                logger.warn("AbortBatchJob-abortBatch 任务锁获取失败，已有实例开始同步任务，本实例不执行");
                return;
            }
            jedisClient.expire(ABORT_LOCK, 120);

            //逻辑处理
            process("abortBatch", BatchPo.status.PREING, BatchPo.status.SALING);

            jedisClient.del(ABORT_LOCK);
        } catch (Exception e) {
            logger.error("AbortBatchJob abortBatch失败", e);
        }
        logger.info("AbortBatchJob abortBatch任务结束——————");
    }


    /**
     * 批次 准备中/回水中/制作中 TO 售卖中 每分钟一次
     */
    public void batchToSaling() {
        try {
            logger.info("batchToSaling 任务开始——————");

            if (jedisClient.setnx(TOSALING_LOCK, "1") == 0) {
                logger.warn("batchToSaling锁获取失败，已有实例开始同步任务，本实例不执行");
                return;
            }
            jedisClient.expire(TOSALING_LOCK, 120);

            //逻辑处理
            process("batchToSaling", BatchPo.status.SALING, BatchPo.status.TO_ABORT);

            jedisClient.del(TOSALING_LOCK);
        } catch (Exception e) {
            logger.error("批次废弃任务失败", e);
        }
        logger.info("batchToSaling 任务结束——————");
    }


    private void process(String processName, int searchStatus, int targetStatus) {
        BatchPo po = new BatchPo();
        po.setStatus(searchStatus);
        List<BatchPo> batchPoList = batchBiz.selectByRecord(po);

        batchPoList
                .stream()
                .forEach(batchPo -> {
                    try {
                        logger.info(processName + " 处理批次 batchId:{}", batchPo.getId());

                        KindPo skuKind = kindBiz.selectByPrimaryKey(batchPo.getKindsId());

                        //过期时间
                        Long preEndTime;
                        switch (targetStatus) {
                            case BatchPo.status.SALING:
                                preEndTime = DateUtils.addMin(batchPo.getCreateTime(), skuKind.getDelay());
                                break;
                            case BatchPo.status.TO_ABORT:
                                preEndTime = DateUtils.addMin(batchPo.getCreateTime(), skuKind.getExpired());
                                break;
                            default:
                                throw new BizException("targetStatus错误");
                        }

                        if (System.currentTimeMillis() >= preEndTime) {
                            //当前时间晚于预期过期时间
                            logger.info(processName + " 批次过期 batchId:{}", batchPo.getId());

                            batchPo.setStatus(targetStatus);
                            batchBiz.updateBatchByPrimaryKeyLock(batchPo);
                        }
                    } catch (Exception e) {
                        logger.error(processName + " 处理批次失败 batchId:" + batchPo.getId(), e);
                    }
                });
    }
}

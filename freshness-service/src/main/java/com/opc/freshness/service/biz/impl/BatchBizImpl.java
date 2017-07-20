package com.opc.freshness.service.biz.impl;

import com.google.common.collect.Lists;
import com.opc.freshness.common.util.PageRequest;
import com.opc.freshness.common.util.Pager;
import com.opc.freshness.domain.bo.SkuCountBo;
import com.opc.freshness.domain.bo.SkuDetailBo;
import com.opc.freshness.domain.po.BatchPo;
import com.opc.freshness.domain.po.BatchStatePo;
import com.opc.freshness.domain.vo.BatchLogVo;
import com.opc.freshness.service.biz.BatchBiz;
import com.opc.freshness.service.dao.BatchMapper;
import com.opc.freshness.service.dao.BatchStateMapper;
import com.wormpex.biz.BizException;
import com.wormpex.biz.BizTemplate;
import com.wormpex.cvs.root.bundles.lang.WAssert;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * AUTHOR: qishang
 * DATE:2017/7/12.
 */
@Service
public class BatchBizImpl implements BatchBiz {
    private final static Logger logger = LoggerFactory.getLogger(BatchBizImpl.class);
    @Resource
    private BatchMapper batchMapper;
    @Resource
    private BatchStateMapper batchStateMapper;

    @Override
    public int insertSelective(BatchPo batch) {
        return new BizTemplate<Integer>() {

            @Override
            protected void checkParams() {
                WAssert.notNull(batch);
            }

            @Override
            protected Integer process() {
                return batchMapper.insertSelective(batch);
            }
        }.execute();
    }

    @Override
    public int batchInsertLog(List<BatchStatePo> logs) {
        return batchStateMapper.batchInsert(logs);
    }

    @Override
    public BatchPo selectByPrimaryKey(Integer batchId) {
        return batchMapper.selectByPrimaryKey(batchId);
    }

    @Override
    public List<BatchPo> selectMakeAndAbortList(Integer shopId) {
        logger.info("selectMakeAndAbortList shopId:{}", shopId);
        return batchMapper.selectLastNGroupByKindAndFlag(shopId, Lists.newArrayList(BatchPo.status.MAKING, BatchPo.status.TO_ABORT), 2);
    }

    @Override
    public List<BatchPo> selectByRecord(BatchPo batchPo) {
        return batchMapper.selectByRecord(batchPo);
    }

    @Override
    public int updateByPrimaryKeySelective(BatchPo batchPo) {
        return batchMapper.updateByPrimaryKeySelective(batchPo);
    }

    @Override
    public Pager<BatchLogVo> selectLogByPage(Integer shopId, List<Integer> statusList, Integer pageNo, Integer pageSize) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(new PageRequest.Page(pageSize, pageNo));
        Pager.PageData pageData = new Pager.PageData(pageNo, pageSize, batchStateMapper.selectVoCount(shopId, statusList));
        return new Pager<BatchLogVo>(pageData, batchStateMapper.selectVoList(shopId, statusList, pageRequest));
    }

    @Override
    public List<SkuCountBo> selectSkuCountByStatus(Integer shopId, Integer kindId, Date date, int staus) {
        return batchStateMapper.selectSkuCountByStatus(shopId, kindId, date, staus);
    }

    @Override
    public List<SkuDetailBo> skuDetailInfoList(Integer shopId, Integer categoryId, Date date) {
        return batchStateMapper.skuDetailInfoList(shopId, categoryId, date);
    }

    @Override
    public List<SkuDetailBo> skuDetailInfoListByBatchId(Integer batchId) {
        return batchStateMapper.skuDetailInfoListByBatchId(batchId);
    }

    @Override
    public List<BatchPo> batchListBySkuIdAndKindId(Integer skuId, Integer categoryId, Integer limit) {
        return batchMapper.batchListBySkuIdAndKindId(skuId, categoryId, limit);
    }

    /**
     * 批次更新  - 具有乐观锁的更新
     *
     * @param batchPo -lastModifyTi me 乐观锁字段
     */
    @Override
    @Transactional
    public void updateBatchByPrimaryKeyLock(BatchPo batchPo) {
        Asserts.notNull(batchPo.getLastModifyTime(), "时间戳");
        int result = batchMapper.updateByPrimaryKeySelective(batchPo);
        if (result != 1) {
            throw new BizException("更新失败，请稍后再试");
        }
    }
}

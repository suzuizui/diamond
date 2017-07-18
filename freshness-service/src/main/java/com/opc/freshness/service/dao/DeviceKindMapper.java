package com.opc.freshness.service.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.opc.freshness.domain.po.DeviceKindPo;
import com.opc.freshness.domain.po.KindPo;

@Mapper
public interface DeviceKindMapper {
    int deleteByPrimaryKey(Integer id);


    int insertSelective(DeviceKindPo record);

    DeviceKindPo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeviceKindPo record);

    /**
     * 通过设备Id查询种类Id
     *
     * @param deviceId
     * @return
     */
    List<KindPo> selectKindListByDeviceId(String deviceId);
}
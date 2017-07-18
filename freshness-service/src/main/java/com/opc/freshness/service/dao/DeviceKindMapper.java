package com.opc.freshness.service.dao;

import com.opc.freshness.domain.po.DeviceKindPo;
import com.opc.freshness.domain.po.KindPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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
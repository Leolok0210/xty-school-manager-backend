package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.DeviceTokenEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeviceTokenDao extends BaseMapper<DeviceTokenEntity> {
    DeviceTokenEntity findByToken(@Param("token") String token);

    DeviceTokenEntity findByDeviceSn(@Param("deviceSn") String deviceSn);
}

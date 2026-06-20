package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.DeviceCardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceCardDao extends BaseMapper<DeviceCardEntity> {

    DeviceCardEntity findByCardId(@Param("cardId") String cardId);

    List<DeviceCardEntity> findByStudentId(@Param("studentId") String studentId);

    List<DeviceCardEntity> findByDeviceSn(@Param("deviceSn") String deviceSn);
}

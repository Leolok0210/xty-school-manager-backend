package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.DeviceFaceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceFaceDao extends BaseMapper<DeviceFaceEntity> {

    DeviceFaceEntity findByStudentId(@Param("studentId") String studentId);

    List<DeviceFaceEntity> findByDeviceSn(@Param("deviceSn") String deviceSn);
}

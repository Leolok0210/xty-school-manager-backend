package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.ConventionalPerformanceEntity;
import com.xiaotiyun.school.manager.model.req.ConventionalPerformancePageReqModel;
import com.xiaotiyun.school.manager.model.res.ConventionalPerformancePageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConventionalPerformanceDao extends BaseMapper<ConventionalPerformanceEntity> {

    List<ConventionalPerformancePageResModel> page(@Param("schoolId") Long schoolId, @Param("reqModel") ConventionalPerformancePageReqModel reqModel);
}
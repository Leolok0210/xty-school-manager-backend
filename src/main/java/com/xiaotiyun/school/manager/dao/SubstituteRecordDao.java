package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.SubstituteRecordEntity;
import com.xiaotiyun.school.manager.model.req.SubstitutePageReqModel;
import com.xiaotiyun.school.manager.model.res.SubstitutePageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubstituteRecordDao extends BaseMapper<SubstituteRecordEntity> {

    List<SubstitutePageResModel> page(@Param("schoolId") Long schoolId, @Param("reqModel") SubstitutePageReqModel reqModel);
} 
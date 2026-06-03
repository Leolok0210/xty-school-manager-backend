package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.CompetitionEntity;
import com.xiaotiyun.school.manager.model.req.CompetitionPageReqModel;
import com.xiaotiyun.school.manager.model.res.CompetitionPageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CompetitionMapper extends BaseMapper<CompetitionEntity> {

    List<CompetitionPageResModel> page(@Param("competitionIds") List<Long> competitionIds, @Param("reqModel") CompetitionPageReqModel reqModel);
} 
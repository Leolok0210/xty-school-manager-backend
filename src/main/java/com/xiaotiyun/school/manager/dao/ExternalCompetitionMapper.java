package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionEntity;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionQueryReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionPageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExternalCompetitionMapper extends BaseMapper<ExternalCompetitionEntity> {

    List<ExternalCompetitionPageResModel> page(@Param("competitionIds") List<Long> competitionIds, @Param("reqModel") ExternalCompetitionQueryReqModel reqModel);
} 
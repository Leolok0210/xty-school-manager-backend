package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.LeisureActivitiesScoreEntity;
import com.xiaotiyun.school.manager.model.req.LeisureActivitiesScorePageReqModel;
import com.xiaotiyun.school.manager.model.res.LeisureActivitiesScorePageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 余暇活动成绩信息DAO层接口
 */
@Mapper
public interface LeisureActivitiesScoreDao extends BaseMapper<LeisureActivitiesScoreEntity> {

    List<LeisureActivitiesScorePageResModel> page(@Param("classIds") List<Long> classIds, @Param("reqModel") LeisureActivitiesScorePageReqModel reqModel);
} 
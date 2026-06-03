package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.LeisureActivitiesNoticeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 余暇活动匹配结果通知DAO层接口
 */
@Mapper
public interface LeisureActivitiesNoticeDao extends BaseMapper<LeisureActivitiesNoticeEntity> {

    @Select("SELECT * FROM leisure_activities_notice WHERE student_id = #{studentId} AND period_id = #{periodId} ORDER BY id DESC LIMIT 1")
    LeisureActivitiesNoticeEntity notice(Long studentId, Long periodId);
} 
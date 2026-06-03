package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.SchoolCalendarDateTypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SchoolCalendarDateTypeDao extends BaseMapper<SchoolCalendarDateTypeEntity> {

    List<SchoolCalendarDateTypeEntity> getSchoolIdWeekDayInfo(@Param("schoolId") Long schoolId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("applyType") Integer applyType, @Param("type") Integer type);
}
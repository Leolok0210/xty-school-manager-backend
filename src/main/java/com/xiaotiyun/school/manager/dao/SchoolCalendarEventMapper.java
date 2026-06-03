package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.SchoolCalendarEventEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SchoolCalendarEventMapper extends BaseMapper<SchoolCalendarEventEntity> {
    
    @Update("UPDATE school_calendar_event SET deleted = #{calendarId} WHERE school_calendar_id = #{calendarId} AND deleted = 0")
    void logicDeleteByCalendarId(@Param("calendarId") Long calendarId);

    @Select("SELECT * FROM school_calendar_event WHERE school_calendar_id = #{calendarId} AND deleted = 0")
    List<SchoolCalendarEventEntity> selectByCalendarId(@Param("calendarId") Long calendarId);

    void batchInsert(@Param("list") List<SchoolCalendarEventEntity> events);
} 
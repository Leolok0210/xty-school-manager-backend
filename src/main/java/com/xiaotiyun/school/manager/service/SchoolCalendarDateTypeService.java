package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.SchoolCalendarDateTypeEntity;

import java.time.LocalDate;
import java.util.List;

public interface SchoolCalendarDateTypeService extends IService<SchoolCalendarDateTypeEntity> {

    /**
     * 获取学校工作日信息
     * @param schoolId
     * @param startDate
     * @param endDate
     * @param applyType
     * @return
     */
    List<SchoolCalendarDateTypeEntity> getSchoolIdWeekDayInfo(Long schoolId, LocalDate startDate, LocalDate endDate, Integer applyType);
}
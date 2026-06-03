package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.enums.SchoolCalendarDateTypeEnum;
import com.xiaotiyun.school.manager.dao.SchoolCalendarDateTypeDao;
import com.xiaotiyun.school.manager.model.entity.SchoolCalendarDateTypeEntity;
import com.xiaotiyun.school.manager.service.SchoolCalendarDateTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolCalendarDateTypeServiceImpl extends ServiceImpl<SchoolCalendarDateTypeDao, SchoolCalendarDateTypeEntity> implements SchoolCalendarDateTypeService {

    @Override
    public List<SchoolCalendarDateTypeEntity> getSchoolIdWeekDayInfo(Long schoolId, LocalDate startDate, LocalDate endDate, Integer applyType) {
        return this.getBaseMapper().getSchoolIdWeekDayInfo(schoolId, startDate, endDate, applyType, SchoolCalendarDateTypeEnum.WEEKDAY.getCode());
    }
}
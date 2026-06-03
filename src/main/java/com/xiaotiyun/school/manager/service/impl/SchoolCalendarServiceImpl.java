package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.SchoolCalendarDateApplyTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolCalendarDateTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolCalendarEventTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.dao.SchoolCalendarEventMapper;
import com.xiaotiyun.school.manager.dao.SchoolCalendarMapper;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.SchoolCalendarPageReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolCalendarSaveReqModel;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.HolidaysService;
import com.xiaotiyun.school.manager.service.SchoolCalendarDateTypeService;
import com.xiaotiyun.school.manager.service.SchoolCalendarEventService;
import com.xiaotiyun.school.manager.service.SchoolCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolCalendarServiceImpl extends ServiceImpl<SchoolCalendarMapper, SchoolCalendarEntity> implements SchoolCalendarService {
    private final SchoolCalendarEventMapper eventMapper;
    private final UserSchoolRelDao userSchoolRelDao;
    @Resource
    private SchoolCalendarDateTypeService schoolCalendarDateTypeService;
    @Resource
    private SchoolCalendarEventService schoolCalendarEventService;
    @Resource
    private HolidaysService holidaysService;

    @Override
    public PageInfo<SchoolCalendarPageResModel> page(SchoolCalendarPageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());

        LambdaQueryWrapper<SchoolCalendarEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SchoolCalendarEntity::getSchoolId, reqModel.getSchoolId())
                .like(StringUtils.isNotBlank(reqModel.getCalendarName()), SchoolCalendarEntity::getCalendarName, reqModel.getCalendarName())
                .eq(SchoolCalendarEntity::getDeleted, 0)
                .orderByDesc(SchoolCalendarEntity::getStartDate);

        List<SchoolCalendarEntity> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> userIds = list.stream().map(SchoolCalendarEntity::getCreatorId).collect(Collectors.toList());
            List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelDao.selectBatchIds(userIds);
            Map<Long, UserSchoolRelEntity> userMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(userSchoolRelEntities)) {
                userMap = userSchoolRelEntities.stream().collect(Collectors.toMap(UserSchoolRelEntity::getId, userSchoolRelEntity -> userSchoolRelEntity));
            }
            PageInfo<SchoolCalendarEntity> pageInfo = new PageInfo<>(list);
            List<SchoolCalendarPageResModel> resList = new ArrayList<>();
            for (SchoolCalendarEntity entity : list) {
                SchoolCalendarPageResModel resModel = new SchoolCalendarPageResModel();
                BeanUtils.copyProperties(entity, resModel);
                UserSchoolRelEntity userSchoolRelEntity = userMap.get(entity.getCreatorId());
                if (userSchoolRelEntity != null) {
                    resModel.setCreatorName(userSchoolRelEntity.getUsername());
                }
                resList.add(resModel);
            }
            PageInfo<SchoolCalendarPageResModel> result = new PageInfo<>(resList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            return result;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SchoolCalendarSaveReqModel reqModel) {
        checkTimeConflict(reqModel, null);
        SchoolCalendarEntity entity = BeanConvertUtil.convert(reqModel, SchoolCalendarEntity.class);
        entity.setCreatorId(reqModel.getUserId());
        this.save(entity);
        generateDateType(entity);
        return entity.getId();
    }

    private void generateDateType(SchoolCalendarEntity entity) {
        //获取假日信息
        int startYear = entity.getStartDate().getYear();
        int endYear = entity.getEndDate().getYear();
        List<HolidaysEntity> holidayList = new ArrayList<>(getHolidayList(startYear));
        if (startYear != endYear) {
            holidayList.addAll(getHolidayList(endYear));
        }
        Map<LocalDate, HolidaysEntity> holidayMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(holidayList)) {
            holidayMap = holidayList.stream().collect(Collectors.toMap(HolidaysEntity::getHolidaysDate, holidaysEntity -> holidaysEntity));
        }
        //获取事项信息
        QueryWrapper<SchoolCalendarEventEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SchoolCalendarEventEntity::getSchoolCalendarId, entity.getId())
                .ge(SchoolCalendarEventEntity::getEventDate, entity.getStartDate())
                .le(SchoolCalendarEventEntity::getEventDate, entity.getEndDate());
        List<SchoolCalendarEventEntity> eventEntities = schoolCalendarEventService.list(wrapper);
        Map<LocalDate, List<SchoolCalendarEventEntity>> eventMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(eventEntities)) {
            eventMap = eventEntities.stream().collect(Collectors.groupingBy(SchoolCalendarEventEntity::getEventDate));
        }
        List<LocalDate> dateList = DateUtils.generateDates(entity.getStartDate(), entity.getEndDate());
        List<SchoolCalendarDateTypeEntity> dateTypeList = new ArrayList<>();
        List<SchoolCalendarEventEntity> eventList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            HolidaysEntity holidaysEntity = holidayMap.get(localDate);
            SchoolCalendarDateTypeEntity teacherDateType = new SchoolCalendarDateTypeEntity();
            teacherDateType.setSchoolCalendarId(entity.getId());
            teacherDateType.setCalendarDate(localDate);
            teacherDateType.setApplyType(SchoolCalendarDateApplyTypeEnum.TEACHER.getCode());
            SchoolCalendarDateTypeEntity studentDateType = new SchoolCalendarDateTypeEntity();
            studentDateType.setSchoolCalendarId(entity.getId());
            studentDateType.setCalendarDate(localDate);
            studentDateType.setApplyType(SchoolCalendarDateApplyTypeEnum.STUDENT.getCode());
            if (holidaysEntity != null) {
                //法定假期
                teacherDateType.setType(SchoolCalendarDateTypeEnum.HOLIDAY.getCode());
                studentDateType.setType(SchoolCalendarDateTypeEnum.HOLIDAY.getCode());
                List<SchoolCalendarEventEntity> oldEvents = eventMap.get(localDate);
                if (CollectionUtils.isNotEmpty(oldEvents)) {
                    Map<Integer, SchoolCalendarEventEntity> eventTypeEventMap = oldEvents.stream().collect(Collectors.toMap(SchoolCalendarEventEntity::getEventType, eventEntity -> eventEntity));
                    SchoolCalendarEventEntity teacherEvent = eventTypeEventMap.get(SchoolCalendarEventTypeEnum.TEACHER_HOLIDAY.getCode());
                    if (teacherEvent == null) {
                        teacherEvent = new SchoolCalendarEventEntity();
                        teacherEvent.setSchoolCalendarId(entity.getId());
                        teacherEvent.setEventType(SchoolCalendarEventTypeEnum.TEACHER_HOLIDAY.getCode());
                        teacherEvent.setEventDescription(holidaysEntity.getName());
                        teacherEvent.setIsSystem(true);
                        teacherEvent.setEventDate(localDate);
                        eventList.add(teacherEvent);
                    }
                    SchoolCalendarEventEntity studentEvent = eventTypeEventMap.get(SchoolCalendarEventTypeEnum.STUDENT_HOLIDAY.getCode());
                    if (studentEvent == null) {
                        studentEvent = new SchoolCalendarEventEntity();
                        studentEvent.setSchoolCalendarId(entity.getId());
                        studentEvent.setEventType(SchoolCalendarEventTypeEnum.STUDENT_HOLIDAY.getCode());
                        studentEvent.setEventDescription(holidaysEntity.getName());
                        studentEvent.setEventDate(localDate);
                        studentEvent.setIsSystem(true);
                        eventList.add(studentEvent);
                    }
                } else {
                    SchoolCalendarEventEntity teacherEvent = new SchoolCalendarEventEntity();
                    teacherEvent.setSchoolCalendarId(entity.getId());
                    teacherEvent.setEventType(SchoolCalendarEventTypeEnum.TEACHER_HOLIDAY.getCode());
                    teacherEvent.setEventDescription(holidaysEntity.getName());
                    teacherEvent.setEventDate(localDate);
                    teacherEvent.setIsSystem(true);
                    eventList.add(teacherEvent);
                    SchoolCalendarEventEntity studentEvent = new SchoolCalendarEventEntity();
                    studentEvent.setSchoolCalendarId(entity.getId());
                    studentEvent.setEventType(SchoolCalendarEventTypeEnum.STUDENT_HOLIDAY.getCode());
                    studentEvent.setEventDescription(holidaysEntity.getName());
                    studentEvent.setEventDate(localDate);
                    studentEvent.setIsSystem(true);
                    eventList.add(studentEvent);
                }
            } else {
                //工作日或者双休日
                int value = localDate.getDayOfWeek().getValue();
                if (value < 6) {
                    teacherDateType.setType(SchoolCalendarDateTypeEnum.WEEKDAY.getCode());
                    studentDateType.setType(SchoolCalendarDateTypeEnum.WEEKDAY.getCode());
                } else {
                    teacherDateType.setType(SchoolCalendarDateTypeEnum.WEEKEND.getCode());
                    studentDateType.setType(SchoolCalendarDateTypeEnum.WEEKEND.getCode());
                }
            }
            dateTypeList.add(teacherDateType);
            dateTypeList.add(studentDateType);
        }
        if (CollectionUtils.isNotEmpty(eventList)) {
            schoolCalendarEventService.saveOrUpdateBatch(eventList);
        }
        if (CollectionUtils.isNotEmpty(dateTypeList)) {
            schoolCalendarDateTypeService.saveOrUpdateBatch(dateTypeList);
        }
    }

    private List<HolidaysEntity> getHolidayList(int year) {
        QueryWrapper<HolidaysEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(HolidaysEntity::getYear, year);
        return holidaysService.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SchoolCalendarSaveReqModel reqModel) {
        SchoolCalendarEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.CALENDAR_NOT_EXISTS);
        }
        checkTimeConflict(reqModel, id);
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
        QueryWrapper<SchoolCalendarDateTypeEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SchoolCalendarDateTypeEntity::getSchoolCalendarId, id);
        long count = schoolCalendarDateTypeService.count(wrapper);
        if (count == 0) {
            //为生成过日期类型的需要重新生成日期类型信息
            generateDateType(entity);
        }
    }

    @Override
    public SchoolCalendarResModel info(Long id) {
        SchoolCalendarEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.CALENDAR_NOT_EXISTS);
        }
        SchoolCalendarResModel resModel = BeanConvertUtil.convert(entity, SchoolCalendarResModel.class);
        if (resModel != null) {
            //查询事项
            List<SchoolCalendarEventEntity> eventEntityList = eventMapper.selectByCalendarId(resModel.getId());
            if (CollectionUtils.isNotEmpty(eventEntityList)) {
                List<SchoolCalendarEventResModel> eventList = new ArrayList<>();
                eventEntityList.forEach(schoolCalendarEventEntity -> {
                    SchoolCalendarEventResModel eventResModel = BeanConvertUtil.convert(schoolCalendarEventEntity, SchoolCalendarEventResModel.class);
                    eventList.add(eventResModel);
                });
                resModel.setEventList(eventList);
            }
        }
        return resModel;
    }

    @Override
    public SchoolCalendarV230ResModel infoV230(Long id) {
        SchoolCalendarEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.CALENDAR_NOT_EXISTS);
        }
        SchoolCalendarV230ResModel resModel = BeanConvertUtil.convert(entity, SchoolCalendarV230ResModel.class);
        if (resModel != null) {
            //查询事项
            QueryWrapper<SchoolCalendarEventEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(SchoolCalendarEventEntity::getSchoolCalendarId, resModel.getId());
            List<SchoolCalendarEventEntity> eventEntityList = schoolCalendarEventService.list(wrapper);
            if (CollectionUtils.isNotEmpty(eventEntityList)) {
                List<SchoolCalendarEventResModel> eventList = new ArrayList<>();
                eventEntityList.forEach(schoolCalendarEventEntity -> {
                    SchoolCalendarEventResModel eventResModel = BeanConvertUtil.convert(schoolCalendarEventEntity, SchoolCalendarEventResModel.class);
                    eventList.add(eventResModel);
                });
                resModel.setEventList(eventList);
            }
            //查询日期属性
            QueryWrapper<SchoolCalendarDateTypeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SchoolCalendarDateTypeEntity::getSchoolCalendarId, resModel.getId());
            List<SchoolCalendarDateTypeEntity> dateTypeEntities = schoolCalendarDateTypeService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(dateTypeEntities)) {
                List<SchoolCalendarDateTypeResModel> dateTypeList = new ArrayList<>();
                dateTypeEntities.forEach(schoolCalendarDateTypeEntity -> {
                    SchoolCalendarDateTypeResModel dateTypeResModel = BeanConvertUtil.convert(schoolCalendarDateTypeEntity, SchoolCalendarDateTypeResModel.class);
                    dateTypeList.add(dateTypeResModel);
                });
                resModel.setDateTypeList(dateTypeList);
            }
        }
        return resModel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SchoolCalendarEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.CALENDAR_NOT_EXISTS);
        }
        this.removeById(id);
        // 逻辑删除事项表
        eventMapper.logicDeleteByCalendarId(id);
    }

    private void checkTimeConflict(SchoolCalendarSaveReqModel reqModel, Long excludeId) {
        LambdaQueryWrapper<SchoolCalendarEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SchoolCalendarEntity::getSchoolId, reqModel.getSchoolId())
                .ne(excludeId != null, SchoolCalendarEntity::getId, excludeId)
                .eq(SchoolCalendarEntity::getDeleted, 0)
                .and(q -> q
                        .between(SchoolCalendarEntity::getStartDate, reqModel.getStartDate(), reqModel.getEndDate())
                        .or()
                        .between(SchoolCalendarEntity::getEndDate, reqModel.getStartDate(), reqModel.getEndDate())
                        .or()
                        .le(SchoolCalendarEntity::getStartDate, reqModel.getStartDate())
                        .ge(SchoolCalendarEntity::getEndDate, reqModel.getEndDate())
                );
        if (this.count(wrapper) > 0) {
            throw new BusinessException(LanguageConstants.CALENDAR_TIME_OVERLAP);
        }
    }
}
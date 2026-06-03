package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.SchoolCalendarEventMapper;
import com.xiaotiyun.school.manager.model.entity.SchoolCalendarDateTypeEntity;
import com.xiaotiyun.school.manager.model.entity.SchoolCalendarEntity;
import com.xiaotiyun.school.manager.model.entity.SchoolCalendarEventEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.SchoolCalendarEventResModel;
import com.xiaotiyun.school.manager.service.SchoolCalendarDateTypeService;
import com.xiaotiyun.school.manager.service.SchoolCalendarEventService;
import com.xiaotiyun.school.manager.service.SchoolCalendarService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
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

@Service
@RequiredArgsConstructor
public class SchoolCalendarEventServiceImpl extends ServiceImpl<SchoolCalendarEventMapper, SchoolCalendarEventEntity> implements SchoolCalendarEventService {
    private final SchoolCalendarEventMapper eventMapper;
    @Resource
    private SchoolCalendarService schoolCalendarService;
    private final SchoolCalendarDateTypeService schoolCalendarDateTypeService;
    private final LanguageUtil languageUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SchoolCalendarEventSaveReqModel reqModel) {
        validateEventDate(reqModel);
        LambdaQueryWrapper<SchoolCalendarEventEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SchoolCalendarEventEntity::getSchoolCalendarId, reqModel.getSchoolICalendarId())
                .ge(SchoolCalendarEventEntity::getEventDate, reqModel.getStartDate())
                .le(SchoolCalendarEventEntity::getEventDate, reqModel.getEndDate())
                .eq(SchoolCalendarEventEntity::getDeleted, 0);
        List<SchoolCalendarEventEntity> eventEntities = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(eventEntities)) {
            //日期范围下已有事项，删除历史事项，保存新事项
            this.removeBatchByIds(eventEntities);
        }
        List<LocalDate> dateList = DateUtils.generateDates(reqModel.getStartDate(), reqModel.getEndDate());
        List<SchoolCalendarEventEntity> entities = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            SchoolCalendarEventEntity entity = BeanConvertUtil.convert(reqModel, SchoolCalendarEventEntity.class);
            entity.setSchoolCalendarId(reqModel.getSchoolICalendarId());
            entity.setEventDate(localDate);
            entities.add(entity);
        }
        this.saveBatch(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrEditV230(SchoolCalendarEventSaveV230ReqModel reqModel) {
        validateEventDate(reqModel.getSchoolCalendarId(), reqModel.getStartDate(), reqModel.getEndDate());
        QueryWrapper<SchoolCalendarEventEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SchoolCalendarEventEntity::getSchoolCalendarId, reqModel.getSchoolCalendarId())
                .ge(SchoolCalendarEventEntity::getEventDate, reqModel.getStartDate())
                .le(SchoolCalendarEventEntity::getEventDate, reqModel.getEndDate());
        List<SchoolCalendarEventEntity> eventEntities = this.list(wrapper);
        Map<LocalDate, List<SchoolCalendarEventEntity>> eventMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(eventEntities)) {
            eventMap = eventEntities.stream().collect(Collectors.groupingBy(SchoolCalendarEventEntity::getEventDate));
        }
        QueryWrapper<SchoolCalendarDateTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SchoolCalendarDateTypeEntity::getSchoolCalendarId, reqModel.getSchoolCalendarId())
                .ge(SchoolCalendarDateTypeEntity::getCalendarDate, reqModel.getStartDate())
                .le(SchoolCalendarDateTypeEntity::getCalendarDate, reqModel.getEndDate());
        List<SchoolCalendarDateTypeEntity> dateTypeEntities = schoolCalendarDateTypeService.list(queryWrapper);
        Map<LocalDate, List<SchoolCalendarDateTypeEntity>> dateTypeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dateTypeEntities)) {
            dateTypeMap = dateTypeEntities.stream().collect(Collectors.groupingBy(SchoolCalendarDateTypeEntity::getCalendarDate));
        }
        List<LocalDate> dateList = DateUtils.generateDates(reqModel.getStartDate(), reqModel.getEndDate());
        List<SchoolCalendarEventEntity> eventList = new ArrayList<>();
        List<SchoolCalendarDateTypeEntity> dateTypeList = new ArrayList<>();
        List<SchoolCalendarEventDetailsSaveReqModel> eventDetails = reqModel.getEventDetails();
        List<SchoolCalendarDateTypeSaveV230ReqModel> dateTypes = reqModel.getDateTypes();
        List<Long> deleteEventIds = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            List<SchoolCalendarEventEntity> oldEvents = eventMap.get(localDate);
            if (CollectionUtils.isNotEmpty(eventDetails)) {
                Map<Integer, SchoolCalendarEventEntity> eventTypeEventMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(oldEvents)) {
                    eventTypeEventMap = oldEvents.stream().collect(Collectors.toMap(SchoolCalendarEventEntity::getEventType, eventEntity -> eventEntity));
                }
                List<Integer> updatedEventTypes = new ArrayList<>();
                for (SchoolCalendarEventDetailsSaveReqModel eventDetail : eventDetails) {
                    SchoolCalendarEventEntity schoolCalendarEventEntity = eventTypeEventMap.get(eventDetail.getEventType());
                    if (schoolCalendarEventEntity == null) {
                        schoolCalendarEventEntity = new SchoolCalendarEventEntity();
                    }
                    schoolCalendarEventEntity.setEventType(eventDetail.getEventType());
                    schoolCalendarEventEntity.setEventDescription(eventDetail.getEventDescription());
                    schoolCalendarEventEntity.setSchoolCalendarId(reqModel.getSchoolCalendarId());
                    schoolCalendarEventEntity.setEventDate(localDate);
                    eventList.add(schoolCalendarEventEntity);
                    updatedEventTypes.add(eventDetail.getEventType());
                }
                // 删除未被更新的事件
                if (CollectionUtils.isNotEmpty(oldEvents)) {
                    deleteEventIds.addAll(oldEvents.stream()
                            .filter(event -> !updatedEventTypes.contains(event.getEventType()))
                            .map(SchoolCalendarEventEntity::getId)
                            .collect(Collectors.toList()));
                }
            } else {
                // 删除未被更新的事件
                if (CollectionUtils.isNotEmpty(oldEvents)) {
                    deleteEventIds.addAll(oldEvents.stream().map(SchoolCalendarEventEntity::getId).collect(Collectors.toList()));
                }
            }
            if (CollectionUtils.isNotEmpty(dateTypes)) {
                List<SchoolCalendarDateTypeEntity> oldDateTypes = dateTypeMap.get(localDate);
                Map<Integer, SchoolCalendarDateTypeEntity> applyTypeDateTypeMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(oldDateTypes)) {
                    applyTypeDateTypeMap = oldDateTypes.stream().collect(Collectors.toMap(SchoolCalendarDateTypeEntity::getApplyType, dateType -> dateType));
                }
                for (SchoolCalendarDateTypeSaveV230ReqModel dateType : dateTypes) {
                    SchoolCalendarDateTypeEntity schoolCalendarDateTypeEntity = applyTypeDateTypeMap.get(dateType.getApplyType());
                    if (schoolCalendarDateTypeEntity == null) {
                        schoolCalendarDateTypeEntity = new SchoolCalendarDateTypeEntity();
                    }
                    schoolCalendarDateTypeEntity.setApplyType(dateType.getApplyType());
                    schoolCalendarDateTypeEntity.setType(dateType.getType());
                    schoolCalendarDateTypeEntity.setSchoolCalendarId(reqModel.getSchoolCalendarId());
                    schoolCalendarDateTypeEntity.setCalendarDate(localDate);
                    dateTypeList.add(schoolCalendarDateTypeEntity);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(eventList)) {
            this.saveOrUpdateBatch(eventList);
        }
        if (CollectionUtils.isNotEmpty(dateTypeList)) {
            schoolCalendarDateTypeService.saveOrUpdateBatch(dateTypeList);
        }
        if (CollectionUtils.isNotEmpty(deleteEventIds)) {
            this.removeBatchByIds(deleteEventIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SchoolCalendarEventUpdateReqModel reqModel) {
        SchoolCalendarEventEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.EVENT_NOT_EXISTS);
        }
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SchoolCalendarEventEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.EVENT_NOT_EXISTS);
        }
        this.removeById(entity);
    }

    @Override
    public List<SchoolCalendarEventResModel> listByCalendarId(Long calendarId) {
        return eventMapper.selectByCalendarId(calendarId).stream()
                .map(e -> BeanConvertUtil.convert(e, SchoolCalendarEventResModel.class))
                .collect(Collectors.toList());
    }

    private void validateEventDate(SchoolCalendarEventSaveReqModel reqModel) {
        SchoolCalendarEntity calendar = schoolCalendarService.getById(reqModel.getSchoolICalendarId());
        if (calendar == null || calendar.getDeleted() != 0) {
            throw new BusinessException(LanguageConstants.CALENDAR_NOT_EXISTS_OR_DELETED);
        }

        if (reqModel.getStartDate().isBefore(calendar.getStartDate())
                || reqModel.getEndDate().isAfter(calendar.getEndDate())) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.EVENT_DATE_OUT_OF_RANGE) + "["
                    + calendar.getStartDate() + "-" + calendar.getEndDate() + "]");
        }
    }

    private void validateEventDate(Long schoolICalendarId, LocalDate startDate, LocalDate endDate) {
        SchoolCalendarEntity calendar = schoolCalendarService.getById(schoolICalendarId);
        if (calendar == null) {
            throw new BusinessException(LanguageConstants.CALENDAR_NOT_EXISTS_OR_DELETED);
        }

        if (startDate.isBefore(calendar.getStartDate())
                || endDate.isAfter(calendar.getEndDate())) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.EVENT_DATE_OUT_OF_RANGE) + "["
                    + calendar.getStartDate() + "-" + calendar.getEndDate() + "]");
        }
        // 获取当前月份的第一天和最后一天
        LocalDate currentMonthStart = LocalDate.now().withDayOfMonth(1);

        // 校验日期是否属于已结束的月份
        if (endDate.isBefore(currentMonthStart)) { // 如果结束日期早于当前月份的第一天
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.EVENT_MONTH_ALREADY_ENDED));
        }
    }
}
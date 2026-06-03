package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.TeacherLeaveDao;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.TeacherLeavePageResModel;
import com.xiaotiyun.school.manager.model.res.TeacherLeaveReportResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherLeaveServiceImpl extends ServiceImpl<TeacherLeaveDao, TeacherLeaveEntity>
        implements TeacherLeaveService {
    private final UserSchoolRelDao userSchoolRelDao;
    private final ExportFileHandler exportFileHandler;
    private final UserSchoolRelService userSchoolRelService;
    private final SchoolCalendarService schoolCalendarService;
    private final SchoolCalendarDateTypeService schoolCalendarDateTypeService;
    private final TeacherAttendanceRuleService teacherAttendanceRuleService;
    private final TeacherAttendanceService teacherAttendanceService;
    private final TeacherBusinessService teacherBusinessService;
    private final SysFileRelevanceService sysFileRelevanceService;
    private final LanguageUtil languageUtil;
    private final ActApprovalInstanceService actApprovalInstanceService;
    private final UserDeptRelService userDeptRelService;

    @Override
    public PageInfo<TeacherLeavePageResModel> page(TeacherLeavePageReqModel reqModel) {
        UserEntity userInfo = (UserEntity) StpUtil.getSession().get("userInfo");
        if (userInfo == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        LocalDateTime queryStartTime = reqModel.getStartDate() != null ? reqModel.getStartDate().atStartOfDay() : null;
        LocalDateTime queryEndTime = reqModel.getEndDate() != null ? reqModel.getEndDate().atTime(LocalTime.MAX).withNano(0) : null;
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        // 获取应该被查出的职位人员ids
        List<Long> teacherIds = getCanQueryTeacherIds(reqModel.getSchoolId());
        // 开始查询数据
        QueryWrapper<TeacherLeaveEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TeacherLeaveEntity::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getLeaveType() != null && reqModel.getLeaveType() > 0, TeacherLeaveEntity::getLeaveType, reqModel.getLeaveType())
                .in(ObjectUtils.isNotEmpty(reqModel.getLeaveStatus()), TeacherLeaveEntity::getLeaveStatus, reqModel.getLeaveStatus())
                .ge(queryStartTime != null, TeacherLeaveEntity::getEndTime, queryStartTime)
                .le(queryEndTime != null, TeacherLeaveEntity::getStartTime, queryEndTime)
                .eq(TeacherLeaveEntity::getDeleted, 0);
        // 若当前选择教师为可查询职务教师，则拼接查询条件
        if (reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0) {
            if (reqModel.getTeacherId().equals(userInfo.getId())) {
                wrapper.lambda().eq(TeacherLeaveEntity::getTeacherId, reqModel.getTeacherId());
            } else if (teacherIds.contains(reqModel.getTeacherId())) {
                wrapper.lambda().eq(TeacherLeaveEntity::getTeacherId, reqModel.getTeacherId());
            }
        } else {// 若当前教师id为空，只可查询所有可查询职务教师，若无可查询教师，则直接返回空数据
            if (ObjectUtils.isNotEmpty(teacherIds)) {
                wrapper.lambda().in(TeacherLeaveEntity::getTeacherId, teacherIds);
            } else {
                PageInfo<TeacherLeavePageResModel> result = new PageInfo<>();
                result.setTotal(0);
                result.setPages(0);
                return result;
            }
        }
        List<TeacherLeaveEntity> list = this.list(wrapper.lambda().orderByDesc(TeacherLeaveEntity::getCreateTime));
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> userIds = list.stream().map(TeacherLeaveEntity::getTeacherId).collect(Collectors.toList());
            Map<Long, UserSchoolRelEntity> userMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(userIds)) {
                List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelDao.selectList(new LambdaQueryWrapper<UserSchoolRelEntity>()
                        .eq(UserSchoolRelEntity::getSchoolId, reqModel.getSchoolId())
                        .in(UserSchoolRelEntity::getUserId, userIds)
                        .eq(UserSchoolRelEntity::getDeleted, false));
                if (CollectionUtils.isNotEmpty(userSchoolRelEntities)) {
                    userMap = userSchoolRelEntities.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, userSchoolRelEntity -> userSchoolRelEntity));
                }
            }
            PageInfo<TeacherLeaveEntity> pageInfo = new PageInfo<>(list);
            List<TeacherLeavePageResModel> resList = new ArrayList<>();
            for (TeacherLeaveEntity entity : list) {
                TeacherLeavePageResModel resModel = new TeacherLeavePageResModel();
                BeanUtils.copyProperties(entity, resModel);
                UserSchoolRelEntity userSchoolRelEntity = userMap.get(entity.getTeacherId());
                if (userSchoolRelEntity != null) {
                    resModel.setTeacherName(userSchoolRelEntity.getUsername());
                }
                resList.add(resModel);
            }
            PageInfo<TeacherLeavePageResModel> result = new PageInfo<>(resList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            return result;
        }
        return null;
    }

    private List<Long> getCanQueryTeacherIds(Long schoolId) {
        UserEntity userInfo = (UserEntity) StpUtil.getSession().get("userInfo");
        if (userInfo == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        if (userInfo.getUserType() == 2) {
            return new ArrayList<>();
        }
        List<UserSchoolRelEntity> oprSchoolRel = userSchoolRelService.list(Wrappers.<UserSchoolRelEntity>lambdaQuery()
                .eq(UserSchoolRelEntity::getSchoolId, schoolId)
                .eq(UserSchoolRelEntity::getUserId, userInfo.getId()));
        if (oprSchoolRel.isEmpty()) {
            throw new BusinessException(LanguageConstants.USER_NOT_BOUND_SCHOOL);
        }
        List<String> positions = JobTitleEnum.getNextJobCodes(oprSchoolRel.get(0).getPosition());
        if (ObjectUtils.isEmpty(positions)) {
            return new ArrayList<>();
        }
        List<UserSchoolRelEntity> schoolRelEntities = userSchoolRelService.list(Wrappers.<UserSchoolRelEntity>lambdaQuery()
                .eq(UserSchoolRelEntity::getSchoolId, schoolId)
                .in(UserSchoolRelEntity::getPosition, positions));
        if (schoolRelEntities.isEmpty()) {
            return new ArrayList<>();
        }
        return schoolRelEntities.stream().map(UserSchoolRelEntity::getUserId).collect(Collectors.toList());
    }

    @Override
    public Long getPendingApproval(Long schoolId) {
        List<Long> canQueryTeacherIds = getCanQueryTeacherIds(schoolId);
        if (ObjectUtils.isNotEmpty(canQueryTeacherIds)) {
            return this.count(new LambdaQueryWrapper<TeacherLeaveEntity>()
                    .eq(TeacherLeaveEntity::getSchoolId, schoolId)
                    .in(TeacherLeaveEntity::getTeacherId, canQueryTeacherIds)
                    .eq(TeacherLeaveEntity::getLeaveStatus, 0)
                    .eq(TeacherLeaveEntity::getDeleted, 0));
        } else {
            return 0L;
        }
    }

    @Override
    @Transactional
    public void save(TeacherLeaveSaveReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getStartTime(), reqModel.getEndTime())) {
            throw new BusinessException(LanguageConstants.START_TIME_AFTER_END_TIME);
        }
        checkDuplicate(null, reqModel);
        TeacherLeaveEntity entity = BeanConvertUtil.convert(reqModel, TeacherLeaveEntity.class);
        // 当请假用户为校长时，请假状态默认为1审批通过，否则默认为0待审批
        List<UserSchoolRelEntity> list = userSchoolRelService.list(new LambdaQueryWrapper<UserSchoolRelEntity>()
                .eq(UserSchoolRelEntity::getSchoolId, reqModel.getSchoolId())
                .eq(UserSchoolRelEntity::getUserId, reqModel.getTeacherId()));
        if (ObjectUtils.isNotEmpty(list)) {
            entity.setLeaveStatus(Integer.parseInt(list.get(0).getPosition()) == 1 ? 1 : 0);
        } else {
            entity.setLeaveStatus(0);
        }
        this.save(entity);
    }

    @Override
    @Transactional
    public void update(Long id, TeacherLeaveSaveReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getStartTime(), reqModel.getEndTime())) {
            throw new BusinessException(LanguageConstants.START_TIME_AFTER_END_TIME);
        }
        checkDuplicate(id, reqModel);
        TeacherLeaveEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
    }

    /**
     * 校验时间是否可请假
     */
    private void checkDuplicate(Long id, TeacherLeaveSaveReqModel reqModel) {
        // 校验时间段是否重复
        QueryWrapper<TeacherLeaveEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TeacherLeaveEntity::getSchoolId, reqModel.getSchoolId())
                .eq(TeacherLeaveEntity::getTeacherId, reqModel.getTeacherId())
                .in(TeacherLeaveEntity::getLeaveStatus, Arrays.asList(0, 1))
                .le(TeacherLeaveEntity::getStartTime, reqModel.getEndTime().toLocalDate().atTime(LocalTime.MAX).withNano(0))
                .ge(TeacherLeaveEntity::getEndTime, reqModel.getStartTime().toLocalDate().atStartOfDay())
                .eq(TeacherLeaveEntity::getDeleted, 0);
        if (id != null) {
            wrapper.lambda().ne(TeacherLeaveEntity::getId, id);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(LanguageConstants.LEAVE_EXISTS);
        }
        // 校验请假日期是否包含非工作日
        List<SchoolCalendarEntity> calendarEntities = schoolCalendarService.list(Wrappers.<SchoolCalendarEntity>lambdaQuery()
                .eq(SchoolCalendarEntity::getSchoolId, reqModel.getSchoolId())
                .between(SchoolCalendarEntity::getStartDate, reqModel.getStartTime().toLocalDate(), reqModel.getEndTime().toLocalDate())
                .or()
                .between(SchoolCalendarEntity::getEndDate, reqModel.getStartTime().toLocalDate(), reqModel.getEndTime().toLocalDate())
                .eq(SchoolCalendarEntity::getSchoolId, reqModel.getSchoolId()));
        if (ObjectUtils.isNotEmpty(calendarEntities)) {
            List<Long> calendarIds = calendarEntities.stream().map(SchoolCalendarEntity::getId).collect(Collectors.toList());
            List<SchoolCalendarDateTypeEntity> list = schoolCalendarDateTypeService.list(Wrappers.<SchoolCalendarDateTypeEntity>lambdaQuery()
                    .in(SchoolCalendarDateTypeEntity::getSchoolCalendarId, calendarIds)
                    .in(SchoolCalendarDateTypeEntity::getType, Arrays.asList(2, 3))
                    .eq(SchoolCalendarDateTypeEntity::getApplyType, 1)
                    .between(SchoolCalendarDateTypeEntity::getCalendarDate, reqModel.getStartTime().toLocalDate(), reqModel.getEndTime().toLocalDate()));
            if (ObjectUtils.isNotEmpty(list)) {
                String message = languageUtil.getMessage(LanguageConstants.LEAVE_CONTAINS_NON_WORKING_DAYS);
                StringBuilder messageSB = new StringBuilder(message);
                list.forEach(schoolCalendarDateTypeEntity -> {
                    messageSB.append(schoolCalendarDateTypeEntity.getCalendarDate().getMonth()).append("月")
                            .append(schoolCalendarDateTypeEntity.getCalendarDate().getDayOfMonth()).append("日、");
                });
                messageSB.deleteCharAt(messageSB.length() - 1).append(" ").append(messageSB);
                throw new BusinessMessageException(messageSB.toString());
            }
        }
    }

    /**
     * 校验时间是否可请假
     */
    private void checkDuplicate(Long id, Long schoolId, Long userId, TeacherLeaveStartReqModel reqModel) {
        // 校验时间段是否重复
        QueryWrapper<TeacherLeaveEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TeacherLeaveEntity::getSchoolId, schoolId)
                .eq(TeacherLeaveEntity::getTeacherId, userId)
                .in(TeacherLeaveEntity::getLeaveStatus, Arrays.asList(0, 1))
                .le(TeacherLeaveEntity::getStartTime, reqModel.getEndTime().toLocalDate().atTime(LocalTime.MAX).withNano(0))
                .ge(TeacherLeaveEntity::getEndTime, reqModel.getStartTime().toLocalDate().atStartOfDay())
                .eq(TeacherLeaveEntity::getDeleted, 0);
        if (id != null) {
            wrapper.lambda().ne(TeacherLeaveEntity::getId, id);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(LanguageConstants.LEAVE_EXISTS);
        }
        // 校验请假日期是否包含非工作日
        List<SchoolCalendarEntity> calendarEntities = schoolCalendarService.list(Wrappers.<SchoolCalendarEntity>lambdaQuery()
                .eq(SchoolCalendarEntity::getSchoolId, schoolId)
                .between(SchoolCalendarEntity::getStartDate, reqModel.getStartTime().toLocalDate(), reqModel.getEndTime().toLocalDate())
                .or()
                .between(SchoolCalendarEntity::getEndDate, reqModel.getStartTime().toLocalDate(), reqModel.getEndTime().toLocalDate())
                .eq(SchoolCalendarEntity::getSchoolId, schoolId));
        if (ObjectUtils.isNotEmpty(calendarEntities)) {
            List<Long> calendarIds = calendarEntities.stream().map(SchoolCalendarEntity::getId).collect(Collectors.toList());
            List<SchoolCalendarDateTypeEntity> list = schoolCalendarDateTypeService.list(Wrappers.<SchoolCalendarDateTypeEntity>lambdaQuery()
                    .in(SchoolCalendarDateTypeEntity::getSchoolCalendarId, calendarIds)
                    .in(SchoolCalendarDateTypeEntity::getType, Arrays.asList(2, 3))
                    .eq(SchoolCalendarDateTypeEntity::getApplyType, 1)
                    .between(SchoolCalendarDateTypeEntity::getCalendarDate, reqModel.getStartTime().toLocalDate(), reqModel.getEndTime().toLocalDate()));
            if (ObjectUtils.isNotEmpty(list)) {
                String message = languageUtil.getMessage(LanguageConstants.LEAVE_CONTAINS_NON_WORKING_DAYS);
                StringBuilder messageSB = new StringBuilder(message);
                list.forEach(schoolCalendarDateTypeEntity -> {
                    messageSB.append(schoolCalendarDateTypeEntity.getCalendarDate().getMonth()).append("月")
                            .append(schoolCalendarDateTypeEntity.getCalendarDate().getDayOfMonth()).append("日、");
                });
                messageSB.deleteCharAt(messageSB.length() - 1).append(" ").append(messageSB);
                throw new BusinessMessageException(messageSB.toString());
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TeacherLeaveEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        this.removeById(id);
    }

    @Override
    public String export(TeacherLeavePageReqModel reqModel) {
        LocalDateTime queryStartTime = reqModel.getStartDate() != null ? reqModel.getStartDate().atStartOfDay() : null;
        LocalDateTime queryEndTime = reqModel.getEndDate() != null ? reqModel.getEndDate().atTime(LocalTime.MAX).withNano(0) : null;
        QueryWrapper<TeacherLeaveEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TeacherLeaveEntity::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0, TeacherLeaveEntity::getTeacherId, reqModel.getTeacherId())
                .eq(reqModel.getLeaveType() != null && reqModel.getLeaveType() > 0, TeacherLeaveEntity::getLeaveType, reqModel.getLeaveType())
                .in(ObjectUtils.isNotEmpty(reqModel.getLeaveStatus()), TeacherLeaveEntity::getLeaveStatus, reqModel.getLeaveStatus())
                .ge(queryStartTime != null, TeacherLeaveEntity::getEndTime, queryStartTime)
                .le(queryEndTime != null, TeacherLeaveEntity::getStartTime, queryEndTime)
                .eq(TeacherLeaveEntity::getDeleted, 0)
                .orderByDesc(BaseEntity::getCreateTime);
        List<TeacherLeaveEntity> list = this.list(wrapper.lambda().orderByDesc(TeacherLeaveEntity::getCreateTime));
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> userIds = list.stream().map(TeacherLeaveEntity::getTeacherId).collect(Collectors.toList());
            Map<Long, UserSchoolRelEntity> userMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(userIds)) {
                List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelDao.selectList(new LambdaQueryWrapper<UserSchoolRelEntity>()
                        .eq(UserSchoolRelEntity::getSchoolId, reqModel.getSchoolId())
                        .in(BaseEntity::getId, userIds)
                        .eq(UserSchoolRelEntity::getDeleted, false));
                if (CollectionUtils.isNotEmpty(userSchoolRelEntities)) {
                    userMap = userSchoolRelEntities.stream().collect(Collectors.toMap(UserSchoolRelEntity::getId, userSchoolRelEntity -> userSchoolRelEntity));
                }
            }
            List<TeacherLeavePageResModel> resList = new ArrayList<>();
            for (TeacherLeaveEntity entity : list) {
                TeacherLeavePageResModel resModel = new TeacherLeavePageResModel();
                BeanUtils.copyProperties(entity, resModel);
                UserSchoolRelEntity userSchoolRelEntity = userMap.get(entity.getTeacherId());
                if (userSchoolRelEntity != null) {
                    resModel.setTeacherName(userSchoolRelEntity.getUsername());
                }
                resList.add(resModel);
            }

            String fileName = "教师请假数据.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();

            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                fileName = "Teacher Leave and Absence Data.xlsx";
                List<TeacherLeaveExportEnModel> exportEnModels = resList.stream()
                        .map(resModel -> {
                            TeacherLeaveExportEnModel exportModel = new TeacherLeaveExportEnModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setStartTime(resModel.getStartTime().toString());
                            exportModel.setEndTime(resModel.getEndTime().toString());
                            exportModel.setLeaveType(formatLeaveType(resModel.getLeaveType(), SchoolLanguageEnum.EN_US));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, TeacherLeaveExportEnModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                fileName = "Dados de Licença e Faltas do Professor.xlsx";
                List<TeacherLeaveExportPtModel> exportPtModels = resList.stream()
                        .map(resModel -> {
                            TeacherLeaveExportPtModel exportModel = new TeacherLeaveExportPtModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setStartTime(resModel.getStartTime().toString());
                            exportModel.setEndTime(resModel.getEndTime().toString());
                            exportModel.setLeaveType(formatLeaveType(resModel.getLeaveType(), SchoolLanguageEnum.PT_PT));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, TeacherLeaveExportPtModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else {
                return exportFileHandler.doExportExcel(handleExportData(resList), fileName, TeacherLeaveExportModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            }
        }
        return null;
    }

    @Override
    public void handle(TeacherLeaveHandleReqModel reqModel) {
        TeacherLeaveEntity entity = this.getById(reqModel.getId());
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        if (entity.getLeaveStatus() != 0) {
            throw new BusinessException(LanguageConstants.NOT_PROCESSING_CANNOT_OPERATE);
        }
        if (reqModel.getHandleType() == 0 && StringUtils.isBlank(reqModel.getHandleOpinion())) {
            throw new BusinessException(LanguageConstants.REJECT_REASON_NOT_EMPTY);
        }
        if (reqModel.getHandleType() == 1) {//同意
            entity.setLeaveStatus(1);
        } else if (reqModel.getHandleType() == 2) {//撤回
            entity.setLeaveStatus(3);
        } else if (reqModel.getHandleType() == 0) {//拒绝
            entity.setLeaveStatus(2);
            entity.setHandleOpinion(reqModel.getHandleOpinion());
        } else {
            throw new BusinessException(LanguageConstants.PARAM_ERROR);
        }
        entity.setCompleteTime(LocalDateTime.now());
        this.updateById(entity);
    }

    @Override
    public List<TeacherLeaveReportResModel> report(TeacherLeaveReportReqModel reqModel) {
        // 处理需要计算的时间
        LocalDate startDate = reqModel.getStartDate() == null ? LocalDate.of(reqModel.getYear(), reqModel.getMonth(), 1) : reqModel.getStartDate();
        LocalDate endDate = reqModel.getEndDate() == null ? LocalDate.of(reqModel.getYear(), reqModel.getMonth(), startDate.lengthOfMonth()) : reqModel.getEndDate();
        // 获取本月应出勤时间，获取学校所有校历
        List<SchoolCalendarEntity> calendarEntities = schoolCalendarService.list(Wrappers.<SchoolCalendarEntity>lambdaQuery()
                .eq(SchoolCalendarEntity::getSchoolId, reqModel.getSchoolId()));
        long shouldAttendanceDays = 0L;
        List<LocalDate> workDates = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(calendarEntities)) {
            List<Long> calendarIds = calendarEntities.stream().map(SchoolCalendarEntity::getId).collect(Collectors.toList());
            List<SchoolCalendarDateTypeEntity> list = schoolCalendarDateTypeService.list(Wrappers.<SchoolCalendarDateTypeEntity>lambdaQuery()
                    .in(SchoolCalendarDateTypeEntity::getSchoolCalendarId, calendarIds)
                    .eq(SchoolCalendarDateTypeEntity::getType, 1)
                    .eq(SchoolCalendarDateTypeEntity::getApplyType, 1)
                    .between(SchoolCalendarDateTypeEntity::getCalendarDate, startDate, endDate));
            if (ObjectUtils.isNotEmpty(list)) {
                shouldAttendanceDays = list.size();
                workDates = list.stream().map(SchoolCalendarDateTypeEntity::getCalendarDate).collect(Collectors.toList());
            }
        }
        // 获取出勤规则
        List<TeacherAttendanceRule> attendanceRules = teacherAttendanceRuleService.list(Wrappers.<TeacherAttendanceRule>lambdaQuery()
                .eq(TeacherAttendanceRule::getSchoolId, reqModel.getSchoolId()));
        // 获取时间范围内所有出勤记录
        List<TeacherAttendanceEntity> attendanceList = teacherAttendanceService.list(Wrappers.<TeacherAttendanceEntity>lambdaQuery()
                .eq(TeacherAttendanceEntity::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0,
                        TeacherAttendanceEntity::getTeacherId, reqModel.getTeacherId())
                .between(TeacherAttendanceEntity::getAttendanceDate, startDate, endDate));
        Map<Long, List<TeacherAttendanceEntity>> teacherIdAttMap = attendanceList.stream().collect(Collectors.groupingBy(TeacherAttendanceEntity::getTeacherId));
        // 获取时间内的所有请假记录
        List<TeacherLeaveEntity> leaveList = this.list(Wrappers.<TeacherLeaveEntity>lambdaQuery()
                .eq(TeacherLeaveEntity::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0,
                        TeacherLeaveEntity::getTeacherId, reqModel.getTeacherId())
                .and(wrapper -> wrapper
                        .between(TeacherLeaveEntity::getStartTime, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX).withNano(0))
                        .or()
                        .between(TeacherLeaveEntity::getEndTime, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX).withNano(0))));
        Map<Long, List<TeacherLeaveEntity>> idLeaveMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(leaveList)) {
            List<Long> leaveIds = leaveList.stream().map(TeacherLeaveEntity::getId).collect(Collectors.toList());
            QueryWrapper<ActApprovalInstanceEntity> approvalInstanceWrapper = new QueryWrapper<>();
            approvalInstanceWrapper.lambda().in(ActApprovalInstanceEntity::getBusinessId, leaveIds)
                    .eq(ActApprovalInstanceEntity::getProcessType, ActProcessTemplateTypeEnum.TEACHER_LEAVE.getCode());
            List<ActApprovalInstanceEntity> instanceEntities = actApprovalInstanceService.list(approvalInstanceWrapper);
            Map<Long, ActApprovalInstanceEntity> instanceMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(instanceEntities)) {
                instanceMap = instanceEntities.stream().collect(Collectors.toMap(ActApprovalInstanceEntity::getBusinessId, instance -> instance));
            }
            Iterator<TeacherLeaveEntity> iterator = leaveList.iterator();
            while (iterator.hasNext()) {
                TeacherLeaveEntity next = iterator.next();
                if (next.getLeaveStatus() == null) {
                    ActApprovalInstanceEntity actApprovalInstanceEntity = instanceMap.get(next.getId());
                    if (actApprovalInstanceEntity != null && !actApprovalInstanceEntity.getStatus().equals(ActApprovalInstanceStatusEnum.COMPLETED.getCode())) {
                        iterator.remove();
                    }
                } else if (!next.getLeaveStatus().equals(1)) {
                    iterator.remove();
                }
            }
            if (CollectionUtils.isNotEmpty(leaveList)) {
                idLeaveMap = leaveList.stream().collect(Collectors.groupingBy(TeacherLeaveEntity::getTeacherId));
            }
        }
        // 获取时间范围内所有的记录
        List<TeacherBusinessEntity> businessList = teacherBusinessService.list(Wrappers.<TeacherBusinessEntity>lambdaQuery()
                .eq(TeacherBusinessEntity::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0,
                        TeacherBusinessEntity::getTeacherId, reqModel.getTeacherId())
                .and(wrapper -> wrapper
                        .between(TeacherBusinessEntity::getStartTime, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX).withNano(0))
                        .or()
                        .between(TeacherBusinessEntity::getEndTime, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX).withNano(0))));
        Map<Long, List<TeacherBusinessEntity>> idBusMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(businessList)) {
            List<Long> businessIds = businessList.stream().map(TeacherBusinessEntity::getId).collect(Collectors.toList());
            QueryWrapper<ActApprovalInstanceEntity> approvalInstanceWrapper = new QueryWrapper<>();
            approvalInstanceWrapper.lambda().in(ActApprovalInstanceEntity::getBusinessId, businessIds)
                    .eq(ActApprovalInstanceEntity::getProcessType, ActProcessTemplateTypeEnum.TEACHER_BUSINESS.getCode());
            List<ActApprovalInstanceEntity> instanceEntities = actApprovalInstanceService.list(approvalInstanceWrapper);
            Map<Long, ActApprovalInstanceEntity> instanceMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(instanceEntities)) {
                instanceMap = instanceEntities.stream().collect(Collectors.toMap(ActApprovalInstanceEntity::getBusinessId, instance -> instance));
            }
            Iterator<TeacherBusinessEntity> iterator = businessList.iterator();
            while (iterator.hasNext()) {
                TeacherBusinessEntity next = iterator.next();
                ActApprovalInstanceEntity actApprovalInstanceEntity = instanceMap.get(next.getId());
                if (actApprovalInstanceEntity != null && !actApprovalInstanceEntity.getStatus().equals(ActApprovalInstanceStatusEnum.COMPLETED.getCode())) {
                    iterator.remove();
                }
            }
            if (CollectionUtils.isNotEmpty(businessList)) {
                idBusMap = businessList.stream().collect(Collectors.groupingBy(TeacherBusinessEntity::getTeacherId));
            }
        }
        // 获取学校所有老师
        LambdaQueryWrapper<UserSchoolRelEntity> where = Wrappers.<UserSchoolRelEntity>lambdaQuery()
                .eq(UserSchoolRelEntity::getSchoolId, reqModel.getSchoolId())
                .eq(StringUtils.isNotEmpty(reqModel.getLeaveType()) && !reqModel.getLeaveType().equals("0"), UserSchoolRelEntity::getPosition, reqModel.getLeaveType())
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0,
                        UserSchoolRelEntity::getUserId, reqModel.getTeacherId())
                .eq(UserSchoolRelEntity::getStatus, 1);
        if (StringUtils.isNotEmpty(reqModel.getNameOrCode())) {
            where.and(wrapper -> wrapper
                    .like(UserSchoolRelEntity::getUsername, reqModel.getNameOrCode())
                    .or()
                    .like(UserSchoolRelEntity::getUserNumber, reqModel.getNameOrCode()));
        }
        List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelService.list(where);
        if (ObjectUtils.isEmpty(userSchoolRelEntities)) {
            return Collections.emptyList();
        }
        //获取教师部门信息
        List<Long> teacherIds = userSchoolRelEntities.stream().map(UserSchoolRelEntity::getId).collect(Collectors.toList());
        QueryWrapper<UserDeptRelEntity> userDeptRelQueryWrapper = new QueryWrapper<>();
        userDeptRelQueryWrapper.lambda().eq(UserDeptRelEntity::getSchoolId, reqModel.getSchoolId())
                .in(UserDeptRelEntity::getUserId, teacherIds)
                .eq(UserDeptRelEntity::getIsMaster, 1);
        List<UserDeptRelEntity> userDeptRelEntities = userDeptRelService.list(userDeptRelQueryWrapper);
        Map<Long, UserDeptRelEntity> userDeptMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userDeptRelEntities)) {
            userDeptMap = userDeptRelEntities.stream().collect(Collectors.toMap(UserDeptRelEntity::getUserId, userDeptRel -> userDeptRel));
        }
        // 处理数据
        List<TeacherLeaveReportResModel> reportResModels = new ArrayList<>();
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage());
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        for (UserSchoolRelEntity teacher : userSchoolRelEntities) {
            // 获取老师的公务和请假信息
            List<TeacherLeaveEntity> teacherLeaveEntities = idLeaveMap.getOrDefault(teacher.getUserId(), new ArrayList<>());
            List<TeacherBusinessEntity> teacherBusinessEntities = idBusMap.getOrDefault(teacher.getUserId(), new ArrayList<>());
            // 拼装返回数据
            TeacherLeaveReportResModel resModel = new TeacherLeaveReportResModel();
            resModel.setTeacherName(teacher.getUsername());
            resModel.setTeacherNumber(teacher.getUserNumber());
            resModel.setTeacherPosition(JobTitleEnum.getValue(teacher.getPosition(), languageEnum));
            resModel.setShouldAttendanceDays(Math.toIntExact(shouldAttendanceDays));
            resModel.setLeaveCount(teacherLeaveEntities.size());
            int lateCount = 0;
            int earlyCount = 0;
            int businessCount = 0;
            int noReasonCount = 0;
            int actualAttendanceDays = 0;
            Map<LocalDate, TeacherAttendanceEntity> dateAttMap = new HashMap<>();
            if (teacherIdAttMap.containsKey(teacher.getUserId())) {
                List<TeacherAttendanceEntity> teacherAttendanceEntities = teacherIdAttMap.get(teacher.getUserId());
                dateAttMap = teacherAttendanceEntities.stream().collect(Collectors.toMap(TeacherAttendanceEntity::getAttendanceDate, Function.identity()));
            }
            for (int i = 1; i <= endDate.lengthOfMonth(); i++) {
                LocalDate today = LocalDate.of(reqModel.getYear(), reqModel.getMonth(), i);
                // 今天若不是工作日，则不计算
                if (!workDates.contains(today)) continue;
                // 获取老师的考勤规则
                Long deptId = userDeptMap.get(teacher.getId()) == null ? null : userDeptMap.get(teacher.getId()).getDeptId();
                TeacherAttendanceRule teacherAttendanceRule = getTeacherAttendanceRule(deptId, teacher.getUserId(), today.getDayOfWeek().getValue(), attendanceRules);
                // 获取今日出勤记录
                TeacherAttendanceEntity attendanceEntity = dateAttMap.get(today);
                // 实际出勤天数
                if (attendanceEntity != null) {
                    actualAttendanceDays++;
                }
                // 获取今天的公务和请假信息，查询时间覆盖今天的
                List<TeacherLeaveEntity> todayLeave = teacherLeaveEntities.stream()
                        .filter(a -> a.getStartTime().compareTo(today.atTime(23,59,59)) <= 0
                                && a.getEndTime().compareTo(today.atTime(0,0,0)) > 0)
                        .collect(Collectors.toList());
                List<TeacherBusinessEntity> todayBus = teacherBusinessEntities.stream()
                        .filter(a -> a.getStartTime().compareTo(today.atTime(23,59,59)) <= 0
                                && a.getEndTime().compareTo(today.atTime(0,0,0)) > 0)
                        .collect(Collectors.toList());
                // 判断是否有公务
                if (!todayBus.isEmpty()) {
                    businessCount++;
                }
                // 无由不打卡情况
                if (ObjectUtils.isEmpty(attendanceEntity) &&
                        todayLeave.isEmpty() && todayBus.isEmpty()) {
                    noReasonCount++;
                }
                if (attendanceEntity != null && StringUtils.isNotEmpty(attendanceEntity.getStatus())) {
                    // 判断迟到
                    if (attendanceEntity.getStatus().contains("2")) {
                        boolean isLate = true;
                        if (teacherAttendanceRule != null) {
                            // 判断是否有请假
                            if (!todayLeave.isEmpty()) {
                                // 遍历今天所有请假记录
                                for (TeacherLeaveEntity leaveEntity : todayLeave) {
                                    isLate = DateUtils.getIsLate(leaveEntity.getStartTime(), leaveEntity.getEndTime(), today, teacherAttendanceRule, attendanceEntity);
                                }
                            }
                            // 判断是否有公务
                            if (!todayBus.isEmpty()) {
                                // 遍历今天所有公务记录
                                for (TeacherBusinessEntity businessEntity : todayBus) {
                                    isLate = DateUtils.getIsLate(businessEntity.getStartTime(), businessEntity.getEndTime(), today, teacherAttendanceRule, attendanceEntity);
                                }
                            }
                        }
                        if (isLate)
                            lateCount++;
                    }
                    // 判断早退
                    if (attendanceEntity.getStatus().contains("3")) {
                        boolean isEarly = true;
                        if (teacherAttendanceRule != null) {
                            // 判断是否有请假
                            if (!todayLeave.isEmpty()) {
                                // 遍历今天所有请假记录
                                for (TeacherLeaveEntity leaveEntity : todayLeave) {
                                    isEarly = DateUtils.getIsEarly(leaveEntity.getStartTime(), leaveEntity.getEndTime(), today, teacherAttendanceRule, attendanceEntity);
                                }
                            }
                            // 判断是否有公务
                            if (!todayBus.isEmpty()) {
                                // 遍历今天所有公务记录
                                for (TeacherBusinessEntity businessEntity : todayBus) {
                                    isEarly = DateUtils.getIsEarly(businessEntity.getStartTime(), businessEntity.getEndTime(), today, teacherAttendanceRule, attendanceEntity);
                                }
                            }
                        }
                        if (isEarly)
                            earlyCount++;
                    }
                }
            }
            resModel.setLateCount(lateCount);
            resModel.setEarlyCount(earlyCount);
            resModel.setOfficialCount(businessCount);
            resModel.setNoReasonCount(noReasonCount);
            resModel.setActualAttendanceDays(actualAttendanceDays);
            reportResModels.add(resModel);
        }
        return reportResModels;
    }

    private TeacherAttendanceRule getTeacherAttendanceRule(Long deptId, Long teacherId, Integer dayOfWeek, List<TeacherAttendanceRule> attendanceRules) {
        if (CollectionUtils.isEmpty(attendanceRules)) {
            return null;
        }
        // 过滤出包含当天的规则
        List<TeacherAttendanceRule> effectiveRules = attendanceRules.stream()
                .filter(rule -> {
                    // 判断是否包含当天
                    return StringUtils.isNotBlank(rule.getEffectiveScope()) && JSON.parseArray(rule.getEffectiveScope(), Integer.class).contains(dayOfWeek);
                })
                .collect(Collectors.toList());
        if (effectiveRules.isEmpty()) {
            return null;
        }
        // 按照规则类型优先级排序：特殊规则 > 默认规则
        effectiveRules.sort((r1, r2) -> {
            // 规则类型优先级
            if (!r1.getType().equals(r2.getType())) {
                return Integer.compare(r2.getType(), r1.getType());
            }
            // 同类型按创建时间倒序
            return r2.getCreateTime().compareTo(r1.getCreateTime());
        });
        // 依次匹配部门规则、用户规则
        for (TeacherAttendanceRule rule : effectiveRules) {
            // 部门规则匹配
            if (deptId != null && StringUtils.isNotBlank(rule.getDepIds()) && JSON.parseArray(rule.getDepIds(), Long.class).contains(deptId)) {
                return rule;
            }
            // 用户规则匹配
            if (StringUtils.isNotBlank(rule.getUserIds()) && JSON.parseArray(rule.getUserIds(), Long.class).contains(teacherId)) {
                return rule;
            }
        }
        return null;
    }


    @Override
    public ResponseEntity<byte[]> reportExport(TeacherLeaveReportReqModel reqModel) throws UnsupportedEncodingException {
        List<TeacherLeaveReportResModel> resModels = report(reqModel);
        // 设置导出相应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        // 根据语言，导出不同的语言文档
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
            List<TeacherLeaveReportExportEnModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        TeacherLeaveReportExportEnModel exportModel = new TeacherLeaveReportExportEnModel();
                        BeanUtils.copyProperties(resModel, exportModel);
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, TeacherLeaveReportExportEnModel.class)
                    .sheet("Teacher attendance report")
                    .doWrite(exportEnModels);
            headers.setContentDispositionFormData("attachment", "Teacher attendance report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } else if (currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode())) {
            List<TeacherLeaveReportExportModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        TeacherLeaveReportExportModel exportModel = new TeacherLeaveReportExportModel();
                        BeanUtils.copyProperties(resModel, exportModel);
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, TeacherLeaveReportExportModel.class)
                    .sheet("教師出勤報表")
                    .doWrite(exportEnModels);
            String encodedFileName = URLEncoder.encode("教師出勤報表_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx", "UTF-8");
            headers.setContentDispositionFormData("attachment", encodedFileName);
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
            List<TeacherLeaveReportExportPtModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        TeacherLeaveReportExportPtModel exportModel = new TeacherLeaveReportExportPtModel();
                        BeanUtils.copyProperties(resModel, exportModel);
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, TeacherLeaveReportExportPtModel.class)
                    .sheet("Declaração de presença do professor")
                    .doWrite(exportEnModels);
            headers.setContentDispositionFormData("attachment", "Declaração de presença do professor_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        }
        return null;
    }

    private String formatLeaveType(Integer leaveType, SchoolLanguageEnum language) {
        if (leaveType == null) {
            return "";
        }

        TeacherLeaveTypeEnum leaveTypeEnum = TeacherLeaveTypeEnum.toEnum(leaveType);
        if (leaveTypeEnum == null) {
            return "";
        }

        if (language == SchoolLanguageEnum.EN_US) {

            //Personal Leave
            //Sick Leave
            //Annual Leave
            //Maternity Leave
            //Paternity Leave
            //Marriage Leave
            //Bereavement Leave
            //Prenatal Check-up Leave
            //Parental Leave
            switch (leaveTypeEnum) {
                case PERSONAL_LEAVE:
                    return "Personal Leave";
                case SICK_LEAVE:
                    return "Sick Leave";
                case ANNUAL_LEAVE:
                    return "Annual Leave";
                case MATERNITY_LEAVE:
                    return "Maternity Leave";
                case PATERNITY_LEAVE:
                    return "Paternity Leave";
                case MARRIAGE_LEAVE:
                    return "Marriage Leave";
                case BEREAVEMENT_LEAVE:
                    return "Bereavement Leave";
                case PRENATAL_LEAVE:
                    return "Prenatal Check-up Leave";
                case PARENTING_LEAVE:
                    return "Parental Leave";
                default:
                    return "";
            }
        } else if (language == SchoolLanguageEnum.PT_PT) {

            //Licença Pessoal
            //Licença Médica
            //Licença Anual
            //Licença Maternidade
            //Licença Paternidade
            //Licença de Casamento
            //Licença por Luto
            //Licença para Exame Pré-Natal
            //Licença Parental
            switch (leaveTypeEnum) {
                case PERSONAL_LEAVE:
                    return "Licença Pessoal";
                case SICK_LEAVE:
                    return "Licença Médica";
                case ANNUAL_LEAVE:
                    return "Licença Anual";
                case MATERNITY_LEAVE:
                    return "Licença Maternidade";
                case PATERNITY_LEAVE:
                    return "Licença Paternidade";
                case MARRIAGE_LEAVE:
                    return "Licença de Casamento";
                case BEREAVEMENT_LEAVE:
                    return "Licença por Luto";
                case PRENATAL_LEAVE:
                    return "Licença para Exame Pré-Natal";
                case PARENTING_LEAVE:
                    return "Licença Parental";
                default:
                    return "";
            }
        }

        return leaveTypeEnum.getName();
    }

    private List<TeacherLeaveExportModel> handleExportData(List<TeacherLeavePageResModel> exportDTOS) {
        List<TeacherLeaveExportModel> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(exportDTOS)) {
            exportDTOS.forEach(resModel -> {
                TeacherLeaveExportModel exportModel = new TeacherLeaveExportModel();
                BeanUtils.copyProperties(resModel, exportModel);
                exportModel.setLeaveType(TeacherLeaveTypeEnum.getNameByCode(resModel.getLeaveType()));
                exportModel.setStartTime(resModel.getStartTime().toString());
                exportModel.setEndTime(resModel.getEndTime().toString());
                result.add(exportModel);
            });
        }
        return result;
    }

    @Override
    @Transactional
    public void start(Long schoolId, Long userId, TeacherLeaveStartReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getStartTime(), reqModel.getEndTime())) {
            throw new BusinessException(LanguageConstants.START_TIME_AFTER_END_TIME);
        }
        checkDuplicate(null, schoolId, userId, reqModel);
        TeacherLeaveEntity entity = BeanConvertUtil.convert(reqModel, TeacherLeaveEntity.class);
        entity.setSchoolId(schoolId);
        entity.setTeacherId(userId);
        this.save(entity);
        // 关联所有文件
        if (CollectionUtils.isNotEmpty(reqModel.getFileIds())) {
            sysFileRelevanceService.saveBatch(reqModel.getFileIds().stream().map(fileId -> {
                SysFileRelevanceEntity fileRelevanceEntity = new SysFileRelevanceEntity();
                fileRelevanceEntity.setFileId(fileId);
                fileRelevanceEntity.setType(FileRelevanceTypeEnum.TEACHER_LEAVE.getType());
                fileRelevanceEntity.setBusinessId(entity.getId());
                fileRelevanceEntity.setSchoolId(schoolId);
                return fileRelevanceEntity;
            }).collect(Collectors.toList()));
        }
        //发起请假流程
        ActApprovalInstancePreviewReqModel previewReqModel = new ActApprovalInstancePreviewReqModel();
        previewReqModel.setTemplateId(reqModel.getTemplateId());
        previewReqModel.setDefinitionId(reqModel.getDefinitionId());
        previewReqModel.setApplyDays(reqModel.getLeaveDays());
        previewReqModel.setLeaveType(reqModel.getLeaveType());
        previewReqModel.setApprover(reqModel.getApprover());
        actApprovalInstanceService.startProcess(schoolId, userId, entity.getId(), previewReqModel);
    }
}
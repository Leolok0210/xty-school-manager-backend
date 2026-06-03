package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.TeacherAttendanceDao;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.listener.TeacherAttendanceImportEnUsListener;
import com.xiaotiyun.school.manager.listener.TeacherAttendanceImportPtPtListener;
import com.xiaotiyun.school.manager.listener.TeacherAttendanceImportZhTwListener;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.dto.TeacherAttendanceImportDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.TeacherAttendancePageReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherAttendanceStatisticsReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherAttendanceUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.TeacherAttendancePageResModel;
import com.xiaotiyun.school.manager.model.res.TeacherAttendanceStatisticsResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherAttendanceServiceImpl extends ServiceImpl<TeacherAttendanceDao, TeacherAttendanceEntity>
        implements TeacherAttendanceService {
    private final UserSchoolRelDao userSchoolRelDao;
    private final ExportFileHandler exportFileHandler;
    private final ImportTaskService importTaskService;
    private final ImportRecordService importRecordService;
    private final UserSchoolRelService userSchoolRelService;
    private final UserDeptRelService userDeptRelService;
    private final TeacherAttendanceRuleService teacherAttendanceRuleService;
    @Lazy
    @Resource
    private TeacherLeaveService teacherLeaveService;
    @Lazy
    @Resource
    private TeacherBusinessService teacherBusinessService;

    private final LanguageUtil languageUtil;
    private static final ExecutorService importPool = new ThreadPoolExecutor(10, 15, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100));

    @Override
    public PageInfo<TeacherAttendancePageResModel> page(TeacherAttendancePageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        QueryWrapper<TeacherAttendanceEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TeacherAttendanceEntity::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0, TeacherAttendanceEntity::getTeacherId, reqModel.getTeacherId())
                .eq(reqModel.getStatus() != null && reqModel.getStatus() > 0, TeacherAttendanceEntity::getStatus, reqModel.getStatus())
                .ge(reqModel.getStartDate() != null, TeacherAttendanceEntity::getAttendanceDate, reqModel.getStartDate())
                .le(reqModel.getEndDate() != null, TeacherAttendanceEntity::getAttendanceDate, reqModel.getEndDate())
                .eq(TeacherAttendanceEntity::getDeleted, 0);
        List<TeacherAttendanceEntity> list = this.list(wrapper.lambda().orderByDesc(TeacherAttendanceEntity::getAttendanceDate));
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> userIds = list.stream().map(TeacherAttendanceEntity::getTeacherId).collect(Collectors.toList());
            //获取学校相关关系
            Map<Long, UserSchoolRelEntity> userSchoolRelMap = new HashMap<>();
            List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelDao.selectList(
                    new LambdaQueryWrapper<UserSchoolRelEntity>()
                            .eq(UserSchoolRelEntity::getSchoolId, reqModel.getSchoolId())
                            .in(UserSchoolRelEntity::getUserId, userIds)
                            .eq(UserSchoolRelEntity::getDeleted, 0));
            if (CollectionUtils.isNotEmpty(userSchoolRelEntities)) {
                userSchoolRelMap = userSchoolRelEntities.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, userSchoolRelEntity -> userSchoolRelEntity));
            }
            PageInfo<TeacherAttendanceEntity> pageInfo = new PageInfo<>(list);
            List<TeacherAttendancePageResModel> resList = new ArrayList<>();
            for (TeacherAttendanceEntity teacherAttendanceEntity : list) {
                TeacherAttendancePageResModel resModel = new TeacherAttendancePageResModel();
                BeanUtils.copyProperties(teacherAttendanceEntity, resModel);
                UserSchoolRelEntity userSchoolRelEntity = userSchoolRelMap.get(teacherAttendanceEntity.getTeacherId());
                if (userSchoolRelEntity != null) {
                    resModel.setTeacherName(userSchoolRelEntity.getUsername());
                    resModel.setTeacherNumber(userSchoolRelEntity.getUserNumber());
                }
                resList.add(resModel);
            }
            PageInfo<TeacherAttendancePageResModel> result = new PageInfo<>(resList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            return result;
        }
        return null;
    }

    @Transactional
    @Override
    public void update(Long id, TeacherAttendanceUpdateReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getClockInTime(), reqModel.getClockOutTime())) {
            throw new BusinessException(LanguageConstants.CLOCK_IN_BEFORE_CLOCK_OUT);
        }
        TeacherAttendanceEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        BeanUtils.copyProperties(reqModel, entity);
        //更新状态
        LocalTime ruleClockInTime = null;
        LocalTime ruleClockOutTime = null;
        //获取考勤规则信息
        QueryWrapper<TeacherAttendanceRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TeacherAttendanceRule::getSchoolId, entity.getSchoolId())
                .eq(TeacherAttendanceRule::getDeleted, 0);
        List<TeacherAttendanceRule> attendanceRules = teacherAttendanceRuleService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(attendanceRules)) {
            //获取用户部门
            QueryWrapper<UserSchoolRelEntity> userWrapper = new QueryWrapper<>();
            userWrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, entity.getSchoolId())
                    .eq(UserSchoolRelEntity::getUserId, entity.getTeacherId());
            UserSchoolRelEntity userSchoolRelEntity = userSchoolRelService.getOne(userWrapper);
            if (userSchoolRelEntity != null) {
                QueryWrapper<UserDeptRelEntity> deptWrapper = new QueryWrapper<>();
                deptWrapper.lambda().eq(UserDeptRelEntity::getSchoolId, entity.getSchoolId())
                        .eq(UserDeptRelEntity::getUserId, userSchoolRelEntity.getId())
                        .eq(UserDeptRelEntity::getIsMaster, 1);
                UserDeptRelEntity userDeptRel = userDeptRelService.getOne(deptWrapper);
                Long deptId = userDeptRel == null ? null : userDeptRel.getDeptId();
                TeacherAttendanceRule attendanceRule = getTeacherAttendanceRule(deptId, entity.getTeacherId(), entity.getAttendanceDate().getDayOfWeek().getValue(), attendanceRules);
                if (attendanceRule != null) {
                    ruleClockInTime = attendanceRule.getClockInTime();
                    ruleClockOutTime = attendanceRule.getClockOutTime();
                }
            }
        }
        //判断状态
        Set<Integer> status = checkStatus(entity, ruleClockInTime, ruleClockOutTime);
        if (CollectionUtils.isNotEmpty(status)) {
            entity.setStatus(StringUtils.join(status, ","));
        } else {
            entity.setStatus("");
        }
        this.updateById(entity);
    }

    private TeacherAttendanceRule getTeacherAttendanceRule(Long deptId, Long teacherId, Integer dayOfWeek, List<TeacherAttendanceRule> attendanceRules) {
        if (CollectionUtils.isEmpty(attendanceRules)) {
            return null;
        }
        // 过滤出包含当天的规则
        List<TeacherAttendanceRule> effectiveRules = attendanceRules.stream()
                .filter(rule -> {
                    // 判断是否包含当天
                    return com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(rule.getEffectiveScope()) && JSON.parseArray(rule.getEffectiveScope(), Integer.class).contains(dayOfWeek);
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
    public void delete(Long id) {
        TeacherAttendanceEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        this.removeById(id);
    }

    @Override
    public Long importRecord(Long schoolId, MultipartFile file) {
        if (schoolId == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.NO_SCHOOL_FILE_CONTENT_EMPTY));
        }
        //获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        // 读取Excel文件
        List<TeacherAttendanceImportModel> list = readExcelData(file, languageEnum);
        if (CollectionUtils.isNotEmpty(list)) {
            // 创建导入任务
            ImportTaskEntity task = new ImportTaskEntity();
            task.setSchoolId(schoolId);
            task.setFileName(file.getOriginalFilename());
            task.setType(ImportTaskTypeEnum.TEACHER_ATTENDANCE.getCode());
            task.setTotalCount(0);
            task.setSuccessCount(0);
            task.setFailCount(0);
            importTaskService.save(task);
            CompletableFuture.runAsync(() -> {
                try {
                    languageUtil.setLanguage(languageEnum.getCode());
                    log.info("当前使用的语言是:{}", LanguageUtil.getCurrentLanguage());
                    handleImportData(task, list, schoolId);
                } catch (Exception e) {
                    log.error("导入教师考勤taskId=【{}】异常={}",task.getId(),e);
                    throw e;
                }finally {
                    LanguageUtil.clearLanguage();
                }
            }, importPool).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("导入教师考勤任务执行结束taskId=【{}】异常={}",task.getId(),ex);
                } else {
                    log.info("导入教师考勤完成，任务ID={}",task.getId());
                }
                task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                importTaskService.updateById(task);
            });
            return task.getId();
        }
        return null;
    }

    private int processBatchExcelLine(List<ImportRecordSaveDTO> importErrorDTOS, List<TeacherAttendanceImportModel> list, Long schoolId, List<TeacherAttendanceImportDTO> correctList) {
        if (CollectionUtils.isNotEmpty(list)) {
            int correctCount = list.size();//正确处理的条数
            Map<String, UserSchoolRelEntity> userNumberMap = new HashMap<>();
            Set<String> userNumbers = list.stream().map(TeacherAttendanceImportModel::getUserNumber).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(userNumbers)) {
                QueryWrapper<UserSchoolRelEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                        .in(UserSchoolRelEntity::getUserNumber, userNumbers);
                List<UserSchoolRelEntity> userSchoolRelEntityList = userSchoolRelService.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(userSchoolRelEntityList)) {
                    userNumberMap = userSchoolRelEntityList.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserNumber, userSchoolRelDTO -> userSchoolRelDTO, (key1, key2) -> key1));
                }
            }
            //遍历要插入的每一行
            for (TeacherAttendanceImportModel bo : list) {
                List<String> studentErrorList = new ArrayList<>();
                if (!check(bo, studentErrorList, userNumberMap)) {
                    //不合法
                    correctCount--;
                    if (CollectionUtils.isNotEmpty(studentErrorList)) {
                        ImportRecordSaveDTO errorDTO = new ImportRecordSaveDTO();
                        errorDTO.setIncorrectLineno(String.valueOf(bo.getExcelLineNo()));
                        errorDTO.setIncorrectReason(StringUtils.join(studentErrorList, "；"));
                        importErrorDTOS.add(errorDTO);
                    }
                    continue;
                }
                correctList.add(teacherAttendanceImportConvert(bo));
            }
            return correctCount;
        }
        return 0;
    }

    private TeacherAttendanceImportDTO teacherAttendanceImportConvert(TeacherAttendanceImportModel bo) {
        TeacherAttendanceImportDTO result = new TeacherAttendanceImportDTO();
        result.setUserNumber(bo.getUserNumber());
        result.setTeacherName(bo.getTeacherName());
        result.setCardNumber(bo.getCardNumber());
        Date attendanceDate;
        if (StringUtils.isNumeric(bo.getAttendanceDate())) {
            //execl日期格式解析为全数字，如：43444
            attendanceDate = DateUtil.getJavaDate(Double.parseDouble(bo.getAttendanceDate()));
        } else if (bo.getAttendanceDate().contains("/")) {
            //字符串格式日期，如2024/12/18
            attendanceDate = DateUtils.formatStringToDate(bo.getAttendanceDate(), "yyyy/MM/dd");
        } else {
            //字符串格式日期，如2024-12-18
            attendanceDate = DateUtils.formatStringToDate(bo.getAttendanceDate(), "yyyy-MM-dd");
        }
        Instant instant = attendanceDate.toInstant();
        ZoneId zoneId = ZoneId.systemDefault(); // 使用系统默认时区
        result.setAttendanceDate(instant.atZone(zoneId).toLocalDate());
        String time = bo.getAttendanceTime().replaceAll("：", ":");
        String[] split = time.split(":");
        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        for (String s : split) {
            if (i == 1 && s.length() == 1) {
                stringBuilder.append("0").append(s);
            } else {
                stringBuilder.append(s);
            }
            if (i < split.length) {
                stringBuilder.append(":");
            }
            i++;
        }
        time = stringBuilder.toString();
        int count = time.split(":", -1).length - 1;
        if (count == 1) {
            //字符串格式时间，如18:30
            result.setAttendanceTime(DateUtils.formatStringToLocalTime(time, DateUtils.DEFAULT_PATTERN_SHORT_TIME));
        } else if (count == 2) {
            //字符串格式时间，如18:30:00
            result.setAttendanceTime(DateUtils.formatStringToLocalTime(time, DateUtils.DEFAULT_PATTERN_TIME));
        }
        return result;
    }

    private boolean check(TeacherAttendanceImportModel bo, List<String> studentErrorList, Map<String, UserSchoolRelEntity> userNumberMap) {
        //一项一项检查
        if (!StringUtils.isNotBlank(bo.getUserNumber()) || !StringUtils.isNotBlank(bo.getTeacherName())) {
            if (!StringUtils.isNotBlank(bo.getUserNumber())) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.USER_NUMBER_REQUIRED));
            }
            if (!StringUtils.isNotBlank(bo.getTeacherName())) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.TEACHER_NAME_REQUIRED));
            }
        } else {
            UserSchoolRelEntity userSchoolRel = userNumberMap.get(bo.getUserNumber());
            if (userSchoolRel != null) {
                if (!userSchoolRel.getUsername().equals(bo.getTeacherName())) {
                    studentErrorList.add(languageUtil.getMessage(LanguageConstants.TEACHER_NAME_NOT_MATCH));
                }
            } else {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.TEACHER_NUMBER_NOT_FOUND));
            }
        }
        if (StringUtils.isNotBlank(bo.getAttendanceDate())) {
            try {
                if (StringUtils.isNumeric(bo.getAttendanceDate())) {
                    //execl日期格式解析为全数字，如：43444
                    DateUtil.getJavaDate(Double.parseDouble(bo.getAttendanceDate()));
                } else if (bo.getAttendanceDate().contains("/")) {
                    //字符串格式日期，如2024/12/18
                    DateUtils.formatStringToDate(bo.getAttendanceDate(), "yyyy/MM/dd");
                } else {
                    //字符串格式日期，如2024-12-18
                    DateUtils.formatStringToDate(bo.getAttendanceDate(), "yyyy-MM-dd");
                }
            } catch (Exception e) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.DATE_FORMAT_ERROR_YMD));
            }
        } else {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.DATE_REQUIRED));
        }
        if (StringUtils.isNotBlank(bo.getAttendanceTime())) {
            try {
                String time = bo.getAttendanceTime().replaceAll("：", ":");
                String[] split = time.split(":");
                StringBuilder stringBuilder = new StringBuilder();
                int i = 1;
                for (String s : split) {
                    if (i == 1 && s.length() == 1) {
                        stringBuilder.append("0").append(s);
                    } else {
                        stringBuilder.append(s);
                    }
                    if (i < split.length) {
                        stringBuilder.append(":");
                    }
                    i++;
                }
                time = stringBuilder.toString();
                int count = time.split(":", -1).length - 1;
                if (count == 1) {
                    //字符串格式时间，如18:30
                    DateUtils.formatStringToLocalTime(time, DateUtils.DEFAULT_PATTERN_SHORT_TIME);
                } else if (count == 2) {
                    //字符串格式时间，如18:30:00
                    DateUtils.formatStringToLocalTime(time, DateUtils.DEFAULT_PATTERN_TIME);
                } else {
                    studentErrorList.add(languageUtil.getMessage(LanguageConstants.TIME_FORMAT_ERROR_HMS));
                }
            } catch (Exception e) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.TIME_FORMAT_ERROR_HMS));
            }
        } else {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.TIME_REQUIRED));
        }
        return !CollectionUtils.isNotEmpty(studentErrorList);
    }

    private void handleImportData(ImportTaskEntity task, List<TeacherAttendanceImportModel> list, Long schoolId) {
        task.setTotalCount(list.size());
        task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
        importTaskService.updateById(task);
        log.info("开始处理数据导入...");
        Iterator<TeacherAttendanceImportModel> iterator = list.iterator();
        //每500个处理一次
        List<TeacherAttendanceImportModel> batchExcelLine = new ArrayList<>(500);
        int correctCount = 0;
        List<ImportRecordSaveDTO> importRecordSaveDTOS = new ArrayList<>();
        List<TeacherAttendanceImportDTO> correctList = new ArrayList<>();
        while (iterator.hasNext()) {
            TeacherAttendanceImportModel importModel = iterator.next();
            batchExcelLine.add(importModel);
            if (batchExcelLine.size() >= 500) {
                //处理数据 插入数据库
                correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, correctList);
                batchExcelLine.clear();
            }
        }
        if (!batchExcelLine.isEmpty()) {
            correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, correctList);
            batchExcelLine.clear();
        }
        if (CollectionUtils.isNotEmpty(correctList)) {
            //将正确的记录处理后导入数据库
            handleImportAttendanceData(correctList, schoolId);
        }
        //当前处理进度写入数据库
        task.setSuccessCount(correctCount);
        task.setFailCount(list.size() - correctCount);
        //错误信息写入数据库
        if (CollectionUtils.isNotEmpty(importRecordSaveDTOS)) {
            List<ImportRecordEntity> entityList = importRecordSaveDTOS.stream().map(dto -> {
                ImportRecordEntity importRecordEntity = new ImportRecordEntity();
                BeanUtils.copyProperties(dto, importRecordEntity);
                importRecordEntity.setTaskId(task.getId());
                return importRecordEntity;
            }).collect(Collectors.toList());
            importRecordService.saveBatch(entityList);
        }
    }

    private void handleImportAttendanceData(List<TeacherAttendanceImportDTO> correctList, Long schoolId) {
        if (CollectionUtils.isNotEmpty(correctList)) {
            LocalDate startDate = correctList.stream().map(TeacherAttendanceImportDTO::getAttendanceDate).filter(Objects::nonNull).min(LocalDate::compareTo).get();
            LocalDate endDate = correctList.stream().map(TeacherAttendanceImportDTO::getAttendanceDate).filter(Objects::nonNull).max(LocalDate::compareTo).get();
            Map<String, UserSchoolRelEntity> userNumberMap = new HashMap<>();
            Map<Long, UserDeptRelEntity> userDeptMap = new HashMap<>();
            Map<String, TeacherAttendanceEntity> oldAttendanceMap = new HashMap<>();
            List<TeacherAttendanceRule> attendanceRules = new ArrayList<>();
            Set<String> userNumbers = correctList.stream().map(TeacherAttendanceImportDTO::getUserNumber).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(userNumbers)) {
                QueryWrapper<UserSchoolRelEntity> userWrapper = new QueryWrapper<>();
                userWrapper.lambda().in(UserSchoolRelEntity::getUserNumber, userNumbers)
                        .eq(UserSchoolRelEntity::getSchoolId, schoolId);
                List<UserSchoolRelEntity> userSchoolRelList = userSchoolRelService.list(userWrapper);
                if (CollectionUtils.isNotEmpty(userSchoolRelList)) {
                    //获取用户信息
                    userNumberMap = userSchoolRelList.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserNumber, userSchoolRel -> userSchoolRel, (key1, key2) -> key1));
                    Set<Long> userIds = userSchoolRelList.stream().map(UserSchoolRelEntity::getUserId).collect(Collectors.toSet());
                    if (CollectionUtils.isNotEmpty(userIds)) {
                        //获取历史考勤信息
                        QueryWrapper<TeacherAttendanceEntity> wrapper = new QueryWrapper<>();
                        wrapper.lambda().eq(TeacherAttendanceEntity::getSchoolId, schoolId)
                                .in(TeacherAttendanceEntity::getTeacherId, userIds)
                                .ge(TeacherAttendanceEntity::getAttendanceDate, startDate)
                                .le(TeacherAttendanceEntity::getAttendanceDate, endDate)
                                .eq(TeacherAttendanceEntity::getDeleted, 0);
                        List<TeacherAttendanceEntity> attendanceEntities = this.list(wrapper);
                        if (CollectionUtils.isNotEmpty(attendanceEntities)) {
                            oldAttendanceMap = attendanceEntities.stream().collect(Collectors.toMap(entity -> entity.getTeacherId() + "_" + entity.getAttendanceDate().toString(), teacherAttendance -> teacherAttendance));
                        }
                        //获取考勤规则信息
                        QueryWrapper<TeacherAttendanceRule> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().eq(TeacherAttendanceRule::getSchoolId, schoolId)
                                .eq(TeacherAttendanceRule::getDeleted, 0);
                        attendanceRules = teacherAttendanceRuleService.list(queryWrapper);
                    }
                    //获取教师部门信息
                    List<Long> teacherIds = userSchoolRelList.stream().map(UserSchoolRelEntity::getId).collect(Collectors.toList());
                    QueryWrapper<UserDeptRelEntity> userDeptRelQueryWrapper = new QueryWrapper<>();
                    userDeptRelQueryWrapper.lambda().eq(UserDeptRelEntity::getSchoolId, schoolId)
                            .in(UserDeptRelEntity::getUserId, teacherIds)
                            .eq(UserDeptRelEntity::getIsMaster, 1);
                    List<UserDeptRelEntity> userDeptRelEntities = userDeptRelService.list(userDeptRelQueryWrapper);
                    if (CollectionUtils.isNotEmpty(userDeptRelEntities)) {
                        userDeptMap = userDeptRelEntities.stream().collect(Collectors.toMap(UserDeptRelEntity::getUserId, userDeptRel -> userDeptRel));
                    }
                }
            }
            List<TeacherAttendanceEntity> saveOrUpdateList = new ArrayList<>();
            Map<String, List<TeacherAttendanceImportDTO>> attendanceMap = correctList.stream().collect(Collectors.groupingBy(TeacherAttendanceImportDTO::getUserNumber));
            for (Map.Entry<String, List<TeacherAttendanceImportDTO>> stringListEntry : attendanceMap.entrySet()) {
                //一个一个人处理
                List<TeacherAttendanceImportDTO> attendanceImportDTOS = stringListEntry.getValue();
                UserSchoolRelEntity userSchoolRel = userNumberMap.get(attendanceImportDTOS.get(0).getUserNumber());
                if (userSchoolRel != null) {
                    //按打卡日期聚合
                    Map<LocalDate, List<TeacherAttendanceImportDTO>> attendanceDateMap = attendanceImportDTOS.stream().collect(Collectors.groupingBy(TeacherAttendanceImportDTO::getAttendanceDate));
                    for (Map.Entry<LocalDate, List<TeacherAttendanceImportDTO>> localDateListEntry : attendanceDateMap.entrySet()) {
                        //按日期处理
                        LocalDate attendanceDate = localDateListEntry.getKey();
                        List<TeacherAttendanceImportDTO> attendanceImportDTOList = localDateListEntry.getValue();
                        TeacherAttendanceEntity teacherAttendance = oldAttendanceMap.get(userSchoolRel.getUserId() + "_" + attendanceDate.toString());
                        LocalTime clockInTime = null;
                        Optional<LocalTime> clockIn = attendanceImportDTOList.stream().map(TeacherAttendanceImportDTO::getAttendanceTime).min(LocalTime::compareTo);
                        if (clockIn.isPresent()) {
                            clockInTime = clockIn.get();
                        }
                        //下班时间
                        LocalTime clockOutTime = null;
                        Optional<LocalTime> clockOut = attendanceImportDTOList.stream().map(TeacherAttendanceImportDTO::getAttendanceTime).max(LocalTime::compareTo);
                        if (clockOut.isPresent()) {
                            clockOutTime = clockOut.get();
                            if (clockOutTime.equals(clockInTime)) {
                                //上下班时间相同时，下班时间不保存
                                clockOutTime = null;
                            }
                        }
                        if (teacherAttendance != null) {
                            //更新
                            if (clockInTime != null) {
                                if (teacherAttendance.getClockInTime() == null || clockInTime.isBefore(teacherAttendance.getClockInTime())) {
                                    //新导入数据上班时间早于旧数据，则更新
                                    teacherAttendance.setClockInTime(clockInTime);
                                }
                            }
                            if (clockInTime != null) {
                                if (teacherAttendance.getClockOutTime() == null || clockInTime.isAfter(teacherAttendance.getClockOutTime())) {
                                    //新导入数据早上出校时间晚于旧数据，则更新
                                    teacherAttendance.setClockOutTime(clockInTime);
                                }
                            }
                        } else {
                            teacherAttendance = new TeacherAttendanceEntity();
                            teacherAttendance.setSchoolId(schoolId);
                            teacherAttendance.setTeacherId(userSchoolRel.getUserId());
                            teacherAttendance.setAttendanceDate(attendanceDate);
                            teacherAttendance.setClockInTime(clockInTime);
                            teacherAttendance.setClockOutTime(clockOutTime);
                        }
                        //将有卡号的信息根据时间排序
                        List<TeacherAttendanceImportDTO> teacherAttendanceImportDTOS = attendanceImportDTOList.stream().filter(importDTO -> StringUtils.isNotBlank(importDTO.getCardNumber())).sorted(Comparator.comparing(TeacherAttendanceImportDTO::getAttendanceTime).reversed()).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(teacherAttendanceImportDTOS)) {
                            teacherAttendance.setCardNumber(teacherAttendanceImportDTOS.get(0).getCardNumber());
                        }
                        LocalTime ruleClockInTime = null;
                        LocalTime ruleClockOutTime = null;
                        if (CollectionUtils.isNotEmpty(attendanceRules)) {
                            //获取用户部门
                            Long deptId = userDeptMap.get(userSchoolRel.getId()) == null ? null : userDeptMap.get(userSchoolRel.getId()).getDeptId();
                            TeacherAttendanceRule attendanceRule = getTeacherAttendanceRule(deptId, userSchoolRel.getUserId(), attendanceDate.getDayOfWeek().getValue(), attendanceRules);
                            if (attendanceRule != null) {
                                ruleClockInTime = attendanceRule.getClockInTime();
                                ruleClockOutTime = attendanceRule.getClockOutTime();
                            }
                        }
                        //判断状态
                        Set<Integer> status = checkStatus(teacherAttendance, ruleClockInTime, ruleClockOutTime);
                        if (CollectionUtils.isNotEmpty(status)) {
                            teacherAttendance.setStatus(StringUtils.join(status, ","));
                        } else {
                            teacherAttendance.setStatus("");
                        }
                        saveOrUpdateList.add(teacherAttendance);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(saveOrUpdateList)) {
                this.saveOrUpdateBatch(saveOrUpdateList);
            }
        }
    }

    /**
     * 状态判断核心逻辑
     *
     * @param attendance
     * @param ruleClockInTime
     * @param ruleClockOutTime
     * @return
     */
    private Set<Integer> checkStatus(TeacherAttendanceEntity attendance, LocalTime ruleClockInTime, LocalTime ruleClockOutTime) {
        Set<Integer> status = new HashSet<>();
        // 未匹配到规则，则无状态
        if (ruleClockInTime == null || ruleClockOutTime == null) {
            return status;
        }
        boolean isNormal = true;
        // 缺卡判断（上午入校或下午出校缺失）
        if (attendance.getClockInTime() == null || attendance.getClockOutTime() == null) {
            status.add(TeacherAttendanceStatusEnum.MISSING_CARD.getCode());
            isNormal = false;
        }
        // 迟到判断（上午入校晚于8点）
        if (attendance.getClockInTime() != null && attendance.getClockInTime().isAfter(ruleClockInTime)) {
            status.add(TeacherAttendanceStatusEnum.BE_LATE.getCode());
            isNormal = false;
        }
        // 早退判断（下午出校早于18点）
        if (attendance.getClockOutTime() != null && attendance.getClockOutTime().isBefore(ruleClockOutTime)) {
            status.add(TeacherAttendanceStatusEnum.LEAVE_EARLY.getCode());
            isNormal = false;
        }
        // 无异常
        if (isNormal) {
            status.add(TeacherAttendanceStatusEnum.NORMAL.getCode());
        }
        return status;
    }

    private List<TeacherAttendanceImportModel> readExcelData(MultipartFile file, SchoolLanguageEnum schoolLanguageEnum) {
        List<TeacherAttendanceImportModel> result = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            switch (schoolLanguageEnum) {
                case ZH_MO:
                    TeacherAttendanceImportZhTwListener importZhTwListener = new TeacherAttendanceImportZhTwListener();
                    EasyExcel.read(inputStream, TeacherAttendanceImportZhTwModel.class, importZhTwListener).sheet().doReadSync();
                    List<TeacherAttendanceImportZhTwModel> importZhTwModels = importZhTwListener.getDataList();
                    result = importZhTwModels.stream().map(item -> {
                        TeacherAttendanceImportModel model = new TeacherAttendanceImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case EN_US:
                    TeacherAttendanceImportEnUsListener importEnUsListener = new TeacherAttendanceImportEnUsListener();
                    EasyExcel.read(inputStream, TeacherAttendanceImportEnUsModel.class, importEnUsListener).sheet().doReadSync();
                    List<TeacherAttendanceImportEnUsModel> importEnUsModels = importEnUsListener.getDataList();
                    result = importEnUsModels.stream().map(item -> {
                        TeacherAttendanceImportModel model = new TeacherAttendanceImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case PT_PT:
                    TeacherAttendanceImportPtPtListener importPtPtListener = new TeacherAttendanceImportPtPtListener();
                    EasyExcel.read(inputStream, TeacherAttendanceImportPtPtModel.class, importPtPtListener).sheet().doReadSync();
                    List<TeacherAttendanceImportPtPtModel> importPtPtModels = importPtPtListener.getDataList();
                    result = importPtPtModels.stream().map(item -> {
                        TeacherAttendanceImportModel model = new TeacherAttendanceImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            log.error("Excel文件读取失败", e);
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.FILE_READ_ERROR));
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @Override
    public String export(TeacherAttendancePageReqModel reqModel) {
        QueryWrapper<TeacherAttendanceEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TeacherAttendanceEntity::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0, TeacherAttendanceEntity::getTeacherId, reqModel.getTeacherId())
                .eq(reqModel.getStatus() != null && reqModel.getStatus() > 0, TeacherAttendanceEntity::getStatus, reqModel.getStatus())
                .ge(reqModel.getStartDate() != null, TeacherAttendanceEntity::getAttendanceDate, reqModel.getStartDate())
                .le(reqModel.getEndDate() != null, TeacherAttendanceEntity::getAttendanceDate, reqModel.getEndDate())
                .eq(TeacherAttendanceEntity::getDeleted, 0);
        List<TeacherAttendanceEntity> list = this.list(wrapper.lambda().orderByDesc(TeacherAttendanceEntity::getAttendanceDate));
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> userIds = list.stream().map(TeacherAttendanceEntity::getTeacherId).collect(Collectors.toList());
            //获取学校相关关系
            Map<Long, UserSchoolRelEntity> userSchoolRelMap = new HashMap<>();
            List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelDao.selectList(
                    new LambdaQueryWrapper<UserSchoolRelEntity>()
                            .eq(UserSchoolRelEntity::getSchoolId, reqModel.getSchoolId())
                            .in(UserSchoolRelEntity::getUserId, userIds)
                            .eq(UserSchoolRelEntity::getDeleted, 0));
            if (CollectionUtils.isNotEmpty(userSchoolRelEntities)) {
                userSchoolRelMap = userSchoolRelEntities.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, userSchoolRelEntity -> userSchoolRelEntity));
            }
            List<TeacherAttendancePageResModel> resList = new ArrayList<>();
            for (TeacherAttendanceEntity teacherAttendanceEntity : list) {
                TeacherAttendancePageResModel resModel = new TeacherAttendancePageResModel();
                BeanUtils.copyProperties(teacherAttendanceEntity, resModel);
                UserSchoolRelEntity userSchoolRelEntity = userSchoolRelMap.get(teacherAttendanceEntity.getTeacherId());
                if (userSchoolRelEntity != null) {
                    resModel.setTeacherName(userSchoolRelEntity.getUsername());
                    resModel.setTeacherNumber(userSchoolRelEntity.getUserNumber());
                }
                resList.add(resModel);
            }

            String fileName = "教师出勤数据.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();

            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                fileName = "Teacher Attendance Data.xlsx";
                List<TeacherAttendanceExportEnModel> exportEnModels = resList.stream()
                        .map(resModel -> {
                            TeacherAttendanceExportEnModel exportModel = new TeacherAttendanceExportEnModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setAttendanceDate(resModel.getAttendanceDate() != null ? resModel.getAttendanceDate().toString() : "");
                            exportModel.setClockInTime(resModel.getClockInTime() != null ? resModel.getClockInTime().toString() : "");
                            exportModel.setClockOutTime(resModel.getClockOutTime() != null ? resModel.getClockOutTime().toString() : "");
                            exportModel.setStatus(formatStatus(resModel.getStatus(), SchoolLanguageEnum.EN_US));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, TeacherAttendanceExportEnModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                fileName = "Dados de Presença dos Professores.xlsx";
                List<TeacherAttendanceExportPtModel> exportPtModels = resList.stream()
                        .map(resModel -> {
                            TeacherAttendanceExportPtModel exportModel = new TeacherAttendanceExportPtModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setAttendanceDate(resModel.getAttendanceDate() != null ? resModel.getAttendanceDate().toString() : "");
                            exportModel.setClockInTime(resModel.getClockInTime() != null ? resModel.getClockInTime().toString() : "");
                            exportModel.setClockOutTime(resModel.getClockOutTime() != null ? resModel.getClockOutTime().toString() : "");
                            exportModel.setStatus(formatStatus(resModel.getStatus(), SchoolLanguageEnum.PT_PT));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, TeacherAttendanceExportPtModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else {
                return exportFileHandler.doExportExcel(handleExportData(resList), fileName, TeacherAttendanceExportModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            }
        }
        return null;
    }

    private String formatStatus(String status, SchoolLanguageEnum language) {
        if (StringUtils.isBlank(status)) {
            return "";
        }
        List<Integer> statusList = Arrays.stream(status.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(statusList)) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        for (Integer statusCode : statusList) {
            String statusStr = "";
            if (language == SchoolLanguageEnum.EN_US) {
                //Late
                //Leave Early
                //Missing Card
                //Normal
                if (statusCode == TeacherAttendanceStatusEnum.BE_LATE.getCode()) {
                    statusStr = "Late";
                } else if (statusCode == TeacherAttendanceStatusEnum.LEAVE_EARLY.getCode()) {
                    statusStr = "Leave Early";
                } else if (statusCode == TeacherAttendanceStatusEnum.MISSING_CARD.getCode()) {
                    statusStr = "Missing Card";
                } else if (statusCode == TeacherAttendanceStatusEnum.NORMAL.getCode()) {
                    statusStr = "Normal";
                }
            } else if (language == SchoolLanguageEnum.PT_PT) {
                //Atrasado
                //Sair Mais Cedo
                //Cartão Faltando
                //Normal
                if (statusCode == TeacherAttendanceStatusEnum.BE_LATE.getCode()) {
                    statusStr = "Atrasado";
                } else if (statusCode == TeacherAttendanceStatusEnum.LEAVE_EARLY.getCode()) {
                    statusStr = "Sair Mais Cedo";
                } else if (statusCode == TeacherAttendanceStatusEnum.MISSING_CARD.getCode()) {
                    statusStr = "Cartão Faltando";
                } else if (statusCode == TeacherAttendanceStatusEnum.NORMAL.getCode()) {
                    statusStr = "Normal";
                }
            } else {
                statusStr = TeacherAttendanceStatusEnum.getValue(statusCode);
            }
            stringBuilder.append(statusStr);
            if (i < statusList.size()) {
                stringBuilder.append("，");
            }
            i++;
        }
        return stringBuilder.toString();
    }

    private List<TeacherAttendanceExportModel> handleExportData(List<TeacherAttendancePageResModel> exportDTOS) {
        List<TeacherAttendanceExportModel> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(exportDTOS)) {
            exportDTOS.forEach(resModel -> {
                TeacherAttendanceExportModel exportModel = new TeacherAttendanceExportModel();
                BeanUtils.copyProperties(resModel, exportModel);
                exportModel.setAttendanceDate(resModel.getAttendanceDate() != null ? resModel.getAttendanceDate().toString() : "");
                exportModel.setClockInTime(resModel.getClockInTime() != null ? resModel.getClockInTime().toString() : "");
                exportModel.setClockOutTime(resModel.getClockOutTime() != null ? resModel.getClockOutTime().toString() : "");
                if (StringUtils.isNotBlank(resModel.getStatus())) {
                    List<Integer> statusList = Arrays.stream(resModel.getStatus().split(",")).map(Integer::parseInt).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(statusList)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        int i = 1;
                        for (Integer status : statusList) {
                            String statusStr = TeacherAttendanceStatusEnum.getValue(status);
                            stringBuilder.append(statusStr);
                            if (i < statusList.size()) {
                                stringBuilder.append("，");
                            }
                            i++;
                        }
                        exportModel.setStatus(stringBuilder.toString());
                    }
                }
                result.add(exportModel);
            });
        }
        return result;
    }

    @Override
    public List<TeacherAttendanceStatisticsResModel> statistics(TeacherAttendanceStatisticsReqModel reqModel) {
        List<TeacherAttendanceStatisticsResModel> result = new ArrayList<>();
        // 处理需要计算的时间
        LocalDate startDate = reqModel.getStartDate();
        LocalDate endDate = reqModel.getEndDate();
        List<LocalDate> dates = DateUtils.generateDates(reqModel.getStartDate(), reqModel.getEndDate());
        // 获取出勤规则
        List<TeacherAttendanceRule> list = teacherAttendanceRuleService.list(Wrappers.<TeacherAttendanceRule>lambdaQuery()
                .eq(TeacherAttendanceRule::getSchoolId, reqModel.getSchoolId())
                .like(TeacherAttendanceRule::getUserIds, reqModel.getTeacherId()));
        if (ObjectUtils.isEmpty(list)) {
            return result;
        }
        TeacherAttendanceRule teacherAttendanceRule = list.get(0);
        // 获取时间范围内所有出勤记录
        List<TeacherAttendanceEntity> attendanceList = this.list(Wrappers.<TeacherAttendanceEntity>lambdaQuery()
                .eq(TeacherAttendanceEntity::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0,
                        TeacherAttendanceEntity::getTeacherId, reqModel.getTeacherId())
                .between(TeacherAttendanceEntity::getAttendanceDate, startDate, endDate));
        Map<LocalDate, TeacherAttendanceEntity> dateAttMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(attendanceList)) {
            dateAttMap = attendanceList.stream().collect(Collectors.toMap(TeacherAttendanceEntity::getAttendanceDate, Function.identity()));
        }
        // 获取时间内的所有请假记录
        List<TeacherLeaveEntity> leaveList = teacherLeaveService.list(Wrappers.<TeacherLeaveEntity>lambdaQuery()
                .eq(TeacherLeaveEntity::getSchoolId, reqModel.getSchoolId())
                .eq(TeacherLeaveEntity::getLeaveStatus, 1)
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0,
                        TeacherLeaveEntity::getTeacherId, reqModel.getTeacherId())
                .between(TeacherLeaveEntity::getStartTime, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX).withNano(0))
                .or()
                .between(TeacherLeaveEntity::getEndTime, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX).withNano(0))
                .eq(TeacherLeaveEntity::getSchoolId, reqModel.getSchoolId())
                .eq(TeacherLeaveEntity::getLeaveStatus, 1)
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0,
                        TeacherLeaveEntity::getTeacherId, reqModel.getTeacherId()));
        // 获取时间范围内所有的记录
        List<TeacherBusinessEntity> businessList = teacherBusinessService.list(Wrappers.<TeacherBusinessEntity>lambdaQuery()
                .eq(TeacherBusinessEntity::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0,
                        TeacherBusinessEntity::getTeacherId, reqModel.getTeacherId())
                .between(TeacherBusinessEntity::getStartTime, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX).withNano(0))
                .or()
                .between(TeacherBusinessEntity::getEndTime, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX).withNano(0))
                .eq(TeacherBusinessEntity::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0,
                        TeacherBusinessEntity::getTeacherId, reqModel.getTeacherId()));
        // 拼装返回数据
        for (LocalDate today : dates) {
            TeacherAttendanceStatisticsResModel resModel = new TeacherAttendanceStatisticsResModel();
            // 获取今天的公务和请假信息
            List<TeacherLeaveEntity> todayLeave = leaveList.stream().filter(a -> a.getStartTime().toLocalDate().equals(today) ||
                    a.getEndTime().toLocalDate().equals(today)).collect(Collectors.toList());
            List<TeacherBusinessEntity> todayBus = businessList.stream().filter(a -> a.getStartTime().toLocalDate().equals(today) ||
                    a.getEndTime().toLocalDate().equals(today)).collect(Collectors.toList());
            if (dateAttMap.containsKey(today)) {
                TeacherAttendanceEntity attendanceEntity = dateAttMap.get(today);
                resModel.setDate(today);
                String status = attendanceEntity.getStatus();

                if (StringUtils.isNotEmpty(attendanceEntity.getStatus())) {
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
                        // 若是迟到，则更新迟到状态，若不是迟到，则抹去状态中的迟到
                        if (isLate) {
                            resModel.setIsLeave(false);
                        } else {
                            status = status.replace("2", "").replace(",2", "");
                            resModel.setIsLeave(true);
                        }
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
                        // 若不是早退，则抹去状态中的早退
                        if (isEarly) {
                            resModel.setIsLeave(false);
                        } else {
                            status = status.replace("3", "").replace(",3", "");
                            resModel.setIsLeave(true);
                        }
                    }
                }
                resModel.setStatus(status.isEmpty() ? "1" : status);
                result.add(resModel);
            } else if (!todayBus.isEmpty() || !todayLeave.isEmpty()) {
                resModel.setDate(today);
                resModel.setIsLeave(true);
            }
        }
        return result;
    }
}
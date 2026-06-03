package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtils;
import com.xiaotiyun.school.manager.dao.StudentAttendanceDao;
import com.xiaotiyun.school.manager.dao.StudentLeaveDao;
import com.xiaotiyun.school.manager.dao.StudentBusinessDao;
import com.xiaotiyun.school.manager.dao.StudentLeaveCourseDao;
import com.xiaotiyun.school.manager.dao.LessonDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.listener.StudentAttendanceImportEnUsListener;
import com.xiaotiyun.school.manager.listener.StudentAttendanceImportPtPtListener;
import com.xiaotiyun.school.manager.listener.StudentAttendanceImportZhTwListener;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.dto.StudentAttendanceImportDTO;
import com.xiaotiyun.school.manager.model.dto.StudentLateRecordDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.StudentAttendancePageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceReportReqModel;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceStatisticsReqModel;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendancePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendanceReportResModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendanceStatisticsResModel;
import com.xiaotiyun.school.manager.model.res.StudentLateCountResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentAttendanceServiceImpl extends ServiceImpl<StudentAttendanceDao, StudentAttendanceEntity>
        implements StudentAttendanceService {
    private final StudentAttendanceRuleService studentAttendanceRuleService;
    private final SystemSettingService systemSettingService;
    private final ImportTaskService importTaskService;
    private final ImportRecordService importRecordService;
    private final StudentService studentService;
    private final SysClassService sysClassService;
    private final SemesterService semesterService;
    private final ExportFileHandler exportFileHandler;
    private final LanguageUtil languageUtil;
    private final SchoolCalendarDateTypeService schoolCalendarDateTypeService;
    private final StudentLeaveService studentLeaveService;
    private final StudentBusinessService studentBusinessService;
    private final StudentLeaveCourseService studentLeaveCourseService;
    private final CourseScheduleService courseScheduleService;
    private final LessonService lessonService;
    private static final ExecutorService importPool = new ThreadPoolExecutor(10, 15, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100));

    @Autowired
    private StudentAttendanceDao studentAttendanceDao;

    @Autowired
    private StudentLeaveDao studentLeaveDao;

    @Autowired
    private StudentBusinessDao studentBusinessDao;

    @Autowired
    private StudentLeaveCourseDao studentLeaveCourseDao;

    @Autowired
    private LessonDao lessonDao;
    
    
    @Autowired
    private UserAuthHelper userAuthHelper;

    @Override
    @Transactional
    public void update(Long id, StudentAttendanceUpdateReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getMorningInTime(), reqModel.getAfternoonOutTime())) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ATTENDANCE_TIME_INVALID));
        }
        StudentAttendanceEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.RECORD_NOT_EXIST));
        }
        BeanUtils.copyProperties(reqModel, entity);
        // 处理状态
        LocalTime ruleMorningInTime = null;
        LocalTime ruleAfternoonOutTime = null;
        StudentEntity studentEntity = studentService.getById(entity.getStudentId());
        if (studentEntity != null) {
            SysClass sysClass = sysClassService.getById(studentEntity.getClassId());
            if (sysClass != null) {
                // 获取考勤规则信息
                QueryWrapper<StudentAttendanceRuleEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(StudentAttendanceRuleEntity::getSchoolId, entity.getSchoolId())
                        .eq(StudentAttendanceRuleEntity::getDeleted, 0);
                List<StudentAttendanceRuleEntity> attendanceRules = studentAttendanceRuleService.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(attendanceRules)) {
                    List<StudentAttendanceRuleEntity> attendanceRuleList = attendanceRules.stream().filter(attendanceRule -> {
                        List<Long> gradeIdList = JSONArray.parseArray(attendanceRule.getGrade()).toJavaList(Long.class);
                        return gradeIdList.contains(sysClass.getGradeGroup());
                    }).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(attendanceRuleList)) {
                        StudentAttendanceRuleEntity studentAttendanceRule = attendanceRuleList.get(0);
                        ruleMorningInTime = studentAttendanceRule.getMorningInTime();
                        ruleAfternoonOutTime = studentAttendanceRule.getAfternoonOutTime();
                    }
                }
            }
        }
        // 判断状态
        Set<Integer> status = checkStatus(entity, ruleMorningInTime, ruleAfternoonOutTime);
        if (CollectionUtils.isNotEmpty(status)) {
            entity.setStatus(StringUtils.join(status, ","));
        } else {
            entity.setStatus("");
        }
        this.updateById(entity);
    }

    @Override
    public PageInfo<StudentAttendancePageResModel> page(StudentAttendancePageReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                return new PageInfo<>(new ArrayList<>());
            }
            reqModel.setClassIds(classIds);
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<StudentAttendancePageResModel> list = this.getBaseMapper().page(reqModel);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StudentAttendanceEntity entity = getById(id);
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
        // 获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        // 读取Excel文件
        List<StudentAttendanceImportModel> list = readExcelData(file, languageEnum);
        if (CollectionUtils.isNotEmpty(list)) {
            // 创建导入任务
            ImportTaskEntity task = new ImportTaskEntity();
            task.setSchoolId(schoolId);
            task.setFileName(file.getOriginalFilename());
            task.setType(ImportTaskTypeEnum.STUDENT_ATTENDANCE.getCode());
            task.setTotalCount(0);
            task.setSuccessCount(0);
            task.setFailCount(0);
            importTaskService.save(task);
            CompletableFuture.runAsync(() -> {
                languageUtil.setLanguage(languageEnum.getCode());
                log.info("当前使用的语言是:{}", LanguageUtil.getCurrentLanguage());
                handleStudentAttendanceImport(task, list, schoolId, languageEnum);
                LanguageUtil.clearLanguage();
            }, importPool).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("导入学生出勤任务执行结束taskId=【{}】异常={}",task.getId(),ex);
                } else {
                    log.info("导入学生出勤完成，任务ID={}",task.getId());
                }
                task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                importTaskService.updateById(task);
            });
            return task.getId();
        }
        return null;
    }

    private List<StudentAttendanceImportModel> readExcelData(MultipartFile file, SchoolLanguageEnum schoolLanguageEnum) {
        List<StudentAttendanceImportModel> result = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            switch (schoolLanguageEnum) {
                case ZH_MO:
                    StudentAttendanceImportZhTwListener importZhTwListener = new StudentAttendanceImportZhTwListener();
                    EasyExcel.read(inputStream, StudentAttendanceImportZhTwModel.class, importZhTwListener).sheet().doReadSync();
                    List<StudentAttendanceImportZhTwModel> importZhTwModels = importZhTwListener.getDataList();
                    result = importZhTwModels.stream().map(item -> {
                        StudentAttendanceImportModel model = new StudentAttendanceImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case EN_US:
                    StudentAttendanceImportEnUsListener importEnUsListener = new StudentAttendanceImportEnUsListener();
                    EasyExcel.read(inputStream, StudentAttendanceImportEnUsModel.class, importEnUsListener).sheet().doReadSync();
                    List<StudentAttendanceImportEnUsModel> importEnUsModels = importEnUsListener.getDataList();
                    result = importEnUsModels.stream().map(item -> {
                        StudentAttendanceImportModel model = new StudentAttendanceImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case PT_PT:
                    StudentAttendanceImportPtPtListener importPtPtListener = new StudentAttendanceImportPtPtListener();
                    EasyExcel.read(inputStream, StudentAttendanceImportPtPtModel.class, importPtPtListener).sheet().doReadSync();
                    List<StudentAttendanceImportPtPtModel> importPtPtModels = importPtPtListener.getDataList();
                    result = importPtPtModels.stream().map(item -> {
                        StudentAttendanceImportModel model = new StudentAttendanceImportModel();
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

    private void handleStudentAttendanceImport(ImportTaskEntity task, List<StudentAttendanceImportModel> list, Long schoolId, SchoolLanguageEnum schoolLanguageEnum) {
        task.setTotalCount(list.size());
        task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
        importTaskService.updateById(task);
        log.info("开始处理数据导入...");
        Iterator<StudentAttendanceImportModel> iterator = list.iterator();
        // 每500个处理一次
        List<StudentAttendanceImportModel> batchExcelLine = new ArrayList<>(500);
        int correctCount = 0;
        List<ImportRecordSaveDTO> importRecordSaveDTOS = new ArrayList<>();
        List<StudentAttendanceImportDTO> correctList = new ArrayList<>();
        while (iterator.hasNext()) {
            StudentAttendanceImportModel importModel = iterator.next();
            batchExcelLine.add(importModel);
            if (batchExcelLine.size() >= 500) {
                //处理数据 插入数据库
                correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, correctList, schoolLanguageEnum);
                batchExcelLine.clear();
            }
        }
        if (!batchExcelLine.isEmpty()) {
            correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, correctList, schoolLanguageEnum);
            batchExcelLine.clear();
        }
        if (CollectionUtils.isNotEmpty(correctList)) {
            // 将正确的记录处理后导入数据库
            handleImportAttendanceData(correctList, schoolId);
        }
        // 当前处理进度写入数据库
        task.setSuccessCount(correctCount);
        task.setFailCount(list.size() - correctCount);
        task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
        importTaskService.updateById(task);
        // 错误信息写入数据库
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

    private void handleImportAttendanceData(List<StudentAttendanceImportDTO> correctList, Long schoolId) {
        if (CollectionUtils.isNotEmpty(correctList)) {
            LocalDate startDate = correctList.stream().map(StudentAttendanceImportDTO::getAttendanceDate).filter(Objects::nonNull).min(LocalDate::compareTo).get();
            LocalDate endDate = correctList.stream().map(StudentAttendanceImportDTO::getAttendanceDate).filter(Objects::nonNull).max(LocalDate::compareTo).get();
            LocalTime noonBreakTime = LocalTime.of(13, 0);
            Map<String, StudentEntity> studentNumberMap = new HashMap<>();
            Map<Long, SysClass> classMap = new HashMap<>();
            List<StudentAttendanceRuleEntity> attendanceRules = new ArrayList<>();
            Map<String, StudentAttendanceEntity> oldAttendanceMap = new HashMap<>();
            Set<String> studentNumbers = correctList.stream().map(StudentAttendanceImportDTO::getStudentNo).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(studentNumbers)) {
                QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().in(StudentEntity::getStudentNo, studentNumbers)
                        .eq(StudentEntity::getSchoolId, schoolId)
                        .eq(StudentEntity::getDeleted, 0);
                List<StudentEntity> byStudentNumbers = studentService.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(byStudentNumbers)) {
                    //获取学生信息
                    studentNumberMap = byStudentNumbers.stream().collect(Collectors.toMap(StudentEntity::getStudentNo, student -> student, (key1, key2) -> key1));
                    Set<Long> studentIds = byStudentNumbers.stream().map(StudentEntity::getId).collect(Collectors.toSet());
                    if (CollectionUtils.isNotEmpty(studentIds)) {
                        QueryWrapper<StudentAttendanceEntity> wrapper = new QueryWrapper<>();
                        wrapper.lambda().eq(StudentAttendanceEntity::getSchoolId, schoolId)
                                .in(StudentAttendanceEntity::getStudentId, studentIds)
                                .ge(StudentAttendanceEntity::getAttendanceDate, startDate)
                                .le(StudentAttendanceEntity::getAttendanceDate, endDate)
                                .eq(StudentAttendanceEntity::getDeleted, 0);
                        List<StudentAttendanceEntity> attendanceEntities = this.list(wrapper);
                        if (CollectionUtils.isNotEmpty(attendanceEntities)) {
                            oldAttendanceMap = attendanceEntities.stream().collect(Collectors.toMap(entity -> entity.getStudentId() + "_" + entity.getAttendanceDate().toString(), studentAttendance -> studentAttendance));
                        }
                    }
                    Set<Long> classIds = byStudentNumbers.stream().map(StudentEntity::getClassId).collect(Collectors.toSet());
                    if (CollectionUtils.isNotEmpty(classIds)) {
                        // 获取班级信息
                        List<SysClass> sysClasses = sysClassService.listByIds(classIds);
                        if (CollectionUtils.isNotEmpty(sysClasses)) {
                            classMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, sysClass -> sysClass));
                        }
                    }
                    //获取考勤规则信息
                    QueryWrapper<StudentAttendanceRuleEntity> wrapper = new QueryWrapper<>();
                    wrapper.lambda().eq(StudentAttendanceRuleEntity::getSchoolId, schoolId)
                            .eq(StudentAttendanceRuleEntity::getDeleted, 0);
                    attendanceRules = studentAttendanceRuleService.list(wrapper);
                }
            }
            List<StudentAttendanceEntity> saveOrUpdateList = new ArrayList<>();
            Map<String, List<StudentAttendanceImportDTO>> studentAttendanceMap = correctList.stream().collect(Collectors.groupingBy(StudentAttendanceImportDTO::getStudentNo));
            for (Map.Entry<String, List<StudentAttendanceImportDTO>> stringListEntry : studentAttendanceMap.entrySet()) {
                //一个一个人处理
                List<StudentAttendanceImportDTO> studentAttendanceImportDTOS = stringListEntry.getValue();
                StudentEntity studentEntity = studentNumberMap.get(studentAttendanceImportDTOS.get(0).getStudentNo());
                if (studentEntity != null) {
                    SysClass sysClass = classMap.get(studentEntity.getClassId());
                    if (sysClass != null) {
                        //按打卡日期聚合
                        Map<LocalDate, List<StudentAttendanceImportDTO>> studentAttendanceDateMap = studentAttendanceImportDTOS.stream().collect(Collectors.groupingBy(StudentAttendanceImportDTO::getAttendanceDate));
                        for (Map.Entry<LocalDate, List<StudentAttendanceImportDTO>> localDateListEntry : studentAttendanceDateMap.entrySet()) {
                            //按日期处理
                            LocalDate attendanceDate = localDateListEntry.getKey();
                            List<StudentAttendanceImportDTO> attendanceImportDTOList = localDateListEntry.getValue();
                            StudentAttendanceEntity studentAttendance = oldAttendanceMap.get(studentEntity.getId() + "_" + attendanceDate.toString());
                            //上午入校时间
                            LocalTime morningInTime = null;
                            Optional<LocalTime> morningIn = attendanceImportDTOList.stream().filter(importDTO -> importDTO.getAttendanceTime().isBefore(noonBreakTime) && importDTO.getType() == StudentAttendanceImportTypeEnum.IN).map(StudentAttendanceImportDTO::getAttendanceTime).min(LocalTime::compareTo);
                            if (morningIn.isPresent()) {
                                morningInTime = morningIn.get().truncatedTo(ChronoUnit.MINUTES);
                            }
                            //上午离校时间
                            LocalTime morningOutTime = null;
                            Optional<LocalTime> morningOut = attendanceImportDTOList.stream().filter(importDTO -> importDTO.getAttendanceTime().isBefore(noonBreakTime) && importDTO.getType() == StudentAttendanceImportTypeEnum.OUT).map(StudentAttendanceImportDTO::getAttendanceTime).max(LocalTime::compareTo);
                            if (morningOut.isPresent()) {
                                morningOutTime = morningOut.get().truncatedTo(ChronoUnit.MINUTES);
                            }
                            //下午入校时间
                            LocalTime afternoonInTime = null;
                            Optional<LocalTime> afternoonIn = attendanceImportDTOList.stream().filter(importDTO -> (importDTO.getAttendanceTime().isAfter(noonBreakTime) || importDTO.getAttendanceTime().equals(noonBreakTime)) && importDTO.getType() == StudentAttendanceImportTypeEnum.IN).map(StudentAttendanceImportDTO::getAttendanceTime).min(LocalTime::compareTo);
                            if (afternoonIn.isPresent()) {
                                afternoonInTime = afternoonIn.get().truncatedTo(ChronoUnit.MINUTES);
                            }
                            //下午离校时间
                            LocalTime afternoonOutTime = null;
                            Optional<LocalTime> afternoonOut = attendanceImportDTOList.stream().filter(importDTO -> (importDTO.getAttendanceTime().isAfter(noonBreakTime) || importDTO.getAttendanceTime().equals(noonBreakTime)) && importDTO.getType() == StudentAttendanceImportTypeEnum.OUT).map(StudentAttendanceImportDTO::getAttendanceTime).max(LocalTime::compareTo);
                            if (afternoonOut.isPresent()) {
                                afternoonOutTime = afternoonOut.get().truncatedTo(ChronoUnit.MINUTES);
                            }
                            if (studentAttendance != null) {
                                //更新
                                if (morningInTime != null) {
                                    if (studentAttendance.getMorningInTime() == null || morningInTime.isBefore(studentAttendance.getMorningInTime())) {
                                        //新导入数据早上入校时间早于旧数据，则更新
                                        studentAttendance.setMorningInTime(morningInTime);
                                    }
                                }
                                if (morningOutTime != null) {
                                    if (studentAttendance.getMorningOutTime() == null || morningOutTime.isAfter(studentAttendance.getMorningOutTime())) {
                                        //新导入数据早上出校时间晚于旧数据，则更新
                                        studentAttendance.setMorningOutTime(morningOutTime);
                                    }
                                }
                                if (afternoonInTime != null) {
                                    if (studentAttendance.getAfternoonInTime() == null || afternoonInTime.isBefore(studentAttendance.getAfternoonInTime())) {
                                        //新导入数据下午入校时间早于旧数据，则更新
                                        studentAttendance.setAfternoonInTime(afternoonInTime);
                                    }
                                }
                                if (afternoonOutTime != null) {
                                    if (studentAttendance.getAfternoonOutTime() == null || afternoonOutTime.isAfter(studentAttendance.getAfternoonOutTime())) {
                                        //新导入数据下午出校时间晚于旧数据，则更新
                                        studentAttendance.setAfternoonOutTime(afternoonOutTime);
                                    }
                                }
                            } else {
                                studentAttendance = new StudentAttendanceEntity();
                                studentAttendance.setSchoolId(schoolId);
                                studentAttendance.setStudentId(studentEntity.getId());
                                studentAttendance.setClassId(studentEntity.getClassId());
                                studentAttendance.setSchoolYear(sysClass.getSid());
                                studentAttendance.setAttendanceDate(attendanceDate);
                                studentAttendance.setMorningInTime(morningInTime);
                                studentAttendance.setMorningOutTime(morningOutTime);
                                studentAttendance.setAfternoonInTime(afternoonInTime);
                                studentAttendance.setAfternoonOutTime(afternoonOutTime);
                            }
                            LocalTime ruleMorningInTime = null;
                            LocalTime ruleAfternoonOutTime = null;
                            if (CollectionUtils.isNotEmpty(attendanceRules)) {
                                List<StudentAttendanceRuleEntity> attendanceRuleList = attendanceRules.stream().filter(attendanceRule -> {
                                    List<Long> gradeIdList = JSONArray.parseArray(attendanceRule.getGrade()).toJavaList(Long.class);
                                    return gradeIdList.contains(sysClass.getGradeGroup());
                                }).collect(Collectors.toList());
                                if (CollectionUtils.isNotEmpty(attendanceRuleList)) {
                                    StudentAttendanceRuleEntity studentAttendanceRule = attendanceRuleList.get(0);
                                    ruleMorningInTime = studentAttendanceRule.getMorningInTime();
                                    ruleAfternoonOutTime = studentAttendanceRule.getAfternoonOutTime();
                                }
                            }
                            //判断状态
                            Set<Integer> status = checkStatus(studentAttendance, ruleMorningInTime, ruleAfternoonOutTime);
                            if (CollectionUtils.isNotEmpty(status)) {
                                studentAttendance.setStatus(StringUtils.join(status, ","));
                            } else {
                                studentAttendance.setStatus("");
                            }
                            saveOrUpdateList.add(studentAttendance);
                        }
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
     * @param studentAttendance
     * @return
     */
    private Set<Integer> checkStatus(StudentAttendanceEntity studentAttendance, LocalTime ruleMorningInTime, LocalTime ruleAfternoonOutTime) {
        Set<Integer> status = new HashSet<>();
        // 未匹配到规则，则无状态
        if (ruleMorningInTime == null || ruleAfternoonOutTime == null) {
            return status;
        }
        // 无入校时间异常判断
        if (studentAttendance.getMorningInTime() == null && studentAttendance.getAfternoonInTime() == null) {
            status.add(StudentAttendanceStatusEnum.DATA_EXCEPTION.getCode());
            return status;
        }
        // 无离校时间异常判断
        if (studentAttendance.getMorningOutTime() == null && studentAttendance.getAfternoonOutTime() == null) {
            status.add(StudentAttendanceStatusEnum.DATA_EXCEPTION.getCode());
            return status;
        }
        // 上午时间段异常判断
        if (studentAttendance.getMorningInTime() != null && studentAttendance.getMorningOutTime() != null
                && studentAttendance.getMorningInTime().isAfter(studentAttendance.getMorningOutTime())) {
            status.add(StudentAttendanceStatusEnum.DATA_EXCEPTION.getCode());
            return status;
        }
        // 下午时间段异常判断
        if (studentAttendance.getAfternoonInTime() != null && studentAttendance.getAfternoonOutTime() != null
                && studentAttendance.getAfternoonInTime().isAfter(studentAttendance.getAfternoonOutTime())) {
            status.add(StudentAttendanceStatusEnum.DATA_EXCEPTION.getCode());
            return status;
        }
        boolean isNormal = true;
        // 缺卡判断（上午入校或下午出校缺失）
        if (studentAttendance.getMorningInTime() == null || studentAttendance.getAfternoonOutTime() == null) {
            status.add(StudentAttendanceStatusEnum.MISSING_CARD.getCode());
            isNormal = false;
        }
        // 迟到判断（上午入校晚于8点）
        LocalTime inTime = studentAttendance.getMorningInTime();
        if (inTime == null) {
            inTime = studentAttendance.getAfternoonInTime();
        }
        if (inTime.isAfter(ruleMorningInTime)) {
            status.add(StudentAttendanceStatusEnum.BE_LATE.getCode());
            isNormal = false;
        }
        // 早退判断（下午出校早于18点）
        LocalTime outTime = studentAttendance.getAfternoonOutTime();
        if (outTime == null) {
            outTime = studentAttendance.getMorningOutTime();
        }
        if (outTime.isBefore(ruleAfternoonOutTime)) {
            status.add(StudentAttendanceStatusEnum.LEAVE_EARLY.getCode());
            isNormal = false;
        }
        // 无异常
        if (isNormal) {
            status.add(StudentAttendanceStatusEnum.NORMAL.getCode());
        }
        return status;
    }

    private int processBatchExcelLine(List<ImportRecordSaveDTO> importErrorDTOS, List<StudentAttendanceImportModel> list, Long schoolId,
                                      List<StudentAttendanceImportDTO> correctList, SchoolLanguageEnum schoolLanguageEnum) {
        if (CollectionUtils.isNotEmpty(list)) {
            int correctCount = list.size();//正确处理的条数
            Map<String, StudentEntity> studentNumberMap = new HashMap<>();
            Set<String> studentNumbers = list.stream().map(StudentAttendanceImportModel::getStudentNo).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(studentNumbers)) {
                QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().in(StudentEntity::getStudentNo, studentNumbers)
                        .eq(StudentEntity::getSchoolId, schoolId)
                        .eq(StudentEntity::getDeleted, 0);
                List<StudentEntity> byStudentNumbers = studentService.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(byStudentNumbers)) {
                    studentNumberMap = byStudentNumbers.stream().collect(Collectors.toMap(StudentEntity::getStudentNo, student -> student, (key1, key2) -> key1));
                }
            }
            //遍历要插入的每一行
            for (StudentAttendanceImportModel bo : list) {
                List<String> studentErrorList = new ArrayList<>();
                if (!check(bo, studentErrorList, studentNumberMap, schoolLanguageEnum)) {
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
                correctList.add(studentAttendanceImportConvert(bo, schoolLanguageEnum));
            }
            return correctCount;
        }
        return 0;
    }

    private StudentAttendanceImportDTO studentAttendanceImportConvert(StudentAttendanceImportModel bo, SchoolLanguageEnum schoolLanguageEnum) {
        StudentAttendanceImportDTO result = new StudentAttendanceImportDTO();
        result.setStudentNo(bo.getStudentNo());
        result.setStudentName(bo.getStudentName());
        result.setType(LanguageUtils.getStudentAttendanceImportTypeEnum(schoolLanguageEnum, bo.getType()));
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

    private boolean check(StudentAttendanceImportModel bo, List<String> studentErrorList, Map<String, StudentEntity> studentNoMap, SchoolLanguageEnum schoolLanguageEnum) {
        //一项一项检查
        if (!StringUtils.isNotBlank(bo.getStudentNo()) || !StringUtils.isNotBlank(bo.getStudentName())) {
            if (!StringUtils.isNotBlank(bo.getStudentNo())) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_NO_REQUIRED));
            }
            if (!StringUtils.isNotBlank(bo.getStudentName())) {
                //STUDENT_NAME_REQUIRED
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_NAME_REQUIRED));
            }
        } else {
            StudentEntity studentEntity = studentNoMap.get(bo.getStudentNo());
            if (studentEntity != null) {
                if (!studentEntity.getChineseName().equals(bo.getStudentName())) {
                    //STUDENT_NAME_NOT_MATCH
                    studentErrorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_NAME_NOT_MATCH));
                }
            } else {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_NO_NOT_FOUND));
            }
        }
        if (StringUtils.isNotBlank(bo.getType())) {
            StudentAttendanceImportTypeEnum attendanceImportTypeEnum = LanguageUtils.getStudentAttendanceImportTypeEnum(schoolLanguageEnum, bo.getType());
            if (attendanceImportTypeEnum == null) {
                //TYPE_FORMAT_ERROR
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.TYPE_FORMAT_ERROR));
            }
        } else {
            //TYPE_REQUIRED
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.TYPE_REQUIRED));
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
                //DATE_FORMAT_ERROR
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.DATE_FORMAT_ERROR));
            }
        } else {
            //DATE_REQUIRED
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
                    //TIME_FORMAT_ERROR
                    studentErrorList.add(languageUtil.getMessage(LanguageConstants.TIME_FORMAT_ERROR));
                }
            } catch (Exception e) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.TIME_FORMAT_ERROR));
            }
        } else {
            //TIME_REQUIRED
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.TIME_REQUIRED));
        }
        return !CollectionUtils.isNotEmpty(studentErrorList);
    }

    @Override
    public String export(StudentAttendancePageReqModel reqModel) {
        List<StudentAttendancePageResModel> list = this.getBaseMapper().page(reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            String fileName = "学生考勤信息导出.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();

            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                fileName = "Student Attendance Data.xlsx";
                List<StudentAttendanceExportEnModel> exportEnModels = list.stream()
                        .map(resModel -> {
                            StudentAttendanceExportEnModel exportModel = new StudentAttendanceExportEnModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setAttendanceDate(resModel.getAttendanceDate() != null ? resModel.getAttendanceDate().toString() : "");
                            exportModel.setClassName(StringUtils.isNotBlank(resModel.getGradeName()) && StringUtils.isNotBlank(resModel.getClassName()) ?
                                    resModel.getGradeName() + resModel.getClassName() : "");
                            exportModel.setMorningInTime(resModel.getMorningInTime() != null ? resModel.getMorningInTime().toString() : "");
                            exportModel.setMorningOutTime(resModel.getMorningOutTime() != null ? resModel.getMorningOutTime().toString() : "");
                            exportModel.setAfternoonInTime(resModel.getAfternoonInTime() != null ? resModel.getAfternoonInTime().toString() : "");
                            exportModel.setAfternoonOutTime(resModel.getAfternoonOutTime() != null ? resModel.getAfternoonOutTime().toString() : "");
                            exportModel.setStatus(formatStatus(resModel.getStatus(), SchoolLanguageEnum.EN_US));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, StudentAttendanceExportEnModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                fileName = "Dados de Presença dos Alunos.xlsx";
                List<StudentAttendanceExportPtModel> exportPtModels = list.stream()
                        .map(resModel -> {
                            StudentAttendanceExportPtModel exportModel = new StudentAttendanceExportPtModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setAttendanceDate(resModel.getAttendanceDate() != null ? resModel.getAttendanceDate().toString() : "");
                            exportModel.setClassName(StringUtils.isNotBlank(resModel.getGradeName()) && StringUtils.isNotBlank(resModel.getClassName()) ?
                                    resModel.getGradeName() + resModel.getClassName() : "");
                            exportModel.setMorningInTime(resModel.getMorningInTime() != null ? resModel.getMorningInTime().toString() : "");
                            exportModel.setMorningOutTime(resModel.getMorningOutTime() != null ? resModel.getMorningOutTime().toString() : "");
                            exportModel.setAfternoonInTime(resModel.getAfternoonInTime() != null ? resModel.getAfternoonInTime().toString() : "");
                            exportModel.setAfternoonOutTime(resModel.getAfternoonOutTime() != null ? resModel.getAfternoonOutTime().toString() : "");
                            exportModel.setStatus(formatStatus(resModel.getStatus(), SchoolLanguageEnum.PT_PT));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, StudentAttendanceExportPtModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else {
                return exportFileHandler.doExportExcel(handleStudentAttendanceExportData(list), fileName, StudentAttendanceExportModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
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
                //Data Anomaly
                if (statusCode == StudentAttendanceStatusEnum.BE_LATE.getCode()) {
                    statusStr = "Late";
                } else if (statusCode == StudentAttendanceStatusEnum.LEAVE_EARLY.getCode()) {
                    statusStr = "Leave Early";
                } else if (statusCode == StudentAttendanceStatusEnum.MISSING_CARD.getCode()) {
                    statusStr = "Missing Card";
                } else if (statusCode == StudentAttendanceStatusEnum.NORMAL.getCode()) {
                    statusStr = "Normal";
                } else if (statusCode == StudentAttendanceStatusEnum.DATA_EXCEPTION.getCode()) {
                    statusStr = "Data Anomaly";
                }
            } else if (language == SchoolLanguageEnum.PT_PT) {
                //Atrasado
                //Sair Mais Cedo
                //Cartão Faltando
                //Normal
                //Anomalia de Dados
                if (statusCode == StudentAttendanceStatusEnum.BE_LATE.getCode()) {
                    statusStr = "Atrasado";
                } else if (statusCode == StudentAttendanceStatusEnum.LEAVE_EARLY.getCode()) {
                    statusStr = "Sair Mais Cedo";
                } else if (statusCode == StudentAttendanceStatusEnum.MISSING_CARD.getCode()) {
                    statusStr = "Cartão Faltando";
                } else if (statusCode == StudentAttendanceStatusEnum.NORMAL.getCode()) {
                    statusStr = "Normal";
                } else if (statusCode == StudentAttendanceStatusEnum.DATA_EXCEPTION.getCode()) {
                    statusStr = "Anomalia de Dados";
                }
            } else {
                statusStr = StudentAttendanceStatusEnum.getValue(statusCode);
            }
            stringBuilder.append(statusStr);
            if (i < statusList.size()) {
                stringBuilder.append("，");
            }
            i++;
        }

        return stringBuilder.toString();

    }


    @Override
    public List<StudentAttendanceStatisticsResModel> statistics(StudentAttendanceStatisticsReqModel reqModel) {
        List<StudentAttendanceStatisticsResModel> result = new ArrayList<>();
        StudentEntity studentEntity = studentService.getById(reqModel.getStudentId());
        if (studentEntity != null) {
            SysClass sysClass = sysClassService.getById(studentEntity.getClassId());
            if (sysClass != null) {
                //获取学生的学年信息
                QueryWrapper<SemesterEntity> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(SemesterEntity::getSchoolId, studentEntity.getSchoolId())
                        .eq(SemesterEntity::getDepartment, sysClass.getDepartment())
                        .eq(SemesterEntity::getSchoolYear, reqModel.getSchoolYear())
                        .eq(SemesterEntity::getDeleted, 0);
                List<SemesterEntity> semesterEntities = semesterService.list(wrapper);
                if (CollectionUtils.isNotEmpty(semesterEntities)) {
                    for (SemesterEntity semesterEntity : semesterEntities) {
                        StudentAttendanceStatisticsResModel statisticsResModel = new StudentAttendanceStatisticsResModel();
                        statisticsResModel.setDepartment(semesterEntity.getDepartment());
                        statisticsResModel.setSemesterName(semesterEntity.getName());
                        //查询考勤统计
                        List<StudentLateCountResModel> countResModels = this.getBaseMapper().selectStudentAttendanceCount(studentEntity.getId(), reqModel.getStatus(), semesterEntity.getStartTime().toLocalDate(), semesterEntity.getEndTime().toLocalDate());
                        if (CollectionUtils.isNotEmpty(countResModels)) {
                            statisticsResModel.setCount(countResModels.get(0).getLateCount());
                        } else {
                            statisticsResModel.setCount(0);
                        }
                        result.add(statisticsResModel);
                    }
                }
            }
        }
        return result;
    }

    private List<StudentAttendanceExportModel> handleStudentAttendanceExportData
            (List<StudentAttendancePageResModel> exportDTOS) {
        List<StudentAttendanceExportModel> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(exportDTOS)) {
            exportDTOS.forEach(resModel -> {
                StudentAttendanceExportModel exportModel = new StudentAttendanceExportModel();
                BeanUtils.copyProperties(resModel, exportModel);
                exportModel.setAttendanceDate(resModel.getAttendanceDate() != null ? resModel.getAttendanceDate().toString() : "");
                exportModel.setClassName(StringUtils.isNotBlank(resModel.getGradeName()) && StringUtils.isNotBlank(resModel.getClassName()) ? resModel.getGradeName() + resModel.getClassName() : "");
                exportModel.setMorningInTime(resModel.getMorningInTime() != null ? resModel.getMorningInTime().toString() : "");
                exportModel.setMorningOutTime(resModel.getMorningOutTime() != null ? resModel.getMorningOutTime().toString() : "");
                exportModel.setAfternoonInTime(resModel.getAfternoonInTime() != null ? resModel.getAfternoonInTime().toString() : "");
                exportModel.setAfternoonOutTime(resModel.getAfternoonOutTime() != null ? resModel.getAfternoonOutTime().toString() : "");
                if (StringUtils.isNotBlank(resModel.getStatus())) {
                    List<Integer> statusList = Arrays.stream(resModel.getStatus().split(",")).map(Integer::parseInt).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(statusList)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        int i = 1;
                        for (Integer status : statusList) {
                            String statusStr = StudentAttendanceStatusEnum.getValue(status);
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
    public List<StudentLateCountResModel> getStudentLateCount(Long schoolId, Long classId, List<Long> studentIds) {
        if (schoolId == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SCHOOL_ID_REQUIRED));
        }
        return this.baseMapper.selectStudentLateCount(schoolId, classId, studentIds);
    }

    @Override
    public Map<Long, Integer> countStudentLateDays(Long classId, Long periodId) {
        //查询学段时间
        SemesterEntity semesterEntity = semesterService.getById(periodId);
        // 调用mapper查询学生的迟到次数
        List<StudentLateRecordDTO> studentLateRecordDTOS = this.getBaseMapper().countLateDays(classId, semesterEntity.getStartTime().toLocalDate(), semesterEntity.getEndTime().toLocalDate());


        // 将结果转换为Map
        Map<Long, Integer> lateCountMap = new HashMap<>();
        for (StudentLateRecordDTO studentLateRecordDTO : studentLateRecordDTOS) {
            lateCountMap.put(studentLateRecordDTO.getStudentId(), studentLateRecordDTO.getLateCount());
        }

        return lateCountMap;
    }

    @Override
    public List<StudentAttendanceReportResModel> report(Long schoolId, StudentAttendanceReportReqModel reqModel) {
        //获取学生信息
        QueryWrapper<StudentEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StudentEntity::getSchoolId, schoolId)
                .eq(StudentEntity::getClassId, reqModel.getClassId())
                .eq(reqModel.getStudentId() != null && reqModel.getStudentId() > 0, StudentEntity::getId, reqModel.getStudentId());
        List<StudentEntity> studentList = studentService.list(wrapper);
        if (CollectionUtils.isNotEmpty(studentList)) {
            List<StudentAttendanceReportResModel> resModels = new ArrayList<>();
            //获取班级信息
            SysClass sysClass = sysClassService.getById(reqModel.getClassId());
            StudentAttendanceRuleEntity attendanceRule = null;
            if (sysClass != null) {
                //获取考勤规则信息
                QueryWrapper<StudentAttendanceRuleEntity> studentAttendanceRuleWrapper = new QueryWrapper<>();
                studentAttendanceRuleWrapper.lambda().eq(StudentAttendanceRuleEntity::getSchoolId, schoolId);
                List<StudentAttendanceRuleEntity> attendanceRules = studentAttendanceRuleService.list(studentAttendanceRuleWrapper);
                if (CollectionUtils.isNotEmpty(attendanceRules)) {
                    List<StudentAttendanceRuleEntity> attendanceRuleList = attendanceRules.stream().filter(attendanceRuleEntity -> {
                        List<Long> gradeIdList = JSONArray.parseArray(attendanceRuleEntity.getGrade()).toJavaList(Long.class);
                        return gradeIdList.contains(sysClass.getGradeGroup());
                    }).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(attendanceRuleList)) {
                        attendanceRule = attendanceRuleList.get(0);
                    }
                }
            }
            //获取班级课程信息
            QueryWrapper<CourseScheduleEntity> courseScheduleWrapper = new QueryWrapper<>();
            courseScheduleWrapper.lambda().eq(CourseScheduleEntity::getSchoolId, schoolId)
                    .eq(CourseScheduleEntity::getPeriodId, reqModel.getPeriodId())
                    .eq(CourseScheduleEntity::getClassId, reqModel.getClassId())
                    .ge(CourseScheduleEntity::getCourseDate, reqModel.getQueryStartDate())
                    .le(CourseScheduleEntity::getCourseDate, reqModel.getQueryEndDate());
            List<CourseScheduleEntity> courseScheduleList = courseScheduleService.list(courseScheduleWrapper);
            //获取学生工作日信息
            List<SchoolCalendarDateTypeEntity> studentWeekDayInfo = schoolCalendarDateTypeService.getSchoolIdWeekDayInfo(schoolId, reqModel.getQueryStartDate(), reqModel.getQueryEndDate(), SchoolCalendarDateApplyTypeEnum.STUDENT.getCode());
            //获取学生打卡记录
            QueryWrapper<StudentAttendanceEntity> attendanceWrapper = new QueryWrapper<>();
            attendanceWrapper.lambda().eq(StudentAttendanceEntity::getSchoolId, schoolId)
                    .eq(StudentAttendanceEntity::getSchoolYear, reqModel.getSchoolYear())
                    .eq(StudentAttendanceEntity::getClassId, reqModel.getClassId())
                    .ge(StudentAttendanceEntity::getAttendanceDate, reqModel.getQueryStartDate())
                    .le(StudentAttendanceEntity::getAttendanceDate, reqModel.getQueryEndDate())
                    .eq(reqModel.getStudentId() != null && reqModel.getStudentId() > 0, StudentAttendanceEntity::getStudentId, reqModel.getStudentId());
            List<StudentAttendanceEntity> studentAttendanceList = this.list(attendanceWrapper);
            Map<Long, List<StudentAttendanceEntity>> studentAttendanceMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(studentAttendanceList)) {
                studentAttendanceMap = studentAttendanceList.stream().collect(Collectors.groupingBy(StudentAttendanceEntity::getStudentId));
            }
            //获取学生请假记录
            QueryWrapper<StudentLeaveEntity> leaveWrapper = new QueryWrapper<>();
            leaveWrapper.lambda().eq(StudentLeaveEntity::getSchoolId, schoolId)
                    .eq(StudentLeaveEntity::getSchoolYear, reqModel.getSchoolYear())
                    .eq(StudentLeaveEntity::getClassId, reqModel.getClassId())
                    .ge(StudentLeaveEntity::getLeaveDate, reqModel.getQueryStartDate())
                    .le(StudentLeaveEntity::getLeaveDate, reqModel.getQueryEndDate())
                    .eq(StudentLeaveEntity::getLeaveType, StudentLeaveTypeEnum.LEAVE.getCode())
                    .eq(reqModel.getStudentId() != null && reqModel.getStudentId() > 0, StudentLeaveEntity::getStudentId, reqModel.getStudentId());
            List<StudentLeaveEntity> studentLeaveList = studentLeaveService.list(leaveWrapper);
            Map<Long, List<StudentLeaveEntity>> studentLeaveMap = new HashMap<>();
            Map<Long, List<StudentLeaveCourseEntity>> studentLeaveCourseMap = new HashMap<>();
            Map<Long, LessonEntity> lessonMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(studentLeaveList)) {
                studentLeaveMap = studentLeaveList.stream().collect(Collectors.groupingBy(StudentLeaveEntity::getStudentId));
                List<Long> leaveIds = studentLeaveList.stream().map(StudentLeaveEntity::getId).collect(Collectors.toList());
                QueryWrapper<StudentLeaveCourseEntity> leaveCourseWrapper = new QueryWrapper<>();
                leaveCourseWrapper.lambda().in(StudentLeaveCourseEntity::getLeaveId, leaveIds);
                List<StudentLeaveCourseEntity> studentLeaveCourseList = studentLeaveCourseService.list(leaveCourseWrapper);
                if (CollectionUtils.isNotEmpty(studentLeaveCourseList)) {
                    studentLeaveCourseMap = studentLeaveCourseList.stream().collect(Collectors.groupingBy(StudentLeaveCourseEntity::getLeaveId));
                    List<Long> courseIds = studentLeaveCourseList.stream().map(StudentLeaveCourseEntity::getCourseId).collect(Collectors.toList());
                    QueryWrapper<LessonEntity> lessonWrapper = new QueryWrapper<>();
                    lessonWrapper.lambda().in(LessonEntity::getId, courseIds);
                    List<LessonEntity> lessonList = lessonService.list(lessonWrapper);
                    if (CollectionUtils.isNotEmpty(lessonList)) {
                        lessonMap = lessonList.stream().collect(Collectors.toMap(LessonEntity::getId, lessonEntity -> lessonEntity));
                    }
                }
            }
            //获取学生公务记录
            QueryWrapper<StudentBusinessEntity> businessWrapper = new QueryWrapper<>();
            businessWrapper.lambda().eq(StudentBusinessEntity::getSchoolId, schoolId)
                    .eq(StudentBusinessEntity::getSchoolYear, reqModel.getSchoolYear())
                    .eq(StudentBusinessEntity::getClassId, reqModel.getClassId())
                    .le(StudentBusinessEntity::getStartTime, reqModel.getQueryEndDate())
                    .ge(StudentBusinessEntity::getEndTime, reqModel.getQueryStartDate())
                    .eq(reqModel.getStudentId() != null && reqModel.getStudentId() > 0, StudentBusinessEntity::getStudentId, reqModel.getStudentId());
            List<StudentBusinessEntity> studentBusinessList = studentBusinessService.list(businessWrapper);
            Map<Long, List<StudentBusinessEntity>> studentBusinessMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(studentBusinessList)) {
                studentBusinessMap = studentBusinessList.stream().collect(Collectors.groupingBy(StudentBusinessEntity::getStudentId));
            }
            for (StudentEntity studentEntity : studentList) {
                StudentAttendanceReportResModel resModel = new StudentAttendanceReportResModel();
                resModel.setSeatNo(studentEntity.getSeatNo());
                resModel.setStudentName(studentEntity.getChineseName());
                //应到校打卡天数
                if (CollectionUtils.isNotEmpty(studentWeekDayInfo)) {
                    resModel.setClockDays(studentWeekDayInfo.size());
                }
                //实际到校打卡天数
                List<StudentAttendanceEntity> attendanceEntities = studentAttendanceMap.get(studentEntity.getId());
                if (CollectionUtils.isNotEmpty(attendanceEntities)) {
                    resModel.setActualClockDays(attendanceEntities.size());
                }
                // 迟到次数
                int lateCount = computeLateCount(attendanceEntities, studentLeaveMap.get(studentEntity.getId()),
                        studentLeaveCourseMap, courseScheduleList, studentBusinessMap.get(studentEntity.getId()),
                        studentWeekDayInfo, lessonMap, attendanceRule);
                resModel.setBeLateDays(lateCount);
                // 早退次数
                int earlyLeaveCount = computeEarlyLeaveCount(attendanceEntities, studentLeaveMap.get(studentEntity.getId()),
                        studentLeaveCourseMap, courseScheduleList, studentBusinessMap.get(studentEntity.getId()),
                        studentWeekDayInfo, lessonMap, attendanceRule);
                resModel.setEarlyDays(earlyLeaveCount);
                //请假次数
                List<StudentLeaveEntity> leaveEntities = studentLeaveMap.get(studentEntity.getId());
                if (CollectionUtils.isNotEmpty(leaveEntities)) {
                    int sum = leaveEntities.stream().mapToInt(StudentLeaveEntity::getPeriods).sum();
                    resModel.setLeaveDays(sum);
                }
                //公务天数
                List<StudentBusinessEntity> businessEntities = studentBusinessMap.get(studentEntity.getId());
                if (CollectionUtils.isNotEmpty(businessEntities)) {
                    resModel.setBusinessDays(computeBusinessDays(businessEntities, reqModel.getQueryStartDate(), reqModel.getQueryEndDate()));
                }
                // 计算无由不打卡天数
                int noReasonNoClockDays = computeNoReasonNoClockDays(studentWeekDayInfo, attendanceEntities,
                        studentLeaveMap.get(studentEntity.getId()), businessEntities);
                resModel.setNotClockDays(noReasonNoClockDays);
                resModels.add(resModel);
            }
            return resModels;
        }
        return Collections.emptyList();
    }

    private int computeBusinessDays(List<StudentBusinessEntity> studentBusinessList, LocalDate startDate, LocalDate endDate) {
        int result = 0;
        List<LocalDate> dateList = DateUtils.generateDates(startDate, endDate);
        if (CollectionUtils.isNotEmpty(studentBusinessList) && CollectionUtils.isNotEmpty(dateList)) {
            for (LocalDate localDate : dateList) {
                long count = studentBusinessList.stream()
                        .filter(businessEntity -> !businessEntity.getStartTime().toLocalDate().isAfter(localDate) && !businessEntity.getEndTime().toLocalDate().isBefore(localDate))
                        .count();
                if (count > 0) {
                    result += 1;
                }
            }
        }
        return result;
    }

    private int computeLateCount(List<StudentAttendanceEntity> attendanceEntities, List<StudentLeaveEntity> leaveEntities,
                                 Map<Long, List<StudentLeaveCourseEntity>> leaveCourseMap, List<CourseScheduleEntity> courseScheduleList,
                                 List<StudentBusinessEntity> businessEntities, List<SchoolCalendarDateTypeEntity> studentWeekDayInfo,
                                 Map<Long, LessonEntity> lessonMap, StudentAttendanceRuleEntity attendanceRule) {
        if (CollectionUtils.isEmpty(studentWeekDayInfo) || CollectionUtils.isEmpty(attendanceEntities)) {
            return 0;
        }
        Map<LocalDate, SchoolCalendarDateTypeEntity> weekDayMap = studentWeekDayInfo.stream().collect(Collectors.toMap(SchoolCalendarDateTypeEntity::getCalendarDate, schoolCalendarDateTypeEntity -> schoolCalendarDateTypeEntity));
        int lateCount = 0;
        for (StudentAttendanceEntity attendance : attendanceEntities) {
            if (StringUtils.contains(attendance.getStatus(), String.valueOf(StudentAttendanceStatusEnum.BE_LATE.getCode())) && weekDayMap.get(attendance.getAttendanceDate()) != null) {
                LocalDate date = attendance.getAttendanceDate();
                LocalTime actualInTime = attendance.getMorningInTime();
                if (actualInTime == null) {
                    //优先取“入校时间（上午）”，没有的话取“入校时间（下午）
                    actualInTime = attendance.getAfternoonInTime();
                }
                if (actualInTime == null) {
                    //当天没有入校打卡记录
                    lateCount++;
                } else {
                    // 检查请假记录
                    boolean hasLeave = checkLeaveForLate(leaveEntities, leaveCourseMap, courseScheduleList, lessonMap, date, actualInTime);
                    // 检查公务记录
                    boolean hasBusiness = checkBusinessForLate(businessEntities, attendanceRule, date, actualInTime);
                    if (!hasLeave && !hasBusiness) {
                        lateCount++;
                    }
                }
            }
        }
        return lateCount;
    }

    private int computeEarlyLeaveCount(List<StudentAttendanceEntity> attendanceEntities, List<StudentLeaveEntity> leaveEntities,
                                       Map<Long, List<StudentLeaveCourseEntity>> leaveCourseMap, List<CourseScheduleEntity> courseScheduleList,
                                       List<StudentBusinessEntity> businessEntities, List<SchoolCalendarDateTypeEntity> studentWeekDayInfo,
                                       Map<Long, LessonEntity> lessonMap, StudentAttendanceRuleEntity attendanceRule) {
        if (CollectionUtils.isEmpty(studentWeekDayInfo) || CollectionUtils.isEmpty(attendanceEntities)) {
            return 0;
        }
        Map<LocalDate, SchoolCalendarDateTypeEntity> weekDayMap = studentWeekDayInfo.stream().collect(Collectors.toMap(SchoolCalendarDateTypeEntity::getCalendarDate, schoolCalendarDateTypeEntity -> schoolCalendarDateTypeEntity));
        int earlyLeaveCount = 0;
        for (StudentAttendanceEntity attendance : attendanceEntities) {
            if (StringUtils.contains(attendance.getStatus(), String.valueOf(StudentAttendanceStatusEnum.LEAVE_EARLY.getCode())) && weekDayMap.get(attendance.getAttendanceDate()) != null) {
                LocalDate date = attendance.getAttendanceDate();
                LocalTime actualOutTime = attendance.getAfternoonOutTime();
                if (actualOutTime == null) {
                    //优先取“离校时间（下午）”，没有的话取“离校时间（上午）
                    actualOutTime = attendance.getMorningOutTime();
                }
                if (actualOutTime == null) {
                    //当天没有离校打卡记录
                    earlyLeaveCount++;
                } else {
                    // 检查请假记录
                    boolean hasLeave = checkLeaveForEarlyLeave(leaveEntities, leaveCourseMap, courseScheduleList, lessonMap, date, actualOutTime);
                    // 检查公务记录
                    boolean hasBusiness = checkBusinessForEarlyLeave(businessEntities, attendanceRule, date, actualOutTime);
                    if (!hasLeave && !hasBusiness) {
                        earlyLeaveCount++;
                    }
                }
            }
        }
        return earlyLeaveCount;
    }

    private int computeNoReasonNoClockDays(List<SchoolCalendarDateTypeEntity> weekDayInfo,
                                           List<StudentAttendanceEntity> attendanceEntities, List<StudentLeaveEntity> leaveEntities,
                                           List<StudentBusinessEntity> businessEntities) {
        if (CollectionUtils.isEmpty(weekDayInfo)) {
            return 0;
        }
        Set<LocalDate> attendanceDates = new HashSet<>();
        if (CollectionUtils.isNotEmpty(attendanceEntities)) {
            attendanceDates = attendanceEntities.stream()
                    .map(StudentAttendanceEntity::getAttendanceDate)
                    .collect(Collectors.toSet());
        }
        Set<LocalDate> leaveDates = new HashSet<>();
        if (CollectionUtils.isNotEmpty(leaveEntities)) {
            leaveDates = leaveEntities.stream()
                    .map(StudentLeaveEntity::getLeaveDate)
                    .collect(Collectors.toSet());
        }
        Set<LocalDate> businessDates = new HashSet<>();
        if (CollectionUtils.isNotEmpty(businessEntities)) {
            for (StudentBusinessEntity businessEntity : businessEntities) {
                LocalDate startDate = businessEntity.getStartTime().toLocalDate();
                LocalDate endDate = businessEntity.getEndTime().toLocalDate();
                if (startDate.equals(endDate)) {
                    businessDates.add(startDate);
                } else {
                    List<LocalDate> dateList = DateUtils.generateDates(startDate, endDate);
                    businessDates.addAll(dateList);
                }
            }
        }
        Set<LocalDate> finalAttendanceDates = attendanceDates;
        Set<LocalDate> finalLeaveDates = leaveDates;
        return (int) weekDayInfo.stream()
                .filter(day -> {
                    LocalDate date = day.getCalendarDate();
                    return !finalAttendanceDates.contains(date)
                            && !finalLeaveDates.contains(date)
                            && !businessDates.contains(date);
                })
                .count();
    }

    private boolean checkLeaveForLate(List<StudentLeaveEntity> leaveEntities, Map<Long, List<StudentLeaveCourseEntity>> leaveCourseMap,
                                      List<CourseScheduleEntity> courseScheduleList, Map<Long, LessonEntity> lessonMap,
                                      LocalDate date, LocalTime actualInTime) {
        if (CollectionUtils.isEmpty(leaveEntities)) {
            return false;
        }
        Map<LocalDate, List<StudentLeaveEntity>> leaveMap = leaveEntities.stream().collect(Collectors.groupingBy(StudentLeaveEntity::getLeaveDate));
        List<StudentLeaveEntity> studentLeaveEntities = leaveMap.get(date);
        if (CollectionUtils.isNotEmpty(studentLeaveEntities)) {
            List<LessonEntity> leaveLessonList = new ArrayList<>();
            List<Long> notClockLessonIds = new ArrayList<>();
            for (StudentLeaveEntity leave : studentLeaveEntities) {
                List<StudentLeaveCourseEntity> leaveCourses = leaveCourseMap.get(leave.getId());
                if (CollectionUtils.isNotEmpty(leaveCourses)) {
                    for (StudentLeaveCourseEntity leaveCourse : leaveCourses) {
                        notClockLessonIds.add(leaveCourse.getCourseId());
                        leaveLessonList.add(lessonMap.get(leaveCourse.getCourseId()));
                    }
                }
            }
            //学生剩余需要打卡的课节
            if (CollectionUtils.isNotEmpty(courseScheduleList)) {
                List<CourseScheduleEntity> clockCourseScheduleList = courseScheduleList.stream().filter(courseSchedule -> !notClockLessonIds.contains(courseSchedule.getLessonId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(clockCourseScheduleList)) {
                    //最早需要打卡的课节
                    CourseScheduleEntity firstCourseSchedule = clockCourseScheduleList.stream().min(Comparator.comparing(CourseScheduleEntity::getStartTime)).orElse(null);
                    return firstCourseSchedule != null && !actualInTime.isAfter(firstCourseSchedule.getStartTime());
                } else {
                    //请假后，如果后面没有课，取请假的结束时间（也就是 请假的里面最后一节课的结束时间）
                    LessonEntity lastLesson = leaveLessonList.stream().max(Comparator.comparing(LessonEntity::getStartTime)).orElse(null);
                    return lastLesson != null && !actualInTime.isAfter(lastLesson.getEndTime());
                }
            } else {
                //请假后，如果后面没有课，取请假的结束时间（也就是 请假的里面最后一节课的结束时间）
                LessonEntity lastLesson = leaveLessonList.stream().max(Comparator.comparing(LessonEntity::getStartTime)).orElse(null);
                return lastLesson != null && !actualInTime.isAfter(lastLesson.getEndTime());
            }
        }
        return false;
    }

    private boolean checkBusinessForLate(List<StudentBusinessEntity> businessEntities, StudentAttendanceRuleEntity attendanceRule, LocalDate date, LocalTime actualInTime) {
        if (CollectionUtils.isEmpty(businessEntities) || attendanceRule == null) {
            return false;
        }
        List<StudentBusinessEntity> businessList = businessEntities.stream()
                .filter(businessEntity -> !businessEntity.getStartTime().toLocalDate().isAfter(date) && !businessEntity.getEndTime().toLocalDate().isBefore(date))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(businessList)) {
            //计算公务时间范围
            LocalTime morningInTime = attendanceRule.getMorningInTime();
            for (StudentBusinessEntity studentBusinessEntity : businessList) {
                if (studentBusinessEntity.getEndTime().toLocalDate().equals(date)) {
                    //当天结束的公务
                    if (studentBusinessEntity.getStartTime().toLocalDate().equals(date)) {
                        //开始时间为当天
                        if (studentBusinessEntity.getStartTime().toLocalTime().isBefore(morningInTime) && studentBusinessEntity.getEndTime().toLocalTime().isAfter(morningInTime)) {
                            morningInTime = studentBusinessEntity.getEndTime().toLocalTime();
                        }
                    } else {
                        //开始时间不为当天
                        if (morningInTime.isBefore(studentBusinessEntity.getEndTime().toLocalTime())) {
                            morningInTime = studentBusinessEntity.getEndTime().toLocalTime();
                        }
                    }
                } else {
                    //当天不结束的公务
                    if (!studentBusinessEntity.getStartTime().toLocalDate().equals(date)) {
                        //开始时间不为当天
                        morningInTime = LocalTime.MAX;
                    } else {
                        //开始时间为当天
                        if (studentBusinessEntity.getStartTime().toLocalTime().isBefore(attendanceRule.getMorningInTime())) {
                            morningInTime = LocalTime.MAX;
                        }
                    }
                }
            }
            return !actualInTime.isAfter(morningInTime);
        }
        return false;
    }

    private boolean checkLeaveForEarlyLeave(List<StudentLeaveEntity> leaveEntities, Map<Long, List<StudentLeaveCourseEntity>> leaveCourseMap,
                                            List<CourseScheduleEntity> courseScheduleList, Map<Long, LessonEntity> lessonMap, LocalDate date, LocalTime actualOutTime) {
        if (CollectionUtils.isEmpty(leaveEntities)) {
            return false;
        }
        Map<LocalDate, List<StudentLeaveEntity>> leaveMap = leaveEntities.stream().collect(Collectors.groupingBy(StudentLeaveEntity::getLeaveDate));
        List<StudentLeaveEntity> studentLeaveEntities = leaveMap.get(date);
        if (CollectionUtils.isNotEmpty(studentLeaveEntities)) {
            List<LessonEntity> leaveLessonList = new ArrayList<>();
            List<Long> notClockLessonIds = new ArrayList<>();
            for (StudentLeaveEntity leave : studentLeaveEntities) {
                List<StudentLeaveCourseEntity> leaveCourses = leaveCourseMap.get(leave.getId());
                if (CollectionUtils.isNotEmpty(leaveCourses)) {
                    for (StudentLeaveCourseEntity leaveCourse : leaveCourses) {
                        notClockLessonIds.add(leaveCourse.getCourseId());
                        leaveLessonList.add(lessonMap.get(leaveCourse.getCourseId()));
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(courseScheduleList)) {
                //学生剩余需要打卡的课节
                List<CourseScheduleEntity> clockCourseScheduleList = courseScheduleList.stream().filter(courseSchedule -> !notClockLessonIds.contains(courseSchedule.getLessonId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(clockCourseScheduleList)) {
                    //最早需要打卡的课节
                    CourseScheduleEntity firstCourseSchedule = clockCourseScheduleList.stream().max(Comparator.comparing(CourseScheduleEntity::getStartTime)).orElse(null);
                    return firstCourseSchedule != null && !actualOutTime.isBefore(firstCourseSchedule.getEndTime());
                } else {
                    //请假后，如果前面都没有课，取请假的开始时间（也就是 请假的里面第一节课的开始时间）
                    LessonEntity lastLesson = leaveLessonList.stream().min(Comparator.comparing(LessonEntity::getStartTime)).orElse(null);
                    return lastLesson != null && !actualOutTime.isBefore(lastLesson.getStartTime());
                }
            } else {
                //请假后，如果前面都没有课，取请假的开始时间（也就是 请假的里面第一节课的开始时间）
                LessonEntity lastLesson = leaveLessonList.stream().min(Comparator.comparing(LessonEntity::getStartTime)).orElse(null);
                return lastLesson != null && !actualOutTime.isBefore(lastLesson.getStartTime());
            }
        }
        return false;
    }

    private boolean checkBusinessForEarlyLeave(List<StudentBusinessEntity> businessEntities, StudentAttendanceRuleEntity attendanceRule, LocalDate date, LocalTime actualOutTime) {
        if (CollectionUtils.isEmpty(businessEntities) || attendanceRule == null) {
            return false;
        }
        List<StudentBusinessEntity> businessList = businessEntities.stream()
                .filter(businessEntity -> !businessEntity.getStartTime().toLocalDate().isAfter(date) && !businessEntity.getEndTime().toLocalDate().isBefore(date))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(businessList)) {
            //计算公务时间范围
            LocalTime afternoonOutTime = attendanceRule.getAfternoonOutTime();
            for (StudentBusinessEntity studentBusinessEntity : businessList) {
                if (studentBusinessEntity.getEndTime().toLocalDate().equals(date)) {
                    //当天结束的公务
                    if (studentBusinessEntity.getStartTime().toLocalDate().equals(date)) {
                        //开始时间为当天
                        if (studentBusinessEntity.getStartTime().toLocalTime().isBefore(afternoonOutTime) && studentBusinessEntity.getEndTime().toLocalTime().isAfter(afternoonOutTime)) {
                            afternoonOutTime = studentBusinessEntity.getStartTime().toLocalTime();
                        }
                    } else {
                        //开始时间不为当天
                        if (studentBusinessEntity.getEndTime().toLocalTime().isAfter(attendanceRule.getAfternoonOutTime())) {
                            afternoonOutTime = LocalTime.MIN;
                        }
                    }
                } else {
                    //当天不结束的公务
                    if (studentBusinessEntity.getStartTime().toLocalDate().equals(date)) {
                        //开始时间为当天
                        if (studentBusinessEntity.getStartTime().toLocalTime().isBefore(afternoonOutTime)) {
                            afternoonOutTime = studentBusinessEntity.getStartTime().toLocalTime();
                        }
                    } else {
                        //开始时间不为当天
                        afternoonOutTime = LocalTime.MIN;
                    }
                }
            }
            return !actualOutTime.isBefore(afternoonOutTime);
        }
        return false;
    }

    @Override
    public String reportExport(Long schoolId, StudentAttendanceReportReqModel reqModel) {
        List<StudentAttendanceReportResModel> resModels = report(schoolId, reqModel);
        if (CollectionUtils.isNotEmpty(resModels)) {
            String fileName = "学生出勤统计.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();
            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                List<StudentAttendanceReportExportEnModel> exportEnModels = resModels.stream()
                        .map(item -> {
                            StudentAttendanceReportExportEnModel resModel = new StudentAttendanceReportExportEnModel();
                            BeanUtils.copyProperties(item, resModel);
                            resModel.setSeatNo(item.getSeatNo() == null ? "" : String.valueOf(item.getSeatNo()));
                            resModel.setClockDays(String.valueOf(item.getClockDays()));
                            resModel.setActualClockDays(String.valueOf(item.getActualClockDays()));
                            resModel.setBeLateDays(String.valueOf(item.getBeLateDays()));
                            resModel.setEarlyDays(String.valueOf(item.getEarlyDays()));
                            resModel.setLeaveDays(String.valueOf(item.getLeaveDays()));
                            resModel.setBusinessDays(String.valueOf(item.getBusinessDays()));
                            resModel.setNotClockDays(String.valueOf(item.getNotClockDays()));
                            return resModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, StudentAttendanceReportExportEnModel.class, FileTypeEnum.EXPORT, schoolId);
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                List<StudentAttendanceReportExportPtModel> exportPtModels = resModels.stream()
                        .map(item -> {
                            StudentAttendanceReportExportPtModel resModel = new StudentAttendanceReportExportPtModel();
                            BeanUtils.copyProperties(item, resModel);
                            resModel.setSeatNo(item.getSeatNo() == null ? "" : String.valueOf(item.getSeatNo()));
                            resModel.setClockDays(String.valueOf(item.getClockDays()));
                            resModel.setActualClockDays(String.valueOf(item.getActualClockDays()));
                            resModel.setBeLateDays(String.valueOf(item.getBeLateDays()));
                            resModel.setEarlyDays(String.valueOf(item.getEarlyDays()));
                            resModel.setLeaveDays(String.valueOf(item.getLeaveDays()));
                            resModel.setBusinessDays(String.valueOf(item.getBusinessDays()));
                            resModel.setNotClockDays(String.valueOf(item.getNotClockDays()));
                            return resModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, StudentAttendanceReportExportPtModel.class, FileTypeEnum.EXPORT, schoolId);
            } else {
                List<StudentAttendanceReportExportModel> exportMoModels = resModels.stream()
                        .map(item -> {
                            StudentAttendanceReportExportModel resModel = new StudentAttendanceReportExportModel();
                            BeanUtils.copyProperties(item, resModel);
                            resModel.setSeatNo(item.getSeatNo() == null ? "" : String.valueOf(item.getSeatNo()));
                            resModel.setClockDays(String.valueOf(item.getClockDays()));
                            resModel.setActualClockDays(String.valueOf(item.getActualClockDays()));
                            resModel.setBeLateDays(String.valueOf(item.getBeLateDays()));
                            resModel.setEarlyDays(String.valueOf(item.getEarlyDays()));
                            resModel.setLeaveDays(String.valueOf(item.getLeaveDays()));
                            resModel.setBusinessDays(String.valueOf(item.getBusinessDays()));
                            resModel.setNotClockDays(String.valueOf(item.getNotClockDays()));
                            return resModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportMoModels, fileName, StudentAttendanceReportExportModel.class, FileTypeEnum.EXPORT, schoolId);
            }
        }
        return null;
    }

    @Override
    public Map<Long, Integer> countFilterStudentLateDays(Long classId, Long periodId) {
        // 查询学段时间
        SemesterEntity semesterEntity = semesterService.getById(periodId);
        // 调用mapper查询学生的迟到次数
        List<StudentAttendanceEntity> studentAttendanceEntities = this.getBaseMapper().getLateDays(classId,
                semesterEntity.getStartTime().toLocalDate(), semesterEntity.getEndTime().toLocalDate());
        Map<Long, Integer> lateCountMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(studentAttendanceEntities)) {
            Map<Long, List<StudentAttendanceEntity>> listMap = studentAttendanceEntities.stream()
                    .collect(Collectors.groupingBy(StudentAttendanceEntity::getStudentId));
            List<StudentLateCountResModel> studentLateCountResModels = handlerStudentLate(listMap,classId);
            if (CollectionUtils.isNotEmpty(studentLateCountResModels)) {
                return studentLateCountResModels.stream()
                        .collect(Collectors.toMap(StudentLateCountResModel::getStudentId,
                                StudentLateCountResModel::getLateCount, (x1, x2) -> x1));
            }
        }
        return lateCountMap;
    }

    @Override
    public List<StudentAttendanceEntity> getFilterRecords(Map<Long, List<StudentAttendanceEntity>> listMap) {

        return handlerStudentLateRecords(listMap,null);
    }

    private List<StudentLateCountResModel> handlerStudentLate(Map<Long, List<StudentAttendanceEntity>> attendanceMap,Long classId) {
        List<StudentLateCountResModel> result = new ArrayList<>();

        if (attendanceMap == null || attendanceMap.isEmpty()) {
            return result;
        }

        // 遍历每个学生的考勤记录
        for (Map.Entry<Long, List<StudentAttendanceEntity>> entry : attendanceMap.entrySet()) {
            Long studentId = entry.getKey();
            List<StudentAttendanceEntity> attendanceList = entry.getValue();
            int lateCount = 0;

            if (CollectionUtils.isEmpty(attendanceList)) {
                log.info("学生{}的考勤记录为空，跳过处理", studentId);
                continue;
            }
            StudentEntity studentEntity = studentService.getById(studentId);
            if (studentEntity == null) {
                log.info("学生{}不存在，跳过处理", studentId);
                continue;
            }
            if(classId == null){
                classId = studentEntity.getClassId();
            }
            SysClass sysClass = sysClassService.getById(classId);
            if (sysClass == null) {
                log.info("学生{}的班级{}不存在，跳过处理", studentId, studentEntity.getClassId());
                continue;
            }
            // 获取考勤规则信息
            QueryWrapper<StudentAttendanceRuleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(StudentAttendanceRuleEntity::getSchoolId, studentEntity.getSchoolId())
                    .eq(StudentAttendanceRuleEntity::getDeleted, 0);
            List<StudentAttendanceRuleEntity> attendanceRules = studentAttendanceRuleService
                    .list(queryWrapper);

            // 获取考勤规则中的应到时间
            LocalTime ruleMorningInTime = null;
            if (CollectionUtils.isNotEmpty(attendanceRules)) {
                List<StudentAttendanceRuleEntity> attendanceRuleList = attendanceRules.stream()
                        .filter(attendanceRule -> {
                            List<Long> gradeIdList = JSONArray.parseArray(attendanceRule.getGrade())
                                    .toJavaList(Long.class);
                            return gradeIdList.contains(sysClass.getGradeGroup());
                        }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(attendanceRuleList)) {
                    StudentAttendanceRuleEntity studentAttendanceRule = attendanceRuleList.get(0);
                    ruleMorningInTime = studentAttendanceRule.getMorningInTime();
                }
            }

            // 按日期分组，处理同一天的一条记录
            Map<LocalDate, StudentAttendanceEntity> dateGroupMap = attendanceList.stream()
                    .collect(Collectors.toMap(StudentAttendanceEntity::getAttendanceDate, Function.identity(),
                            (x1, x2) -> x1));

            // 处理每一天的记录
            for (Map.Entry<LocalDate, StudentAttendanceEntity> dateEntry : dateGroupMap.entrySet()) {
                LocalDate date = dateEntry.getKey();
                StudentAttendanceEntity dayRecord = dateEntry.getValue();

                // 如果没有入校时间记录，跳过处理
                if (dayRecord == null) {
                    log.info("学生{}在{}没有入校时间记录，跳过处理", studentId, date);
                    lateCount++;
                    continue;
                }

                // 获取当天的入校时间
                LocalTime actualInTime = dayRecord.getMorningInTime();
                if(actualInTime == null){
                    actualInTime = dayRecord.getAfternoonInTime();
                }
                if(actualInTime == null){
                    log.info("学生{}在当天的入校时间为空，跳过处理", studentId);
                    lateCount++;
                    continue;
                }


                // 检查是否有请假记录
                boolean hasLeaveRecord = false;
                LocalTime leaveLatestInTime = null;

                List<StudentLeaveEntity> leaveRecords = studentLeaveDao.selectList(
                        new QueryWrapper<StudentLeaveEntity>()
                                .eq("student_id", studentId)
                                .eq("leave_date", date)
                                .eq("deleted", 0));

                if (CollectionUtils.isNotEmpty(leaveRecords)) {
                    hasLeaveRecord = true;
                    // 获取请假后的第一节课开始时间
                    leaveLatestInTime = getFirstLessonStartTimeAfterLeave(studentId, date, studentEntity, sysClass,
                            leaveRecords.stream().map(StudentLeaveEntity::getId).collect(Collectors.toList()));
                }

                // 检查是否有公出记录
                boolean hasOfficialRecord = false;
                LocalTime officialLatestInTime = null;

                List<StudentBusinessEntity> businessRecords = studentBusinessDao.selectList(
                        new QueryWrapper<StudentBusinessEntity>()
                                .eq("student_id", studentId)
                                .le("start_time", date.atTime(LocalTime.MAX))
                                .ge("end_time", date.atTime(LocalTime.MIN))
                                .eq("deleted", 0));

                if (CollectionUtils.isNotEmpty(businessRecords)) {
                    if (ruleMorningInTime == null) {
                        log.info("学生{}的考勤规则不存在或未设置应到时间", studentId);
                        lateCount++;
                        continue;
                    }

                    // 检查是否有包含原应到时间的公务记录
                    boolean hasValidBusinessRecord = false;
                    for (StudentBusinessEntity businessRecord : businessRecords) {
                        LocalDateTime businessStartTime = businessRecord.getStartTime();
                        LocalDateTime businessEndTime = businessRecord.getEndTime();

                        // 将ruleMorningInTime转换为当天的日期时间
                        LocalDateTime ruleDateTime = date.atTime(ruleMorningInTime);

                        // 检查原应到时间是否在公务时间范围内
                        if (!ruleDateTime.isBefore(businessStartTime) && !ruleDateTime.isAfter(businessEndTime)) {
                            hasValidBusinessRecord = true;
                            officialLatestInTime = businessEndTime.toLocalTime();
                            break;
                        }
                    }

                    // 如果没有满足条件的公务记录，使用原应到时间
                    if (!hasValidBusinessRecord) {
                        officialLatestInTime = ruleMorningInTime;
                    }

                    hasOfficialRecord = true;
                }

                // 判断是否计入迟到次数
                boolean countAsLate = true;

                // 如果有请假记录，检查实际入校时间是否早于等于请假后最晚应该入校的时间
                if (hasLeaveRecord && leaveLatestInTime != null) {
                    if (!actualInTime.isAfter(leaveLatestInTime)) {
                        countAsLate = false;
                    }
                }

                // 如果有公务记录，检查实际入校时间是否早于等于公务后最晚可入校时间
                if (hasOfficialRecord && officialLatestInTime != null) {
                    if (!actualInTime.isAfter(officialLatestInTime)) {
                        countAsLate = false;
                    }
                }

                // 如果计入迟到，则增加迟到次数
                if (countAsLate) {
                    lateCount++;
                }
            }

            // 创建返回结果
            StudentLateCountResModel resModel = new StudentLateCountResModel();
            resModel.setStudentId(studentId);
            resModel.setLateCount(lateCount);
            result.add(resModel);
        }

        return result;
    }

    private List<StudentAttendanceEntity> handlerStudentLateRecords(Map<Long, List<StudentAttendanceEntity>> attendanceMap,Long classId) {
        List<StudentAttendanceEntity> result = new ArrayList<>();

        if (attendanceMap == null || attendanceMap.isEmpty()) {
            return result;
        }

        // 遍历每个学生的考勤记录
        for (Map.Entry<Long, List<StudentAttendanceEntity>> entry : attendanceMap.entrySet()) {
            Long studentId = entry.getKey();
            List<StudentAttendanceEntity> attendanceList = entry.getValue();

            if (CollectionUtils.isEmpty(attendanceList)) {
                log.info("学生{}的考勤记录为空，跳过处理", studentId);
                continue;
            }
            StudentEntity studentEntity = studentService.getById(studentId);
            if (studentEntity == null) {
                log.info("学生{}不存在，跳过处理", studentId);
                continue;
            }
            if(classId == null){
                classId = studentEntity.getClassId();
            }
            SysClass sysClass = sysClassService.getById(classId);
            if (sysClass == null) {
                log.info("学生{}的班级{}不存在，跳过处理", studentId, studentEntity.getClassId());
                continue;
            }
            // 获取考勤规则信息
            QueryWrapper<StudentAttendanceRuleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(StudentAttendanceRuleEntity::getSchoolId, studentEntity.getSchoolId())
                    .eq(StudentAttendanceRuleEntity::getDeleted, 0);
            List<StudentAttendanceRuleEntity> attendanceRules = studentAttendanceRuleService
                    .list(queryWrapper);

            // 获取考勤规则中的应到时间
            LocalTime ruleMorningInTime = null;
            if (CollectionUtils.isNotEmpty(attendanceRules)) {
                List<StudentAttendanceRuleEntity> attendanceRuleList = attendanceRules.stream()
                        .filter(attendanceRule -> {
                            List<Long> gradeIdList = JSONArray.parseArray(attendanceRule.getGrade())
                                    .toJavaList(Long.class);
                            return gradeIdList.contains(sysClass.getGradeGroup());
                        }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(attendanceRuleList)) {
                    StudentAttendanceRuleEntity studentAttendanceRule = attendanceRuleList.get(0);
                    ruleMorningInTime = studentAttendanceRule.getMorningInTime();
                }
            }

            // 按日期分组，处理同一天的一条记录
            Map<LocalDate, StudentAttendanceEntity> dateGroupMap = attendanceList.stream()
                    .collect(Collectors.toMap(StudentAttendanceEntity::getAttendanceDate, Function.identity(),
                            (x1, x2) -> x1));

            // 处理每一天的记录
            for (Map.Entry<LocalDate, StudentAttendanceEntity> dateEntry : dateGroupMap.entrySet()) {
                LocalDate date = dateEntry.getKey();
                StudentAttendanceEntity dayRecord = dateEntry.getValue();

                // 如果没有入校时间记录，跳过处理
                if (dayRecord == null) {
                    log.info("学生{}在{}没有入校时间记录，跳过处理", studentId, date);
                    result.add(dayRecord);
                    continue;
                }

                // 获取当天的入校时间
                LocalTime actualInTime = dayRecord.getMorningInTime();
                if(actualInTime == null){
                    actualInTime = dayRecord.getAfternoonInTime();
                }
                if(actualInTime == null){
                    log.info("学生{}在当天的入校时间为空，跳过处理", studentId);
                    result.add(dayRecord);
                    continue;
                }


                // 检查是否有请假记录
                boolean hasLeaveRecord = false;
                LocalTime leaveLatestInTime = null;

                List<StudentLeaveEntity> leaveRecords = studentLeaveDao.selectList(
                        new QueryWrapper<StudentLeaveEntity>()
                                .eq("student_id", studentId)
                                .eq("leave_date", date)
                                .eq("deleted", 0));

                if (CollectionUtils.isNotEmpty(leaveRecords)) {
                    hasLeaveRecord = true;
                    // 获取请假后的第一节课开始时间
                    leaveLatestInTime = getFirstLessonStartTimeAfterLeave(studentId, date, studentEntity, sysClass,
                            leaveRecords.stream().map(StudentLeaveEntity::getId).collect(Collectors.toList()));
                }

                // 检查是否有公出记录
                boolean hasOfficialRecord = false;
                LocalTime officialLatestInTime = null;

                List<StudentBusinessEntity> businessRecords = studentBusinessDao.selectList(
                        new QueryWrapper<StudentBusinessEntity>()
                                .eq("student_id", studentId)
                                .le("start_time", date.atTime(LocalTime.MAX))
                                .ge("end_time", date.atTime(LocalTime.MIN))
                                .eq("deleted", 0));

                if (CollectionUtils.isNotEmpty(businessRecords)) {
                    if (ruleMorningInTime == null) {
                        log.info("学生{}的考勤规则不存在或未设置应到时间", studentId);
                        result.add(dayRecord);
                        continue;
                    }

                    // 检查是否有包含原应到时间的公务记录
                    boolean hasValidBusinessRecord = false;
                    for (StudentBusinessEntity businessRecord : businessRecords) {
                        LocalDateTime businessStartTime = businessRecord.getStartTime();
                        LocalDateTime businessEndTime = businessRecord.getEndTime();

                        // 将ruleMorningInTime转换为当天的日期时间
                        LocalDateTime ruleDateTime = date.atTime(ruleMorningInTime);

                        // 检查原应到时间是否在公务时间范围内
                        if (!ruleDateTime.isBefore(businessStartTime) && !ruleDateTime.isAfter(businessEndTime)) {
                            hasValidBusinessRecord = true;
                            officialLatestInTime = businessEndTime.toLocalTime();
                            break;
                        }
                    }

                    // 如果没有满足条件的公务记录，使用原应到时间
                    if (!hasValidBusinessRecord) {
                        officialLatestInTime = ruleMorningInTime;
                    }

                    hasOfficialRecord = true;
                }

                // 判断是否计入迟到次数
                boolean countAsLate = true;

                // 如果有请假记录，检查实际入校时间是否早于等于请假后最晚应该入校的时间
                if (hasLeaveRecord && leaveLatestInTime != null) {
                    if (!actualInTime.isAfter(leaveLatestInTime)) {
                        countAsLate = false;
                    }
                }

                // 如果有公务记录，检查实际入校时间是否早于等于公务后最晚可入校时间
                if (hasOfficialRecord && officialLatestInTime != null) {
                    if (!actualInTime.isAfter(officialLatestInTime)) {
                        countAsLate = false;
                    }
                }

                // 如果计入迟到，则增加迟到次数
                if (countAsLate) {
                    result.add(dayRecord);
                }
            }
        }

        return result;
    }

    /**
     * 获取请假后的第一节课开始时间
     *
     * @param studentId 学生ID
     * @param leaveDate 请假日期
     * @return 第一节课开始时间
     */
    private LocalTime getFirstLessonStartTimeAfterLeave(Long studentId, LocalDate leaveDate,
            StudentEntity studentEntity, SysClass sysClass, List<Long> leaveIds) {
        if (studentEntity == null || sysClass == null || CollectionUtils.isEmpty(leaveIds)) {
            return null;
        }

        // 1. 获取请假课程信息
        List<StudentLeaveCourseEntity> leaveCourses = studentLeaveCourseDao.selectList(
                new QueryWrapper<StudentLeaveCourseEntity>()
                        .in("leave_id", leaveIds)
                        .eq("deleted", 0)
                        .orderByAsc("course_id"));

        if (CollectionUtils.isEmpty(leaveCourses)) {
            return null;
        }

        // 2. 获取请假课程的ID列表
        List<Long> leaveCourseIds = leaveCourses.stream()
                .map(StudentLeaveCourseEntity::getCourseId)
                .collect(Collectors.toList());

        // 3. 获取学生当天的所有课程（添加学校ID和年级组过滤）
        List<LessonEntity> allLessons = lessonDao.selectList(
                new QueryWrapper<LessonEntity>()
                        .eq("school_id", studentEntity.getSchoolId())
                        .eq("grade_id", sysClass.getGradeGroup())
                        .eq("deleted", 0)
                        .orderByAsc("start_time"));

        if (CollectionUtils.isEmpty(allLessons)) {
            return null;
        }

        // 4. 找到请假后第一节课的开始时间
        // 先找到请假课程中开始时间最晚的课程
        LocalTime latestLeaveLessonTime = leaveCourses.stream()
                .map(StudentLeaveCourseEntity::getCourseId)
                .map(courseId -> allLessons.stream()
                        .filter(lesson -> lesson.getId().equals(courseId))
                        .map(LessonEntity::getStartTime)
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .max(LocalTime::compareTo)
                .orElse(null);

        if (latestLeaveLessonTime == null) {
            return null;
        }

        // 找到请假后第一节课的开始时间
        return allLessons.stream()
                .filter(lesson -> !leaveCourseIds.contains(lesson.getId()))
                .map(LessonEntity::getStartTime)
                .filter(startTime -> startTime.isAfter(latestLeaveLessonTime))
                .min(LocalTime::compareTo)
                .orElse(null);
    }

}
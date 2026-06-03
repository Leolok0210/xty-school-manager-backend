package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.VolunteerMapper;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.listener.VolunteerImportEnUsListener;
import com.xiaotiyun.school.manager.listener.VolunteerImportPtPtListener;
import com.xiaotiyun.school.manager.listener.VolunteerImportZhTwListener;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.dto.StudentCountDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.VolunteerPageReqModel;
import com.xiaotiyun.school.manager.model.req.VolunteerSaveReqModel;
import com.xiaotiyun.school.manager.model.req.VolunteerSaveStudentReqModel;
import com.xiaotiyun.school.manager.model.res.GradeRecordSettingResModel;
import com.xiaotiyun.school.manager.model.res.VolunteerResModel;
import com.xiaotiyun.school.manager.model.res.VolunteerStudentSumResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 义工服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VolunteerServiceImpl extends ServiceImpl<VolunteerMapper, VolunteerEntity> implements VolunteerService {
    private static final ExecutorService volunteerImportPool = new ThreadPoolExecutor(10, 15, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100));
    private static final Pattern SCHOOL_YEAR_PATTERN = Pattern.compile("^\\d{4}-\\d{4}$");
    private final SysClassService sysClassService;
    private final GradeGroupService gradeGroupService;
    private final ImportTaskService importTaskService;
    private final ImportRecordService importRecordService;
    private final ExportFileHandler exportFileHandler;
    //semesterService
    private final SemesterService semesterService;
    private final LanguageUtil languageUtil;
    private final GradeRecordSettingService gradeRecordSettingService;
    private final StudentService studentService;
    private final UserAuthHelper userAuthHelper;

    /**
     * 分页查询义工记录
     *
     * @param reqModel 分页查询参数
     * @return 分页结果
     */
    @Override
    public PageInfo<VolunteerResModel> page(VolunteerPageReqModel reqModel) {
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
        List<VolunteerResModel> list = this.getBaseMapper().page(reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(volunteerResModel -> {
                if (volunteerResModel.getServiceHours() != null) {
                    volunteerResModel.setServiceHours(convertSecondsToHours(volunteerResModel.getServiceSeconds()));
                }
            });
        }
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<VolunteerResModel> pageByStudent(VolunteerPageReqModel reqModel) {
        // 获取学生信息
        StudentEntity nowStudent = (StudentEntity) StpUtil.getSession().get("student");
        if (nowStudent == null){
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        reqModel.setStudentId(nowStudent.getId());
        // 获取学段信息，若有，需要处理成时间范围
        if (reqModel.getSemesterId() != null) {
            SemesterEntity semester = semesterService.getById(reqModel.getSemesterId());
            if (semester != null) {
                reqModel.setServiceDateStart(LocalDate.from(semester.getStartTime()));
                reqModel.setServiceDateEnd(LocalDate.from(semester.getEndTime()));
            }
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        // 查询列表
        List<VolunteerResModel> list = this.getBaseMapper().page(reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(volunteerResModel -> {
                if (volunteerResModel.getServiceHours() == null) {
                    volunteerResModel.setServiceHours(convertSecondsToHours(volunteerResModel.getServiceSeconds()));
                }
            });
        }
        return new PageInfo<>(list);
    }

    /**
     * 新增义工记录
     *
     * @param reqModel 保存参数
     */
    @Override
    @Transactional
    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.VOLUNTEER_SERVICE)
    public List<VolunteerEntity> save(VolunteerSaveReqModel reqModel) {
        if (reqModel.getStartTime() != null && reqModel.getEndTime() != null && !DateUtils.isTimeValid(reqModel.getStartTime(), reqModel.getEndTime())) {
            throw new BusinessException(LanguageConstants.START_TIME_BEFORE_END_TIME);
        }
        List<SysClass> sysClass = sysClassService.list(Wrappers.<SysClass>lambdaQuery().in(SysClass::getId, reqModel.getStudents().stream().map(VolunteerSaveStudentReqModel::getClassId).collect(Collectors.toList())));
        if (sysClass != null) {
            List<Long> classIds = sysClass.stream().map(BaseEntity::getId).collect(Collectors.toList());
            //获取成绩录入设定
            GradeRecordSettingResModel settingResModel = gradeRecordSettingService.getSetting(reqModel.getSchoolId(), reqModel.getSchoolYear());
            if (settingResModel != null && settingResModel.getClassSettings() != null) {
                long count = settingResModel.getClassSettings().stream().filter(classSettingItem -> classIds.contains(classSettingItem.getClassId()) && classSettingItem.getCanRecordVolunteer()).count();
                if (count > 0) {
                    QueryWrapper<SemesterEntity> wrapper = new QueryWrapper<>();
                    wrapper.lambda().eq(SemesterEntity::getSchoolId, reqModel.getSchoolId())
                            .eq(SemesterEntity::getSchoolYear, reqModel.getSchoolYear())
                            .le(SemesterEntity::getStartTime, reqModel.getServiceDate())
                            .ge(SemesterEntity::getEndTime, reqModel.getServiceDate());
                    List<SemesterEntity> semester = semesterService.list(wrapper);
                    if (semester == null || semester.isEmpty()) {
                        throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SEMESTER_NOT_EXISTS));
                    }
                    Map<Integer, List<SemesterEntity>> deptSemesterMap = semester.stream().collect(Collectors.groupingBy(SemesterEntity::getDepartment));
                    String errorMessage = null;
                    a:
                    for (GradeRecordSettingResModel.TimeSettingItem timeSetting : settingResModel.getTimeSettings()) {
                        b:
                        for (SysClass classInfo : sysClass) {
                            if (deptSemesterMap.containsKey(classInfo.getDepartment())) {
                                List<SemesterEntity> semesterEntities = deptSemesterMap.get(classInfo.getDepartment());
                                SemesterEntity semesterEntity = semesterEntities.get(0);
                                if (timeSetting.getDepartment().equals(classInfo.getDepartment()) && timeSetting.getSemesterId().equals(semesterEntity.getId())) {
                                    if (!timeSetting.getStartTime().isBefore(LocalDateTime.now()) || !timeSetting.getEndTime().isAfter(LocalDateTime.now())) {
                                        errorMessage = classInfo.getSid() + semesterEntity.getName();
                                        errorMessage = String.format(languageUtil.getMessage(LanguageConstants.VOLUNTEER_INPUT_TIME_RANGE), errorMessage) + DateUtils.formatDateToString(timeSetting.getStartTime(), languageUtil.getMessage(LanguageConstants.YEAR_MONTH_DAY)) + "-" + DateUtils.formatDateToString(timeSetting.getEndTime(), languageUtil.getMessage(LanguageConstants.YEAR_MONTH_DAY));
                                    }
                                    break a;
                                }
                            } else {
                                throw new BusinessMessageException(classInfo.getClassName() + "," + languageUtil.getMessage(LanguageConstants.SEMESTER_NOT_EXISTS));
                            }
                        }
                    }
                    if (StringUtils.isNotBlank(errorMessage)) {
                        throw new BusinessMessageException(errorMessage);
                    }
                }
            }
        }
        // 处理基础数据
        VolunteerEntity entity = BeanConvertUtil.convert(reqModel, VolunteerEntity.class);
        // 处理批量学生ID
        List<VolunteerEntity> entities = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reqModel.getStudents())) {
            reqModel.getStudents().forEach(student -> {
                VolunteerEntity volunteerEntity = new VolunteerEntity();
                BeanUtils.copyProperties(entity, volunteerEntity);
                volunteerEntity.setStudentId(student.getStudentId());
                volunteerEntity.setClassId(student.getClassId());
                volunteerEntity.setClassName(student.getClassName());
                volunteerEntity.setGradeName(student.getGradeName());
                entities.add(volunteerEntity);
            });
        }
        // 批量插入
        if (!entities.isEmpty()) {
            this.saveBatch(entities);
        }
        return entities;
    }

    /**
     * 更新义工记录
     *
     * @param id       记录ID
     * @param reqModel 更新参数
     */
    @Override
    @Transactional
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.VOLUNTEER_SERVICE)
    public VolunteerEntity update(Long id, VolunteerSaveReqModel reqModel) {
        if (reqModel.getStartTime() != null && reqModel.getEndTime() != null && !DateUtils.isTimeValid(reqModel.getStartTime(), reqModel.getEndTime())) {
            throw new BusinessException(LanguageConstants.START_TIME_BEFORE_END_TIME);
        }
        VolunteerEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        BeanUtils.copyProperties(reqModel, entity);
//        calculateServiceSeconds(entity, reqModel.getStartTime(), reqModel.getEndTime());
        this.updateById(entity);
        return entity;
    }

    /**
     * 删除义工记录（逻辑删除）
     *
     * @param id 记录ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        VolunteerEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        this.removeById(id);
    }

    /**
     * 计算服务秒数并设置到实体
     */
    private void calculateServiceSeconds(VolunteerEntity entity, LocalTime start, LocalTime end) {
        long seconds = ChronoUnit.SECONDS.between(start, end);
        if (seconds <= 0) {
            throw new BusinessException(LanguageConstants.SERVICE_HOURS_GREATER_THAN_ZERO);
        }
        entity.setServiceSeconds(seconds);
    }

    /**
     * 秒转小时保留一位小数
     */
    private Double convertSecondsToHours(Long seconds) {
        return Math.round(seconds / 3600.0 * 10) / 10.0;
    }

    @Override
    public String export(VolunteerPageReqModel reqModel) {
        List<VolunteerResModel> list = this.getBaseMapper().page(reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            String fileName = "义工服务信息导出.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();

            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                fileName = "Volunteer Service Data.xlsx";
                List<VolunteerExportEnModel> exportEnModels = list.stream()
                        .map(volunteerResModel -> {
                            VolunteerExportEnModel volunteerExportModel = new VolunteerExportEnModel();
                            BeanUtils.copyProperties(volunteerResModel, volunteerExportModel);
                            volunteerExportModel.setClassName(volunteerResModel.getGradeName() + volunteerResModel.getClassName());
                            volunteerExportModel.setSeatNo(volunteerResModel.getSeatNo() == null ? "" : String.valueOf(volunteerResModel.getSeatNo()));
                            volunteerExportModel.setServiceSeconds(String.valueOf(convertSecondsToHours(ChronoUnit.SECONDS.between(volunteerResModel.getStartTime(), volunteerResModel.getEndTime()))));
                            volunteerExportModel.setServiceDate(volunteerResModel.getServiceDate().toString());
                            volunteerExportModel.setStartTime(volunteerResModel.getStartTime().toString());
                            volunteerExportModel.setEndTime(volunteerResModel.getEndTime().toString());
                            return volunteerExportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, VolunteerExportEnModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                fileName = "Dados de Serviço Voluntário.xlsx";
                List<VolunteerExportPtModel> exportPtModels = list.stream()
                        .map(volunteerResModel -> {
                            VolunteerExportPtModel volunteerExportModel = new VolunteerExportPtModel();
                            BeanUtils.copyProperties(volunteerResModel, volunteerExportModel);
                            volunteerExportModel.setClassName(volunteerResModel.getGradeName() + volunteerResModel.getClassName());
                            volunteerExportModel.setSeatNo(volunteerResModel.getSeatNo() == null ? "" : String.valueOf(volunteerResModel.getSeatNo()));
                            volunteerExportModel.setServiceSeconds(String.valueOf(convertSecondsToHours(ChronoUnit.SECONDS.between(volunteerResModel.getStartTime(), volunteerResModel.getEndTime()))));
                            volunteerExportModel.setServiceDate(volunteerResModel.getServiceDate().toString());
                            volunteerExportModel.setStartTime(volunteerResModel.getStartTime().toString());
                            volunteerExportModel.setEndTime(volunteerResModel.getEndTime().toString());
                            return volunteerExportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, VolunteerExportPtModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else {
                return exportFileHandler.doExportExcel(handleVolunteerExportData(list), fileName, VolunteerExportModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            }
        }
        return null;
    }

    @Override
    public Long importVolunteer(Long schoolId, String schoolYear, MultipartFile file) {
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
        //学年检查
        if (!StringUtils.isNotBlank(schoolYear)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SCHOOL_YEAR_REQUIRED));
        } else {
            if (!SCHOOL_YEAR_PATTERN.matcher(schoolYear).matches()) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SCHOOL_YEAR_FORMAT_ERROR));
            } else {
                // 验证后一年是否大于前一年
                String[] years = schoolYear.split("-");
                if (Integer.parseInt(years[1]) <= Integer.parseInt(years[0])) {
                    throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SCHOOL_YEAR_FORMAT_ERROR));
                }
            }
        }
        // 读取Excel文件
        List<VolunteerImportModel> list = readExcelData(file, languageEnum);
        if (CollectionUtils.isNotEmpty(list)) {
            // 创建导入任务
            ImportTaskEntity task = new ImportTaskEntity();
            task.setSchoolId(schoolId);
            task.setFileName(file.getOriginalFilename());
            task.setType(ImportTaskTypeEnum.VOLUNTEER.getCode());
            task.setTotalCount(0);
            task.setSuccessCount(0);
            task.setFailCount(0);
            importTaskService.save(task);
            CompletableFuture.runAsync(() -> {
                languageUtil.setLanguage(languageEnum.getCode());
                handleVolunteerImport(task, list, schoolId, schoolYear, languageEnum);
                LanguageUtil.clearLanguage();
            }, volunteerImportPool).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("导入学生义工任务执行结束taskId=【{}】异常={}",task.getId(),ex);
                } else {
                    log.info("导入学生义工完成，任务ID={}",task.getId());
                }
                task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                importTaskService.updateById(task);
            });
            return task.getId();
        }
        return null;
    }

    private void handleVolunteerImport(ImportTaskEntity task, List<VolunteerImportModel> list, Long schoolId, String schoolYear, SchoolLanguageEnum schoolLanguageEnum) {
        if (CollectionUtils.isNotEmpty(list)) {
            task.setTotalCount(list.size());
            task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
            importTaskService.updateById(task);
            log.info("开始处理数据导入...");
            Iterator<VolunteerImportModel> iterator = list.iterator();
            //每500个处理一次
            List<VolunteerImportModel> batchExcelLine = new ArrayList<>(500);
            List<String> studentNoList = list.stream().map(VolunteerImportModel::getStudentNo).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
            Map<String, StudentEntity> studentNoMap = new HashMap<>();
            Map<Long, SysClass> classMap = new HashMap<>();
            Map<Long, GradeGroup> gradeMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(studentNoList)) {
                QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(StudentEntity::getSchoolId, schoolId)
                        .in(StudentEntity::getStudentNo, studentNoList);
                List<StudentEntity> studentEntities = studentService.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(studentEntities)) {
                    studentNoMap = studentEntities.stream().collect(Collectors.toMap(StudentEntity::getStudentNo, studentEntity -> studentEntity));
                    List<Long> classIds = studentEntities.stream().map(StudentEntity::getClassId).distinct().collect(Collectors.toList());
                    List<SysClass> sysClasses = sysClassService.listByIds(classIds);
                    if (CollectionUtils.isNotEmpty(sysClasses)) {
                        classMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, sysClass -> sysClass));
                        List<Long> gradeIds = sysClasses.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
                        List<GradeGroup> gradeGroups = gradeGroupService.listByIds(gradeIds);
                        if (CollectionUtils.isNotEmpty(gradeGroups)) {
                            gradeMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, gradeGroup -> gradeGroup));
                        }
                    }
                }
            }
            int correctCount = 0;
            List<ImportRecordSaveDTO> importRecordSaveDTOS = new ArrayList<>();
            while (iterator.hasNext()) {
                VolunteerImportModel importModel = iterator.next();
                batchExcelLine.add(importModel);
                if (batchExcelLine.size() >= 500) {
                    //处理数据 插入数据库
                    correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, schoolYear, studentNoMap, classMap, gradeMap, schoolLanguageEnum);
                    batchExcelLine.clear();
                }
            }
            if (!batchExcelLine.isEmpty()) {
                correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, schoolYear, studentNoMap, classMap, gradeMap, schoolLanguageEnum);
                batchExcelLine.clear();
            }
            //当前处理进度写入数据库
            task.setSuccessCount(correctCount);
            task.setFailCount(list.size() - correctCount);
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.updateById(task);
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
        } else {
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.updateById(task);
        }
    }

    public int processBatchExcelLine(List<ImportRecordSaveDTO> importErrorDTOS, List<VolunteerImportModel> list,
                                     Long schoolId, String schoolYear, Map<String, StudentEntity> studentNoMap,
                                     Map<Long, SysClass> classMap, Map<Long, GradeGroup> gradeMap, SchoolLanguageEnum schoolLanguageEnum) {
        if (CollectionUtils.isNotEmpty(list)) {
            int correctCount = list.size();//正确处理的条数
            List<VolunteerEntity> insertList = new ArrayList<>();
            //遍历要插入的每一行
            for (VolunteerImportModel bo : list) {
                List<String> studentErrorList = new ArrayList<>();
                if (!check(bo, studentErrorList, studentNoMap)) {
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
                VolunteerEntity entity = importConvert(schoolId, schoolYear, bo, studentNoMap, classMap, gradeMap);
                insertList.add(entity);
            }
            if (CollectionUtils.isNotEmpty(insertList)) {
                log.info("导入数据义工服务信息新增开始");
                this.saveBatch(insertList);
            }
            return correctCount;
        }
        return 0;
    }

    private VolunteerEntity importConvert(Long schoolId, String schoolYear, VolunteerImportModel bo, Map<String, StudentEntity> studentNoMap,
                                          Map<Long, SysClass> classMap, Map<Long, GradeGroup> gradeMap) {
        VolunteerEntity entity = new VolunteerEntity();
        entity.setSchoolId(schoolId);
        entity.setSchoolYear(schoolYear);
        StudentEntity studentEntity = studentNoMap.get(bo.getStudentNo());
        if (studentEntity != null) {
            entity.setStudentId(studentEntity.getId());
            SysClass sysClass = classMap.get(studentEntity.getClassId());
            if (sysClass != null) {
                entity.setClassId(sysClass.getId());
                entity.setClassName(sysClass.getClassName());
                GradeGroup gradeGroup = gradeMap.get(sysClass.getGradeGroup());
                if (gradeGroup != null) {
                    entity.setGradeName(gradeGroup.getGradeGroupName());
                }
            }
        }
        entity.setActivityName(bo.getActivityName());
        entity.setOrganization(bo.getOrganization());
        entity.setServiceDate(DateUtils.convertImportDate(bo.getServiceDate()));
        entity.setServiceSeconds((long)(Double.parseDouble(bo.getServiceHours()) * 3600));
        return entity;
    }

    private boolean check(VolunteerImportModel bo, List<String> errorList, Map<String, StudentEntity> studentNoMap) {
        //一项一项检查
        //学生信息检查
        if (StringUtils.isNotBlank(bo.getStudentName()) && StringUtils.isNotBlank(bo.getStudentNo())) {
            StudentEntity studentEntity = studentNoMap.get(bo.getStudentNo());
            if (studentEntity != null) {
                if (!studentEntity.getChineseName().equals(bo.getStudentName())) {
                    errorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_NAME_NOT_MATCH));
                }
            } else {
                errorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_NO_NOT_FOUND));
            }
        } else {
            if (!StringUtils.isNotBlank(bo.getStudentName())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_NAME_REQUIRED));
            }
            if (!StringUtils.isNotBlank(bo.getStudentNo())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_NO_REQUIRED));
            }
        }
        //活动名称检查
        if (!StringUtils.isNotBlank(bo.getActivityName())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.ACTIVITY_NAME_REQUIRED));
        }
        //机构名称检查
        if (!StringUtils.isNotBlank(bo.getOrganization())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.ORGANIZATION_REQUIRED));
        }
        //服务日期检查
        if (StringUtils.isNotBlank(bo.getServiceDate())) {
            try {
                if (StringUtils.isNumeric(bo.getServiceDate())) {
                    //execl日期格式解析为全数字，如：43444
                    DateUtil.getJavaDate(Double.parseDouble(bo.getServiceDate()));
                } else if (bo.getServiceDate().contains("/")) {
                    //字符串格式日期，如2024/12/18
                    DateUtils.formatStringToDate(bo.getServiceDate(), "yyyy/MM/dd");
                } else {
                    //字符串格式日期，如2024-12-18
                    DateUtils.formatStringToDate(bo.getServiceDate(), "yyyy-MM-dd");
                }
            } catch (Exception e) {
                errorList.add(String.format(languageUtil.getMessage(LanguageConstants.SERVICE_DATE_FORMAT_ERROR), bo.getServiceDate()));
            }
        }
        //服务时数检查
        if (!StringUtils.isNotBlank(bo.getServiceHours())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.SERVICE_HOURS_REQUIRED));
        } else {
            try {
                double hours = Double.parseDouble(bo.getServiceHours());
                if (hours <= 0) {
                    errorList.add(languageUtil.getMessage(LanguageConstants.SERVICE_HOURS_GREATER_THAN_ZERO));
                }
            } catch (NumberFormatException e) {
                errorList.add(languageUtil.getMessage(LanguageConstants.SERVICE_HOURS_FORMAT_ERROR));
            }
        }
        return !CollectionUtils.isNotEmpty(errorList);
    }

    private List<VolunteerImportModel> readExcelData(MultipartFile file, SchoolLanguageEnum schoolLanguageEnum) {
        List<VolunteerImportModel> result = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            switch (schoolLanguageEnum) {
                case ZH_MO:
                    VolunteerImportZhTwListener importZhTwListener = new VolunteerImportZhTwListener();
                    EasyExcel.read(inputStream, VolunteerImportZhTwModel.class, importZhTwListener).sheet().headRowNumber(1).doReadSync();
                    List<VolunteerImportZhTwModel> importZhTwModels = importZhTwListener.getDataList();
                    result = importZhTwModels.stream().map(item -> {
                        VolunteerImportModel model = new VolunteerImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case EN_US:
                    VolunteerImportEnUsListener importEnUsListener = new VolunteerImportEnUsListener();
                    EasyExcel.read(inputStream, VolunteerImportEnUsModel.class, importEnUsListener).sheet().headRowNumber(1).doReadSync();
                    List<VolunteerImportEnUsModel> importEnUsModels = importEnUsListener.getDataList();
                    result = importEnUsModels.stream().map(item -> {
                        VolunteerImportModel model = new VolunteerImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case PT_PT:
                    VolunteerImportPtPtListener importPtPtListener = new VolunteerImportPtPtListener();
                    EasyExcel.read(inputStream, VolunteerImportPtPtModel.class, importPtPtListener).sheet().headRowNumber(1).doReadSync();
                    List<VolunteerImportPtPtModel> importPtPtModels = importPtPtListener.getDataList();
                    result = importPtPtModels.stream().map(item -> {
                        VolunteerImportModel model = new VolunteerImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                default:
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
    public Map<Long, Integer> getVolunteerCount(Long classId, Long periodId) {
        //查询学段时间
        SemesterEntity semesterEntity = semesterService.getById(periodId);
        List<StudentCountDTO> volunteerCount = this.getBaseMapper().getVolunteerCount(classId, semesterEntity.getStartTime(), semesterEntity.getEndTime());
        if (CollectionUtils.isNotEmpty(volunteerCount)) {
            return volunteerCount.stream().collect(Collectors.toMap(StudentCountDTO::getStudentId, StudentCountDTO::getCount));
        }
        return Collections.emptyMap();

    }

    @Override
    public List<VolunteerStudentSumResModel> sumByStudent(Long schoolId, String schoolYear, Long groupId) {
        List<VolunteerStudentSumResModel> result = new ArrayList<>();
        // 获取学生信息
        StudentEntity nowStudent = (StudentEntity) StpUtil.getSession().get("student");
        if (nowStudent == null){
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        // 获取级组信息
        GradeGroup groupEntity = gradeGroupService.getById(groupId);
        if (groupEntity == null){
            throw new BusinessException(LanguageConstants.PARAM_ERROR);
        }
        // 查询义工数据
        List<VolunteerResModel> list = this.baseMapper.sumByStudent(schoolId,schoolYear,nowStudent.getId());
        if (ObjectUtils.isNotEmpty(list)) {
            // 查询学段信息
            List<SemesterEntity> semesterList = semesterService.list(Wrappers.<SemesterEntity>lambdaQuery()
                    .eq(SemesterEntity::getSchoolId, schoolId)
                    .eq(SemesterEntity::getSchoolYear, schoolYear)
                    .eq(SemesterEntity::getDepartment, groupEntity.getDepartment()));
            if (ObjectUtils.isNotEmpty(semesterList)) {
                for (SemesterEntity semester : semesterList) {
                    VolunteerStudentSumResModel model = new VolunteerStudentSumResModel();
                    model.setSemesterId(semester.getId());
                    model.setSemesterName(semester.getName());
                    // 获取学段时间内的义工时数
                    try {
                        model.setServiceHours(convertSecondsToHours(list.stream()
                                .filter(item -> semester.getStartTime().isBefore(item.getServiceDate().atStartOfDay()) &&
                                        semester.getEndTime().isAfter(item.getServiceDate().atStartOfDay()))
                                .mapToLong(VolunteerResModel::getServiceSeconds).sum()));
                    } catch (Exception e) {
                        log.error("义工时数转换错误", e);
                        model.setServiceHours(0D);
                    }
                    result.add(model);
                }
            }
        }
        return result;
    }

    private List<VolunteerExportModel> handleVolunteerExportData(List<VolunteerResModel> list) {
        List<VolunteerExportModel> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(volunteerResModel -> {
                VolunteerExportModel volunteerExportModel = new VolunteerExportModel();
                BeanUtils.copyProperties(volunteerResModel, volunteerExportModel);
                volunteerExportModel.setClassName(volunteerResModel.getGradeName() + volunteerResModel.getClassName());
                volunteerExportModel.setSeatNo(volunteerResModel.getSeatNo() == null ? "" : String.valueOf(volunteerResModel.getSeatNo()));
                volunteerExportModel.setServiceSeconds(String.valueOf(convertSecondsToHours(volunteerResModel.getServiceSeconds() != null ? volunteerResModel.getServiceSeconds() : ChronoUnit.SECONDS.between(volunteerResModel.getStartTime(), volunteerResModel.getEndTime()))));
                volunteerExportModel.setServiceDate(volunteerResModel.getServiceDate().toString());
                volunteerExportModel.setStartTime(volunteerResModel.getStartTime() != null ? volunteerResModel.getStartTime().toString() : "");
                volunteerExportModel.setEndTime(volunteerResModel.getEndTime() != null ? volunteerResModel.getEndTime().toString() : "");
                result.add(volunteerExportModel);
            });
        }
        return result;
    }
} 
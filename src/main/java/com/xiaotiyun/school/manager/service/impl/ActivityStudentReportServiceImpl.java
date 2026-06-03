package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.*;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.listener.ActivityStudentReportImportEnListener;
import com.xiaotiyun.school.manager.listener.ActivityStudentReportImportListener;
import com.xiaotiyun.school.manager.model.dto.ActivityStudentReportExportDTO;
import com.xiaotiyun.school.manager.model.dto.ActivityVolunteerLensonRoundDTO;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.ActivityStudentReportExportResModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentReportListResModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentReportQueryResModel;
import com.xiaotiyun.school.manager.model.res.ImportActivityStudentReportResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 活动已匹配表服务实现类
 */
@Slf4j
@Service
public class ActivityStudentReportServiceImpl extends ServiceImpl<ActivityStudentReportDao, ActivityStudentReportEntity> implements ActivityStudentReportService {

    @Resource
    private ImportTaskService importTaskService;

    @Resource
    private ImportRecordService importRecordService;

    @Resource
    private StudentService studentService;

    @Resource(name = "importExecutor")
    private ThreadPoolTaskExecutor importExecutor;

    @Resource
    private LanguageUtil languageUtil;

    @Resource
    private LeisureActivityCoursesRecordDao leisureActivityCoursesRecordDao;

    @Resource
    private LeisureCourseOpRecordService leisureCourseOpRecordService;

    @Resource
    private LeisureActivityRecordService leisureActivityRecordService;

    @Autowired
    private LeisureActivityRecordDao leisureActivityRecordDao;

    @Autowired
    private SemesterService semesterService;

    @Resource
    private UserDao userDao;

    @Resource
    private ActivityStudentApplyReportDao activityStudentApplyReportDao;

    @Resource
    private ActivityStudentReportDao activityStudentReportDao;

    @Resource
    private ActivityVolunteerLensonService activityVolunteerLensonService;

    @Resource
    private LeisureActivityCoursesRecordService leisureActivityCoursesRecordService;

    @Resource
    private ExportFileHandler exportFileHandler;


    @Resource
    private SysClassService sysClassService;

    @Override
    public ImportActivityStudentReportResModel importActivityStudentReport(ImportActivityStudentReportReqModel reqModel) {
        // 获取当前语言
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR));
        }

        // 读取Excel文件
        List<ActivityStudentReportImportModel> list = readExcelData(reqModel.getUploadFile(), currentLanguage);
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.IMPORT_DATA_EMPTY));
        }

        // 获取课程名额信息
        Integer courseQuota = getCourseQuota(reqModel.getLensonId(), reqModel.getActivityId());
        if (courseQuota < list.size()) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_COURSE_QUOTA_FULL));
        }

        // 创建导入任务
        ImportTaskEntity task = new ImportTaskEntity();
        task.setSchoolId(reqModel.getSchoolId());
        task.setFileName(reqModel.getUploadFile().getOriginalFilename());
        task.setType(ImportTaskTypeEnum.ACTIVITY_STUDENT_REPORT.getCode());
        task.setTotalCount(0);
        task.setSuccessCount(0);
        task.setFailCount(0);
        importTaskService.save(task);

        // 异步处理导入
        CompletableFuture.runAsync(() -> {
            languageUtil.setLanguage(currentLanguage);
            handleImport(task, list, reqModel, currentLanguage);
            LanguageUtil.clearLanguage();
        }, importExecutor).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("导入活动已匹配任务执行结束taskId=【{}】异常={}",task.getId(),ex);
            } else {
                log.info("导入活动已匹配完成，任务ID={}",task.getId());
            }
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.updateById(task);
        });

        // 返回任务ID
        ImportActivityStudentReportResModel resModel = new ImportActivityStudentReportResModel();
        resModel.setTaskId(String.valueOf(task.getId()));
        return resModel;
    }

    @Override
    public PageInfo<ActivityStudentReportQueryResModel> getStudentCourseList(ActivityStudentReportQueryReqModel reqModel) {
        if (reqModel == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.QUERY_PARAM_EMPTY));
        }
        if (reqModel.getSchoolId() == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SCHOOL_ID_REQUIRED));
        }
        if (reqModel.getSchoolYear() == null || reqModel.getSchoolYear().trim().isEmpty()) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SCHOOL_YEAR_REQUIRED));
        }
        if (reqModel.getDepartment() == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.DEPARTMENT_REQUIRED));
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ActivityStudentReportQueryResModel> resModels = this.baseMapper.selectStudentCourseList(reqModel);
        PageInfo<ActivityStudentReportQueryResModel> pageInfo = new PageInfo<>(resModels);
        return pageInfo;
    }

    private void handleImport(ImportTaskEntity task, List<ActivityStudentReportImportModel> list,
                              ImportActivityStudentReportReqModel reqModel, String currentLanguage) {
        task.setTotalCount(list.size());
        task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
        importTaskService.updateById(task);

        List<ImportRecordSaveDTO> failureReasons = new ArrayList<>();
        List<LeisureCourseOpRecordEntity> entities = new ArrayList<>();
        int successCount = 0;

        try {
            // 1. 校验学号是否有重复
            Map<String, Long> studentNoCountMap = list.stream()
                    .filter(model -> StringUtils.isNotBlank(model.getStudentNo()))
                    .collect(Collectors.groupingBy(ActivityStudentReportImportModel::getStudentNo, Collectors.counting()));

            // 检查重复的学号
            List<String> duplicateStudentNos = studentNoCountMap.entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // 为重复的学号记录错误信息
            for (String duplicateStudentNo : duplicateStudentNos) {
                List<ActivityStudentReportImportModel> duplicateModels = list.stream()
                        .filter(model -> duplicateStudentNo.equals(model.getStudentNo()))
                        .collect(Collectors.toList());

                for (ActivityStudentReportImportModel model : duplicateModels) {
                    ImportRecordSaveDTO failureReason = new ImportRecordSaveDTO();
                    failureReason.setTaskId(task.getId());
                    failureReason.setIncorrectLineno(String.valueOf(model.getRowIndex()));
                    failureReason.setIncorrectReason(languageUtil.getMessage(LanguageConstants.EXCEL_STUDENT_NO_DUPLICATE));
                    failureReasons.add(failureReason);
                }
            }

            // 获取所有学生信息
            List<String> studentNos = list.stream()
                    .map(ActivityStudentReportImportModel::getStudentNo)
                    .distinct() // 去重，避免重复查询
                    .collect(Collectors.toList());
            Map<String, StudentEntity> studentMap = studentService.getStudentMapByStudentNos(reqModel.getSchoolId(), studentNos);

            // 查询已存在的活动学生记录
            List<Long> studentIds = studentMap.values().stream()
                    .map(StudentEntity::getId)
                    .collect(Collectors.toList());

            Map<Long, ActivityStudentReportEntity> existingRecordMap = new HashMap<>();
            if (!studentIds.isEmpty()) {
                LambdaQueryWrapper<ActivityStudentReportEntity> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ActivityStudentReportEntity::getActivityId, reqModel.getActivityId())
                        .in(ActivityStudentReportEntity::getStudentId, studentIds)
                        .eq(ActivityStudentReportEntity::getDeleted, 0L);
                List<ActivityStudentReportEntity> existingRecords = this.list(queryWrapper);
                existingRecordMap = existingRecords.stream()
                        .collect(Collectors.toMap(ActivityStudentReportEntity::getStudentId, record -> record));
            }

            LeisureActivityRecordEntity activityRecord = leisureActivityRecordDao.selectById(reqModel.getActivityId());

            //查班级
            Set<Long> classIds = studentMap.values().stream()
                    .map(StudentEntity::getClassId)
                    .collect(Collectors.toSet());
            Map<Long, SysClass> classMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(classIds)) {
                //获取班级信息
                List<SysClass> sysClasses = sysClassService.listByIds(classIds);
                if (!CollectionUtils.isEmpty(sysClasses)) {
                    classMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, sysClass -> sysClass));
                }
            }

            // 处理导入数据
            for (ActivityStudentReportImportModel model : list) {
                // 跳过重复学号的数据
                if (duplicateStudentNos.contains(model.getStudentNo())) {
                    continue;
                }

                if (!validateImportData(model, failureReasons, task.getId(), studentMap, currentLanguage)) {
                    continue;
                }

                StudentEntity student = studentMap.get(model.getStudentNo());
                if (student == null) {
                    ImportRecordSaveDTO failureReason = new ImportRecordSaveDTO();
                    failureReason.setTaskId(task.getId());
                    failureReason.setIncorrectLineno(String.valueOf(model.getRowIndex()));
                    failureReason.setIncorrectReason(languageUtil.getMessage(LanguageConstants.STUDENT_NOT_FOUND));
                    failureReasons.add(failureReason);
                    continue;
                }

                // 检查学生姓名和编号是否一致
                if (!student.getChineseName().equals(model.getStudentName())) {
                    ImportRecordSaveDTO failureReason = new ImportRecordSaveDTO();
                    failureReason.setTaskId(task.getId());
                    failureReason.setIncorrectLineno(String.valueOf(model.getRowIndex()));
                    failureReason.setIncorrectReason(languageUtil.getMessage(LanguageConstants.STUDENT_NAME_NOT_MATCH_STUDENT_NO));
                    failureReasons.add(failureReason);
                    continue;
                }

                //检测学生学部和活动学部是否一致
                SysClass sysClass = classMap.get(student.getClassId());
                if (sysClass == null || !sysClass.getDepartment().equals(activityRecord.getDepartment())) {
                    ImportRecordSaveDTO failureReason = new ImportRecordSaveDTO();
                    failureReason.setTaskId(task.getId());
                    failureReason.setIncorrectLineno(String.valueOf(model.getRowIndex()));
                    failureReason.setIncorrectReason(languageUtil.getMessage(LanguageConstants.STUDENT_DEPARTMENT_NOT_MATCH));
                    failureReasons.add(failureReason);
                    continue;
                }

                //校验学年
                if (!activityRecord.getSchoolYear().equals(sysClass.getSid())) {
                    ImportRecordSaveDTO failureReason = new ImportRecordSaveDTO();
                    failureReason.setTaskId(task.getId());
                    failureReason.setIncorrectLineno(String.valueOf(model.getRowIndex()));
                    failureReason.setIncorrectReason(languageUtil.getMessage(LanguageConstants.STUDENT_YEAR_NOT_MATCH));
                    failureReasons.add(failureReason);
                    continue;
                }

                // 检查是否已存在记录
                ActivityStudentReportEntity existingRecord = existingRecordMap.get(student.getId());
                ActivityStudentReportEntity entity;

                if (existingRecord != null) {
                    // 更新已存在的记录
                    entity = existingRecord;
                    entity.setLensonId(reqModel.getLensonId());
                    entity.setUpdateTime(LocalDateTime.now());
                    // 可以根据需要更新其他字段
                } else {
                    // 创建新的活动匹配记录
                    entity = new ActivityStudentReportEntity();
                    entity.setActivityId(reqModel.getActivityId());
                    entity.setLensonId(reqModel.getLensonId());
                    entity.setStudentId(student.getId());
                    entity.setVolunteerType(0L);
                    entity.setType(LeisureActivityMatchTypeEnum.PRE_IMPORT.getCode()); // 预先导入
                    entity.setStatus(1); // 匹配状态
                    entity.setCreateTime(LocalDateTime.now());
                    entity.setUpdateTime(LocalDateTime.now());
                    entity.setDeleted(0L);
                }

                try {
                    if (existingRecord != null) {
                        updateById(entity);
                    } else {
                        save(entity);
                    }
                } catch (DuplicateKeyException ex) {
                    log.error("导入报名记录失败：", ex);
                    ImportRecordSaveDTO failureReason = new ImportRecordSaveDTO();
                    failureReason.setTaskId(task.getId());
                    failureReason.setIncorrectLineno(String.valueOf(model.getRowIndex()));
                    failureReason.setIncorrectReason(languageUtil.getMessage(LanguageConstants.ACTIVITY_STUDENT_RECORD_ERROR));
                    failureReasons.add(failureReason);
                    continue;
                } catch (Exception e) {
                    log.error("导入报名记录失败：", e);
                    ImportRecordSaveDTO failureReason = new ImportRecordSaveDTO();
                    failureReason.setTaskId(task.getId());
                    failureReason.setIncorrectLineno(String.valueOf(model.getRowIndex()));
                    failureReason.setIncorrectReason(languageUtil.getMessage(LanguageConstants.SAVE_ERROR));
                    failureReasons.add(failureReason);
                    continue;
                }

                LeisureCourseOpRecordEntity recordEntity = new LeisureCourseOpRecordEntity();
                recordEntity.setStudentId(entity.getStudentId());
                recordEntity.setStudentName(student.getChineseName());
                recordEntity.setSchoolId(reqModel.getSchoolId());
                recordEntity.setCoursesId(reqModel.getLensonId());
                recordEntity.setOperatorId(reqModel.getUserId());
                recordEntity.setOperationType(LeiSureOperationTypeEnum.BATCH_IMPORT.getCode());
                recordEntity.setOperatorName(reqModel.getUsername());
                recordEntity.setActivityId(reqModel.getActivityId());
                recordEntity.setSourceId(PageSourceEnum.PRE_IMPORT.getCode());
                entities.add(recordEntity);
                successCount++;
            }

            // 批量保存
            if (!entities.isEmpty()) {
                leisureCourseOpRecordService.saveBatch(entities);
            }

            // 保存错误信息
            if (!failureReasons.isEmpty()) {
                importRecordService.save(failureReasons);
            }
        } finally {
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            task.setFailCount(list.size() - successCount);
            task.setSuccessCount(successCount);
            importTaskService.updateById(task);
        }
    }

    private boolean validateImportData(ActivityStudentReportImportModel model,
                                       List<ImportRecordSaveDTO> failureReasons,
                                       Long taskId,
                                       Map<String, StudentEntity> studentMap,
                                       String currentLanguage) {
        boolean isValid = true;
        StringBuilder failureReason = new StringBuilder();

        // 验证学生姓名
        if (StringUtils.isBlank(model.getStudentName())) {
            failureReason.append(languageUtil.getMessage(LanguageConstants.STUDENT_NAME_REQUIRED));
            isValid = false;
        }

        // 验证学生编号
        if (StringUtils.isBlank(model.getStudentNo())) {
            failureReason.append(languageUtil.getMessage(LanguageConstants.STUDENT_NO_REQUIRED));
            isValid = false;
        } else if (!studentMap.containsKey(model.getStudentNo())) {
            failureReason.append(languageUtil.getMessage(LanguageConstants.STUDENT_NOT_FOUND));
            isValid = false;
        }

//        //验证学生姓名是否一致
//        StudentEntity studentEntity = studentMap.get(model.getStudentNo());
//        if(studentEntity != null)
//        {
//            if(studentEntity.getChineseName())
//        }

        if (!isValid) {
            ImportRecordSaveDTO failureReasonDTO = new ImportRecordSaveDTO();
            failureReasonDTO.setTaskId(taskId);
            failureReasonDTO.setIncorrectLineno(String.valueOf(model.getRowIndex()));
            failureReasonDTO.setIncorrectReason(failureReason.toString());
            failureReasons.add(failureReasonDTO);
        }

        return isValid;
    }

    private List<ActivityStudentReportImportModel> readExcelData(MultipartFile file, String currentLanguage) {
        List<ActivityStudentReportImportModel> result = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            // 根据语言选择不同的监听器
            switch (currentLanguage) {
                case "zh-MO":
                    ActivityStudentReportImportListener zhListener = new ActivityStudentReportImportListener();
                    EasyExcel.read(inputStream, ActivityStudentReportImportModel.class, zhListener)
                            .sheet()
                            .headRowNumber(1)
                            .doReadSync();
                    result = zhListener.getDataList();
                    break;
                case "en-US":
                    ActivityStudentReportImportEnListener enListener = new ActivityStudentReportImportEnListener();
                    EasyExcel.read(inputStream, ActivityStudentReportImportEnModel.class, enListener)
                            .sheet()
                            .headRowNumber(1)
                            .doReadSync();
                    result = enListener.getDataList().stream()
                            .map(item -> {
                                ActivityStudentReportImportModel model = new ActivityStudentReportImportModel();
                                model.setStudentName(item.getStudentName());
                                model.setStudentNo(item.getStudentNo());
                                model.setRowIndex(item.getRowIndex());
                                return model;
                            })
                            .collect(Collectors.toList());
                    break;
//                case "pt_PT":
//                    ActivityStudentReportImportPtListener ptListener = new ActivityStudentReportImportPtListener();
//                    EasyExcel.read(inputStream, ActivityStudentReportImportPtModel.class, ptListener)
//                            .sheet()
//                            .headRowNumber(2)
//                            .doReadSync();
//                    result = ptListener.getDataList().stream()
//                            .map(item -> {
//                                ActivityStudentReportImportModel model = new ActivityStudentReportImportModel();
//                                model.setStudentName(item.getStudentName());
//                                model.setStudentNo(item.getStudentNo());
//                                model.setRowIndex(item.getRowIndex());
//                                return model;
//                            })
//                            .collect(Collectors.toList());
//                    break;
                default:
                    throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR));
            }
        } catch (IOException e) {
            log.error("Excel文件读取失败", e);
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.FILE_READ_ERROR));
        }
        return result;
    }

    /**
     * 获取课程名额总数（余暇活动课程记录表）
     */
    private Integer getCourseQuota(Long lessonId, Long activityId) {
        // 查询所有未删除的课程名额
        LeisureActivityCoursesRecordEntity courseList = leisureActivityCoursesRecordDao.selectOne(
                new LambdaQueryWrapper<LeisureActivityCoursesRecordEntity>()
                        .eq(LeisureActivityCoursesRecordEntity::getId, lessonId)
                        .eq(LeisureActivityCoursesRecordEntity::getActivityId, activityId)
                        .eq(LeisureActivityCoursesRecordEntity::getDeleted, 0L)
        );
        return courseList.getQuotaTotal();
    }

    /**
     * 批量保存活动匹配记录
     */
    private void batchSaveActivityStudentReport(List<ActivityStudentReportEntity> batchList) {
        this.saveBatch(batchList);
    }

    @Override
    public Integer getNoCourseStudentCount(Long activityId) {
        if (activityId == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_ID_REQUIRED));
        }

        // 获取活动信息
        LeisureActivityRecordEntity leisureActivityRecordEntity = leisureActivityRecordDao.selectById(activityId);
        if (leisureActivityRecordEntity == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_EXISTS));
        }

        // 获取学期信息
        SemesterEntity semester = semesterService.getById(leisureActivityRecordEntity.getSemesterId());
        if (semester == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SEMESTER_NOT_EXISTS));
        }

        // 查询活动总人数
        Integer activityTotalCount = this.baseMapper.selectActivityTotalStudentCount(
                leisureActivityRecordEntity.getSchoolId(),
                semester.getSchoolYear(),
                leisureActivityRecordEntity.getDepartment()
        );

        // 查询课程总人数
        Integer courseTotalCount = this.baseMapper.selectCourseTotalStudentCount(activityId);

        // 计算无课程总人数
        int noCourseCount = (activityTotalCount != null ? activityTotalCount : 0) - (courseTotalCount != null ? courseTotalCount : 0);

        return Math.max(noCourseCount, 0); // 确保返回非负数
    }

    @Override
    public PageInfo<ActivityStudentReportListResModel> getActivityStudentReportList(ActivityStudentReportListReqModel reqModel) {
        if (reqModel == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.QUERY_PARAM_EMPTY));
        }

        // 参数校验
        if (reqModel.getLensonId() == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.COURSE_ID_REQUIRED));
        }

        // 通过课程ID获取活动信息
        LeisureActivityCoursesRecordEntity courseRecord = leisureActivityCoursesRecordDao.selectById(reqModel.getLensonId());
        if (courseRecord == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.COURSE_NOT_EXISTS));
        }

        // 通过活动ID获取活动信息
        LeisureActivityRecordEntity activityRecord = leisureActivityRecordDao.selectById(courseRecord.getActivityId());
        if (activityRecord == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_EXISTS));
        }

        // 通过学段ID获取学年信息
        SemesterEntity semester = semesterService.getById(activityRecord.getSemesterId());
        if (semester == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SEMESTER_NOT_EXISTS));
        }

        // 设置查询参数
        reqModel.setSchoolId(activityRecord.getSchoolId());
        reqModel.setSchoolYear(semester.getSchoolYear());
        reqModel.setDepartment(activityRecord.getDepartment());
        reqModel.setActivityId(activityRecord.getId());

        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ActivityStudentReportListResModel> resModels = this.baseMapper.selectActivityStudentReportList(reqModel);
        PageInfo<ActivityStudentReportListResModel> pageInfo = new PageInfo<>(resModels);
        return pageInfo;
    }

    @Override
    public Boolean batchRemoveActivityStudentReport(ActivityStudentReportRemoveReqModel reqModel) {
        if (reqModel == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.QUERY_PARAM_EMPTY));
        }

        if (reqModel.getIds() == null || reqModel.getIds().isEmpty()) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.STUDENT_ID_REQUIRED));
        }

        if (reqModel.getActivityId() == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_ID_REQUIRED));
        }

        // 查询要移除的记录
        LambdaQueryWrapper<ActivityStudentReportEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityStudentReportEntity::getActivityId, reqModel.getActivityId())
                .in(ActivityStudentReportEntity::getStudentId, reqModel.getIds())
                .eq(ActivityStudentReportEntity::getDeleted, 0L);

        List<ActivityStudentReportEntity> records = this.list(queryWrapper);

        if (records.isEmpty()) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.RECORD_NOT_EXIST));
        }

        // 检查是否有已发布的记录（状态为2）
        List<ActivityStudentReportEntity> publishedRecords = records.stream()
                .filter(record -> record.getStatus() != null && record.getStatus() == 2)
                .collect(Collectors.toList());

        if (!publishedRecords.isEmpty()) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_PUBLISHED_CANNOT_REMOVE));
        }

        // 执行逻辑删除
        List<Long> recordIds = records.stream()
                .map(ActivityStudentReportEntity::getId)
                .collect(Collectors.toList());

        // 逻辑删除，设置deleted为主键id，这里需要为每个记录设置对应的deleted值
        boolean result = this.removeByIds(recordIds);

        if (result) {
            log.info("批量移除活动匹配成功，移除记录数：{}", recordIds.size());

            // 记录操作日志到leisure_course_op_record表
            try {
                // 获取当前用户信息
                Long currentUserId = StpUtil.getLoginIdAsLong();
                UserEntity currentUser = userDao.selectById(currentUserId);
                String operatorName = currentUser != null ? currentUser.getUsername() : "未知用户";

                // 获取活动信息，用于获取学校ID和课程ID
                LeisureActivityRecordEntity activityRecord = leisureActivityRecordDao.selectById(reqModel.getActivityId());
                if (activityRecord != null) {
                    // 批量插入操作记录
                    List<LeisureCourseOpRecordEntity> opRecords = new ArrayList<>();

                    for (ActivityStudentReportEntity record : records) {
                        LeisureCourseOpRecordEntity opRecord = new LeisureCourseOpRecordEntity();
                        opRecord.setSchoolId(activityRecord.getSchoolId());
                        opRecord.setCoursesId(record.getLensonId()); // 课程ID
                        opRecord.setStudentId(record.getStudentId());

                        // 获取学生姓名
                        StudentEntity student = studentService.getById(record.getStudentId());
                        if (student != null) {
                            opRecord.setStudentName(student.getChineseName());
                        }

                        opRecord.setOperatorId(currentUserId);
                        opRecord.setOperatorName(operatorName);
                        opRecord.setOperationType(reqModel.getOpType() != null ? reqModel.getOpType() : 2); // 默认批量移除
                        opRecord.setActivityId(reqModel.getActivityId());
                        opRecord.setSourceId(reqModel.getSource());

                        opRecords.add(opRecord);
                    }

                    // 批量保存操作记录
                    if (!opRecords.isEmpty()) {
                        leisureCourseOpRecordService.saveBatch(opRecords);
                        log.info("操作记录保存成功，记录数：{}", opRecords.size());
                    }
                }
            } catch (Exception e) {
                log.error("保存操作记录失败：", e);
                // 不影响主业务流程，只记录错误日志
            }
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean transferCourse(List<ActivityStudentReportTransferReqModel> reqModelList) {
        if (reqModelList == null || reqModelList.isEmpty()) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.QUERY_PARAM_EMPTY));
        }

        // 按课程ID分组，检查每个课程的名额
        Map<Long, List<ActivityStudentReportTransferReqModel>> courseGroupMap = reqModelList.stream()
                .collect(Collectors.groupingBy(ActivityStudentReportTransferReqModel::getLensonId));

        // 检查每个课程的名额是否足够
        for (Map.Entry<Long, List<ActivityStudentReportTransferReqModel>> entry : courseGroupMap.entrySet()) {
            Long courseId = entry.getKey();
            List<ActivityStudentReportTransferReqModel> transferList = entry.getValue();

            // 获取课程信息
            LeisureActivityCoursesRecordEntity courseRecord = leisureActivityCoursesRecordDao.selectById(courseId);
            if (courseRecord == null) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.COURSE_NOT_EXISTS));
            }

            // 获取课程当前已报名人数
            LambdaQueryWrapper<ActivityStudentReportEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ActivityStudentReportEntity::getLensonId, courseId)
                    .eq(ActivityStudentReportEntity::getDeleted, 0L);
            long currentEnrollCount = this.count(queryWrapper);

            // 检查名额是否足够
            if (currentEnrollCount + transferList.size() > courseRecord.getQuotaTotal()) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_COURSE_QUOTA_FULL));
            }
        }

        // 获取当前用户信息
        Long currentUserId = StpUtil.getLoginIdAsLong();
        UserEntity currentUser = userDao.selectById(currentUserId);
        String operatorName = currentUser != null ? currentUser.getUsername() : "";

        // 批量处理转课程
        List<LeisureCourseOpRecordEntity> opRecords = new ArrayList<>();
        boolean allSuccess = true;
        List<Long> activityIds = reqModelList.stream().map(ActivityStudentReportTransferReqModel::getActivityId).distinct().collect(Collectors.toList());
        List<LeisureActivityRecordEntity> activityRecordEntities = leisureActivityRecordDao.selectBatchIds(activityIds);
        Map<Long, LeisureActivityRecordEntity> activityRecordMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(activityRecordEntities)) {
            activityRecordMap = activityRecordEntities.stream().collect(Collectors.toMap(LeisureActivityRecordEntity::getId, activityRecord -> activityRecord));
        }
        for (ActivityStudentReportTransferReqModel reqModel : reqModelList) {
            try {
                // 检查学生是否已有匹配数据
                LambdaQueryWrapper<ActivityStudentReportEntity> existingQuery = new LambdaQueryWrapper<>();
                existingQuery.eq(ActivityStudentReportEntity::getStudentId, reqModel.getStudentId())
                        .eq(ActivityStudentReportEntity::getActivityId, reqModel.getActivityId())
                        .eq(ActivityStudentReportEntity::getDeleted, 0L);

                ActivityStudentReportEntity existingRecord = this.getOne(existingQuery);

                // 如果已有匹配数据，检查是否为公布状态
                ActivityStudentReportEntity newRecord = null;
                LeisureActivityRecordEntity leisureActivityRecordEntity = activityRecordMap.get(reqModel.getActivityId());
                if (leisureActivityRecordEntity == null) {
                    throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_EXISTS));
                }
                if (existingRecord != null) {
                    if (existingRecord.getStatus() != null && existingRecord.getStatus() == 2) {
                        throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_PUBLISHED_CANNOT_REMOVE));
                    }

                    // 先移除原有记录
                    existingRecord.setLensonId(reqModel.getLensonId());
                    if (leisureActivityRecordEntity.getSecondEndTime() != null) {
                        //二次报名
                        existingRecord.setType(LeisureActivityMatchTypeEnum.SECOND_ASSIGN.getCode());
                    } else {
                        //一次报名
                        existingRecord.setType(LeisureActivityMatchTypeEnum.ASSIGN.getCode());
                    }
                    newRecord = existingRecord;
                } else {
                    // 创建新的匹配记录
                    newRecord = new ActivityStudentReportEntity();
                    newRecord.setActivityId(reqModel.getActivityId());
                    newRecord.setLensonId(reqModel.getLensonId());
                    newRecord.setStudentId(reqModel.getStudentId());
                    if (leisureActivityRecordEntity.getSecondEndTime() != null) {
                        //二次报名
                        newRecord.setType(LeisureActivityMatchTypeEnum.SECOND_ASSIGN.getCode());
                    } else {
                        //一次报名
                        newRecord.setType(LeisureActivityMatchTypeEnum.ASSIGN.getCode());
                    }
                    newRecord.setStatus(1); // 匹配状态
                    newRecord.setVolunteerType(0L); // 默认第一志愿
                }

                if (newRecord.getId() != null && newRecord.getId() > 0) {
                    this.updateById(newRecord);
                } else {
                    this.save(newRecord);
                }

                // 记录转入操作
                LeisureCourseOpRecordEntity transferOpRecord = new LeisureCourseOpRecordEntity();
                transferOpRecord.setSchoolId(reqModel.getSchoolId());
                transferOpRecord.setCoursesId(newRecord.getLensonId());
                transferOpRecord.setStudentId(newRecord.getStudentId());

                // 获取学生姓名
                StudentEntity student = studentService.getById(newRecord.getStudentId());
                if (student != null) {
                    transferOpRecord.setStudentName(student.getChineseName());
                }

                transferOpRecord.setOperatorId(currentUserId);
                transferOpRecord.setOperatorName(operatorName);
                transferOpRecord.setOperationType(reqModel.getOpType() != null ? reqModel.getOpType() : 8); // 默认转入
                transferOpRecord.setActivityId(reqModel.getActivityId());
                transferOpRecord.setSourceId(reqModel.getSource());
                opRecords.add(transferOpRecord);

            } catch (Exception e) {
                log.error("转课程失败，学生ID：{}，课程ID：{}", reqModel.getStudentId(), reqModel.getLensonId(), e);
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SAVE_ERROR));
            }
        }

        // 批量保存操作记录
        if (!opRecords.isEmpty()) {
            try {
                leisureCourseOpRecordService.saveBatch(opRecords);
                log.info("转课程操作记录保存成功，记录数：{}", opRecords.size());
            } catch (Exception e) {
                log.error("保存转课程操作记录失败：", e);
                // 不影响主业务流程，只记录错误日志
            }
        }

        return allSuccess;
    }

    /**
     * 处理单个活动
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processActivity(LeisureActivityRecordEntity activity,boolean endNow) {
        Long activityId = activity.getId();
        log.info("开始处理活动，活动ID: {}, 活动名称: {}", activityId, activity.getName());

        // 1. 获取报名学生和志愿信息
        Map<Long, List<ActivityVolunteerLensonEntity>> studentVolunteersMap = getStudentVolunteersMap(activityId, activity);

        // if (activity.getSecondEndTime() == null && studentVolunteersMap.isEmpty()) {
        //     log.info("活动 {} 没有报名学生", activityId);
        //     return;
        // }

        // 2. 获取活动下的所有课程
        List<LeisureActivityCoursesRecordEntity> courses = getActivityCourses(activityId);

        if (courses.isEmpty()) {
            log.info("活动 {} 没有课程", activityId);
            return;
        }

        // 3. 按志愿层级进行录取匹配
        Map<Long, ActivityVolunteerLensonRoundDTO> enrolledStudents = new HashMap<>(); // 学生ID -> 录取的课程ID
        Integer volunteerNum = activity.getVolunteerNum();

        // 按志愿层级处理
        for (int round = 1; round <= volunteerNum; round++) {
            processVolunteerRound(activityId, round, courses, studentVolunteersMap, enrolledStudents);
        }

        // 二次报名时，无课程学生需要再次随机抽取课程
        if (activity.getSecondEndTime() != null) {
            ActivityStudentReportQueryReqModel reqModel = new ActivityStudentReportQueryReqModel();
            reqModel.setSchoolId(activity.getSchoolId());
            reqModel.setSchoolYear(activity.getSchoolYear());
            reqModel.setDepartment(activity.getDepartment());
            reqModel.setActivityId(activityId);
            List<ActivityStudentReportQueryResModel> noSelectStudentCourseList = this.baseMapper.selectStudentCourseList(reqModel);
            if (!CollectionUtils.isEmpty(noSelectStudentCourseList)) {
                secondaryRandomSelection(activityId, courses, noSelectStudentCourseList, enrolledStudents);
            }
        }

        // 4. 更新活动状态
        if (activity.getDrawStatus() == 0) {
            if (endNow){
                activity.setEndTime(new Date());
            }
            activity.setDrawStatus(1);
        } else if (activity.getDrawStatus() == 1 && activity.getSecondEndTime() != null) {
            if (endNow){
                activity.setSecondEndTime(new Date());
            }
            activity.setDrawStatus(2);
        }
        leisureActivityRecordService.updateById(activity);

        // 5. 更新报名状态和添加匹配结果
        updateEnrollmentResults(activityId, enrolledStudents, studentVolunteersMap, activity);




        log.info("活动 {} 处理完成，共录取 {} 名学生", activityId, enrolledStudents.size());
    }

    /**
     * 获取报名学生和志愿信息
     */
    private Map<Long, List<ActivityVolunteerLensonEntity>> getStudentVolunteersMap(Long activityId, LeisureActivityRecordEntity activity) {
        // 确定报名类型
        Integer applyType = activity.getSecondEndTime() != null ? 2 : 1; // 有二次报名时间则为二次报名

        // 通过连表查询一次性获取所有学生报名记录和志愿信息
        List<ActivityVolunteerLensonEntity> volunteers = activityStudentApplyReportDao.selectStudentVolunteersByActivity(
                activityId, applyType);

        if (volunteers.isEmpty()) {
            return new HashMap<>();
        }

        // 按学生ID分组
        return volunteers.stream()
                .collect(Collectors.groupingBy(ActivityVolunteerLensonEntity::getStudentId));
    }

    /**
     * 获取活动下的所有课程（包含剩余名额信息）
     */
    private List<LeisureActivityCoursesRecordEntity> getActivityCourses(Long activityId) {
        LambdaQueryWrapper<LeisureActivityCoursesRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeisureActivityCoursesRecordEntity::getActivityId, activityId)
                .eq(LeisureActivityCoursesRecordEntity::getDeleted, 0L);

        List<LeisureActivityCoursesRecordEntity> courses = leisureActivityCoursesRecordService.list(queryWrapper);

        // 计算每个课程的剩余名额
        for (LeisureActivityCoursesRecordEntity course : courses) {
            // 查询该课程已经匹配的学生数量
            LambdaQueryWrapper<ActivityStudentReportEntity> matchQueryWrapper = new LambdaQueryWrapper<>();
            matchQueryWrapper.eq(ActivityStudentReportEntity::getActivityId, activityId)
                    .eq(ActivityStudentReportEntity::getLensonId, course.getId())
                    .eq(ActivityStudentReportEntity::getDeleted, 0L);

            long matchedCount = activityStudentReportDao.selectCount(matchQueryWrapper);

            // 计算剩余名额
            int remainingQuota = course.getQuotaTotal() - (int) matchedCount;
            course.setQuotaTotal(Math.max(0, remainingQuota)); // 确保不为负数

            log.debug("课程 {} 总名额: {}, 已匹配: {}, 剩余名额: {}",
                    course.getId(), course.getQuotaTotal() + matchedCount, matchedCount, remainingQuota);
        }

        return courses;
    }

    /**
     * 处理指定轮次的志愿
     */
    private void processVolunteerRound(Long activityId, int round, List<LeisureActivityCoursesRecordEntity> courses,
                                       Map<Long, List<ActivityVolunteerLensonEntity>> studentVolunteersMap,
                                       Map<Long, ActivityVolunteerLensonRoundDTO> enrolledStudents) {
        log.info("处理活动 {} 的第 {} 轮志愿", activityId, round);

        // 按课程处理
        for (LeisureActivityCoursesRecordEntity course : courses) {
            // 获取该课程当前轮次的所有志愿学生（排除已录取学生）
            List<ActivityVolunteerLensonEntity> courseVolunteers = getCourseVolunteersForRound(
                    course.getId(), round, studentVolunteersMap, enrolledStudents);

            if (courseVolunteers.isEmpty()) {
                continue;
            }

            // 打乱学生顺序，确保公平性
            List<ActivityVolunteerLensonEntity> shuffledStudents = new ArrayList<>(courseVolunteers);
            Collections.shuffle(shuffledStudents);

            // 录取学生
            int quota = course.getQuotaTotal();
            int enrolledCount = 0;

            for (ActivityVolunteerLensonEntity volunteer : shuffledStudents) {
                if (enrolledCount >= quota) {
                    break;
                }

                Long studentId = volunteer.getStudentId();
                if (!enrolledStudents.containsKey(studentId)) {
                    ActivityVolunteerLensonRoundDTO roundDTO = new ActivityVolunteerLensonRoundDTO();
                    roundDTO.setCourseId(course.getId());
                    roundDTO.setRound(round);
                    enrolledStudents.put(studentId, roundDTO);
                    enrolledCount++;
                    log.debug("学生 {} 被课程 {} 录取（第{}志愿）", studentId, course.getId(), round);
                }
            }
            //更新课程剩余数量
            course.setQuotaTotal(Math.max(0, quota - enrolledCount));
        }
    }

    /**
     * 二次随机抽取
     */
    private void secondaryRandomSelection(Long activityId, List<LeisureActivityCoursesRecordEntity> courses,
                                          List<ActivityStudentReportQueryResModel> noSelectStudentCourseList,
                                          Map<Long, ActivityVolunteerLensonRoundDTO> enrolledStudents) {
        // 过滤出还有剩余名额的课程（增加判空处理）
        List<LeisureActivityCoursesRecordEntity> availableCourses = courses.stream()
                .filter(course -> course.getQuotaTotal() != null && course.getQuotaTotal() > 0)
                .collect(Collectors.toList());

        // 如果没有可选课程，直接返回
        if (availableCourses.isEmpty()) {
            log.info("活动 {} 没有剩余名额的课程可供二次随机分配", activityId);
            return;
        }

        // 打乱学生顺序，确保公平性
        List<ActivityStudentReportQueryResModel> shuffledStudents = new ArrayList<>(noSelectStudentCourseList);
        Collections.shuffle(shuffledStudents);

        // 为每个学生随机分配一门课程
        for (ActivityStudentReportQueryResModel student : shuffledStudents) {
            // 再次检查是否该学生已经分配过课程
            if (enrolledStudents.containsKey(student.getStudentId())) {
                continue;
            }

            // 重新获取还有名额的课程列表（因为名额在分配过程中会减少）
            List<LeisureActivityCoursesRecordEntity> currentAvailableCourses = availableCourses.stream()
                    .filter(course -> course.getQuotaTotal() != null && course.getQuotaTotal() > 0)
                    .collect(Collectors.toList());

            // 如果没有可选课程，跳出循环
            if (currentAvailableCourses.isEmpty()) {
                log.info("活动 {} 剩余名额已分配完毕", activityId);
                break;
            }

            // 随机选择一门课程
            Random random = new Random();
            int randomIndex = random.nextInt(currentAvailableCourses.size());
            LeisureActivityCoursesRecordEntity selectedCourse = currentAvailableCourses.get(randomIndex);

            // 记录分配结果
            ActivityVolunteerLensonRoundDTO roundDTO = new ActivityVolunteerLensonRoundDTO();
            roundDTO.setCourseId(selectedCourse.getId());
            roundDTO.setRound(0);
            enrolledStudents.put(student.getStudentId(), roundDTO);

            // 减少该课程的剩余名额
            selectedCourse.setQuotaTotal(selectedCourse.getQuotaTotal() - 1);

            log.debug("学生 {} 被随机分配到课程 {}", student.getStudentId(), selectedCourse.getId());
        }

        log.info("二次随机分配完成，共分配 {} 名学生", enrolledStudents.size());
    }

    /**
     * 获取指定课程指定轮次的所有志愿学生
     */
    private List<ActivityVolunteerLensonEntity> getCourseVolunteersForRound(Long courseId, int round,
                                                                            Map<Long, List<ActivityVolunteerLensonEntity>> studentVolunteersMap,
                                                                            Map<Long, ActivityVolunteerLensonRoundDTO> enrolledStudents) {
        List<ActivityVolunteerLensonEntity> result = new ArrayList<>();

        for (Map.Entry<Long, List<ActivityVolunteerLensonEntity>> entry : studentVolunteersMap.entrySet()) {
            Long studentId = entry.getKey();

            // 跳过已录取学生
            if (enrolledStudents.containsKey(studentId)) {
                continue;
            }

            // 查找该学生的指定轮次志愿
            List<ActivityVolunteerLensonEntity> studentVolunteers = entry.getValue();
            for (ActivityVolunteerLensonEntity volunteer : studentVolunteers) {
                if (volunteer.getVolunteerType() == round && volunteer.getLensonId().equals(courseId)) {
                    result.add(volunteer);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * 更新录取结果
     */
    private void updateEnrollmentResults(Long activityId, Map<Long, ActivityVolunteerLensonRoundDTO> enrolledStudents,
                                         Map<Long, List<ActivityVolunteerLensonEntity>> studentVolunteersMap,
                                         LeisureActivityRecordEntity activity) {
        // 确定匹配类型
        Integer matchType = activity.getSecondEndTime() != null ? 4 : 3; // 有二次报名时间为4，否则为3

        if (activity.getSecondEndTime() == null && studentVolunteersMap.isEmpty()) {
            return;
        }

        // 分离已匹配和未匹配的学生
        List<Long> enrolledStudentIds = new ArrayList<>();
        List<Long> unmatchedStudentIds = new ArrayList<>();

        for (Long studentId : studentVolunteersMap.keySet()) {
            if (enrolledStudents.containsKey(studentId)) {
                enrolledStudentIds.add(studentId);
            } else {
                unmatchedStudentIds.add(studentId);
            }
        }

        // 1. 更新已匹配学生的状态为已匹配
        if (!enrolledStudentIds.isEmpty()) {
            LambdaQueryWrapper<ActivityStudentApplyReportEntity> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(ActivityStudentApplyReportEntity::getActivityId, activityId)
                    .in(ActivityStudentApplyReportEntity::getStudentId, enrolledStudentIds)
                    .eq(ActivityStudentApplyReportEntity::getDeleted, 0L);

            ActivityStudentApplyReportEntity updateEntity = new ActivityStudentApplyReportEntity();
            updateEntity.setStatus(2); // 已匹配状态

            activityStudentApplyReportDao.update(updateEntity, updateWrapper);
        }

        // 2. 更新未匹配学生的状态为匹配失败
        if (!unmatchedStudentIds.isEmpty()) {
            LambdaQueryWrapper<ActivityStudentApplyReportEntity> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(ActivityStudentApplyReportEntity::getActivityId, activityId)
                    .in(ActivityStudentApplyReportEntity::getStudentId, unmatchedStudentIds)
                    .eq(ActivityStudentApplyReportEntity::getDeleted, 0L);

            ActivityStudentApplyReportEntity updateEntity = new ActivityStudentApplyReportEntity();
            updateEntity.setStatus(3); // 匹配失败状态

            activityStudentApplyReportDao.update(updateEntity, updateWrapper);
        }

        // 3. 添加匹配记录
        List<ActivityStudentReportEntity> matchRecords = new ArrayList<>();
        for (Map.Entry<Long, ActivityVolunteerLensonRoundDTO> entry : enrolledStudents.entrySet()) {
            Long studentId = entry.getKey();
            ActivityVolunteerLensonRoundDTO roundDTO = entry.getValue();

            ActivityStudentReportEntity matchRecord = new ActivityStudentReportEntity();
            matchRecord.setActivityId(activityId);
            matchRecord.setStudentId(studentId);
            matchRecord.setLensonId(roundDTO.getCourseId());
            matchRecord.setVolunteerType(roundDTO.getRound().longValue());
            matchRecord.setStatus(1); // 匹配状态
            if (!enrolledStudentIds.isEmpty() && enrolledStudentIds.contains(studentId)) {
                matchRecord.setType(matchType); // 匹配类型：3=一次报名志愿录入，4=二次报名志愿录入
            } else {
                matchRecord.setType(5); // 匹配类型：5=系统分配
            }

            matchRecords.add(matchRecord);
        }

        // 批量插入匹配记录
        this.saveBatch(matchRecords);

        log.info("活动 {} 匹配结果更新完成：已匹配 {} 人，匹配失败 {} 人",
                activityId, enrolledStudentIds.size(), unmatchedStudentIds.size());
    }

    @Override
    public ActivityStudentReportExportResModel exportActivityStudentReport(ActivityStudentReportExportReqModel reqModel) {
        // 参数校验
        if (reqModel == null || reqModel.getActivityId() == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.QUERY_PARAM_EMPTY));
        }

        // 查询导出数据
        List<ActivityStudentReportExportDTO> exportData = activityStudentReportDao.selectActivityStudentReportExportData(reqModel.getActivityId());

        if (CollectionUtils.isEmpty(exportData)) {
            return new ActivityStudentReportExportResModel();
        }

        // 获取当前语言
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        String fileName = "課程分配數據.xlsx";

        // 根据语言设置不同的文件名和导出模型
        if (SchoolLanguageEnum.EN_US.getCode().equals(currentLanguage)) {
            fileName = "Course Assignment Data.xlsx";
            List<ActivityStudentReportExportEnModel> exportEnModels = exportData.stream()
                    .map(dto -> {
                        ActivityStudentReportExportEnModel exportModel = new ActivityStudentReportExportEnModel();
                        BeanUtils.copyProperties(dto, exportModel);
                        return exportModel;
                    })
                    .collect(Collectors.toList());

            String url = exportFileHandler.doExportExcel(exportEnModels, fileName, ActivityStudentReportExportEnModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());

            ActivityStudentReportExportResModel resModel = new ActivityStudentReportExportResModel();
            resModel.setUrl(url);
            return resModel;

        } else if (SchoolLanguageEnum.PT_PT.getCode().equals(currentLanguage)) {
            fileName = "Dados de Atribuição de Cursos.xlsx";
            List<ActivityStudentReportExportPtModel> exportPtModels = exportData.stream()
                    .map(dto -> {
                        ActivityStudentReportExportPtModel exportModel = new ActivityStudentReportExportPtModel();
                        BeanUtils.copyProperties(dto, exportModel);
                        return exportModel;
                    })
                    .collect(Collectors.toList());

            String url = exportFileHandler.doExportExcel(exportPtModels, fileName, ActivityStudentReportExportPtModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());

            ActivityStudentReportExportResModel resModel = new ActivityStudentReportExportResModel();
            resModel.setUrl(url);
            return resModel;

        } else {
            // 默认中文
            List<ActivityStudentReportExportModel> exportModels = exportData.stream()
                    .map(dto -> {
                        ActivityStudentReportExportModel exportModel = new ActivityStudentReportExportModel();
                        BeanUtils.copyProperties(dto, exportModel);
                        return exportModel;
                    })
                    .collect(Collectors.toList());

            String url = exportFileHandler.doExportExcel(exportModels, fileName, ActivityStudentReportExportModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());

            ActivityStudentReportExportResModel resModel = new ActivityStudentReportExportResModel();
            resModel.setUrl(url);
            return resModel;
        }
    }
} 
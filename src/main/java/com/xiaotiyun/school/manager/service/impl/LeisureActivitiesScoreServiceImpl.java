package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.ImportTaskStatusEnum;
import com.xiaotiyun.school.manager.basic.enums.ImportTaskTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.LeisureActivitiesScoreDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.listener.LeisureActivitiesScoreImportEnUsListener;
import com.xiaotiyun.school.manager.listener.LeisureActivitiesScoreImportZhTwListener;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.dto.LeisureActivitiesRatingDTO;
import com.xiaotiyun.school.manager.model.dto.LeisureActivitiesRatingRangeDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.LeisureActivitiesScorePageReqModel;
import com.xiaotiyun.school.manager.model.req.LeisureActivitiesScoreSaveReqModel;
import com.xiaotiyun.school.manager.model.res.LeisureActivitiesScorePageResModel;
import com.xiaotiyun.school.manager.model.res.SystemSettingResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 余暇活动成绩信息Service层实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeisureActivitiesScoreServiceImpl extends ServiceImpl<LeisureActivitiesScoreDao, LeisureActivitiesScoreEntity> implements LeisureActivitiesScoreService {
    private static final ExecutorService importPool = new ThreadPoolExecutor(10, 15, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100));
    private final LanguageUtil languageUtil;
    private final ExportFileHandler exportFileHandler;
    private final ImportTaskService importTaskService;
    private final StudentService studentService;
    private final ImportRecordService importRecordService;
    private final LeisureActivityRecordService leisureActivityRecordService;
    private final LeisureActivityCoursesRecordService leisureActivityCoursesRecordService;
    private final SystemSettingService systemSettingService;
    private final UserAuthHelper userAuthHelper;

    @Override
    public PageInfo<LeisureActivitiesScorePageResModel> page(Long schoolId, Long userId, LeisureActivitiesScorePageReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(userId, schoolId);
        List<Long> classIds = new ArrayList<>();
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(userId, schoolId);
            if (CollectionUtils.isEmpty(classIds)) {
                return null;
            }
        }
        if (CollectionUtils.isNotEmpty(classIds) && reqModel.getClassId() != null && reqModel.getClassId() > 0) {
            //普通用户班级权限不为空，请求选择了某个班级
            if (!classIds.contains(reqModel.getClassId())) {
                return null;
            }
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<LeisureActivitiesScorePageResModel> list = this.getBaseMapper().page(classIds, reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            //查询学生成绩信息
            List<Long> studentIds = list.stream().map(LeisureActivitiesScorePageResModel::getStudentId).distinct().collect(Collectors.toList());
            QueryWrapper<LeisureActivitiesScoreEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(LeisureActivitiesScoreEntity::getActivityId, reqModel.getActivityId())
                    .in(LeisureActivitiesScoreEntity::getStudentId, studentIds);
            List<LeisureActivitiesScoreEntity> scoreEntities = this.list(wrapper);
            Map<String, LeisureActivitiesScoreEntity> scoreMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(scoreEntities)) {
                scoreMap = scoreEntities.stream().collect(Collectors.toMap(score -> score.getCourseId() + "_" + score.getStudentId(), score -> score));
            }
            for (LeisureActivitiesScorePageResModel resModel : list) {
                LeisureActivitiesScoreEntity scoreEntity = scoreMap.get(resModel.getCourseId() + "_" + resModel.getStudentId());
                if (scoreEntity != null) {
                    BeanUtils.copyProperties(scoreEntity, resModel);
                }
            }
        }
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LeisureActivitiesScoreEntity save(Long schoolId, LeisureActivitiesScoreSaveReqModel reqModel) {
        LeisureActivitiesScoreEntity entity = BeanConvertUtil.convert(reqModel, LeisureActivitiesScoreEntity.class);
        entity.setSchoolId(schoolId);
        this.save(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LeisureActivitiesScoreEntity update(Long id, LeisureActivitiesScoreSaveReqModel reqModel) {
        LeisureActivitiesScoreEntity entity = this.getById(id);
        if (entity != null) {
            BeanUtils.copyProperties(reqModel, entity);
            this.updateById(entity);
        }
        return entity;
    }

    @Override
    public Long importScore(Long schoolId, Long activityId, MultipartFile file) {
        if (schoolId == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.NO_SCHOOL_FILE_CONTENT_EMPTY));
        }
        SystemSettingResModel schoolSettings = systemSettingService.getSchoolSettings(schoolId);
        if (schoolSettings == null || !StringUtils.isNotBlank(schoolSettings.getSettings().get("leisureActivitiesRating"))) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.LEISURE_ACTIVITIES_RATING_NOT_SET));
        }
        // 余暇活动记录
        LeisureActivityRecordEntity recordEntities = leisureActivityRecordService.getById(activityId);
        if (ObjectUtils.isEmpty(recordEntities)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        List<LeisureActivitiesRatingDTO> ratingDTO = JSON.parseArray(schoolSettings.getSettings().get("leisureActivitiesRating"), LeisureActivitiesRatingDTO.class);
        if (CollectionUtils.isNotEmpty(ratingDTO)) {
            ratingDTO = ratingDTO.stream().filter(item -> item.getDepartment().equals(recordEntities.getDepartment())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(ratingDTO)){
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.LEISURE_ACTIVITIES_RATING_NOT_SET));
            }
        } else {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.LEISURE_ACTIVITIES_RATING_NOT_SET));
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
        List<LeisureActivitiesScoreImportModel> list = readExcelData(file, languageEnum);
        if (CollectionUtils.isNotEmpty(list)) {
            LeisureActivitiesRatingDTO leisureActivitiesRatingDTO = ratingDTO.get(0);
            // 创建导入任务
            ImportTaskEntity task = new ImportTaskEntity();
            task.setSchoolId(schoolId);
            task.setFileName(file.getOriginalFilename());
            task.setType(ImportTaskTypeEnum.LEISURE_ACTIVITIES_SCORE.getCode());
            task.setTotalCount(0);
            task.setSuccessCount(0);
            task.setFailCount(0);
            importTaskService.save(task);
            CompletableFuture.runAsync(() -> {
                languageUtil.setLanguage(languageEnum.getCode());
                handleImport(task, list, schoolId, activityId, leisureActivitiesRatingDTO);
                LanguageUtil.clearLanguage();
            }, importPool).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("导入余暇活动成绩任务执行结束taskId=【{}】异常={}",task.getId(),ex);
                } else {
                    log.info("导入余暇活动成绩完成，任务ID={}",task.getId());
                }
                task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                importTaskService.updateById(task);
            });
            return task.getId();
        }
        return null;
    }

    private void handleImport(ImportTaskEntity task, List<LeisureActivitiesScoreImportModel> list, Long schoolId, Long activityId, LeisureActivitiesRatingDTO ratingDTO) {
        if (CollectionUtils.isNotEmpty(list)) {
            task.setTotalCount(list.size());
            task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
            importTaskService.updateById(task);
            log.info("开始处理数据导入...");
            Iterator<LeisureActivitiesScoreImportModel> iterator = list.iterator();
            //每500个处理一次
            List<LeisureActivitiesScoreImportModel> batchExcelLine = new ArrayList<>(500);
            List<String> studentNoList = list.stream().map(LeisureActivitiesScoreImportModel::getStudentNo).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
            Map<String, StudentEntity> studentNoMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(studentNoList)) {
                QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(StudentEntity::getSchoolId, schoolId)
                        .in(StudentEntity::getStudentNo, studentNoList);
                List<StudentEntity> studentEntities = studentService.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(studentEntities)) {
                    studentNoMap = studentEntities.stream().collect(Collectors.toMap(StudentEntity::getStudentNo, studentEntity -> studentEntity));
                }
            }
            //获取课程信息
            QueryWrapper<LeisureActivityCoursesRecordEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(LeisureActivityCoursesRecordEntity::getActivityId, activityId);
            List<LeisureActivityCoursesRecordEntity> coursesRecordEntities = leisureActivityCoursesRecordService.list(queryWrapper);
            Map<String, LeisureActivityCoursesRecordEntity> coursesRecordMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(coursesRecordEntities)) {
                coursesRecordMap = coursesRecordEntities.stream().collect(Collectors.toMap(LeisureActivityCoursesRecordEntity::getName, coursesRecordEntity -> coursesRecordEntity));
            }
            //获取历史成绩
            QueryWrapper<LeisureActivitiesScoreEntity> queryWrapperScore = new QueryWrapper<>();
            queryWrapperScore.lambda().eq(LeisureActivitiesScoreEntity::getActivityId, activityId);
            List<LeisureActivitiesScoreEntity> scoreEntities = this.list(queryWrapperScore);
            Map<String, LeisureActivitiesScoreEntity> oldScoreMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(scoreEntities)) {
                oldScoreMap = scoreEntities.stream().collect(Collectors.toMap(scoreEntity -> scoreEntity.getCourseId() + "_" + scoreEntity.getStudentId(), scoreEntity -> scoreEntity));
            }
            int correctCount = 0;
            List<ImportRecordSaveDTO> importRecordSaveDTOS = new ArrayList<>();
            while (iterator.hasNext()) {
                LeisureActivitiesScoreImportModel importModel = iterator.next();
                batchExcelLine.add(importModel);
                if (batchExcelLine.size() >= 500) {
                    //处理数据 插入数据库
                    correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, activityId,
                            studentNoMap, coursesRecordMap, ratingDTO, oldScoreMap);
                    batchExcelLine.clear();
                }
            }
            if (!batchExcelLine.isEmpty()) {
                correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, activityId,
                        studentNoMap, coursesRecordMap, ratingDTO, oldScoreMap);
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

    public int processBatchExcelLine(List<ImportRecordSaveDTO> importErrorDTOS, List<LeisureActivitiesScoreImportModel> list,
                                     Long schoolId, Long activityId, Map<String, StudentEntity> studentNoMap,
                                     Map<String, LeisureActivityCoursesRecordEntity> coursesRecordMap, LeisureActivitiesRatingDTO ratingDTO,
                                     Map<String, LeisureActivitiesScoreEntity> oldScoreMap) {
        if (CollectionUtils.isNotEmpty(list)) {
            int correctCount = list.size();//正确处理的条数
            List<LeisureActivitiesScoreEntity> insertOrSaveList = new ArrayList<>();
            //遍历要插入的每一行
            for (LeisureActivitiesScoreImportModel bo : list) {
                List<String> studentErrorList = new ArrayList<>();
                if (!check(bo, studentErrorList, studentNoMap, coursesRecordMap, ratingDTO)) {
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
                LeisureActivitiesScoreEntity entity = importConvert(schoolId, activityId, bo, studentNoMap, coursesRecordMap, ratingDTO, oldScoreMap);
                insertOrSaveList.add(entity);
            }
            if (CollectionUtils.isNotEmpty(insertOrSaveList)) {
                log.info("导入数据余暇活动成绩信息开始");
                this.saveOrUpdateBatch(insertOrSaveList);
            }
            return correctCount;
        }
        return 0;
    }

    private LeisureActivitiesScoreEntity importConvert(Long schoolId, Long activityId, LeisureActivitiesScoreImportModel bo, Map<String, StudentEntity> studentNoMap,
                                                       Map<String, LeisureActivityCoursesRecordEntity> coursesRecordMap, LeisureActivitiesRatingDTO ratingDTO,
                                                       Map<String, LeisureActivitiesScoreEntity> oldScoreMap) {
        LeisureActivitiesScoreEntity entity = new LeisureActivitiesScoreEntity();
        entity.setSchoolId(schoolId);
        StudentEntity studentEntity = studentNoMap.get(bo.getStudentNo());
        if (studentEntity != null) {
            entity.setStudentId(studentEntity.getId());
        }
        entity.setActivityId(activityId);
        int attendCount = Integer.parseInt(bo.getAttendCount());
        entity.setAttendCount(attendCount);
        LeisureActivityCoursesRecordEntity coursesRecord = coursesRecordMap.get(bo.getCourseName());
        if (coursesRecord != null) {
            entity.setCourseId(coursesRecord.getId());
            // 计算出席率分数
            if (coursesRecord.getCoursesNum() != null && coursesRecord.getCoursesNum() > 0) {
                //出席率分数=出席次数/课程次数*出勤率占比*100（如遇小数，则四舍五入）
                BigDecimal attendanceScore = new BigDecimal(attendCount)
                        .divide(new BigDecimal(coursesRecord.getCoursesNum()), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(ratingDTO.getAttendanceRatio()))
                        .setScale(0, RoundingMode.HALF_UP);
                entity.setAttendScore(attendanceScore.intValue() * 100);
            }
        }
        int lessonScore = Integer.parseInt(bo.getLessonScore());
        entity.setLessonScore(lessonScore * 100);
        //总分数=出席率分数+课节表现分数(总分数仅保留小数点后两位，超出四舍五入)
        entity.setTotalScore(entity.getLessonScore() + entity.getAttendScore());
        String level = calculateScoreLevel(entity.getTotalScore(), ratingDTO.getScoreRange());
        entity.setReferenceLevel(level);
        entity.setFinalLevel(level);
        if (oldScoreMap.get(entity.getCourseId() + "_" + entity.getStudentId()) != null) {
            //存在历史成绩
            entity.setId(oldScoreMap.get(entity.getCourseId() + "_" + entity.getStudentId()).getId());
        }
        return entity;
    }

    private String calculateScoreLevel(int score, List<LeisureActivitiesRatingRangeDTO> ratingRanges) {
        for (LeisureActivitiesRatingRangeDTO range : ratingRanges) {
            if (score >= range.getMinValue() && score <= range.getMaxValue()) {
                return range.getLevel();
            }
        }
        return null;
    }

    private boolean check(LeisureActivitiesScoreImportModel bo, List<String> errorList, Map<String, StudentEntity> studentNoMap,
                          Map<String, LeisureActivityCoursesRecordEntity> coursesRecordMap, LeisureActivitiesRatingDTO ratingDTO) {
        //一项一项检查
        //课程检查
        if (!StringUtils.isNotBlank(bo.getCourseName())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.COURSE_NAME_REQUIRED));
        } else {
            if (coursesRecordMap.get(bo.getCourseName()) == null) {
                errorList.add(languageUtil.getMessage(LanguageConstants.COURSE_NAME_NOT_FOUND));
            }
        }
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
        //出席次数检查
        if (!StringUtils.isNotBlank(bo.getAttendCount())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.ATTEND_COUNT_REQUIRED));
        } else {
            try {
                int count = Integer.parseInt(bo.getAttendCount());
                if (count <= 0) {
                    errorList.add(languageUtil.getMessage(LanguageConstants.ATTEND_COUNT_MUST_POSITIVE_INTEGER));
                } else {
                    // 新增检查：出席次数不可超过课程总次数
                    LeisureActivityCoursesRecordEntity coursesRecord = coursesRecordMap.get(bo.getCourseName());
                    if (coursesRecord != null && coursesRecord.getCoursesNum() != null &&
                            count > coursesRecord.getCoursesNum()) {
                        errorList.add(languageUtil.getMessage(LanguageConstants.ATTEND_COUNT_OUT_OF_COURSE_RANGE));
                    }
                }
            } catch (NumberFormatException e) {
                errorList.add(languageUtil.getMessage(LanguageConstants.ATTEND_COUNT_MUST_POSITIVE_INTEGER));
            }
        }
        // 课节表现分数检查
        if (!StringUtils.isNotBlank(bo.getLessonScore())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.LESSON_SCORE_REQUIRED));
        } else {
            try {
                int score = Integer.parseInt(bo.getLessonScore());
                if (score < 0) {
                    errorList.add(languageUtil.getMessage(LanguageConstants.LESSON_SCORE_MUST_NON_NEGATIVE_INTEGER));
                } else if (ratingDTO != null && score > ratingDTO.getClassParticipationRatio()) {
                    errorList.add(languageUtil.getMessage(LanguageConstants.LESSON_SCORE_EXCEED_CLASS_PARTICIPATION_RATIO));
                }
            } catch (NumberFormatException e) {
                errorList.add(languageUtil.getMessage(LanguageConstants.LESSON_SCORE_MUST_NON_NEGATIVE_INTEGER));
            }
        }

        return !CollectionUtils.isNotEmpty(errorList);
    }

    private List<LeisureActivitiesScoreImportModel> readExcelData(MultipartFile file, SchoolLanguageEnum schoolLanguageEnum) {
        List<LeisureActivitiesScoreImportModel> result = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            switch (schoolLanguageEnum) {
                case ZH_MO:
                    LeisureActivitiesScoreImportZhTwListener importZhTwListener = new LeisureActivitiesScoreImportZhTwListener();
                    EasyExcel.read(inputStream, LeisureActivitiesScoreImportZhTwModel.class, importZhTwListener).sheet().headRowNumber(1).doReadSync();
                    List<LeisureActivitiesScoreImportZhTwModel> importZhTwModels = importZhTwListener.getDataList();
                    result = importZhTwModels.stream().map(item -> {
                        LeisureActivitiesScoreImportModel model = new LeisureActivitiesScoreImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case EN_US:
                    LeisureActivitiesScoreImportEnUsListener importEnUsListener = new LeisureActivitiesScoreImportEnUsListener();
                    EasyExcel.read(inputStream, LeisureActivitiesScoreImportEnUsModel.class, importEnUsListener).sheet().headRowNumber(1).doReadSync();
                    List<LeisureActivitiesScoreImportEnUsModel> importEnUsModels = importEnUsListener.getDataList();
                    result = importEnUsModels.stream().map(item -> {
                        LeisureActivitiesScoreImportModel model = new LeisureActivitiesScoreImportModel();
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
    public String export(Long schoolId, Long userId, LeisureActivitiesScorePageReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(userId, schoolId);
        List<Long> classIds = new ArrayList<>();
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(userId, schoolId);
            if (CollectionUtils.isEmpty(classIds)) {
                return null;
            }
        }
        if (CollectionUtils.isNotEmpty(classIds) && reqModel.getClassId() != null && reqModel.getClassId() > 0) {
            //普通用户班级权限不为空，请求选择了某个班级
            if (!classIds.contains(reqModel.getClassId())) {
                return null;
            }
        }
        List<LeisureActivitiesScorePageResModel> list = this.getBaseMapper().page(classIds, reqModel);
        if (CollectionUtils.isNotEmpty(list)) {
            //查询学生成绩信息
            List<Long> studentIds = list.stream().map(LeisureActivitiesScorePageResModel::getStudentId).distinct().collect(Collectors.toList());
            QueryWrapper<LeisureActivitiesScoreEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(LeisureActivitiesScoreEntity::getActivityId, reqModel.getActivityId())
                    .in(LeisureActivitiesScoreEntity::getStudentId, studentIds);
            List<LeisureActivitiesScoreEntity> scoreEntities = this.list(wrapper);
            Map<String, LeisureActivitiesScoreEntity> scoreMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(scoreEntities)) {
                scoreMap = scoreEntities.stream().collect(Collectors.toMap(score -> score.getCourseId() + "_" + score.getStudentId(), score -> score));
            }
            for (LeisureActivitiesScorePageResModel resModel : list) {
                LeisureActivitiesScoreEntity scoreEntity = scoreMap.get(resModel.getCourseId() + "_" + resModel.getStudentId());
                if (scoreEntity != null) {
                    BeanUtils.copyProperties(scoreEntity, resModel);
                }
            }
            String fileName = "余暇活动成绩数据.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();
            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                List<LeisureActivitiesScoreExportEnModel> exportEnModels = list.stream()
                        .map(resModel -> {
                            LeisureActivitiesScoreExportEnModel exportModel = new LeisureActivitiesScoreExportEnModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setSeatNo(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                            exportModel.setClassName(resModel.getGroupName() + resModel.getClassName());
                            exportModel.setAttendCount(resModel.getAttendCount() != null ? String.valueOf(resModel.getAttendCount()) : "");
                            exportModel.setAttendScore(resModel.getAttendScore() != null ? String.valueOf(resModel.getAttendScore() / 100) : "");
                            exportModel.setLessonScore(resModel.getLessonScore() != null ? String.valueOf(resModel.getAttendScore() / 100) : "");
                            exportModel.setTotalScore(resModel.getTotalScore() != null ? String.valueOf(resModel.getAttendScore() / 100) : "");
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, LeisureActivitiesScoreExportEnModel.class, FileTypeEnum.EXPORT, schoolId);
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                List<LeisureActivitiesScoreExportPtModel> exportPtModels = list.stream()
                        .map(resModel -> {
                            LeisureActivitiesScoreExportPtModel exportModel = new LeisureActivitiesScoreExportPtModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setSeatNo(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                            exportModel.setClassName(resModel.getGroupName() + resModel.getClassName());
                            exportModel.setAttendCount(resModel.getAttendCount() != null ? String.valueOf(resModel.getAttendCount()) : "");
                            exportModel.setAttendScore(resModel.getAttendScore() != null ? String.valueOf(resModel.getAttendScore() / 100) : "");
                            exportModel.setLessonScore(resModel.getLessonScore() != null ? String.valueOf(resModel.getAttendScore() / 100) : "");
                            exportModel.setTotalScore(resModel.getTotalScore() != null ? String.valueOf(resModel.getAttendScore() / 100) : "");
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, LeisureActivitiesScoreExportPtModel.class, FileTypeEnum.EXPORT, schoolId);
            } else {
                List<LeisureActivitiesScoreExportModel> exportPtModels = list.stream()
                        .map(resModel -> {
                            LeisureActivitiesScoreExportModel exportModel = new LeisureActivitiesScoreExportModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setSeatNo(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                            exportModel.setClassName(resModel.getGroupName() + resModel.getClassName());
                            exportModel.setAttendCount(resModel.getAttendCount() != null ? String.valueOf(resModel.getAttendCount()) : "");
                            exportModel.setAttendScore(resModel.getAttendScore() != null ? String.valueOf(resModel.getAttendScore() / 100) : "");
                            exportModel.setLessonScore(resModel.getLessonScore() != null ? String.valueOf(resModel.getAttendScore() / 100) : "");
                            exportModel.setTotalScore(resModel.getTotalScore() != null ? String.valueOf(resModel.getAttendScore() / 100) : "");
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, LeisureActivitiesScoreExportModel.class, FileTypeEnum.EXPORT, schoolId);
            }
        }
        return null;
    }
}
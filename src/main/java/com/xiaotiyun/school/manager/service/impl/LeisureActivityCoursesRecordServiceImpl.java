package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ImportTaskStatusEnum;
import com.xiaotiyun.school.manager.basic.enums.ImportTaskTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.enums.WeekdayEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.LeisureActivityCoursesRecordDao;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.LeisureActivityCourseImportModel;
import com.xiaotiyun.school.manager.model.req.LeisureActivityCoursesQueryRecordReq;
import com.xiaotiyun.school.manager.model.res.LeisureActivityCoursesRecordRes;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeisureActivityCoursesRecordServiceImpl extends ServiceImpl<LeisureActivityCoursesRecordDao, LeisureActivityCoursesRecordEntity> implements LeisureActivityCoursesRecordService {

    private final UserSchoolRelService userSchoolRelService;
    private final ClassroomService classroomService;

    private final ImportTaskService importTaskService;
    private final ImportRecordService importRecordService;

    private final LanguageUtil languageUtil;

    private static final ExecutorService LeisureActivityCoursesImportPool = new ThreadPoolExecutor(2, 8, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100));

    @Override
    public PageInfo<LeisureActivityCoursesRecordRes> pageAndPre(LeisureActivityCoursesQueryRecordReq reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<LeisureActivityCoursesRecordRes> list = this.getBaseMapper().pageAndPre(reqModel);

        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<LeisureActivityCoursesRecordRes> pageAndApply(LeisureActivityCoursesQueryRecordReq reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<LeisureActivityCoursesRecordRes> list = this.getBaseMapper().pageAndApply(reqModel);

        return new PageInfo<>(getLeisureActivityCoursesRecordRes(list));
    }

    @Override
    public List<LeisureActivityCoursesRecordRes> listAndApply(LeisureActivityCoursesQueryRecordReq reqModel) {
        List<LeisureActivityCoursesRecordRes> resList = this.getBaseMapper().pageAndApply(reqModel);
        return getLeisureActivityCoursesRecordRes(resList);
    }

    @Override
    public List<LeisureActivityCoursesRecordRes> pageAndApplyByStudent(LeisureActivityCoursesQueryRecordReq reqModel) {
        List<LeisureActivityCoursesRecordRes> resList = this.getBaseMapper().pageAndApplyByStudent(reqModel);
        if (ObjectUtils.isNotEmpty(resList)) {
            resList = resList.stream().filter(a->a.getEnrollStatus()==0).collect(Collectors.toList());
        }
        return getLeisureActivityCoursesRecordRes(resList);
    }

    @Override
    public Long importCourses(MultipartFile file, Long activityId, Long schoolId) {
        ImportTaskEntity taskEntity = new ImportTaskEntity();
        taskEntity.setSchoolId(schoolId);
        taskEntity.setFileName(file.getOriginalFilename());
        taskEntity.setType(ImportTaskTypeEnum.ACTIVITY_COURSES.getCode());
        taskEntity.setTotalCount(0);
        taskEntity.setSuccessCount(0);
        taskEntity.setFailCount(0);
        taskEntity.setStatus(ImportTaskStatusEnum.UNTREATED.getCode());
        // 异步发起任务
        // 获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        // 验证数据格式，转换数据格式，使用 EasyExcel 读取 Excel 文件并转换为 LeisureActivityCourseImportModel 列表
        List<LeisureActivityCourseImportModel> courseList = null;
        try{
            courseList = EasyExcel.read(file.getInputStream())
                    .head(LeisureActivityCourseImportModel.class)
                    .sheet()
                    .headRowNumber(2)
                    .doReadSync();
        } catch (IOException e){
            log.error("导入余暇活动课程错误！文件读取失败", e);
            taskEntity.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
        } catch (Exception e) {
            log.error("导入余暇活动课程，系统错误！", e);
            taskEntity.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
        }
        importTaskService.save(taskEntity);
        Long taskId = taskEntity.getId();
        // 发起异步任务
        List<LeisureActivityCourseImportModel> tmpList = courseList;
        CompletableFuture.runAsync(() -> {
                    languageUtil.setLanguage(languageEnum.getCode());
                    importCoursesRecord(tmpList, activityId, taskId, schoolId);
                }, LeisureActivityCoursesImportPool)
                .exceptionally(ex -> {
                    // 打印异常信息
                    log.error("异步导入余暇活动课程任务发生异常", ex);
                    taskEntity.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                    importTaskService.save(taskEntity);
                    return null;
                })
                .thenRun(LanguageUtil::clearLanguage);
        // 返回导入任务id
        return taskId;
    }

    private void importCoursesRecord(List<LeisureActivityCourseImportModel> courseList, Long activityId, Long taskId, Long schoolId) {
        // 获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        ImportTaskEntity taskEntity = new ImportTaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
        // 保存任务
        List<LeisureActivityCoursesRecordEntity> sucData = new ArrayList<>();
        List<ImportRecordEntity> failData = new ArrayList<>();
        try {
            if (ObjectUtils.isEmpty(courseList)) {
                log.error("余暇活动课程导入失败！导入数据为空");
                taskEntity.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                importTaskService.updateById(taskEntity);
                return;
            }
            List<String> teacherNumbers = courseList.stream().map(a -> {
                if (ObjectUtils.isNotEmpty(a.getTeacherName())) {
                    // 教师名称格式为 中文名称(userNumber)
                    return getTeacherUserNumber(a.getTeacherName());
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            // 查询教师信息
            if (ObjectUtils.isEmpty(teacherNumbers)) {
                log.error("余暇活动课程导入失败！教师名称有误或为空");
                taskEntity.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                importTaskService.updateById(taskEntity);
                return;
            }
            List<UserSchoolRelEntity> oldTeachers = userSchoolRelService.list(Wrappers.<UserSchoolRelEntity>lambdaQuery()
                    .in(UserSchoolRelEntity::getUserNumber, teacherNumbers));
            if (ObjectUtils.isEmpty(oldTeachers)) {
                log.error("余暇活动课程导入失败！所有老师信息都不存在！");
                taskEntity.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                importTaskService.updateById(taskEntity);
                return;
            }
            List<String> oldTeacherNames = oldTeachers.stream().map(a -> a.getUsername() + "(" + a.getUserNumber() + ")").collect(Collectors.toList());
            Map<String, UserSchoolRelEntity> oldTeacherNamesMap = oldTeachers.stream().collect(Collectors.toMap(a -> a.getUsername() + "(" + a.getUserNumber() + ")", Function.identity()));
            // 查询教室信息
            List<String> classRoomNames = courseList.stream().map(LeisureActivityCourseImportModel::getClassRoomName).collect(Collectors.toList());
            List<ClassroomEntity> classRoomList = classroomService.list(Wrappers.<ClassroomEntity>lambdaQuery()
                    .in(ClassroomEntity::getName, classRoomNames));
            Map<String, ClassroomEntity> oldClassRoomNamesMap = new HashMap<>();
            if (ObjectUtils.isNotEmpty(classRoomList)) {
                oldClassRoomNamesMap = classRoomList.stream().collect(Collectors.toMap(ClassroomEntity::getName, Function.identity()));
            }
            // 校验名称是否重复
            List<LeisureActivityCoursesRecordEntity> oldCourses = this.list(Wrappers.<LeisureActivityCoursesRecordEntity>lambdaQuery()
                   .eq(LeisureActivityCoursesRecordEntity::getActivityId, activityId));
            List<String> oldCoursesNames = new ArrayList<>();
            if (ObjectUtils.isNotEmpty(oldCourses)) {
                oldCoursesNames = oldCourses.stream().map(LeisureActivityCoursesRecordEntity::getName).collect(Collectors.toList());
            }
            List<String> nowCoursesNames = new ArrayList<>();
            // 处理读取到的数据
            int rowNum = 3;
            for (LeisureActivityCourseImportModel course : courseList) {
                // 定义错误详情类
                ImportRecordEntity entity = new ImportRecordEntity();
                entity.setTaskId(taskId);
                entity.setIncorrectLineno(rowNum + "");
                rowNum++;
                // 判断课程名称是否重复
                if (nowCoursesNames.contains(course.getCourseName()) || oldCoursesNames.contains(course.getCourseName())) {
                    entity.setIncorrectReason(languageUtil.getMessage(LanguageConstants.COURSE_NAME_DUPLICATED_IN_ACTIVITY));
                    failData.add(entity);
                    continue;
                }
                // 判断基础信息错误
                String incorrectReason = checkCoursesImportInfo(course);
                if (incorrectReason != null) {
                    entity.setIncorrectReason(incorrectReason);
                    failData.add(entity);
                    continue;
                }
                // 在这里可以对每个课程进行处理，例如保存到数据库
                if (oldTeacherNames.contains(course.getTeacherName())) {
                    LeisureActivityCoursesRecordEntity record = new LeisureActivityCoursesRecordEntity();
                    record.setActivityId(activityId);
                    record.setSchoolId(schoolId);
                    record.setName(course.getCourseName());
                    record.setTeacher(course.getTeacherName().split("\\(")[0]);
                    record.setTeacherId(oldTeacherNamesMap.get(course.getTeacherName()).getId());
                    record.setClassroom(course.getClassRoomName());
                    record.setClassroomId(oldClassRoomNamesMap.containsKey(course.getClassRoomName()) ?
                            oldClassRoomNamesMap.get(course.getClassRoomName()).getId() : -1L);
                    record.setQuotaTotal(Integer.valueOf(course.getQuotaTotal()));
                    record.setCoursesNum(Integer.valueOf(course.getCoursesNum()));
                    record.setStatus(0);
                    Map<String, Object> courseTimeMap = new HashMap<>();
                    courseTimeMap.put("startTime", course.getCourseTimeStart());
                    courseTimeMap.put("endTime", course.getCourseTimeEnd());
                    if (languageEnum.equals(SchoolLanguageEnum.EN_US) || languageEnum.equals(SchoolLanguageEnum.PT_PT))
                        courseTimeMap.put("week", WeekdayEnum.getByEnglishName(course.getCourseTimeWeeks()).getNumber() + "");
                    if (languageEnum.equals(SchoolLanguageEnum.ZH_MO))
                        courseTimeMap.put("week", WeekdayEnum.getByChineseName(course.getCourseTimeWeeks()).getNumber() + "");
                    record.setCourseTime(JSON.toJSONString(Collections.singletonList(courseTimeMap)));
                    sucData.add(record);
                    nowCoursesNames.add(course.getCourseName());
                } else {
                    if (!oldTeacherNames.contains(course.getTeacherName())) {
                        entity.setIncorrectReason(languageUtil.getMessage(LanguageConstants.TEACHER_NOT_FOUND));
                    }
                    failData.add(entity);
                }
            }
        } catch (Exception e) {
            log.error("导入余暇活动课程,异步任务错误！", e);
            taskEntity.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.updateById(taskEntity);
            return;
        }
        // 保存导入任务信息
        taskEntity.setTotalCount(sucData.size() + failData.size());
        taskEntity.setSuccessCount(sucData.size());
        taskEntity.setFailCount(failData.size());
        taskEntity.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
        importTaskService.updateById(taskEntity);
        // 保存导入任务详情
        if (ObjectUtils.isNotEmpty(failData)) {
            importRecordService.saveBatch(failData);
        }
        // 保存业务数据
        this.saveBatch(sucData);
    }

    private String checkCoursesImportInfo(LeisureActivityCourseImportModel course) {
        if (StringUtils.isEmpty(course.getCourseName())) {
            return languageUtil.getMessage(LanguageConstants.COURSE_NAME_REQUIRED);
        }
        if (StringUtils.isEmpty(course.getTeacherName())) {
            return languageUtil.getMessage(LanguageConstants.TEACHER_REQUIRED);
        }
        if (StringUtils.isEmpty(course.getClassRoomName())) {
            return languageUtil.getMessage(LanguageConstants.CLASSROOM_REQUIRED);
        }
        if (course.getQuotaTotal() == null) {
            return languageUtil.getMessage(LanguageConstants.COURSE_QUOTA_REQUIRED);
        }
        int quotaTotal;
        try {
            quotaTotal = Integer.parseInt(course.getQuotaTotal());
        } catch (Exception e) {
            return languageUtil.getMessage(LanguageConstants.COURSE_QUOTA_MUST_NUMBER);
        }
        if (quotaTotal <= 0) {
            return languageUtil.getMessage(LanguageConstants.COURSE_QUOTA_MUST_POSITIVE_INTEGER);
        }
        if (course.getCoursesNum() == null) {
            return languageUtil.getMessage(LanguageConstants.COURSE_TIMES_NOT_EMPTY);
        }
        int coursesNum;
        try {
            coursesNum = Integer.parseInt(course.getCoursesNum());
        } catch (Exception e) {
            return languageUtil.getMessage(LanguageConstants.COURSE_TIMES_MUST_NUMBER);
        }
        if (coursesNum <= 0) {
            return languageUtil.getMessage(LanguageConstants.COURSE_TIMES_MUST_POSITIVE_INTEGER);
        }
        if (StringUtils.isEmpty(course.getCourseTimeWeeks())) {
            return languageUtil.getMessage(LanguageConstants.COURSE_TIME_NOT_EMPTY);
        }
        if (StringUtils.isEmpty(course.getCourseTimeEnd())) {
            return languageUtil.getMessage(LanguageConstants.COURSE_START_TIME_NOT_EMPTY);
        }
        if (StringUtils.isEmpty(course.getCourseTimeEnd())) {
            return languageUtil.getMessage(LanguageConstants.COURSE_END_TIME_NOT_EMPTY);
        }
        return null;
    }

    private String getTeacherUserNumber(String teacherName) {
        String[] split = teacherName.split("\\(");
        if (split.length == 2) {
            return split[1].replace(")", "");
        }
        return null;
    }

    private List<LeisureActivityCoursesRecordRes> getLeisureActivityCoursesRecordRes(List<LeisureActivityCoursesRecordRes> resList) {
        if (ObjectUtils.isNotEmpty(resList)) {
            resList.forEach(a->{
                if (a.getEnrollNum() == null) {
                    a.setEnrollNum(0);
                }
                if (a.getEnrollQuota() == null) {
                    a.setEnrollQuota(a.getQuotaTotal());
                }
            });
            return resList;
        }
        return resList == null ? new ArrayList<>():resList;
    }
}

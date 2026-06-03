package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.AdmissionInfoEnum;
import com.xiaotiyun.school.manager.basic.enums.ApplyStageEnum;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.VolunteerNumberUtil;
import com.xiaotiyun.school.manager.dao.ActivityStudentApplyReportDao;
import com.xiaotiyun.school.manager.dao.ActivityStudentReportDao;
import com.xiaotiyun.school.manager.dao.LeisureActivityRecordDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.dto.ActivityStudentApplyReportQueryDTO;
import com.xiaotiyun.school.manager.model.dto.LeisureActivitiesNoticeSendDTO;
import com.xiaotiyun.school.manager.model.dto.LeisureActivitiesNoticeSendStudentDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 活动报名表服务实现类
 */
@Slf4j
@Service
public class ActivityStudentApplyReportServiceImpl extends ServiceImpl<ActivityStudentApplyReportDao, ActivityStudentApplyReportEntity> implements ActivityStudentApplyReportService {

    @Resource
    private ActivityStudentReportDao activityStudentReportDao;

    @Resource
    private LeisureActivityRecordDao leisureActivityRecordDao;

    @Resource
    private ActivityStudentReportService activityStudentReportService;

    @Resource
    private ActivityVolunteerLensonService activityVolunteerLensonService;

    @Resource
    private SemesterService semesterService;

    @Resource
    private LanguageUtil languageUtil;

    @Resource
    private LeisureActivityCoursesRecordService leisureActivityCoursesRecordService;

    @Resource
    private LeisureActivitiesNoticeService leisureActivitiesNoticeService;

    @Resource
    private UserAuthHelper userAuthHelper;
    @Resource
    private ExportFileHandler exportFileHandler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean publishActivityResult(ActivityStudentApplyReportPublishReqModel reqModel) {
        Long activityId = reqModel.getActivityId();

        try {
            LeisureActivityRecordEntity leisureActivityRecordEntity = leisureActivityRecordDao.selectById(activityId);
            if (leisureActivityRecordEntity == null) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_EXISTS));
            }

            if (leisureActivityRecordEntity.getPublishStatus() == 2) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_HAS_PUBLISH));
            }

            // 校验活动是否已经截止
            Date currentTime = new Date();
            Date endTime = leisureActivityRecordEntity.getEndTime();

            // 如果活动已经公布了第一次，则检查二次报名结束时间
            if (leisureActivityRecordEntity.getPublishStatus() != null && leisureActivityRecordEntity.getPublishStatus() == 1) {
                Date secondEndTime = leisureActivityRecordEntity.getSecondEndTime();
                if (secondEndTime != null && currentTime.before(secondEndTime)) {
                    throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_ENDED_CANNOT_PUBLISH));
                }
                if (secondEndTime == null) {
                    throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_HAS_PUBLISH));
                }
            } else {
                // 第一次公布，检查活动结束时间
                if (endTime != null && currentTime.before(endTime)) {
                    throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_ENDED_CANNOT_PUBLISH));
                }
            }

            int status = 1;
            if (leisureActivityRecordEntity.getPublishStatus() != null && leisureActivityRecordEntity.getPublishStatus() == 1) {
                status = 2;
//                Integer noCourseStudentCount = activityStudentReportService.getNoCourseStudentCount(activityId);
//                if(noCourseStudentCount > 0)
//                {
//                    log.error("二次公布失败，未匹配人数大于0");
//                    return false;
//                }
            }
            // 1. 将activity_student_report表中该活动下的所有匹配记录改为公布状态
            LambdaQueryWrapper<ActivityStudentReportEntity> query = new LambdaQueryWrapper<>();
            query.eq(ActivityStudentReportEntity::getActivityId, activityId)
                    .eq(ActivityStudentReportEntity::getStatus, 1)
                    .eq(ActivityStudentReportEntity::getDeleted, 0);
            List<ActivityStudentReportEntity> entities = activityStudentReportDao.selectList(query);
            //entities tomap
            Map<Long, ActivityStudentReportEntity> entityMap = entities.stream()
                    .collect(Collectors.toMap(ActivityStudentReportEntity::getStudentId, entity -> entity));

            LambdaUpdateWrapper<ActivityStudentReportEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ActivityStudentReportEntity::getActivityId, activityId)
                    .eq(ActivityStudentReportEntity::getStatus, 1) // 只更新状态为1（匹配）的记录
                    .set(ActivityStudentReportEntity::getStatus, 2); // 设置为2（发布）

            int updateCount = activityStudentReportDao.update(null, updateWrapper);
            log.info("活动ID: {}, 更新了 {} 条匹配记录为公布状态", activityId, updateCount);

            // 2. 将leisure_activity_record表中对应活动的公布状态改为已公布
            LambdaUpdateWrapper<LeisureActivityRecordEntity> activityUpdateWrapper = new LambdaUpdateWrapper<>();
            activityUpdateWrapper.eq(LeisureActivityRecordEntity::getId, activityId)
                    .set(LeisureActivityRecordEntity::getPublishStatus, status);

            int activityUpdateCount = leisureActivityRecordDao.update(null, activityUpdateWrapper);
            log.info("活动ID: {}, 更新了 {} 条活动记录的公布状态为已公布", activityId, activityUpdateCount);


            //通知发布学生信息
            LeisureActivitiesNoticeSendDTO sendDTO = new LeisureActivitiesNoticeSendDTO();
            sendDTO.setActivityId(activityId);
            sendDTO.setSchoolId(leisureActivityRecordEntity.getSchoolId());
            sendDTO.setPeriodId(leisureActivityRecordEntity.getSemesterId());
            LambdaQueryWrapper<ActivityStudentApplyReportEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ActivityStudentApplyReportEntity::getActivityId, activityId)
                    .eq(ActivityStudentApplyReportEntity::getType, status)
                    .eq(ActivityStudentApplyReportEntity::getDeleted, 0);
            List<ActivityStudentApplyReportEntity> applyReportEntities = this.list(wrapper);
            if (!CollectionUtils.isEmpty(applyReportEntities)) {
                List<LeisureActivitiesNoticeSendStudentDTO> publishResults = applyReportEntities.stream().map(item -> {
                    ActivityStudentReportEntity studentReportEntity = entityMap.get(item.getStudentId());
                    LeisureActivitiesNoticeSendStudentDTO studentDTO = new LeisureActivitiesNoticeSendStudentDTO();
                    studentDTO.setStudentId(item.getStudentId());
                    if (studentReportEntity != null) {
                        studentDTO.setCourseId(studentReportEntity.getLensonId());
                    }
                    return studentDTO;
                }).collect(Collectors.toList());
                sendDTO.setPublishResults(publishResults);
                leisureActivitiesNoticeService.sendNotice(sendDTO);
            }
            return true;
        } catch (BusinessMessageException e) {
            log.error("一键公布报名结果失败，活动ID: {}, 错误信息: {}", activityId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("一键公布报名结果失败，活动ID: {}", activityId, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startSecondApply(ActivityStudentApplyReportSecondApplyReqModel reqModel) {
        Long activityId = reqModel.getActivityId();
        Date endTime = reqModel.getEndTime();

        try {
            // 验证活动是否存在
            LeisureActivityRecordEntity leisureActivityRecordEntity = leisureActivityRecordDao.selectById(activityId);
            if (leisureActivityRecordEntity == null) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_EXISTS));
            }

            // 验证活动是否已经公布
            if (leisureActivityRecordEntity.getPublishStatus() == null || leisureActivityRecordEntity.getPublishStatus() != 1) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_PUBLISHED_CANNOT_SECOND_APPLY));
            }

            // 验证活动是否已经结束
            Date currentTime = new Date();
            Date activityEndTime = leisureActivityRecordEntity.getEndTime();
            if (activityEndTime != null && currentTime.before(activityEndTime)) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_ENDED_CANNOT_START_SECOND_APPLY));
            }

            // 验证二次报名结束时间是否合理
            if (endTime.before(new Date())) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SECOND_APPLY_END_TIME_BEFORE_NOW));
            }

            // 更新leisure_activity_record表的second_end_time字段
            LambdaUpdateWrapper<LeisureActivityRecordEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(LeisureActivityRecordEntity::getId, activityId)
                    .set(LeisureActivityRecordEntity::getSecondEndTime, endTime);

            int updateCount = leisureActivityRecordDao.update(null, updateWrapper);
            log.info("活动ID: {}, 发起二次报名，设置结束时间为: {}, 更新了 {} 条记录",
                    activityId, endTime, updateCount);

            return updateCount > 0;
        } catch (BusinessMessageException e) {
            log.error("发起二次报名失败，活动ID: {}, 错误信息: {}", activityId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("发起二次报名失败，活动ID: {}", activityId, e);
            throw e;
        }
    }

    @Override
    public PageInfo<ActivityStudentApplyReportListResModel> getActivityStudentList(Long schoolId, Long userId, ActivityStudentApplyReportListReqModel reqModel) {
        if (reqModel == null || reqModel.getActivityId() == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_ID_REQUIRED));
        }

        // 获取活动信息
        LeisureActivityRecordEntity activity = leisureActivityRecordDao.selectById(reqModel.getActivityId());
        if (activity == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_EXISTS));
        }

        // 获取学段信息
        SemesterEntity semester = semesterService.getById(activity.getSemesterId());
        if (semester == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SEMESTER_NOT_EXISTS));
        }
        boolean commonUser = userAuthHelper.getCommonUser(userId, schoolId);
        List<Long> classIds = new ArrayList<>();
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(userId, schoolId);
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(classIds)) {
                return null;
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(classIds) && reqModel.getClassId() != null && reqModel.getClassId() > 0) {
            //普通用户班级权限不为空，请求选择了某个班级
            if (!classIds.contains(reqModel.getClassId())) {
                return null;
            }
        }
        // 构建查询参数
        ActivityStudentReportQueryReqModel queryReqModel = new ActivityStudentReportQueryReqModel();
        queryReqModel.setSchoolId(activity.getSchoolId());
        queryReqModel.setSchoolYear(semester.getSchoolYear());
        queryReqModel.setDepartment(activity.getDepartment());
        queryReqModel.setClassId(reqModel.getClassId());
        queryReqModel.setClassIds(classIds);
        queryReqModel.setStudentName(reqModel.getStudentName());
        queryReqModel.setStudentNo(reqModel.getStudentNo());
        queryReqModel.setPageNum(reqModel.getPageNum());
        queryReqModel.setPageSize(reqModel.getPageSize());
        queryReqModel.setActivityId(reqModel.getActivityId());

        // 调用已实现的getStudentCourseList方法获取学生列表
        PageInfo<ActivityStudentReportQueryResModel> studentPageInfo = activityStudentReportService.getStudentCourseList(queryReqModel);

        // 组装返回数据
        PageInfo<ActivityStudentApplyReportListResModel> result = new PageInfo<>();
        result.setPageNum(studentPageInfo.getPageNum());
        result.setPageSize(studentPageInfo.getPageSize());
        result.setTotal(studentPageInfo.getTotal());
        result.setPages(studentPageInfo.getPages());

        List<ActivityStudentApplyReportListResModel> resultList = new ArrayList<>();
        if (studentPageInfo.getList() != null && !studentPageInfo.getList().isEmpty()) {
            // 收集所有学生ID
            List<Long> studentIds = studentPageInfo.getList().stream()
                    .map(ActivityStudentReportQueryResModel::getStudentId)
                    .collect(Collectors.toList());

            // 批量查询所有学生的志愿课程列表
            Map<Long, List<ActivityStudentApplyReportVolunteerResModel>> volunteerMap =
                    activityVolunteerLensonService.getVolunteerListByActivityAndStudents(reqModel.getActivityId(), studentIds);

            // 组装返回数据
            for (ActivityStudentReportQueryResModel student : studentPageInfo.getList()) {
                ActivityStudentApplyReportListResModel resModel = new ActivityStudentApplyReportListResModel();
                resModel.setStudentId(student.getStudentId());
                resModel.setStudentName(student.getStudentName());
                resModel.setClassName(student.getClassName());
                resModel.setGradeGroupName(student.getGradeGroupName());
                resModel.setStudentNo(student.getStudentNo());

                // 从批量查询结果中获取该学生的志愿课程列表 因为二次报名开始之后点击不了，所以这里查出来的只能是一次报名的结果。
                List<ActivityStudentApplyReportVolunteerResModel> volunteerList =
                        volunteerMap.getOrDefault(student.getStudentId(), new ArrayList<>());
                resModel.setVolunteerList(volunteerList);

                resultList.add(resModel);
            }
        }
        result.setList(resultList);

        return result;
    }

    @Override
    public PageInfo<ActivityStudentApplyReportQueryDTO> querySecondApplyAndUnmatchedStudents(
            Long schoolId, Integer department, String schoolYear, Long activityId, ActivityStudentApplyReportSecondListReqModel reqModel) {
        // 使用PageHelper进行分页
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ActivityStudentApplyReportQueryDTO> list = this.baseMapper.querySecondApplyAndUnmatchedStudents(
                schoolId, department, schoolYear, reqModel);
        return new PageInfo<>(list);
    }

    @Override
    public Integer getSecondApplyCount(ActivityStudentApplyReportSecondCountReqModel reqModel) {
        return this.baseMapper.countSecondApplyStudents(reqModel.getActivityId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean studentApply(ActivityStudentApplyReportApplyReqModel reqModel) {
        Long activityId = reqModel.getActivityId();
        Long studentId = reqModel.getStudentId();

        try {
            // 1. 校验活动是否存在
            LeisureActivityRecordEntity activity = leisureActivityRecordDao.selectById(activityId);
            if (activity == null) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_EXISTS));
            }

            // 2. 校验活动状态：必须是进行中的活动，或者是二次报名时间范围内
            Date currentTime = new Date();
            boolean isInProgress = activity.getStatus() != null && activity.getStatus() == 1
                    && activity.getStartTime() != null && activity.getEndTime() != null
                    && currentTime.after(activity.getStartTime()) && currentTime.before(activity.getEndTime());

            boolean isInSecondApplyTime = activity.getSecondEndTime() != null
                    && currentTime.before(activity.getSecondEndTime());

            if (!isInProgress && !isInSecondApplyTime) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_IN_PROGRESS_OR_SECOND_APPLY_TIME));
            }

            // 3. 校验学生是否已匹配（已匹配的学生不能报名）
            LambdaQueryWrapper<ActivityStudentReportEntity> matchQueryWrapper = new LambdaQueryWrapper<>();
            matchQueryWrapper.eq(ActivityStudentReportEntity::getActivityId, activityId)
                    .eq(ActivityStudentReportEntity::getStudentId, studentId)
                    .eq(ActivityStudentReportEntity::getDeleted, 0L);

            ActivityStudentReportEntity matchedRecord = activityStudentReportDao.selectOne(matchQueryWrapper);
            if (matchedRecord != null) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.STUDENT_ALREADY_MATCHED));
            }

            // 4. 确定报名类型（一次报名还是二次报名）
            Integer applyType;
            if (isInSecondApplyTime) {
                applyType = 2; // 二次报名
            } else {
                applyType = 1; // 一次报名
            }

            // 5. 校验学生是否已经报名过这次报名
            LambdaQueryWrapper<ActivityStudentApplyReportEntity> applyQueryWrapper = new LambdaQueryWrapper<>();
            applyQueryWrapper.eq(ActivityStudentApplyReportEntity::getActivityId, activityId)
                    .eq(ActivityStudentApplyReportEntity::getStudentId, studentId)
                    .eq(ActivityStudentApplyReportEntity::getType, applyType)
                    .eq(ActivityStudentApplyReportEntity::getDeleted, 0L);

            ActivityStudentApplyReportEntity existingApply = this.getOne(applyQueryWrapper);
            if (existingApply != null) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.STUDENT_ALREADY_APPLIED));
            }

            // 6. 校验志愿列表
            if (reqModel.getVolunteerList() == null || reqModel.getVolunteerList().isEmpty()) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.VOLUNTEER_LIST_NOT_EMPTY));
            }

            // 7. 校验志愿数量是否等于活动设定的志愿数
            if (applyType == 2) {
                // 二次报名志愿数可以不等于活动志愿数，但是必须大于可选课程数
                LeisureActivityCoursesQueryRecordReq queryRecordReq = new LeisureActivityCoursesQueryRecordReq();
                queryRecordReq.setActivityId(activityId);
                queryRecordReq.setStatus(0);
                queryRecordReq.setSchoolId(reqModel.getSchoolId());
                List<LeisureActivityCoursesRecordRes> list = leisureActivityCoursesRecordService.listAndApply(queryRecordReq);
                if (CollectionUtils.isEmpty(list)) {
                    throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.ACTIVITY_NO_COURSE));
                }
                if (list.size() >= activity.getVolunteerNum()) {
                    if (reqModel.getVolunteerList().size() != activity.getVolunteerNum()) {
                        throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.VOLUNTEER_NUM_MUST_EQUAL_ACTIVITY_SETTING));
                    }
                } else {
                    if (reqModel.getVolunteerList().size() != list.size()) {
                        throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.VOLUNTEER_NUM_MUST_EQUAL_ACTIVITY_SETTING));
                    }
                }
            } else if (reqModel.getVolunteerList().size() != activity.getVolunteerNum()) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.VOLUNTEER_NUM_MUST_EQUAL_ACTIVITY_SETTING));
            }

            // 8. 校验志愿是否有重复
            List<Long> lensonIds = reqModel.getVolunteerList().stream()
                    .map(ActivityStudentApplyReportVolunteerResModel::getLensonId)
                    .collect(Collectors.toList());

            Set<Long> lensonIdSet = new HashSet<>(lensonIds);
            if (lensonIdSet.size() != lensonIds.size()) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.VOLUNTEER_DUPLICATE_NOT_ALLOWED));
            }

            // 9. 校验课程是否存在且属于该活动
            LambdaQueryWrapper<LeisureActivityCoursesRecordEntity> courseQueryWrapper = new LambdaQueryWrapper<>();
            courseQueryWrapper.eq(LeisureActivityCoursesRecordEntity::getActivityId, activityId)
                    .in(LeisureActivityCoursesRecordEntity::getId, lensonIds)
                    .eq(LeisureActivityCoursesRecordEntity::getDeleted, 0L);

            List<LeisureActivityCoursesRecordEntity> courses = leisureActivityCoursesRecordService.list(courseQueryWrapper);
            if (courses.size() != lensonIds.size()) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.COURSE_NOT_EXISTS_IN_ACTIVITY));
            }

            // 10. 创建报名记录
            ActivityStudentApplyReportEntity applyEntity = new ActivityStudentApplyReportEntity();
            applyEntity.setActivityId(activityId);
            applyEntity.setStudentId(studentId);
            applyEntity.setType(applyType);
            applyEntity.setStatus(1); // 待匹配状态

            boolean applySaved = this.save(applyEntity);
            if (!applySaved) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SAVE_ERROR));
            }

            // 11. 创建志愿课程记录
            List<ActivityVolunteerLensonEntity> volunteerEntities = new ArrayList<>();
            for (int i = 0; i < reqModel.getVolunteerList().size(); i++) {
                ActivityStudentApplyReportVolunteerResModel volunteer = reqModel.getVolunteerList().get(i);

                ActivityVolunteerLensonEntity volunteerEntity = new ActivityVolunteerLensonEntity();
                volunteerEntity.setLensonId(volunteer.getLensonId());
                volunteerEntity.setStudentId(studentId);
                volunteerEntity.setApplyId(applyEntity.getId());
                volunteerEntity.setVolunteerType(volunteer.getVolunteerType()); // 志愿序号从1开始
                volunteerEntity.setActivityId(activityId);

                volunteerEntities.add(volunteerEntity);
            }

            boolean volunteerSaved = activityVolunteerLensonService.saveBatch(volunteerEntities);
            if (!volunteerSaved) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SAVE_ERROR));
            }

            log.info("学生报名成功，活动ID: {}, 学生ID: {}, 报名类型: {}", activityId, studentId, applyType);
            return true;

        } catch (BusinessMessageException e) {
            log.error("学生报名失败，活动ID: {}, 学生ID: {}, 错误信息: {}", activityId, studentId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("学生报名失败，活动ID: {}, 学生ID: {}", activityId, studentId, e);
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SAVE_ERROR));
        }
    }

    @Override
    public List<ActivityStudentApplyReportCurrentActivityResModel> getMyCourseList(ActivityStudentApplyReportMyCourseReqModel reqModel) {
        Long studentId = reqModel.getStudentId();
        String schoolYear = reqModel.getSchoolYear();

        List<ActivityStudentApplyReportCurrentActivityResModel> resultList = new ArrayList<>();

        // 1. 先查传入的学年下面全部学段id
        LambdaQueryWrapper<SemesterEntity> semesterQueryWrapper = new LambdaQueryWrapper<>();
        semesterQueryWrapper.eq(SemesterEntity::getSchoolYear, schoolYear)
                .eq(SemesterEntity::getDeleted, 0L);
        List<SemesterEntity> semesters = semesterService.list(semesterQueryWrapper);

        if (semesters.isEmpty()) {
            return resultList;
        }

        // 将学段信息缓存为Map，方便后续使用
        Map<Long, SemesterEntity> semesterMap = semesters.stream()
                .collect(Collectors.toMap(SemesterEntity::getId, semester -> semester));

        // 2. 根据学段id查询全部活动
        List<Long> semesterIds = semesters.stream()
                .map(SemesterEntity::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<LeisureActivityRecordEntity> activityQueryWrapper = new LambdaQueryWrapper<>();
        activityQueryWrapper.in(LeisureActivityRecordEntity::getSemesterId, semesterIds)
                .eq(LeisureActivityRecordEntity::getDeleted, 0L)
                .orderByDesc(LeisureActivityRecordEntity::getStartTime);

        List<LeisureActivityRecordEntity> activities = leisureActivityRecordDao.selectList(activityQueryWrapper);

        // 3. 遍历每个活动
        for (LeisureActivityRecordEntity activity : activities) {
            // 4. 查询这个活动下面这个学生已匹配的数据
            LambdaQueryWrapper<ActivityStudentReportEntity> matchQueryWrapper = new LambdaQueryWrapper<>();
            matchQueryWrapper.eq(ActivityStudentReportEntity::getActivityId, activity.getId())
                    .eq(ActivityStudentReportEntity::getStudentId, studentId)
                    .eq(ActivityStudentReportEntity::getStatus, 2)
                    .eq(ActivityStudentReportEntity::getDeleted, 0L);
            List<ActivityStudentReportEntity> matchedRecords = activityStudentReportDao.selectList(matchQueryWrapper);
            //查询出预先导入的数据
            LambdaQueryWrapper<ActivityStudentReportEntity> preMatchQueryWrapper = new LambdaQueryWrapper<>();
            preMatchQueryWrapper.eq(ActivityStudentReportEntity::getActivityId, activity.getId())
                    .eq(ActivityStudentReportEntity::getStudentId, studentId)
                    .eq(ActivityStudentReportEntity::getStatus, 1)
                    .eq(ActivityStudentReportEntity::getType, 1)
                    .eq(ActivityStudentReportEntity::getDeleted, 0L);
            List<ActivityStudentReportEntity> preMatchedRecords = activityStudentReportDao.selectList(preMatchQueryWrapper);
            if (!CollectionUtils.isEmpty(preMatchedRecords)) {
                matchedRecords.addAll(preMatchedRecords);
            }


            // 5. 查询这个活动下面这个学生报名的数据,最新的一条
            LambdaQueryWrapper<ActivityStudentApplyReportEntity> applyQueryWrapper = new LambdaQueryWrapper<>();
            applyQueryWrapper.eq(ActivityStudentApplyReportEntity::getActivityId, activity.getId())
                    .eq(ActivityStudentApplyReportEntity::getStudentId, studentId)
                    .eq(ActivityStudentApplyReportEntity::getDeleted, 0L)
                    .orderByDesc(ActivityStudentApplyReportEntity::getCreateTime)
                    .last("LIMIT 1");

            ActivityStudentApplyReportEntity applyRecord = this.getOne(applyQueryWrapper);

            // 6. 判断逻辑
            boolean hasMatchedData = !matchedRecords.isEmpty();
            boolean hasApplyData = applyRecord != null;

            ActivityStudentApplyReportCurrentActivityResModel activityResModel = new ActivityStudentApplyReportCurrentActivityResModel();
            activityResModel.setActivityId(activity.getId());
            activityResModel.setActivityName(activity.getName());
            activityResModel.setActivityYear(activity.getSchoolYear());
            activityResModel.setActivityDepartment(activity.getDepartment().toString());

            // 获取学段信息（从前面查询的学段信息中获取）
            SemesterEntity semester = semesterMap.get(activity.getSemesterId());
            if (semester != null) {
                activityResModel.setActivityStage(semester.getName());
            }

            // 判断是否为当前活动（进行中的活动）
            Date currentTime = new Date();
            boolean isCurrentActivity = false;
            if (activity.getSecondEndTime() != null && activity.getStatus() != null && activity.getStatus() == 1) {
                //二次报名
                if (activity.getSecondEndTime().after(currentTime)) {
                    isCurrentActivity = true;
                }
            } else {
                isCurrentActivity = activity.getStatus() != null && activity.getStatus() == 1
                        && activity.getStartTime() != null && activity.getEndTime() != null
                        && currentTime.after(activity.getStartTime()) && currentTime.before(activity.getEndTime());
            }
            activityResModel.setCurrentActivity(isCurrentActivity);

            // 处理已匹配的课程
            List<ActivityStudentApplyReportMyCourseResModel> courseList = new ArrayList<>();

            // 批量查询课程信息
            if (!matchedRecords.isEmpty()) {
                List<Long> courseIds = matchedRecords.stream()
                        .map(ActivityStudentReportEntity::getLensonId)
                        .collect(Collectors.toList());
                List<LeisureActivityCoursesRecordEntity> courses = leisureActivityCoursesRecordService.listByIds(courseIds);
                Map<Long, LeisureActivityCoursesRecordEntity> courseMap = courses.stream()
                        .collect(Collectors.toMap(LeisureActivityCoursesRecordEntity::getId, course -> course));

                for (ActivityStudentReportEntity matchedRecord : matchedRecords) {
                    // 查询课程信息
                    LeisureActivityCoursesRecordEntity course = courseMap.get(matchedRecord.getLensonId());
                    if (course != null) {
                        ActivityStudentApplyReportMyCourseResModel courseResModel = new ActivityStudentApplyReportMyCourseResModel();
                        courseResModel.setLensonId(course.getId());
                        courseResModel.setLensonName(course.getName());
                        courseResModel.setTeacherName(course.getTeacher());
                        courseResModel.setAddress(course.getClassroom());
                        courseResModel.setCourseCount(course.getCoursesNum());
                        courseResModel.setCourseTime(course.getCourseTime());
                        courseList.add(courseResModel);
                    }
                }
            }
            activityResModel.setCourseList(courseList);

            // 根据匹配和报名数据判断状态
            if (hasMatchedData) {
                // 有匹配数据
                // 有报名数据：已经匹配状态
                activityResModel.setStatus(matchedRecords.get(0).getType() == 1 ? -1 : 2); // 已匹配状态
                activityResModel.setType(applyRecord == null ? null : applyRecord.getType());
            } else {
                // 没有匹配数据，获取报名数据
                if (hasApplyData) {
                    //已经公布了，没有匹配数据则为失败
                    boolean publishStatus = false;
                    if (applyRecord.getType() == 1) {
                        publishStatus = activity.getPublishStatus() != null && activity.getPublishStatus() == 1;
                    } else {
                        publishStatus = activity.getPublishStatus() != null && activity.getPublishStatus() == 2;
                    }
                    activityResModel.setStatus(publishStatus ? 3 : 1);
                    activityResModel.setType(applyRecord.getType());

                    // 查询志愿列表
                    List<ActivityStudentApplyReportVolunteerResModel> volunteerList =
                            activityVolunteerLensonService.getVolunteerListByActivityAndStudent(applyRecord.getId());
                    activityResModel.setVolunteerList(volunteerList);
                } else {
                    // 既没有匹配也没有报名，跳过这个活动
                    continue;
                }
            }
            resultList.add(activityResModel);
        }

        //把状态=1的排到最前面去，其他的顺序不变
        List<ActivityStudentApplyReportCurrentActivityResModel> currentActivityResModels = new ArrayList<>();
        List<ActivityStudentApplyReportCurrentActivityResModel> otherActivityResModels = new ArrayList<>();
        for (ActivityStudentApplyReportCurrentActivityResModel resModel : resultList) {
            if (resModel.getStatus() == 1) {
                currentActivityResModels.add(resModel);
            } else {
                otherActivityResModels.add(resModel);
            }
        }
        currentActivityResModels.addAll(otherActivityResModels);
        return currentActivityResModels;
    }

    @Override
    public PageInfo<ActivityStudentApplyAdmittedListResModel> admittedList(Long schoolId, Long userId, ActivityStudentApplyAdmittedListReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(userId, schoolId);
        List<Long> classIds = new ArrayList<>();
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(userId, schoolId);
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(classIds)) {
                return null;
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(classIds) && reqModel.getClassId() != null && reqModel.getClassId() > 0) {
            //普通用户班级权限不为空，请求选择了某个班级
            if (!classIds.contains(reqModel.getClassId())) {
                return null;
            }
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ActivityStudentApplyAdmittedListResModel> list = this.getBaseMapper().admittedList(classIds, reqModel);
        return new PageInfo<>(list);
    }

    @Override
    public String admittedExport(Long schoolId, Long userId, ActivityStudentApplyAdmittedListReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(userId, schoolId);
        List<Long> classIds = new ArrayList<>();
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(userId, schoolId);
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(classIds)) {
                return null;
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(classIds) && reqModel.getClassId() != null && reqModel.getClassId() > 0) {
            //普通用户班级权限不为空，请求选择了某个班级
            if (!classIds.contains(reqModel.getClassId())) {
                return null;
            }
        }
        List<ActivityStudentApplyAdmittedListResModel> list = this.getBaseMapper().admittedList(classIds, reqModel);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            String fileName = "余暇活动已录取名单.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();
            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                List<ActivityStudentApplyAdmittedExportEnModel> exportEnModels = list.stream()
                        .map(resModel -> {
                            ActivityStudentApplyAdmittedExportEnModel exportModel = new ActivityStudentApplyAdmittedExportEnModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setSeatNo(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                            exportModel.setClassName(resModel.getGradeGroupName() + resModel.getClassName());
                            // 处理录取阶段和录取方式
                            exportModel.setAdmissionStage(AdmissionInfoEnum.getStage(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            exportModel.setAdmissionMethod(AdmissionInfoEnum.getMethod(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, ActivityStudentApplyAdmittedExportEnModel.class, FileTypeEnum.EXPORT, schoolId);
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                List<ActivityStudentApplyAdmittedExportPtModel> exportPtModels = list.stream()
                        .map(resModel -> {
                            ActivityStudentApplyAdmittedExportPtModel exportModel = new ActivityStudentApplyAdmittedExportPtModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setSeatNo(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                            exportModel.setClassName(resModel.getGradeGroupName() + resModel.getClassName());
                            // 处理录取阶段和录取方式
                            exportModel.setAdmissionStage(AdmissionInfoEnum.getStage(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            exportModel.setAdmissionMethod(AdmissionInfoEnum.getMethod(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, ActivityStudentApplyAdmittedExportPtModel.class, FileTypeEnum.EXPORT, schoolId);
            } else {
                List<ActivityStudentApplyAdmittedExportModel> exportPtModels = list.stream()
                        .map(resModel -> {
                            ActivityStudentApplyAdmittedExportModel exportModel = new ActivityStudentApplyAdmittedExportModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setSeatNo(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                            exportModel.setClassName(resModel.getGradeGroupName() + resModel.getClassName());
                            // 处理录取阶段和录取方式
                            exportModel.setAdmissionStage(AdmissionInfoEnum.getStage(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            exportModel.setAdmissionMethod(AdmissionInfoEnum.getMethod(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, ActivityStudentApplyAdmittedExportModel.class, FileTypeEnum.EXPORT, schoolId);
            }
        }
        return null;
    }

    @Override
    public PageInfo<ActivityStudentApplyRegisteredListResModel> registeredList(Long schoolId, Long userId, ActivityStudentApplyRegisteredListReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(userId, schoolId);
        List<Long> classIds = new ArrayList<>();
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(userId, schoolId);
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(classIds)) {
                return null;
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(classIds) && reqModel.getClassId() != null && reqModel.getClassId() > 0) {
            //普通用户班级权限不为空，请求选择了某个班级
            if (!classIds.contains(reqModel.getClassId())) {
                return null;
            }
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ActivityStudentApplyRegisteredListResModel> list = this.getBaseMapper().registeredList(classIds, reqModel);
        if (!CollectionUtils.isEmpty(list)) {
            //获取志愿信息
            List<Long> ids = list.stream().map(ActivityStudentApplyRegisteredListResModel::getId).distinct().collect(Collectors.toList());
            QueryWrapper<ActivityVolunteerLensonEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().in(ActivityVolunteerLensonEntity::getApplyId, ids);
            List<ActivityVolunteerLensonEntity> volunteerLensonEntities = activityVolunteerLensonService.list(wrapper);
            Map<Long, List<ActivityVolunteerLensonEntity>> volunteerMap = new HashMap<>();
            Map<Long, LeisureActivityCoursesRecordEntity> courseMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(volunteerLensonEntities)) {
                volunteerMap = volunteerLensonEntities.stream().collect(Collectors.groupingBy(ActivityVolunteerLensonEntity::getApplyId));
                List<Long> courseIds = volunteerLensonEntities.stream().map(ActivityVolunteerLensonEntity::getLensonId).distinct().collect(Collectors.toList());
                List<LeisureActivityCoursesRecordEntity> courses = leisureActivityCoursesRecordService.listByIds(courseIds);
                if (!CollectionUtils.isEmpty(courses)) {
                    courseMap = courses.stream().collect(Collectors.toMap(LeisureActivityCoursesRecordEntity::getId, leisureActivityCoursesRecordEntity -> leisureActivityCoursesRecordEntity));
                }
            }
            //获取录取信息
            List<Long> studentIds = list.stream().map(ActivityStudentApplyRegisteredListResModel::getStudentId).distinct().collect(Collectors.toList());
            QueryWrapper<ActivityStudentReportEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ActivityStudentReportEntity::getActivityId, reqModel.getActivityId())
                    .in(ActivityStudentReportEntity::getStudentId, studentIds);
            List<ActivityStudentReportEntity> studentReports = activityStudentReportService.list(queryWrapper);
            Map<Long, ActivityStudentReportEntity> reportMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(studentReports)) {
                reportMap = studentReports.stream().collect(Collectors.toMap(ActivityStudentReportEntity::getStudentId, activityStudentReportEntity -> activityStudentReportEntity));
            }
            for (ActivityStudentApplyRegisteredListResModel resModel : list) {
                List<ActivityVolunteerLensonEntity> lensonEntities = volunteerMap.get(resModel.getId());
                if (!CollectionUtils.isEmpty(lensonEntities)) {
                    List<ActivityStudentApplyRegisteredVolunteerResModel> volunteers = new ArrayList<>();
                    for (ActivityVolunteerLensonEntity lensonEntity : lensonEntities) {
                        ActivityStudentApplyRegisteredVolunteerResModel volunteerResModel = new ActivityStudentApplyRegisteredVolunteerResModel();
                        LeisureActivityCoursesRecordEntity coursesRecord = courseMap.get(lensonEntity.getLensonId());
                        if (coursesRecord != null) {
                            volunteerResModel.setCourseName(coursesRecord.getName());
                        }
                        volunteerResModel.setVolunteerType(lensonEntity.getVolunteerType());
                        ActivityStudentReportEntity studentReport = reportMap.get(resModel.getStudentId());
                        if (studentReport != null && studentReport.getLensonId().equals(lensonEntity.getLensonId())) {
                            // type: 1-一次报名, 2-二次报名
                            // studentReport.type: 1-预先导入, 2-分配, 3-一次报名志愿录入, 4-二次报名志愿录入, 5-二次报名系统随机分配, 6-二次报名人工分配
                            if (resModel.getType() == 1) {
                                // 一次报名学生，匹配类型为1(预先导入)、2(分配)、3(一次报名志愿录入)时为录取
                                volunteerResModel.setIsAdmitted(studentReport.getType() == 1 || studentReport.getType() == 2 || studentReport.getType() == 3);
                            } else {
                                // 二次报名学生，匹配类型为4(二次报名志愿录入)、5(二次报名系统随机分配)、6(二次报名人工分配)时为录取
                                volunteerResModel.setIsAdmitted(studentReport.getType() == 4 || studentReport.getType() == 5 || studentReport.getType() == 6);
                            }
                        } else {
                            volunteerResModel.setIsAdmitted(false);
                        }
                        volunteers.add(volunteerResModel);
                    }
                    resModel.setVolunteers(volunteers);
                }
            }
        }
        return new PageInfo<>(list);
    }

    @Override
    public String registeredExport(Long schoolId, Long userId, ActivityStudentApplyRegisteredListReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(userId, schoolId);
        List<Long> classIds = new ArrayList<>();
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(userId, schoolId);
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(classIds)) {
                return null;
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(classIds) && reqModel.getClassId() != null && reqModel.getClassId() > 0) {
            //普通用户班级权限不为空，请求选择了某个班级
            if (!classIds.contains(reqModel.getClassId())) {
                return null;
            }
        }
        LeisureActivityRecordEntity activityRecord = leisureActivityRecordDao.selectById(reqModel.getActivityId());
        if (activityRecord == null) {
            return null;
        }
        List<ActivityStudentApplyRegisteredListResModel> list = this.getBaseMapper().registeredList(classIds, reqModel);
        if (!CollectionUtils.isEmpty(list)) {
            //获取志愿信息
            List<Long> ids = list.stream().map(ActivityStudentApplyRegisteredListResModel::getId).distinct().collect(Collectors.toList());
            QueryWrapper<ActivityVolunteerLensonEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().in(ActivityVolunteerLensonEntity::getApplyId, ids);
            List<ActivityVolunteerLensonEntity> volunteerLensonEntities = activityVolunteerLensonService.list(wrapper);
            Map<Long, List<ActivityVolunteerLensonEntity>> volunteerMap = new HashMap<>();
            Map<Long, LeisureActivityCoursesRecordEntity> courseMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(volunteerLensonEntities)) {
                volunteerMap = volunteerLensonEntities.stream().collect(Collectors.groupingBy(ActivityVolunteerLensonEntity::getApplyId));
                List<Long> courseIds = volunteerLensonEntities.stream().map(ActivityVolunteerLensonEntity::getLensonId).distinct().collect(Collectors.toList());
                List<LeisureActivityCoursesRecordEntity> courses = leisureActivityCoursesRecordService.listByIds(courseIds);
                if (!CollectionUtils.isEmpty(courses)) {
                    courseMap = courses.stream().collect(Collectors.toMap(LeisureActivityCoursesRecordEntity::getId, leisureActivityCoursesRecordEntity -> leisureActivityCoursesRecordEntity));
                }
            }
            //获取录取信息
            List<Long> studentIds = list.stream().map(ActivityStudentApplyRegisteredListResModel::getStudentId).distinct().collect(Collectors.toList());
            QueryWrapper<ActivityStudentReportEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ActivityStudentReportEntity::getActivityId, reqModel.getActivityId())
                    .in(ActivityStudentReportEntity::getStudentId, studentIds);
            List<ActivityStudentReportEntity> studentReports = activityStudentReportService.list(queryWrapper);
            Map<Long, ActivityStudentReportEntity> reportMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(studentReports)) {
                reportMap = studentReports.stream().collect(Collectors.toMap(ActivityStudentReportEntity::getStudentId, activityStudentReportEntity -> activityStudentReportEntity));
            }
            for (ActivityStudentApplyRegisteredListResModel resModel : list) {
                List<ActivityVolunteerLensonEntity> lensonEntities = volunteerMap.get(resModel.getId());
                if (!CollectionUtils.isEmpty(lensonEntities)) {
                    List<ActivityStudentApplyRegisteredVolunteerResModel> volunteers = new ArrayList<>();
                    for (ActivityVolunteerLensonEntity lensonEntity : lensonEntities) {
                        ActivityStudentApplyRegisteredVolunteerResModel volunteerResModel = new ActivityStudentApplyRegisteredVolunteerResModel();
                        LeisureActivityCoursesRecordEntity coursesRecord = courseMap.get(lensonEntity.getLensonId());
                        if (coursesRecord != null) {
                            volunteerResModel.setCourseName(coursesRecord.getName());
                        }
                        volunteerResModel.setVolunteerType(lensonEntity.getVolunteerType());
                        ActivityStudentReportEntity studentReport = reportMap.get(resModel.getStudentId());
                        if (studentReport != null && studentReport.getLensonId().equals(lensonEntity.getLensonId())) {
                            // type: 1-一次报名, 2-二次报名
                            // studentReport.type: 1-预先导入, 2-分配, 3-一次报名志愿录入, 4-二次报名志愿录入, 5-二次报名系统随机分配, 6-二次报名人工分配
                            if (resModel.getType() == 1) {
                                // 一次报名学生，匹配类型为1(预先导入)、2(分配)、3(一次报名志愿录入)时为录取
                                volunteerResModel.setIsAdmitted(studentReport.getType() == 1 || studentReport.getType() == 2 || studentReport.getType() == 3);
                            } else {
                                // 二次报名学生，匹配类型为4(二次报名志愿录入)、5(二次报名系统随机分配)、6(二次报名人工分配)时为录取
                                volunteerResModel.setIsAdmitted(studentReport.getType() == 4 || studentReport.getType() == 5 || studentReport.getType() == 6);
                            }
                        } else {
                            volunteerResModel.setIsAdmitted(false);
                        }
                        volunteers.add(volunteerResModel);
                    }
                    resModel.setVolunteers(volunteers);
                }
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            String fileName = "余暇活动报名名单.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();
            List<List<String>> headers = getRegisteredHeaders(activityRecord, SchoolLanguageEnum.getDefValue(currentLanguage));
            List<List<String>> data = new ArrayList<>();
            //拼接成绩
            for (ActivityStudentApplyRegisteredListResModel resModel : list) {
                List<String> row = new ArrayList<>();
                row.add(resModel.getGradeGroupName() + resModel.getClassName());
                row.add(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                row.add(resModel.getStudentName());
                row.add(resModel.getStudentNo());
                row.add(ApplyStageEnum.getValue(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                List<ActivityStudentApplyRegisteredVolunteerResModel> volunteers = resModel.getVolunteers();
                Map<Long, ActivityStudentApplyRegisteredVolunteerResModel> volunteerMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(volunteers)) {
                    volunteerMap = volunteers.stream().collect(Collectors.toMap(ActivityStudentApplyRegisteredVolunteerResModel::getVolunteerType, item -> item));
                }
                // 动态添加志愿数据
                for (int i = 0; i < activityRecord.getVolunteerNum(); i++) {
                    ActivityStudentApplyRegisteredVolunteerResModel volunteerResModel = volunteerMap.get((long) (i + 1));
                    if (volunteerResModel != null) {
                        if (volunteerResModel.getIsAdmitted()) {
                            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                                row.add(volunteerResModel.getCourseName() + "(Admitted)");
                            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                                row.add(volunteerResModel.getCourseName() + "(Admitido)");
                            } else {
                                row.add(volunteerResModel.getCourseName() + "(已錄取)");
                            }
                        } else {
                            row.add(volunteerResModel.getCourseName());
                        }
                    } else {
                        row.add("");
                    }
                }
                row.add(resModel.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                data.add(row);
            }
            return exportFileHandler.doExportExcelCommon(data, fileName, headers, FileTypeEnum.EXPORT, schoolId);
        }
        return null;
    }

    private List<List<String>> getRegisteredHeaders(LeisureActivityRecordEntity activityRecord, SchoolLanguageEnum languageEnum) {
        Integer volunteerNum = activityRecord.getVolunteerNum();
        //生成表头
        List<List<String>> headers = new ArrayList<>();
        // 添加基础列头
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_CLASS_NAME)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_SEAT_NO)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_STUDENT_NAME)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_STUDENT_NO)));
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_REGISTRATION_STAGE)));
        // 添加动态指标列
        for (int i = 0; i < volunteerNum; i++) {
            headers.add(Collections.singletonList(VolunteerNumberUtil.getVolunteerNumber(i + 1, languageEnum)));
        }
        headers.add(Collections.singletonList(languageUtil.getMessage(LanguageConstants.EXPORT_REGISTRATION_TIME)));
        return headers;
    }

    @Override
    public PageInfo<ActivityStudentApplyNotRegisteredListResModel> notRegisteredList(Long schoolId, Long userId, ActivityStudentApplyNotRegisteredListReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(userId, schoolId);
        List<Long> classIds = new ArrayList<>();
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(userId, schoolId);
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(classIds)) {
                return null;
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(classIds) && reqModel.getClassId() != null && reqModel.getClassId() > 0) {
            //普通用户班级权限不为空，请求选择了某个班级
            if (!classIds.contains(reqModel.getClassId())) {
                return null;
            }
        }
        LeisureActivityRecordEntity leisureActivityRecordEntity = leisureActivityRecordDao.selectById(reqModel.getActivityId());
        if (leisureActivityRecordEntity == null) {
            return null;
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<ActivityStudentApplyNotRegisteredListResModel> list = this.getBaseMapper().notRegisteredList(schoolId, leisureActivityRecordEntity.getSchoolYear(), leisureActivityRecordEntity.getDepartment(), classIds, reqModel);
        if (!CollectionUtils.isEmpty(list)) {
            List<Long> studentIds = list.stream().map(ActivityStudentApplyNotRegisteredListResModel::getStudentId).collect(Collectors.toList());
            QueryWrapper<ActivityStudentReportEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ActivityStudentReportEntity::getActivityId, reqModel.getActivityId())
                    .in(ActivityStudentReportEntity::getStudentId, studentIds);
            List<ActivityStudentReportEntity> studentReportEntityList = activityStudentReportService.list(queryWrapper);
            Map<Long, ActivityStudentReportEntity> studentReportMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(studentReportEntityList)) {
                studentReportMap = studentReportEntityList.stream().collect(Collectors.toMap(ActivityStudentReportEntity::getStudentId, Function.identity()));
            }
            for (ActivityStudentApplyNotRegisteredListResModel resModel : list) {
                ActivityStudentReportEntity studentReportEntity = studentReportMap.get(resModel.getStudentId());
                if (studentReportEntity != null) {
                    resModel.setIsAdmitted(true);
                    resModel.setType(studentReportEntity.getType());
                } else {
                    resModel.setIsAdmitted(false);
                }
            }
        }
        return new PageInfo<>(list);
    }

    @Override
    public String notRegisteredExport(Long schoolId, Long userId, ActivityStudentApplyNotRegisteredListReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(userId, schoolId);
        List<Long> classIds = new ArrayList<>();
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(userId, schoolId);
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(classIds)) {
                return null;
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(classIds) && reqModel.getClassId() != null && reqModel.getClassId() > 0) {
            //普通用户班级权限不为空，请求选择了某个班级
            if (!classIds.contains(reqModel.getClassId())) {
                return null;
            }
        }
        LeisureActivityRecordEntity leisureActivityRecordEntity = leisureActivityRecordDao.selectById(reqModel.getActivityId());
        if (leisureActivityRecordEntity == null) {
            return null;
        }
        List<ActivityStudentApplyNotRegisteredListResModel> list = this.getBaseMapper().notRegisteredList(schoolId, leisureActivityRecordEntity.getSchoolYear(), leisureActivityRecordEntity.getDepartment(), classIds, reqModel);
        if (!CollectionUtils.isEmpty(list)) {
            List<Long> studentIds = list.stream().map(ActivityStudentApplyNotRegisteredListResModel::getStudentId).collect(Collectors.toList());
            QueryWrapper<ActivityStudentReportEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ActivityStudentReportEntity::getActivityId, reqModel.getActivityId())
                    .in(ActivityStudentReportEntity::getStudentId, studentIds);
            List<ActivityStudentReportEntity> studentReportEntityList = activityStudentReportService.list(queryWrapper);
            Map<Long, ActivityStudentReportEntity> studentReportMap = studentReportEntityList.stream().collect(Collectors.toMap(ActivityStudentReportEntity::getStudentId, Function.identity()));
            for (ActivityStudentApplyNotRegisteredListResModel resModel : list) {
                ActivityStudentReportEntity studentReportEntity = studentReportMap.get(resModel.getStudentId());
                if (studentReportEntity != null) {
                    resModel.setIsAdmitted(true);
                    resModel.setType(studentReportEntity.getType());
                } else {
                    resModel.setIsAdmitted(false);
                }
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
            String fileName = "余暇活动未报名名单.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();
            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                List<ActivityStudentApplyNotRegisteredExportEnModel> exportEnModels = list.stream()
                        .map(resModel -> {
                            ActivityStudentApplyNotRegisteredExportEnModel exportModel = new ActivityStudentApplyNotRegisteredExportEnModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setSeatNo(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                            exportModel.setClassName(resModel.getGradeGroupName() + resModel.getClassName());
                            // 处理录取阶段和录取方式
                            if (resModel.getIsAdmitted()) {
                                exportModel.setAdmissionStage(AdmissionInfoEnum.getStage(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                                exportModel.setAdmissionMethod(AdmissionInfoEnum.getMethod(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            } else {
                                exportModel.setAdmissionStage("- -");
                                exportModel.setAdmissionMethod("- -");
                            }
                            // 处理录取情况字段
                            exportModel.setAdmissionStatus(resModel.getIsAdmitted() ? "Admitted" : "Not Admitted");
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, ActivityStudentApplyNotRegisteredExportEnModel.class, FileTypeEnum.EXPORT, schoolId);
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                List<ActivityStudentApplyNotRegisteredExportPtModel> exportPtModels = list.stream()
                        .map(resModel -> {
                            ActivityStudentApplyNotRegisteredExportPtModel exportModel = new ActivityStudentApplyNotRegisteredExportPtModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setSeatNo(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                            exportModel.setClassName(resModel.getGradeGroupName() + resModel.getClassName());
                            // 处理录取阶段和录取方式
                            if (resModel.getIsAdmitted()) {
                                exportModel.setAdmissionStage(AdmissionInfoEnum.getStage(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                                exportModel.setAdmissionMethod(AdmissionInfoEnum.getMethod(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            } else {
                                exportModel.setAdmissionStage("- -");
                                exportModel.setAdmissionMethod("- -");
                            }
                            // 处理录取情况字段
                            exportModel.setAdmissionStatus(resModel.getIsAdmitted() ? "Admitido" : "Não admitido");
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, ActivityStudentApplyNotRegisteredExportPtModel.class, FileTypeEnum.EXPORT, schoolId);
            } else {
                List<ActivityStudentApplyNotRegisteredExportModel> exportPtModels = list.stream()
                        .map(resModel -> {
                            ActivityStudentApplyNotRegisteredExportModel exportModel = new ActivityStudentApplyNotRegisteredExportModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setSeatNo(resModel.getSeatNo() != null ? String.valueOf(resModel.getSeatNo()) : "");
                            exportModel.setClassName(resModel.getGradeGroupName() + resModel.getClassName());
                            // 处理录取阶段和录取方式
                            if (resModel.getIsAdmitted()) {
                                exportModel.setAdmissionStage(AdmissionInfoEnum.getStage(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                                exportModel.setAdmissionMethod(AdmissionInfoEnum.getMethod(resModel.getType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            } else {
                                exportModel.setAdmissionStage("- -");
                                exportModel.setAdmissionMethod("- -");
                            }
                            // 处理录取情况字段
                            exportModel.setAdmissionStatus(resModel.getIsAdmitted() ? "已錄取" : "未錄取");
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, ActivityStudentApplyNotRegisteredExportModel.class, FileTypeEnum.EXPORT, schoolId);
            }
        }
        return null;
    }
}
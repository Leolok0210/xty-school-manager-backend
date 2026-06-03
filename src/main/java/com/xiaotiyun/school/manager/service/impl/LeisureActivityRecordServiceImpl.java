package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.LeisureActivityRecordDao;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.LeisureActivityRecordAddReqModel;
import com.xiaotiyun.school.manager.model.req.LeisureActivityRecordIndexReqModel;
import com.xiaotiyun.school.manager.model.req.LeisureActivityRecordPageReqModel;
import com.xiaotiyun.school.manager.model.res.LeisureActivityRecordResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 余暇活动记录表 Service 实现类
 */
@Service
public class LeisureActivityRecordServiceImpl extends ServiceImpl<LeisureActivityRecordDao, LeisureActivityRecordEntity> implements LeisureActivityRecordService {
    @Resource
    private LeisureActivityCoursesRecordService coursesRecordService;
    @Resource
    private ActivityStudentReportService activityStudentReportService;
    @Resource
    private ActivityStudentApplyReportService activityStudentApplyReportService;
    @Resource
    private ActivityVolunteerLensonService activityVolunteerLensonService;

    @Resource
    private LanguageUtil languageUtil;

    @Resource
    private UserSchoolRelService userSchoolRelService;
    @Resource
    private SysClassService sysClassService;
    @Resource
    private ClassroomService classroomService;
    @Resource
    private StudentService studentService;

    @Resource
    private GradeGroupService gradeGroupService;
    @Resource
    private SemesterService semesterService;
    @Resource
    @Lazy
    private NoticeService noticeService;

    @Override
    public PageInfo<LeisureActivityRecordResModel> selectPage(LeisureActivityRecordPageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<LeisureActivityRecordResModel> list = this.getBaseMapper().page(reqModel);
        list.forEach(a -> {
            // 活动结束时间是否已过
            if (a.getSecondEndTime() != null) {
                if (a.getSecondEndTime().before(new Date())) {
                    a.setStatus(2);
                }
            } else {
                if (a.getEndTime().before(new Date())) {
                    a.setStatus(2);
                }
            }
        });
        return new PageInfo<>(list);
    }

    @Override
    public List<LeisureActivityRecordResModel> listByReq(LeisureActivityRecordPageReqModel reqModel) {
        LambdaQueryWrapper<LeisureActivityRecordEntity> where = Wrappers.<LeisureActivityRecordEntity>lambdaQuery();
        where.eq(LeisureActivityRecordEntity::getSchoolId, reqModel.getSchoolId());
        where.eq(LeisureActivityRecordEntity::getSchoolYear, reqModel.getSchoolYear());
        where.eq(reqModel.getSemesterId() != null && reqModel.getSemesterId() > 0,
                LeisureActivityRecordEntity::getSemesterId, reqModel.getSemesterId());
        where.eq( reqModel.getDepartment() != null && reqModel.getDepartment() > 0,
                LeisureActivityRecordEntity::getDepartment, reqModel.getDepartment());
        where.orderByDesc(BaseEntity::getCreateTime);
        List<LeisureActivityRecordEntity> list = this.list(where);
        if (ObjectUtils.isNotEmpty(list)) {
            return getResList(list);
        }
        return new ArrayList<>();
    }

    @Override
    public List<LeisureActivityRecordResModel> listByStudent(LeisureActivityRecordIndexReqModel reqModel) {
        // 获取当前学生的级组和学段信息
        GradeGroup gradeGroup = gradeGroupService.getById(reqModel.getGroupId());
        if (ObjectUtils.isEmpty(gradeGroup)) {
            return new ArrayList<>();
        }
        List<SemesterEntity> semesterEntities = semesterService.list(Wrappers.<SemesterEntity>lambdaQuery()
                .eq(SemesterEntity::getSchoolId, reqModel.getSchoolId())
                .eq(SemesterEntity::getDepartment, gradeGroup.getDepartment())
                .eq(SemesterEntity::getSchoolYear, reqModel.getSchoolYear())
                .le(SemesterEntity::getStartTime, new Date())
                .ge(SemesterEntity::getEndTime, new Date()));
        if (ObjectUtils.isEmpty(semesterEntities)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<LeisureActivityRecordEntity> where = Wrappers.<LeisureActivityRecordEntity>lambdaQuery();
        where.eq(LeisureActivityRecordEntity::getSchoolId, reqModel.getSchoolId());
        where.eq(LeisureActivityRecordEntity::getDepartment, gradeGroup.getDepartment());
        where.eq(LeisureActivityRecordEntity::getSchoolYear, reqModel.getSchoolYear());
        where.eq(LeisureActivityRecordEntity::getSemesterId, semesterEntities.get(0).getId());
        where.eq(LeisureActivityRecordEntity::getStatus, 1);
        where.orderByDesc(LeisureActivityRecordEntity::getStartTime);
        List<LeisureActivityRecordEntity> list = this.list(where);
        if (ObjectUtils.isNotEmpty(list)) {
            List<Long> activityIds = list.stream().map(BaseEntity::getId).collect(Collectors.toList());
            // 查询当前学生已匹配
            List<ActivityStudentReportEntity> reportList = activityStudentReportService.list(Wrappers.<ActivityStudentReportEntity>lambdaQuery()
                    .eq(ActivityStudentReportEntity::getStudentId, reqModel.getStudentId())
                    .in(ActivityStudentReportEntity::getActivityId, activityIds));
            Map<Long, ActivityStudentReportEntity> reportMap = new HashMap<>();
            if (ObjectUtils.isNotEmpty(reportList)) {
                reportMap = reportList.stream().collect(Collectors.toMap(ActivityStudentReportEntity::getActivityId, Function.identity()));
            }
            // 过滤出当前学生已报名的活动
            List<ActivityStudentApplyReportEntity> applyReportList = activityStudentApplyReportService.list(Wrappers.<ActivityStudentApplyReportEntity>lambdaQuery()
                    .eq(ActivityStudentApplyReportEntity::getStudentId, reqModel.getStudentId())
                    .in(ActivityStudentApplyReportEntity::getActivityId, activityIds));
            Map<Long, List<ActivityStudentApplyReportEntity>> applyReportMap = new HashMap<>();
            if (ObjectUtils.isNotEmpty(applyReportList)) {
                applyReportMap = applyReportList.stream().collect(Collectors.groupingBy(ActivityStudentApplyReportEntity::getActivityId));
            }
            List<LeisureActivityRecordResModel> resList = new ArrayList<>();
            for (LeisureActivityRecordEntity entity : list) {
                LeisureActivityRecordResModel resModel = new LeisureActivityRecordResModel();
                BeanUtils.copyProperties(entity, resModel);
                resModel.setSemesterName(semesterEntities.get(0).getName());
                // 活动结束时间是否已过，不展示已截止的活动
                if (entity.getSecondEndTime() != null) {
                    if (entity.getSecondEndTime().before(new Date())) {
                        continue;
                    }
                } else {
                    if (entity.getEndTime().before(new Date())) {
                        continue;
                    }
                }
                // 判断是否已报名
                if (reportMap.containsKey(entity.getId())) {
                    resModel.setNeedSignUp(1);
                } else if (applyReportMap.containsKey(entity.getId())) {
                    List<ActivityStudentApplyReportEntity> activityStudentApplyReportEntity = applyReportMap.get(entity.getId());
                    // 若开启二次报名
                    if (entity.getSecondEndTime() != null) {
                        activityStudentApplyReportEntity = activityStudentApplyReportEntity.stream()
                                .filter(a -> a.getType() == 2).collect(Collectors.toList());
                        if (ObjectUtils.isNotEmpty(activityStudentApplyReportEntity)) {
                            resModel.setNeedSignUp(1);
                        } else {
                            resModel.setNeedSignUp(0);
                        }
                    } else {
                        // 一次报名
                        activityStudentApplyReportEntity = activityStudentApplyReportEntity.stream()
                                .filter(a -> a.getType() == 1).collect(Collectors.toList());
                        if (ObjectUtils.isNotEmpty(activityStudentApplyReportEntity)) {
                            resModel.setNeedSignUp(1);
                        } else {
                            resModel.setNeedSignUp(0);
                        }
                    }
                } else {
                    resModel.setNeedSignUp(0);
                }
                resList.add(resModel);
            }
            // 排序，优先将时间大于开始时间且未截止的放在开头，其次是为截止，次要的根据时间倒序排序
            Date now = new Date();
            resList.sort((a, b) -> {
                // 判断活动是否在进行中（当前时间在开始时间之后且未截止）
                boolean aInProgress = a.getStartTime().before(now);
                boolean bInProgress = b.getStartTime().before(now);
                // 优先将进行中的活动排在前面
                if (aInProgress && !bInProgress) {
                    return -1;
                } else if (!aInProgress && bInProgress) {
                    return 1;
                }
                // 最后按开始时间倒序排序
                else {
                    return b.getStartTime().compareTo(a.getStartTime());
                }
            });
            return resList;
        }
        return new ArrayList<>();
    }

    @Override
    public Result<Boolean> changeStatus(Long id, Integer status) {
        LeisureActivityRecordEntity entity = this.getById(id);
        if (ObjectUtils.isEmpty(entity)) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.ACTIVITY_NOT_EXISTS));
        }
        if (Objects.equals(status, entity.getStatus())) {
            return Result.success();
        }
        // 发布
        if (status == 1) {
            // 校验课程数是否大于等于可选志愿数
            List<LeisureActivityCoursesRecordEntity> coursesRecordEntities = coursesRecordService.list(Wrappers.<LeisureActivityCoursesRecordEntity>lambdaQuery()
                    .eq(LeisureActivityCoursesRecordEntity::getStatus, 1)
                    .eq(LeisureActivityCoursesRecordEntity::getActivityId, id));
            if (ObjectUtils.isEmpty(coursesRecordEntities)) {
                return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.COURSE_NOT_FOUND));
            }
            long count = coursesRecordEntities.size();
            long totalQuotaTotal = coursesRecordEntities.stream().mapToLong(LeisureActivityCoursesRecordEntity::getQuotaTotal).sum();
            // 检查课程总名额是否大于学部下学生人数
            List<SysClass> sysClassEntity = sysClassService.list(Wrappers.<SysClass>lambdaQuery()
                    .eq(SysClass::getSchoolId, entity.getSchoolId())
                    .eq(SysClass::getDepartment, entity.getDepartment()));
            if (ObjectUtils.isEmpty(sysClassEntity)) {
                return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.CLASS_NOT_FOUND));
            }
            List<Long> classIds = sysClassEntity.stream().map(BaseEntity::getId).collect(Collectors.toList());
            long studentCount = studentService.count(Wrappers.<StudentEntity>lambdaQuery()
                    .eq(StudentEntity::getSchoolId, entity.getSchoolId())
                    .in(StudentEntity::getClassId, classIds));
            if (totalQuotaTotal < studentCount) {
                return Result.failed(ResultCode.FAILED.getCode(), String.format(languageUtil.getMessage(LanguageConstants.COURSE_QUOTA_LESS_THAN_STUDENT_NUM), studentCount));
            }
            if (entity.getVolunteerNum() <= count) {
                entity.setStatus(status);
                entity.setPublishTime(new Date());
                this.updateById(entity);
                if (entity.getOpenWechatNotice() == 1) {
                    //开启企微通知的活动，创建剩余需要发送通知任务
                    noticeService.createLeisureNotice(entity);
                }
                return Result.success();
            } else {
                return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.COURSE_QUOTA_LESS_THAN_VOLUNTEER_NUM));
            }
        } else if (status == 0) {
            // 召回
            entity.setStatus(status);
            entity.setPublishTime(null);
            this.updateById(entity);
            // 取消报名
            activityStudentApplyReportService.remove(Wrappers.<ActivityStudentApplyReportEntity>lambdaQuery()
                    .eq(ActivityStudentApplyReportEntity::getActivityId, id));
            activityVolunteerLensonService.remove(Wrappers.<ActivityVolunteerLensonEntity>lambdaQuery()
                    .eq(ActivityVolunteerLensonEntity::getActivityId, id));
            if (entity.getOpenWechatNotice() == 1) {
                //开启企微通知的活动，删除剩余需要发送通知任务
                noticeService.deleteLeisureNotice(entity);
            }
            return Result.success();
        }
        return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
    }

    @Override
    public Result<Boolean> copy(LeisureActivityRecordAddReqModel record) {
        Long oldId = record.getId();
        record.setId(null);
        LeisureActivityRecordEntity entity = this.getById(oldId);
        if (ObjectUtils.isEmpty(entity)) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        long count = this.count(Wrappers.<LeisureActivityRecordEntity>lambdaQuery()
                .eq(LeisureActivityRecordEntity::getSchoolId, record.getSchoolId())
                .eq(LeisureActivityRecordEntity::getDepartment, record.getDepartment())
                .eq(LeisureActivityRecordEntity::getSchoolYear, record.getSchoolYear())
                .eq(LeisureActivityRecordEntity::getName, record.getName()));
        if (count > 0) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.ACTIVITY_NAME_DUPLICATED));
        }
        // 复制活动
        LeisureActivityRecordEntity copyEntity = new LeisureActivityRecordEntity();
        BeanUtils.copyProperties(record, copyEntity);
        copyEntity.setStatus(0);
        copyEntity.setPublishStatus(0);
        this.save(copyEntity);
        // 复制课程
        AtomicInteger i = new AtomicInteger(0);// 失败数量
        List<LeisureActivityCoursesRecordEntity> coursesRecordEntities = coursesRecordService.list(Wrappers.<LeisureActivityCoursesRecordEntity>lambdaQuery()
                .eq(LeisureActivityCoursesRecordEntity::getActivityId, oldId));
        if (ObjectUtils.isNotEmpty(coursesRecordEntities)) {
            List<Long> classRoomIds = coursesRecordEntities.stream().map(LeisureActivityCoursesRecordEntity::getClassroomId)
                    .filter(classroomId -> classroomId != -1L)
                    .collect(Collectors.toList());
            List<Long> teaId = coursesRecordEntities.stream().map(LeisureActivityCoursesRecordEntity::getTeacherId).collect(Collectors.toList());
            List<ClassroomEntity> classroomEntities = classroomService.listByIds(classRoomIds);
            if (ObjectUtils.isEmpty(classroomEntities)) {
                return Result.success();
            }
            List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelService.listByIds(teaId);
            if (ObjectUtils.isEmpty(userSchoolRelEntities)) {
                return Result.success();
            }
            List<Long> hasClassroomIds = classroomEntities.stream().map(BaseEntity::getId).collect(Collectors.toList());
            List<Long> hasTeaIds = userSchoolRelEntities.stream().map(BaseEntity::getId).collect(Collectors.toList());
            List<LeisureActivityCoursesRecordEntity> insertList = new ArrayList<>();
            coursesRecordEntities.forEach(a -> {
                if ((hasClassroomIds.contains(a.getClassroomId()) || a.getClassroomId() == -1L) && hasTeaIds.contains(a.getTeacherId())) {
                    LocalDateTime now = LocalDateTime.now();
                    a.setActivityId(copyEntity.getId());
                    a.setId(null);
                    a.setStatus(0);
                    a.setCreateTime(now);
                    a.setUpdateTime(now);
                    insertList.add(a);
                } else {
                    i.getAndIncrement();
                }
            });
            if (ObjectUtils.isNotEmpty(insertList)) {
                coursesRecordService.saveBatch(insertList);
            }
        }
        return Result.successByMessage(languageUtil.getMessage(LanguageConstants.COURSE_COPY_SUCCESS_WITH_ERROR, i.get()));
    }

    private List<LeisureActivityRecordResModel> getResList(List<LeisureActivityRecordEntity> list) {
        return list.stream().map(a -> {
            LeisureActivityRecordResModel resModel = new LeisureActivityRecordResModel();
            BeanUtils.copyProperties(a, resModel);
            // 活动结束时间是否已过
            if (resModel.getSecondEndTime() != null) {
                if (resModel.getSecondEndTime().before(new Date())) {
                    resModel.setStatus(3);
                }
            } else {
                if (resModel.getEndTime().before(new Date())) {
                    resModel.setStatus(2);
                }
            }
            return resModel;
        }).collect(Collectors.toList());
    }
}

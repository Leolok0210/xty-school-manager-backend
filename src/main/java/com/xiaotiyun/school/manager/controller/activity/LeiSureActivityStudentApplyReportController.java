package com.xiaotiyun.school.manager.controller.activity;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.dto.ActivityStudentApplyReportQueryDTO;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityCoursesRecordEntity;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityRecordEntity;
import com.xiaotiyun.school.manager.model.entity.SemesterEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 活动报名表控制器
 */
@Api(tags = "活动报名管理")
@RestController
@RequestMapping("/api/activity/apply")
public class LeiSureActivityStudentApplyReportController extends BasicController {

    @Autowired
    private ActivityStudentApplyReportService activityStudentApplyReportService;

    @Resource
    private ActivityStudentReportService activityStudentReportService;

    @Resource
    private LeisureActivityCoursesRecordService leisureActivityCoursesRecordService;

    @Resource
    private LeisureActivityRecordService leisureActivityRecordService;

    @Resource
    private SemesterService semesterService;

    @Resource
    private NoticeService noticeService;

    @ApiOperation("一键公布报名结果")
    @PostMapping("/publish")
    @SaCheckPermission("activity:student:apply:publish")
    public Result<Boolean> publish(@Valid @RequestBody ActivityStudentApplyReportPublishReqModel reqModel) {
        Boolean result = activityStudentApplyReportService.publishActivityResult(reqModel);
        if (result) {
            LeisureActivityRecordEntity leisureActivityRecordEntity = leisureActivityRecordService.getById(reqModel.getActivityId());
            if (leisureActivityRecordEntity.getOpenWechatNotice() == 1) {
                noticeService.sendLeisureNotice(leisureActivityRecordEntity, leisureActivityRecordEntity.getPublishStatus());
            }
        }
        return Result.success(result);
    }

    @ApiOperation("获取无课程学生人数")
    @PostMapping("/no-course-count")
    @SaCheckPermission("activity:student:apply:count")
    public Result<Integer> getNoCourseCount(@Valid @RequestBody ActivityStudentApplyReportNoCourseCountReqModel reqModel) {
        Integer count = activityStudentReportService.getNoCourseStudentCount(reqModel.getActivityId());
        return Result.success(count);
    }

    @ApiOperation("获取活动二次报名总人数")
    @PostMapping("/second-count")
    @SaCheckPermission("activity:student:apply:secondCount")
    public Result<Integer> getSecondApplyCount(@Valid @RequestBody ActivityStudentApplyReportSecondCountReqModel reqModel) {
        Integer count = activityStudentApplyReportService.getSecondApplyCount(reqModel);
        return Result.success(count);
    }

    @ApiOperation("发起二次报名")
    @PostMapping("/second-apply")
    @SaCheckPermission("activity:student:apply:secondApply")
    public Result<Boolean> secondApply(@Valid @RequestBody ActivityStudentApplyReportSecondApplyReqModel reqModel) {
        Boolean result = activityStudentApplyReportService.startSecondApply(reqModel);
        return Result.success(result);
    }

    @ApiOperation("活动学生管理列表（无课程列表）")
    @PostMapping("/list")
    @SaCheckPermission("activity:student:apply:list")
    public Result<PageInfo<ActivityStudentApplyReportListResModel>> list(@Valid @RequestBody ActivityStudentApplyReportListReqModel reqModel) {
        PageInfo<ActivityStudentApplyReportListResModel> result = activityStudentApplyReportService.getActivityStudentList(getSchoolId(), getUserId(), reqModel);
        return Result.success(result);
    }

    @ApiOperation("二次报名管理列表")
    @PostMapping("/second-list")
    @SaCheckPermission("activity:student:apply:secondList")
    public Result<PageInfo<ActivityStudentApplyReportSecondListResModel>> secondList(@Valid @RequestBody ActivityStudentApplyReportSecondListReqModel reqModel) {
        // 1. 获取活动信息
        LeisureActivityRecordEntity activity = leisureActivityRecordService.getById(reqModel.getActivityId());
        if (activity == null) {
            return Result.success(new PageInfo<>());
        }

        // 2. 获取学段信息，从而获取学年
        SemesterEntity semester = semesterService.getById(activity.getSemesterId());
        if (semester == null) {
            return Result.success(new PageInfo<>());
        }

        // 3. 调用service方法获取学生列表（分页）
        PageInfo<ActivityStudentApplyReportQueryDTO> studentPageInfo = activityStudentApplyReportService.querySecondApplyAndUnmatchedStudents(
                activity.getSchoolId(),
                activity.getDepartment(),
                semester.getSchoolYear(),
                reqModel.getActivityId(),
                reqModel
        );

        // 4. 收集所有课程ID
        List<Long> courseIds = studentPageInfo.getList().stream()
                .map(ActivityStudentApplyReportQueryDTO::getMatchedCourseId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 5. 批量查询课程信息
        final Map<Long, String> courseNameMap = new HashMap<>();
        if (!courseIds.isEmpty()) {
            List<LeisureActivityCoursesRecordEntity> courses = leisureActivityCoursesRecordService.listByIds(courseIds);
            courseNameMap.putAll(courses.stream()
                    .collect(Collectors.toMap(
                            LeisureActivityCoursesRecordEntity::getId,
                            LeisureActivityCoursesRecordEntity::getName
                    )));
        }

        // 6. 组装返回数据
        List<ActivityStudentApplyReportSecondListResModel> resultList = studentPageInfo.getList().stream()
                .map(student -> {
                    ActivityStudentApplyReportSecondListResModel resModel = new ActivityStudentApplyReportSecondListResModel();
                    resModel.setStudentId(student.getStudentId());
                    resModel.setStudentName(student.getStudentName());
                    resModel.setStudentNo(student.getStudentNo());
                    resModel.setLensonId(student.getMatchedCourseId() != null ? student.getMatchedCourseId().toString() : null);
                    resModel.setLensonName(courseNameMap.get(student.getMatchedCourseId()));
                    resModel.setClassName(student.getClassName());
                    resModel.setGradeGroupName(student.getGradeGroupName());
                    return resModel;
                })
                .collect(Collectors.toList());

        // 7. 构建分页结果
        PageInfo<ActivityStudentApplyReportSecondListResModel> result = new PageInfo<>(resultList);
        result.setPageNum(studentPageInfo.getPageNum());
        result.setPageSize(studentPageInfo.getPageSize());
        result.setTotal(studentPageInfo.getTotal());
        result.setPages(studentPageInfo.getPages());

        return Result.success(result);
    }

    @ApiOperation("报名-学生端")
    @PostMapping("/apply")
    public Result<Boolean> apply(@Valid @RequestBody ActivityStudentApplyReportApplyReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        Boolean result = activityStudentApplyReportService.studentApply(reqModel);
        return Result.success(result);
    }

    @ApiOperation("我的课程列表-学生端")
    @PostMapping("/my-course")
    public Result<List<ActivityStudentApplyReportCurrentActivityResModel>> myCourse(@Valid @RequestBody ActivityStudentApplyReportMyCourseReqModel reqModel) {
        List<ActivityStudentApplyReportCurrentActivityResModel> result = activityStudentApplyReportService.getMyCourseList(reqModel);
        return Result.success(result);
    }

    @ApiOperation("已录取名单")
    @GetMapping("/admitted/list")
    @SaCheckPermission("activity:student:apply:admitted:list")
    public Result<PageInfo<ActivityStudentApplyAdmittedListResModel>> admittedList(@Valid ActivityStudentApplyAdmittedListReqModel reqModel) {
        return Result.success(activityStudentApplyReportService.admittedList(getSchoolId(), getUserId(), reqModel));
    }

    @ApiOperation("已录取名单导出")
    @GetMapping("/admitted/export")
    @SaCheckPermission("activity:student:apply:admitted:export")
    public Result<String> admittedExport(@Valid ActivityStudentApplyAdmittedListReqModel reqModel) {
        return Result.success(activityStudentApplyReportService.admittedExport(getSchoolId(), getUserId(), reqModel));
    }

    @ApiOperation("已报名名单")
    @GetMapping("/registered/list")
    @SaCheckPermission("activity:student:apply:registered:list")
    public Result<PageInfo<ActivityStudentApplyRegisteredListResModel>> registeredList(@Valid ActivityStudentApplyRegisteredListReqModel reqModel) {
        return Result.success(activityStudentApplyReportService.registeredList(getSchoolId(), getUserId(), reqModel));
    }

    @ApiOperation("已报名名单导出")
    @GetMapping("/registered/export")
    @SaCheckPermission("activity:student:apply:registered:export")
    public Result<String> registeredExport(@Valid ActivityStudentApplyRegisteredListReqModel reqModel) {
        return Result.success(activityStudentApplyReportService.registeredExport(getSchoolId(), getUserId(), reqModel));
    }

    @ApiOperation("未报名名单")
    @GetMapping("/notRegistered/list")
    @SaCheckPermission("activity:student:apply:notRegistered:list")
    public Result<PageInfo<ActivityStudentApplyNotRegisteredListResModel>> notRegisteredList(@Valid ActivityStudentApplyNotRegisteredListReqModel reqModel) {
        return Result.success(activityStudentApplyReportService.notRegisteredList(getSchoolId(), getUserId(), reqModel));
    }

    @ApiOperation("未报名名单导出")
    @GetMapping("/notRegistered/export")
    @SaCheckPermission("activity:student:apply:notRegistered:export")
    public Result<String> notRegisteredExport(@Valid ActivityStudentApplyNotRegisteredListReqModel reqModel) {
        return Result.success(activityStudentApplyReportService.notRegisteredExport(getSchoolId(), getUserId(), reqModel));
    }
}
package com.xiaotiyun.school.manager.controller.activity;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityRecordEntity;
import com.xiaotiyun.school.manager.model.entity.UserEntity;
import com.xiaotiyun.school.manager.model.req.ActivityStudentReportListReqModel;
import com.xiaotiyun.school.manager.model.req.ActivityStudentReportRemoveReqModel;
import com.xiaotiyun.school.manager.model.req.ActivityStudentReportTransferReqModel;
import com.xiaotiyun.school.manager.model.req.ImportActivityStudentReportReqModel;
import com.xiaotiyun.school.manager.model.req.ActivityStudentReportExportReqModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentReportListResModel;
import com.xiaotiyun.school.manager.model.res.ImportActivityStudentReportResModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentReportExportResModel;
import com.xiaotiyun.school.manager.service.ActivityStudentReportService;
import com.xiaotiyun.school.manager.service.LeisureActivityRecordService;
import com.xiaotiyun.school.manager.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 活动已匹配表控制器
 */
@Api(tags = "活动匹配管理")
@RestController
@RequestMapping("/api/activity/report")
public class LeiSureActivityStudentReportController {

    @Autowired
    private ActivityStudentReportService activityStudentReportService;

    @Autowired
    private UserService userService;

    @Resource
    private LeisureActivityRecordService leisureActivityRecordService;

    @ApiOperation("导入活动匹配")
    @PostMapping("/import")
    @SaCheckPermission("activity:student:report:import")
    public Result<ImportActivityStudentReportResModel> importActivityStudentReport(
            @ApiParam("Excel文件") @RequestPart("uploadFile") MultipartFile uploadFile,
            @ApiParam("活动ID") @RequestParam("activityId") Long activityId,
            @ApiParam("课程ID") @RequestParam("lensonId") Long lensonId,
            @ApiParam("学校ID") @RequestParam("schoolId") Long schoolId) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        UserEntity currentUser = userService.getById(currentUserId);
        String operatorName = currentUser != null ? currentUser.getUsername() : "未知用户";
        
        ImportActivityStudentReportReqModel reqModel = new ImportActivityStudentReportReqModel();
        reqModel.setUploadFile(uploadFile);
        reqModel.setActivityId(activityId);
        reqModel.setLensonId(lensonId);
        reqModel.setSchoolId(schoolId);
        reqModel.setUserId(currentUserId);
        reqModel.setUsername(operatorName);
        
        return Result.success(activityStudentReportService.importActivityStudentReport(reqModel));
    }

    @ApiOperation("活动匹配列表")
    @PostMapping("/list")
    @SaCheckPermission("activity:student:report:list")
    public Result<PageInfo<ActivityStudentReportListResModel>> list(@Valid @RequestBody ActivityStudentReportListReqModel reqModel) {
        return Result.success(activityStudentReportService.getActivityStudentReportList(reqModel));
    }

    @ApiOperation("批量移除活动匹配")
    @PostMapping("/remove")
    @SaCheckPermission("activity:student:report:remove")
    public Result<Boolean> remove(@Valid @RequestBody ActivityStudentReportRemoveReqModel reqModel) {
        return Result.success(activityStudentReportService.batchRemoveActivityStudentReport(reqModel));
    }

    @ApiOperation("转课程")
    @PostMapping("/transfer")
    @SaCheckPermission("activity:student:report:transfer")
    public Result<Boolean> transfer(@Valid @RequestBody List<ActivityStudentReportTransferReqModel> reqModel) {
        return Result.success(activityStudentReportService.transferCourse(reqModel));
    }

    @ApiOperation("导出活动匹配数据")
    @PostMapping("/export")
    @SaCheckPermission("activity:student:report:export")
    public Result<ActivityStudentReportExportResModel> export(@Valid @RequestBody ActivityStudentReportExportReqModel reqModel) {
        return Result.success(activityStudentReportService.exportActivityStudentReport(reqModel));
    }

    @ApiOperation("活动报名结果匹配--定时任务")
    @GetMapping("/leisureActivityTask")
    public Result<Boolean> leisureActivityTask(@RequestParam Long activityId) {
        LeisureActivityRecordEntity recordEntity = leisureActivityRecordService.getById(activityId);
        activityStudentReportService.processActivity(recordEntity,false);
        return Result.success();
    }
} 
package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.model.req.StudentAttendancePageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceReportReqModel;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceStatisticsReqModel;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendancePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendanceReportResModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendanceStatisticsResModel;
import com.xiaotiyun.school.manager.service.StudentAttendanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "学生出勤情况管理")
@RequestMapping("/api/student/attendance")
@RequiredArgsConstructor
public class StudentAttendanceController extends BasicController {
    private final StudentAttendanceService studentAttendanceService;

    private final LanguageUtil languageUtil;

    @GetMapping("/page")
    @ApiOperation(value = "分页查询")
    @SaCheckPermission("studentAttendance:page")
    public Result<PageInfo<StudentAttendancePageResModel>> page(
            @ApiParam(value = "分页查询参数", required = true)
            @Validated StudentAttendancePageReqModel reqModel) {
        reqModel.setUserId(getUserId());
        return Result.success(studentAttendanceService.page(reqModel));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "更新学生出勤情况")
    @SaCheckPermission("studentAttendance:update")
    public Result<Void> update(
            @ApiParam(value = "记录ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "修改参数", required = true)
            @Valid @RequestBody StudentAttendanceUpdateReqModel reqModel) {
        studentAttendanceService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除学生出勤情况")
    @SaCheckPermission("studentAttendance:delete")
    public Result<Void> delete(@PathVariable Long id) {
        studentAttendanceService.delete(id);
        return Result.success();
    }

    @ApiOperation("导入记录")
    @PostMapping("/import")
    @SaCheckPermission("studentAttendance:import")
    public Result<Long> importRecord(
            @ApiParam("Excel文件") @RequestPart("uploadFile") MultipartFile file,
            @ApiParam("学校id") @RequestParam Long schoolId) {
        try {
            Long importId = studentAttendanceService.importRecord(schoolId, file);
            return Result.success(importId);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" + e.getMessage());
        }
    }

    @ApiOperation("导出记录")
    @GetMapping("/export")
    @SaCheckPermission("studentAttendance:export")
    public Result<String> export(@Validated StudentAttendancePageReqModel reqModel) {
        return Result.success(studentAttendanceService.export(reqModel));
    }

    @ApiOperation("考勤统计")
    @GetMapping("/statistics")
    @SaCheckPermission("studentAttendance:statistics")
    public Result<List<StudentAttendanceStatisticsResModel>> statistics(@Validated StudentAttendanceStatisticsReqModel reqModel) {
        return Result.success(studentAttendanceService.statistics(reqModel));
    }

    @ApiOperation("学生出勤报表")
    @GetMapping("/report")
    @SaCheckPermission("studentAttendance:report")
    public Result<List<StudentAttendanceReportResModel>> report(@Validated StudentAttendanceReportReqModel reqModel) {
        return Result.success(studentAttendanceService.report(getSchoolId(), reqModel));
    }

    @ApiOperation("学生出勤报表导出")
    @GetMapping("/report/export")
    @SaCheckPermission("studentAttendance:report:export")
    public Result<String> reportExport(@Validated StudentAttendanceReportReqModel reqModel) {
        return Result.success(studentAttendanceService.reportExport(getSchoolId(), reqModel));
    }
}
package com.xiaotiyun.school.manager.controller.teacher;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.model.req.TeacherAttendancePageReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherAttendanceStatisticsReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherAttendanceUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.TeacherAttendancePageResModel;
import com.xiaotiyun.school.manager.model.res.TeacherAttendanceStatisticsResModel;
import com.xiaotiyun.school.manager.service.TeacherAttendanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/attendance")
@RequiredArgsConstructor
@Api(tags = "教师考勤管理")
public class TeacherAttendanceController extends BasicController {
    private final TeacherAttendanceService teacherAttendanceService;

    private final LanguageUtil languageUtil;
    @GetMapping("/page")
    @SaCheckPermission("teacherAttendance:page")
    @ApiOperation("分页查询考勤记录")
    public Result<PageInfo<TeacherAttendancePageResModel>> page(
            @Validated TeacherAttendancePageReqModel reqModel) {
        return Result.success(teacherAttendanceService.page(reqModel));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("teacherAttendance:update")
    @ApiOperation("修改考勤记录")
    public Result<Void> update(
            @ApiParam(value = "记录ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "修改参数", required = true)
            @Validated @RequestBody TeacherAttendanceUpdateReqModel reqModel) {
        teacherAttendanceService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("teacherAttendance:delete")
    @ApiOperation("删除考勤记录")
    public Result<Void> delete(@PathVariable Long id) {
        teacherAttendanceService.delete(id);
        return Result.success();
    }

    @ApiOperation("导入记录")
    @PostMapping("/import")
    @SaCheckPermission("teacherAttendance:import")
    public Result<Long> importRecord(
            @ApiParam("Excel文件") @RequestPart("uploadFile") MultipartFile file,
            @ApiParam("学校id") @RequestParam Long schoolId) {
        try {
            Long importId = teacherAttendanceService.importRecord(schoolId, file);
            return Result.success(importId);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" +  e.getMessage());
        }
    }

    @GetMapping("/export")
    @SaCheckPermission("teacherAttendance:export")
    @ApiOperation("导出记录")
    public Result<String> export(@Validated TeacherAttendancePageReqModel reqModel) {
        return Result.success(teacherAttendanceService.export(reqModel));
    }

    @GetMapping("/statistics")
    @SaCheckPermission("teacherAttendance:statistics")
    @ApiOperation("首页工作台-考勤统计")
    public Result<List<TeacherAttendanceStatisticsResModel>> statistics(@Validated TeacherAttendanceStatisticsReqModel reqModel) {
        return Result.success(teacherAttendanceService.statistics(reqModel));
    }
}
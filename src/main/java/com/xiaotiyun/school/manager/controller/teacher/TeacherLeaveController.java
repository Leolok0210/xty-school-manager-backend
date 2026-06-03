package com.xiaotiyun.school.manager.controller.teacher;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.TeacherLeavePageResModel;
import com.xiaotiyun.school.manager.model.res.TeacherLeaveReportResModel;
import com.xiaotiyun.school.manager.service.TeacherLeaveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teacher/leave")
@Api(tags = "教师请假管理")
public class TeacherLeaveController extends BasicController {
    private final TeacherLeaveService teacherLeaveService;

    @GetMapping("/page")
    @ApiOperation("分页查询请假记录")
    @SaCheckPermission("teacherLeave:page")
    public Result<PageInfo<TeacherLeavePageResModel>> page(
            @Validated TeacherLeavePageReqModel reqModel) {
        return Result.success(teacherLeaveService.page(reqModel));
    }

    @GetMapping("/get/pending")
    @ApiOperation("获取学校待审批数量")
    @SaCheckPermission("teacherLeave:getPending")
    public Result<Long> getPending() {
        return Result.success(teacherLeaveService.getPendingApproval(getSchoolId()));
    }

    @PostMapping
    @ApiOperation("新增请假记录")
    @SaCheckPermission("teacherLeave:add")
    public Result<Void> save(@Valid @RequestBody TeacherLeaveSaveReqModel reqModel) {
        teacherLeaveService.save(reqModel);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改请假记录")
    @SaCheckPermission("teacherLeave:update")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody TeacherLeaveSaveReqModel reqModel) {
        teacherLeaveService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除请假记录")
    @SaCheckPermission("teacherLeave:delete")
    public Result<Void> delete(@PathVariable Long id) {
        teacherLeaveService.delete(id);
        return Result.success();
    }


    @GetMapping("/export")
    @SaCheckPermission("teacherLeave:export")
    @ApiOperation("导出记录")
    public Result<String> export(@Validated TeacherLeavePageReqModel reqModel) {
        return Result.success(teacherLeaveService.export(reqModel));
    }

    @PostMapping("/handle")
    @SaCheckPermission("teacherLeave:handle")
    @ApiOperation("处理请假记录")
    public Result<Void> handle(@Validated @RequestBody TeacherLeaveHandleReqModel reqModel){
        teacherLeaveService.handle(reqModel);
        return Result.success();
    }

    @GetMapping("/report")
    @SaCheckPermission("teacherLeave:report")
    @ApiOperation("教师出勤报表")
    public Result<List<TeacherLeaveReportResModel>> report(@Validated TeacherLeaveReportReqModel reqModel){
        reqModel.setSchoolId(getSchoolId());
        return Result.success(teacherLeaveService.report(reqModel));
    }

    @GetMapping("/report/export")
    @SaCheckPermission("teacherLeave:reportExport")
    @ApiOperation("教师出勤报表-导出")
    public ResponseEntity<byte[]> reportExport(@Validated TeacherLeaveReportReqModel reqModel) throws UnsupportedEncodingException {
        reqModel.setSchoolId(getSchoolId());
        return teacherLeaveService.reportExport(reqModel);
    }

    @PostMapping("/start")
    @ApiOperation("发起请假审批")
    @SaCheckPermission("teacher:leave:start")
    public Result<Void> start(@Valid @RequestBody TeacherLeaveStartReqModel reqModel) {
        teacherLeaveService.start(getSchoolId(), getUserId(), reqModel);
        return Result.success();
    }
}
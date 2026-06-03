package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentLeavePageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentLeaveSaveAdminReqModel;
import com.xiaotiyun.school.manager.model.req.StudentLeaveSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentLeaveUpdateAdminReqModel;
import com.xiaotiyun.school.manager.model.res.StudentLeavePageResModel;
import com.xiaotiyun.school.manager.service.StudentLeaveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/leave")
@RequiredArgsConstructor
@Api(tags = "学生请假缺席管理")
public class StudentLeaveController extends BasicController {
    private final StudentLeaveService studentLeaveService;

    @GetMapping("/page")
    @SaCheckPermission("studentLeave:page")
    @ApiOperation("分页查询请假记录")
    public Result<PageInfo<StudentLeavePageResModel>> page(
            @Validated StudentLeavePageReqModel reqModel) {
        reqModel.setUserId(getUserId());
        return Result.success(studentLeaveService.page(reqModel));
    }

    @GetMapping("/student/page")
    @ApiOperation("分页查询请假记录-学生端(非鉴权)")
    public Result<PageInfo<StudentLeavePageResModel>> studentPage(
            @Validated StudentLeavePageReqModel reqModel) {
        return Result.success(studentLeaveService.page(reqModel));
    }

    @GetMapping("/teacher/page")
    @ApiOperation("分页查询请假记录-教师端")
    @SaCheckPermission("studentLeave:teacherPage")
    public Result<PageInfo<StudentLeavePageResModel>> teacherPage(
            @Validated StudentLeavePageReqModel reqModel) {
        return Result.success(studentLeaveService.teacherPage(reqModel));
    }

    @GetMapping("/images/{id}")
    @SaCheckPermission("studentLeave:images")
    @ApiOperation("查询图片")
    public Result<List<String>> getImages(@PathVariable Long id) {
        return Result.success(studentLeaveService.getImages(id));
    }

    @GetMapping("/student/images/{id}")
    @ApiOperation("查询图片-学生端(非鉴权)")
    public Result<List<String>> getImagesByStudent(@PathVariable Long id) {
        return Result.success(studentLeaveService.getImages(id));
    }

    @PostMapping
    @SaCheckPermission("studentLeave:add")
    @ApiOperation("新增请假记录")
    public Result<Void> save(@Validated @RequestBody StudentLeaveSaveAdminReqModel reqModel) {
        studentLeaveService.save(reqModel);
        return Result.success();
    }

    @PostMapping("/student/add")
    @ApiOperation("新增请假记录-学生端(非鉴权)")
    public Result<Void> saveByStudent(@Validated @RequestBody StudentLeaveSaveReqModel reqModel) {
        studentLeaveService.saveByStudent(reqModel);
        return Result.success();
    }

    @PutMapping("/{id}")
    @SaCheckPermission("studentLeave:edit")
    @ApiOperation("修改请假记录")
    public Result<Void> update(
            @ApiParam(value = "记录ID", required = true)
            @PathVariable Long id,
            @Validated @RequestBody StudentLeaveUpdateAdminReqModel reqModel) {
        studentLeaveService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("studentLeave:delete")
    @ApiOperation("删除请假记录")
    public Result<Void> delete(@PathVariable Long id) {
        studentLeaveService.delete(id);
        return Result.success();
    }

    @PutMapping("student/{id}")
    @ApiOperation("修改请假记录-学生端(非鉴权)")
    public Result<Void> updateByStudent(
            @ApiParam(value = "记录ID", required = true)
            @PathVariable Long id,
            @Validated @RequestBody StudentLeaveSaveReqModel reqModel) {
        studentLeaveService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("student/{id}")
    @ApiOperation("删除请假记录-学生端(非鉴权)")
    public Result<Void> deleteByStudent(@PathVariable Long id) {
        studentLeaveService.delete(id);
        return Result.success();
    }

    @GetMapping("/export")
    @SaCheckPermission("studentLeave:export")
    @ApiOperation("导出Excel")
    public Result<String> export(@ApiParam("导出查询参数") @Validated StudentLeavePageReqModel reqModel) {
        return Result.success(studentLeaveService.export(reqModel));
    }
} 
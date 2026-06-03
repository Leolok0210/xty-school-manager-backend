package com.xiaotiyun.school.manager.controller.teacher;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.TeacherBusinessPageReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherBusinessSaveReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherBusinessStartReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherLeaveStartReqModel;
import com.xiaotiyun.school.manager.model.res.TeacherBusinessPageResModel;
import com.xiaotiyun.school.manager.service.TeacherBusinessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teacher/business")
@Api(tags = "教师公务管理")
public class TeacherBusinessController extends BasicController {
    private final TeacherBusinessService teacherBusinessService;

    @GetMapping("/page")
    @ApiOperation("分页查询公务记录")
    @SaCheckPermission("teacherBusiness:page")
    public Result<PageInfo<TeacherBusinessPageResModel>> page(
            @Validated TeacherBusinessPageReqModel reqModel) {
        return Result.success(teacherBusinessService.page(reqModel));
    }

    @PostMapping
    @ApiOperation("新增公务记录")
    @SaCheckPermission("teacherBusiness:add")
    public Result<Void> save(@Valid @RequestBody TeacherBusinessSaveReqModel reqModel) {
        teacherBusinessService.save(reqModel);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改公务记录")
    @SaCheckPermission("teacherBusiness:update")
    public Result<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody TeacherBusinessSaveReqModel reqModel) {
        teacherBusinessService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除公务记录")
    @SaCheckPermission("teacherBusiness:delete")
    public Result<Void> delete(@PathVariable Long id) {
        teacherBusinessService.delete(id);
        return Result.success();
    }

    @GetMapping("/export")
    @ApiOperation("导出公务记录")
    @SaCheckPermission("teacherBusiness:export")
    public Result<String> exportBusiness(@Validated TeacherBusinessPageReqModel reqModel) {
        return Result.success(teacherBusinessService.export(reqModel));
    }

    @PostMapping("/start")
    @ApiOperation("发起公务审批")
    @SaCheckPermission("teacher:business:start")
    public Result<Void> start(@Valid @RequestBody TeacherBusinessStartReqModel reqModel) {
        teacherBusinessService.start(getSchoolId(), getUserId(), reqModel);
        return Result.success();
    }
} 
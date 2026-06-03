package com.xiaotiyun.school.manager.controller.teacher;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.TeacherAttendanceRulePageReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherAttendanceRuleSaveReqModel;
import com.xiaotiyun.school.manager.model.res.TeacherAttendanceRulePageResModel;
import com.xiaotiyun.school.manager.service.TeacherAttendanceRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/teacher/attendance/rule")
@RequiredArgsConstructor
@Api(tags = "教师考勤规则管理")
public class TeacherAttendanceRuleController {
    private final TeacherAttendanceRuleService teacherAttendanceRuleService;

    @ApiOperation("查询学校下考勤规则已选择用户id")
    @GetMapping("/selectedUserIds/{schoolId}")
    @SaCheckPermission("teacherAttendanceRule:selectedUserIds")
    public Result<List<Long>> selectedUserIds(@ApiParam(value = "学校id", required = true)
                                              @PathVariable Long schoolId) {
        return Result.success(teacherAttendanceRuleService.selectedUserIds(schoolId));
    }

    @ApiOperation("分页查询")
    @GetMapping("/page")
    @SaCheckPermission("teacherAttendanceRule:page")
    public Result<PageInfo<TeacherAttendanceRulePageResModel>> page(
            @ApiParam(value = "分页查询参数", required = true)
            @Validated TeacherAttendanceRulePageReqModel reqModel) {
        return Result.success(teacherAttendanceRuleService.page(reqModel));
    }

    @PostMapping
    @SaCheckPermission("teacherAttendanceRule:add")
    @ApiOperation("新增考勤规则")
    public Result<Void> save(@Valid @RequestBody TeacherAttendanceRuleSaveReqModel reqModel) {
        teacherAttendanceRuleService.save(reqModel);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改出勤规则")
    @SaCheckPermission("teacherAttendanceRule:update")
    public Result<Void> update(
            @ApiParam(value = "记录ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "修改参数", required = true)
            @Valid @RequestBody TeacherAttendanceRuleSaveReqModel reqModel) {
        teacherAttendanceRuleService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除出勤规则")
    @SaCheckPermission("teacherAttendanceRule:delete")
    public Result<Void> delete(@PathVariable Long id) {
        teacherAttendanceRuleService.delete(id);
        return Result.success();
    }
} 
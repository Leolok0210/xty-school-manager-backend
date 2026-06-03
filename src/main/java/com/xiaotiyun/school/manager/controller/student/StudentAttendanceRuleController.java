package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceRulePageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceRuleSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendanceRulePageResModel;
import com.xiaotiyun.school.manager.service.StudentAttendanceRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "学生出勤规则管理")
@RequiredArgsConstructor
@RequestMapping("/api/student/attendance/rule")
public class StudentAttendanceRuleController extends BasicController {
    private final StudentAttendanceRuleService studentAttendanceRuleService;

    @ApiOperation("查询学校下考勤规则已选择级组id")
    @GetMapping("/selectedGrades/{schoolId}")
    @SaCheckPermission("studentAttendanceRule:selectedGrades")
    public Result<List<Long>> selectedGrades(@ApiParam(value = "学校id", required = true)
                                             @PathVariable Long schoolId) {
        return Result.success(studentAttendanceRuleService.selectedGrades(schoolId));
    }

    @ApiOperation("分页查询")
    @GetMapping("/page")
    @SaCheckPermission("studentAttendanceRule:page")
    public Result<PageInfo<StudentAttendanceRulePageResModel>> page(
            @ApiParam(value = "分页查询参数", required = true)
            @Validated StudentAttendanceRulePageReqModel reqModel) {
        return Result.success(studentAttendanceRuleService.page(reqModel));
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增出勤规则")
    @SaCheckPermission("studentAttendanceRule:add")
    public Result<Void> save(@Valid @RequestBody StudentAttendanceRuleSaveReqModel reqModel) {
        studentAttendanceRuleService.save(reqModel);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改出勤规则")
    @SaCheckPermission("studentAttendanceRule:update")
    public Result<Void> update(
            @ApiParam(value = "记录ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "修改参数", required = true)
            @Valid @RequestBody StudentAttendanceRuleSaveReqModel reqModel) {
        studentAttendanceRuleService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除出勤规则")
    @SaCheckPermission("studentAttendanceRule:delete")
    public Result<Void> delete(@PathVariable Long id) {
        studentAttendanceRuleService.delete(id);
        return Result.success();
    }
}
package com.xiaotiyun.school.manager.controller.school;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.CourseScheduleClassListResModel;
import com.xiaotiyun.school.manager.model.res.CourseScheduleHomeClassListResModel;
import com.xiaotiyun.school.manager.model.res.CourseScheduleTeacherListResModel;
import com.xiaotiyun.school.manager.service.CourseScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course/schedules")
@Api(tags = "课表管理")
public class CourseScheduleController extends BasicController {
    private final CourseScheduleService courseScheduleService;

    @PostMapping
    @SaCheckPermission("course:schedule:add")
    @ApiOperation("新增课表")
    public Result<Void> add(@Validated @RequestBody CourseScheduleSaveReqModel reqModel) {
        courseScheduleService.add(getSchoolId(), reqModel);
        return Result.success();
    }

    @PutMapping("/{id}")
    @SaCheckPermission("course:schedule:update")
    @ApiOperation("修改课表")
    public Result<Void> update(@PathVariable Long id,
                               @Validated @RequestBody CourseScheduleUpdateReqModel reqModel) {
        courseScheduleService.update(id, reqModel);
        return Result.success();
    }

    @PostMapping("/delete")
    @SaCheckPermission("course:schedule:delete")
    @ApiOperation("删除课表")
    public Result<Void> delete(@Validated @RequestBody CourseScheduleDeleteReqModel reqModel) {
        courseScheduleService.delete(reqModel);
        return Result.success();
    }

    @PostMapping("/copy")
    @SaCheckPermission("course:schedule:copy")
    @ApiOperation("复制到其他周次")
    public Result<Void> copyToWeeks(@Validated @RequestBody CourseScheduleCopyReqModel reqModel) {
        courseScheduleService.copyToWeeks(getSchoolId(), reqModel);
        return Result.success();
    }

    @GetMapping("/class/list")
    @SaCheckPermission("course:schedule:class:list")
    @ApiOperation("班级课表")
    public Result<List<CourseScheduleClassListResModel>> classList(@Validated CourseScheduleClassListReqModel reqModel) {
        return Result.success(courseScheduleService.classList(getSchoolId(), getUserId(), reqModel));
    }

    @GetMapping("/student/class/list")
    @ApiOperation("班级课表-学生端(非鉴权)")
    public Result<List<CourseScheduleClassListResModel>> classListByStudent(@Validated CourseScheduleClassListReqModel reqModel) {
        return Result.success(courseScheduleService.classListByStudent(getSchoolId(), reqModel));
    }

    @GetMapping("/class/query")
    @SaCheckPermission("course:schedule:class:query")
    @ApiOperation("班级课表查询")
    public Result<List<CourseScheduleClassListResModel>> classQuery(@Validated CourseScheduleClassQueryReqModel reqModel) {
        return Result.success(courseScheduleService.classQuery(getSchoolId(), getUserId(), reqModel));
    }

    @GetMapping("/class/home/list")
    @SaCheckPermission("course:schedule:home:class:list")
    @ApiOperation("首页班级课表")
    public Result<List<CourseScheduleHomeClassListResModel>> homeClassList(@Validated CourseScheduleHomeClassListReqModel reqModel) {
        return Result.success(courseScheduleService.homeClassList(getSchoolId(), getUserId(), reqModel));
    }

    @GetMapping("/teacher/list")
    @SaCheckPermission("course:schedule:teacher:list")
    @ApiOperation("教师课表")
    public Result<List<CourseScheduleTeacherListResModel>> teacherList(@Validated CourseScheduleTeacherListReqModel reqModel) {
        return Result.success(courseScheduleService.teacherList(getSchoolId(), reqModel));
    }
}
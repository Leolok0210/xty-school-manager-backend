package com.xiaotiyun.school.manager.controller.school;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.LessonCopyReqModel;
import com.xiaotiyun.school.manager.model.req.LessonListReqModel;
import com.xiaotiyun.school.manager.model.req.LessonSaveReqModel;
import com.xiaotiyun.school.manager.model.res.LessonResModel;
import com.xiaotiyun.school.manager.service.LessonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson")
@RequiredArgsConstructor
@Api(tags = "课节设置")
public class LessonController extends BasicController {
    private final LessonService lessonService;

    @GetMapping("/grade/list")
    @ApiOperation("已设置课节的级组列表")
    @SaCheckPermission("lesson:grade:list")
    public Result<List<Long>> gradeList() {
        return Result.success(lessonService.gradeList(getSchoolId()));
    }

    @PostMapping
    @ApiOperation("新增课节")
    @SaCheckPermission("lesson:add")
    public Result<Long> add(@Validated @RequestBody LessonSaveReqModel reqModel) {
        return Result.success(lessonService.add(getSchoolId(), reqModel));
    }

    @PutMapping("/{id}")
    @ApiOperation("修改课节")
    @SaCheckPermission("lesson:update")
    public Result<Void> update(@PathVariable Long id,
                               @Validated @RequestBody LessonSaveReqModel reqModel) {
        lessonService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除课节")
    @SaCheckPermission("lesson:delete")
    public Result<Void> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return Result.success();
    }

    @GetMapping
    @ApiOperation("课节列表")
    @SaCheckPermission("lesson:list")
    public Result<List<LessonResModel>> list(@Validated LessonListReqModel reqModel) {
        return Result.success(lessonService.list(getSchoolId(), reqModel));
    }

    @GetMapping("/student/list")
    @ApiOperation("课节列表-学生端(非鉴权)")
    public Result<List<LessonResModel>> listByStudent(@Validated LessonListReqModel reqModel) {
        return Result.success(lessonService.list(getSchoolId(), reqModel));
    }

    @PostMapping("/copy")
    @ApiOperation("复制")
    @SaCheckPermission("lesson:copy")
    public Result<Void> copy(@Validated @RequestBody LessonCopyReqModel reqModel) {
        lessonService.copy(getSchoolId(), reqModel);
        return Result.success();
    }
}
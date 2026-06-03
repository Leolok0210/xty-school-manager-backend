package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.StudentUsuallyScoreService;
import com.xiaotiyun.school.manager.service.StudentUsuallyTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "平时成绩管理")
@RestController
@RequestMapping("/api/student/usually")
public class StudentUsuallyTaskController extends BasicController {
    @Resource
    private StudentUsuallyTaskService studentUsuallyTaskService;
    @Resource
    private StudentUsuallyScoreService studentUsuallyScoreService;

    @ApiOperation("分页查询列表")
    @GetMapping("/page")
    @SaCheckPermission("studentUsually:page")
    public Result<PageInfo<StudentUsuallyTaskPageResModel>> page(@ApiParam("查询参数") @Validated StudentUsuallyPageReqModel reqModel) {
        reqModel.setUserId(getUserId());
        return Result.success(studentUsuallyTaskService.page(reqModel));
    }

    @ApiOperation("新增平时分记录")
    @PostMapping("/add")
    @SaCheckPermission("studentUsually:add")
    public Result<Void> save(@ApiParam("平时分记录信息") @Validated @RequestBody StudentUsuallyTaskSaveReqModel reqModel) {
        studentUsuallyTaskService.save(reqModel);
        return Result.success();
    }

    @ApiOperation("修改平时分记录")
    @PutMapping("/update/{id}")
    @SaCheckPermission("studentUsually:update")
    public Result<Void> update(
            @ApiParam("id") @PathVariable Long id,
            @ApiParam("平时分记录信息") @Validated @RequestBody StudentUsuallyTaskSaveReqModel reqModel) {
        studentUsuallyTaskService.update(id, reqModel);
        return Result.success();
    }

    @ApiOperation("获取平时分记录信息")
    @GetMapping("/info/{id}")
    @SaCheckPermission("studentUsually:info")
    public Result<StudentUsuallyTaskResModel> info(
            @ApiParam("id") @PathVariable Long id) {
        return Result.success(studentUsuallyTaskService.info(id));
    }

    @ApiOperation("删除平时分记录信息")
    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("studentUsually:delete")
    public Result<Void> delete(@ApiParam("id") @PathVariable Long id) {
        studentUsuallyTaskService.delete(id);
        return Result.success();
    }

    @ApiOperation(value = "检查讯息", notes = "请求结果返回创建人姓名，有结果时表示已被创建人创建，无结果时可创建")
    @GetMapping("/check")
    @SaCheckPermission("studentUsually:check")
    public Result<String> check(@Validated StudentUsuallyTaskCheckReqModel reqModel) {
        return Result.success(studentUsuallyTaskService.check(getSchoolId(), reqModel));
    }

    @ApiOperation("获取成绩列表")
    @GetMapping("/score/page")
    @SaCheckPermission("studentUsually:page")
    public Result<PageInfo<StudentUsuallyScorePageResModel>> scorePage(@ApiParam("查询参数") @Validated StudentUsuallyScorePageReqModel reqModel) {
        return Result.success(studentUsuallyScoreService.page(reqModel));
    }

    @ApiOperation("平时成绩查看-学生端(非鉴权)")
    @GetMapping("/student/score/page")
    public Result<PageInfo<StudentUsuallyScoreResModel>> scorePageByStudent(@Validated StudentUsuallyScoreReqModel reqModel) {
        return Result.success(studentUsuallyScoreService.pageByStudent(reqModel));
    }

    @ApiOperation("新增成绩")
    @PostMapping("/score/add")
    @SaCheckPermission("studentUsually:scoreAdd")
    public Result<Void> scoreAdd(@ApiParam("成绩信息") @Validated @RequestBody StudentUsuallyScoreSaveReqModel reqModel) {
        studentUsuallyScoreService.save(reqModel);
        return Result.success();
    }

    @ApiOperation("修改成绩")
    @PutMapping("/score/update/{id}")
    @SaCheckPermission("studentUsually:scoreUpdate")
    public Result<Void> scoreUpdate(
            @ApiParam("id") @PathVariable Long id,
            @ApiParam("成绩信息") @Validated @RequestBody StudentUsuallyScoreUpdateReqModel reqModel) {
        studentUsuallyScoreService.update(id, reqModel);
        return Result.success();
    }

    @ApiOperation("删除成绩")
    @DeleteMapping("/score/delete/{id}")
    @SaCheckPermission("studentUsually:scoreDelete")
    public Result<Void> scoreDelete(@ApiParam("id") @PathVariable Long id) {
        studentUsuallyScoreService.delete(id);
        return Result.success();
    }

    @ApiOperation("成绩检查")
    @GetMapping("/score/check")
    @SaCheckPermission("studentUsually:scoreCheck")
    public Result<List<StudentUsuallyScoreCheckResModel>> scoreCheck(@ApiParam("查询参数") @Validated StudentUsuallyScoreCheckReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        reqModel.setUserId(getUserId());
        return Result.success(studentUsuallyTaskService.scoreCheck(reqModel));
    }

    @ApiOperation("成绩分析")
    @GetMapping("/score/analysis")
    @SaCheckPermission("studentUsually:scoreAnalysis")
    public Result<List<StudentUsuallyScoreAnalysisResModel>> scoreAnalysis(@ApiParam("查询参数") @Validated StudentUsuallyScoreAnalysisReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        reqModel.setUserId(getUserId());
        return Result.success(studentUsuallyTaskService.scoreAnalysis(reqModel));
    }

    @ApiOperation(value = "检查是否存在平时任务",notes = "返回true表示存在，返回false表示不存在")
    @GetMapping("/check/task")
    @SaCheckPermission("studentUsually:scoreAnalysis")
    public Result<Boolean> taskCheck() {
        return Result.success(studentUsuallyTaskService.checkTask(getSchoolId()));
    }
}
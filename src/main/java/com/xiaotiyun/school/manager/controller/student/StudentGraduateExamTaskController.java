package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.StudentGraduateExamScorePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateExamTaskPageResModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateExamTaskResModel;
import com.xiaotiyun.school.manager.service.StudentGraduateExamScoreService;
import com.xiaotiyun.school.manager.service.StudentGraduateExamTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "毕业考试成绩管理")
@RestController
@RequestMapping("/api/student/graduate/exam")
public class StudentGraduateExamTaskController extends BasicController{
    @Resource
    private StudentGraduateExamTaskService studentGraduateExamTaskService;
    @Resource
    private StudentGraduateExamScoreService studentGraduateExamScoreService;

    @ApiOperation("分页查询列表")
    @GetMapping("/page")
    @SaCheckPermission("studentGraduateExam:page")
    public Result<PageInfo<StudentGraduateExamTaskPageResModel>> page(@ApiParam("查询参数") @Validated StudentGraduateExamPageReqModel reqModel) {
        reqModel.setUserId(getUserId());
        return Result.success(studentGraduateExamTaskService.page(reqModel));
    }

    @ApiOperation("新增")
    @PostMapping("/add")
    @SaCheckPermission("studentGraduateExam:add")
    public Result<Void> save(@ApiParam("毕业考试记录信息") @Validated @RequestBody StudentGraduateExamTaskSaveReqModel reqModel) {
        studentGraduateExamTaskService.save(reqModel);
        return Result.success();
    }

    @ApiOperation("修改")
    @PutMapping("/update/{id}")
    @SaCheckPermission("studentGraduateExam:update")
    public Result<Void> update(
            @ApiParam("id") @PathVariable Long id,
            @ApiParam("毕业考试记录信息") @Validated @RequestBody StudentGraduateExamTaskSaveReqModel reqModel) {
        studentGraduateExamTaskService.update(id, reqModel);
        return Result.success();
    }

    @ApiOperation("获取信息")
    @GetMapping("/info/{id}")
    @SaCheckPermission("studentGraduateExam:info")
    public Result<StudentGraduateExamTaskResModel> info(
            @ApiParam("id") @PathVariable Long id) {
        return Result.success(studentGraduateExamTaskService.info(id));
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("studentGraduateExam:delete")
    public Result<Void> delete(@ApiParam("id") @PathVariable Long id) {
        studentGraduateExamTaskService.delete(id);
        return Result.success();
    }

    @ApiOperation("获取成绩列表")
    @GetMapping("/score/page")
    @SaCheckPermission("studentGraduateExam:page")
    public Result<PageInfo<StudentGraduateExamScorePageResModel>> scorePage(@ApiParam("查询参数") @Validated StudentGraduateExamScorePageReqModel reqModel) {
        return Result.success(studentGraduateExamScoreService.page(reqModel));
    }

    @ApiOperation("新增成绩")
    @PostMapping("/score/add")
    @SaCheckPermission("studentGraduateExam:scoreAdd")
    public Result<Void> scoreAdd(@ApiParam("成绩信息") @Validated @RequestBody StudentGraduateExamScoreSaveReqModel reqModel) {
        studentGraduateExamTaskService.scoreAdd(reqModel);
        return Result.success();
    }

    @ApiOperation("修改成绩")
    @PutMapping("/score/update/{id}")
    @SaCheckPermission("studentGraduateExam:scoreUpdate")
    public Result<Void> scoreUpdate(
            @ApiParam("id") @PathVariable Long id,
            @ApiParam("成绩信息") @Validated @RequestBody StudentGraduateExamScoreUpdateReqModel reqModel) {
        studentGraduateExamScoreService.update(id, reqModel);
        return Result.success();
    }

    @ApiOperation("删除成绩")
    @DeleteMapping("/score/delete/{id}")
    @SaCheckPermission("studentGraduateExam:scoreDelete")
    public Result<Void> scoreDelete(@ApiParam("id") @PathVariable Long id) {
        studentGraduateExamScoreService.delete(id);
        return Result.success();
    }
}
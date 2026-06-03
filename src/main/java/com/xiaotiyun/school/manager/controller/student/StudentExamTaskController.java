package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.StudentExamScoreService;
import com.xiaotiyun.school.manager.service.StudentExamTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "考试成绩管理")
@RestController
@RequestMapping("/api/student/exam")
public class StudentExamTaskController extends BasicController {
    @Resource
    private StudentExamTaskService studentExamTaskService;
    @Resource
    private StudentExamScoreService studentExamScoreService;

    @ApiOperation("分页查询列表")
    @GetMapping("/page")
    @SaCheckPermission("studentExam:page")
    public Result<PageInfo<StudentExamTaskPageResModel>> page(@ApiParam("查询参数") @Validated StudentExamPageReqModel reqModel) {
        reqModel.setUserId(getUserId());
        return Result.success(studentExamTaskService.page(reqModel));
    }

    @ApiOperation("新增")
    @PostMapping("/add")
    @SaCheckPermission("studentExam:add")
    public Result<Void> save(@ApiParam("考试登记记录信息") @Validated @RequestBody StudentExamTaskSaveReqModel reqModel) {
        studentExamTaskService.save(reqModel);
        return Result.success();
    }

    @ApiOperation("修改")
    @PutMapping("/update/{id}")
    @SaCheckPermission("studentExam:update")
    public Result<Void> update(
            @ApiParam("id") @PathVariable Long id,
            @ApiParam("考试登记记录信息") @Validated @RequestBody StudentExamTaskSaveReqModel reqModel) {
        studentExamTaskService.update(id, reqModel);
        return Result.success();
    }

    @ApiOperation("获取信息")
    @GetMapping("/info/{id}")
    @SaCheckPermission("studentExam:info")
    public Result<StudentExamTaskResModel> info(
            @ApiParam("id") @PathVariable Long id) {
        return Result.success(studentExamTaskService.info(id));
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("studentExam:delete")
    public Result<Void> delete(@ApiParam("id") @PathVariable Long id) {
        studentExamTaskService.delete(id);
        return Result.success();
    }

    @ApiOperation(value = "检查讯息", notes = "请求结果返回创建人姓名，有结果时表示已被创建人创建，无结果时可创建")
    @GetMapping("/check")
    @SaCheckPermission("studentExam:check")
    public Result<String> check(@Validated StudentExamTaskCheckReqModel reqModel) {
        return Result.success(studentExamTaskService.check(getSchoolId(), reqModel));
    }

    @ApiOperation("获取成绩列表")
    @GetMapping("/score/page")
    @SaCheckPermission("studentExam:page")
    public Result<PageInfo<StudentExamScorePageResModel>> scorePage(@ApiParam("查询参数") @Validated StudentExamScorePageReqModel reqModel) {
        return Result.success(studentExamScoreService.page(reqModel));
    }

    @ApiOperation("考试成绩查看-学生端(非鉴权)")
    @GetMapping("/student/score/page")
    public Result<PageInfo<StudentExamScoreResModel>> scorePageByStudent(@Validated StudentExamScoreReqModel reqModel) {
        return Result.success(studentExamScoreService.pageByStudent(reqModel));
    }

    @ApiOperation("新增成绩")
    @PostMapping("/score/add")
    @SaCheckPermission("studentExam:scoreAdd")
    public Result<Void> scoreAdd(@ApiParam("成绩信息") @Validated @RequestBody StudentExamScoreSaveReqModel reqModel) {
        studentExamTaskService.scoreAdd(reqModel);
        return Result.success();
    }

    @ApiOperation("修改成绩")
    @PutMapping("/score/update/{id}")
    @SaCheckPermission("studentExam:scoreUpdate")
    public Result<Void> scoreUpdate(
            @ApiParam("id") @PathVariable Long id,
            @ApiParam("成绩信息") @Validated @RequestBody StudentExamScoreUpdateReqModel reqModel) {
        studentExamScoreService.update(id, reqModel);
        return Result.success();
    }

    @ApiOperation("删除成绩")
    @DeleteMapping("/score/delete/{id}")
    @SaCheckPermission("studentExam:scoreDelete")
    public Result<Void> scoreDelete(@ApiParam("id") @PathVariable Long id) {
        studentExamScoreService.delete(id);
        return Result.success();
    }

    @ApiOperation("成绩检查")
    @GetMapping("/score/check")
    @SaCheckPermission("studentExam:scoreCheck")
    public Result<List<StudentExamScoreCheckResModel>> scoreCheck(@ApiParam("查询参数") @Validated StudentExamScoreCheckReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        reqModel.setUserId(getUserId());
        return Result.success(studentExamTaskService.scoreCheck(reqModel));
    }

    @ApiOperation("成绩分析")
    @GetMapping("/score/analysis")
    @SaCheckPermission("studentExam:scoreAnalysis")
    public Result<List<StudentExamScoreAnalysisResModel>> scoreAnalysis(@ApiParam("查询参数") @Validated StudentExamScoreAnalysisReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        reqModel.setUserId(getUserId());
        return Result.success(studentExamTaskService.scoreAnalysis(reqModel));
    }
}
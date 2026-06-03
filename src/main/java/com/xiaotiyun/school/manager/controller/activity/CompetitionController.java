package com.xiaotiyun.school.manager.controller.activity;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.CompetitionPageResModel;
import com.xiaotiyun.school.manager.model.res.CompetitionRecordResModel;
import com.xiaotiyun.school.manager.model.res.CompetitionResModel;
import com.xiaotiyun.school.manager.model.res.CompetitionStudentPageResModel;
import com.xiaotiyun.school.manager.service.CompetitionRecordService;
import com.xiaotiyun.school.manager.service.CompetitionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "课外比赛管理")
@RestController
@RequestMapping("/api/competition")
@RequiredArgsConstructor
public class CompetitionController extends BasicController {
    private final CompetitionService competitionService;
    private final CompetitionRecordService recordService;

    @ApiOperation("分页查询比赛列表")
    @GetMapping("/page")
    @SaCheckPermission("competition:page")
    public Result<PageInfo<CompetitionPageResModel>> page(@Validated CompetitionPageReqModel reqModel) {
        return Result.success(competitionService.page(reqModel));
    }

    @ApiOperation("新增比赛")
    @PostMapping
    @SaCheckPermission("competition:add")
    public Result<Long> save(@Validated @RequestBody CompetitionSaveReqModel reqModel) {
        return Result.success(competitionService.save(reqModel));
    }

    @ApiOperation("获取比赛详情")
    @GetMapping("/{id}")
    @SaCheckPermission("competition:view")
    public Result<CompetitionResModel> getById(
            @ApiParam(value = "比赛ID", required = true)
            @PathVariable Long id) {
        return Result.success(competitionService.getCompetitionById(id));
    }

    @ApiOperation("更新比赛信息")
    @PutMapping("/{id}")
    @SaCheckPermission("competition:update")
    public Result<Void> update(
            @ApiParam(value = "比赛ID", required = true)
            @PathVariable Long id,
            @Validated @RequestBody CompetitionSaveReqModel reqModel) {
        competitionService.updateCompetition(id, reqModel);
        return Result.success();
    }

    @ApiOperation("删除比赛")
    @DeleteMapping("/{id}")
    @SaCheckPermission("competition:delete")
    public Result<Void> delete(
            @ApiParam(value = "比赛ID", required = true)
            @PathVariable Long id) {
        competitionService.deleteCompetition(id);
        return Result.success();
    }

    @ApiOperation("添加参赛记录")
    @PostMapping("/recordAdd")
    @SaCheckPermission("competitionRecord:add")
    public Result<Void> recordAdd(
            @Validated @RequestBody CompetitionRecordBatchCreateReqModel reqModel) {
        recordService.batchCreateRecords(reqModel);
        return Result.success();
    }

    @ApiOperation("分页查询参赛记录")
    @GetMapping("/records/page")
    @SaCheckPermission("competitionRecord:view")
    public Result<PageInfo<CompetitionRecordResModel>> getRecordPage(
            @Validated CompetitionRecordPageReqModel reqModel) {
        return Result.success(recordService.getRecordPage(reqModel));
    }

    @ApiOperation("更新参赛记录")
    @PutMapping("/record/{id}")
    @SaCheckPermission("competitionRecord:update")
    public Result<Void> updateRecord(
            @ApiParam(value = "记录ID", required = true) @PathVariable Long id,
            @Validated @RequestBody CompetitionRecordUpdateReqModel reqModel) {
        recordService.updateRecord(id, reqModel);
        return Result.success();
    }

    @ApiOperation("删除参赛记录")
    @DeleteMapping("/record/{id}")
    @SaCheckPermission("competitionRecord:delete")
    public Result<Void> deleteRecord(
            @ApiParam(value = "记录ID", required = true) @PathVariable Long id) {
        recordService.deleteRecord(id);
        return Result.success();
    }

    @ApiOperation("分页查询学生比赛列表")
    @GetMapping("/student/page")
    @SaCheckPermission("competition:studentPage")
    public Result<PageInfo<CompetitionStudentPageResModel>> studentPage(@Validated CompetitionRecordStudentPageReqModel reqModel) {
        return Result.success(recordService.studentPage(reqModel));
    }
}
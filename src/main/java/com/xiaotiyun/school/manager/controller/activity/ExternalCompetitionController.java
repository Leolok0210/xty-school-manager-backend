package com.xiaotiyun.school.manager.controller.activity;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionCreateReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionQueryReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionPageResModel;
import com.xiaotiyun.school.manager.service.ExternalCompetitionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "校外比赛管理")
@RestController
@RequestMapping("/api/competition/external")
@RequiredArgsConstructor
public class ExternalCompetitionController extends BasicController {
    private final ExternalCompetitionService externalCompetitionService;

    @ApiOperation("新增或修改比赛")
    @PostMapping
    @SaCheckPermission("externalCompetition:add")
    public Result<Long> save(@Validated @RequestBody ExternalCompetitionCreateReqModel reqModel) {
        return Result.success(externalCompetitionService.saveOrUpdate(reqModel));
    }

    @ApiOperation("获取比赛信息")
    @GetMapping("/{id}")
    @SaCheckPermission("externalCompetition:info")
    public Result<ExternalCompetitionPageResModel> info(
            @ApiParam(value = "比赛ID", required = true)
            @PathVariable Long id) {
        return Result.success(externalCompetitionService.info(id));
    }

    @ApiOperation("分页查询")
    @GetMapping("/page")
    @SaCheckPermission("externalCompetition:view")
    public Result<PageInfo<ExternalCompetitionPageResModel>> page(@Validated ExternalCompetitionQueryReqModel req) {
        return Result.success(externalCompetitionService.page(req));
    }

    @ApiOperation("删除比赛")
    @DeleteMapping("/{id}")
    @SaCheckPermission("externalCompetition:delete")
    public Result<Void> delete(
            @ApiParam(value = "比赛ID", required = true)
            @PathVariable Long id) {
        externalCompetitionService.delete(id);
        return Result.success();
    }

    @ApiOperation("导出")
    @GetMapping("/export")
    @SaCheckPermission("externalCompetition:export")
    public Result<String> export(@Validated ExternalCompetitionQueryReqModel req) {
        return Result.success(externalCompetitionService.export(req));
    }
}
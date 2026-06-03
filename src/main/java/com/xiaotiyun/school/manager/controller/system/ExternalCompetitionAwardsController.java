package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionAwardsReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionAwardsSaveReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionAwardsResModel;
import com.xiaotiyun.school.manager.service.ExternalCompetitionAwardsService;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 校外活动奖项评级Controller
 */
@RestController
@RequestMapping("/api/externalCompetitionAwards")
@Api(tags = "校外活动奖项评级管理")
public class ExternalCompetitionAwardsController extends BasicController {

    @Autowired
    private ExternalCompetitionAwardsService externalCompetitionAwardsService;

    /**
     * 分页查询校外活动奖项评级列表
     */
    @GetMapping("/pageList")
    @ApiOperation("分页查询校外活动奖项评级列表")
    @SaCheckPermission("externalCompetitionAwards:list")
    public Result<PageInfo<ExternalCompetitionAwardsResModel>> pageList(@Validated ExternalCompetitionAwardsReqModel reqModel) {
        Long schoolId = getSchoolId();
        PageInfo<ExternalCompetitionAwardsResModel> pageInfo = externalCompetitionAwardsService.pageList(reqModel, schoolId);
        return Result.success(pageInfo);
    }

    /**
     * 新增或修改校外活动奖项评级
     */
    @PostMapping("/addOrUpdate")
    @ApiOperation("新增或修改校外活动奖项评级")
    @SaCheckPermission("externalCompetitionAwards:add")
    public Result<Boolean> addOrUpdate(@Validated @RequestBody List<ExternalCompetitionAwardsSaveReqModel> reqModels) {
        Long schoolId = getSchoolId();
        return externalCompetitionAwardsService.addOrUpdate(reqModels, schoolId);
    }

    /**
     * 删除校外活动奖项评级
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除校外活动奖项评级")
    @SaCheckPermission("externalCompetitionAwards:delete")
    public Result<Boolean> delete(@RequestParam List<Long> ids) {
        Long schoolId = getSchoolId();
        return externalCompetitionAwardsService.delete(ids, schoolId);
    }

    /**
     * 检查奖项评级名称是否存在
     */
    @GetMapping("/checkAwardsNameExists")
    @ApiOperation("检查奖项评级名称是否存在")
    @SaCheckPermission("externalCompetitionAwards:check")
    public Result<Boolean> checkAwardsNameExists(@RequestParam String awardsName, @RequestParam(required = false) Long id) {
        Long schoolId = getSchoolId();
        boolean exists = externalCompetitionAwardsService.checkAwardsNameExists(awardsName, schoolId, id);
        return Result.success(exists);
    }
}


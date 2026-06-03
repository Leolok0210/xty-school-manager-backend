package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionExportRuleCheckReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionExportRuleReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionExportRuleSaveReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionExportRuleResModel;
import com.xiaotiyun.school.manager.service.ExternalCompetitionExportRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 校外活动导出规则表Controller
 */
@RestController
@RequestMapping("/api/externalCompetitionExportRule")
@Api(tags = "校外活动导出规则管理")
public class ExternalCompetitionExportRuleController extends BasicController {

    @Autowired
    private ExternalCompetitionExportRuleService externalCompetitionExportRuleService;

    /**
     * 分页查询校外活动导出规则列表
     */
    @GetMapping("/pageList")
    @ApiOperation("分页查询校外活动导出规则列表")
    @SaCheckPermission("externalCompetitionExportRule:list")
    public Result<PageInfo<ExternalCompetitionExportRuleResModel>> pageList(@Validated ExternalCompetitionExportRuleReqModel reqModel) {
        Long schoolId = getSchoolId();
        PageInfo<ExternalCompetitionExportRuleResModel> pageInfo = externalCompetitionExportRuleService.pageList(reqModel, schoolId);
        return Result.success(pageInfo);
    }

    /**
     * 新增或修改校外活动导出规则
     */
    @PostMapping("/addOrUpdate")
    @ApiOperation("新增或修改校外活动导出规则")
    @SaCheckPermission("externalCompetitionExportRule:add")
    public Result<Boolean> addOrUpdate(@Validated @RequestBody List<ExternalCompetitionExportRuleSaveReqModel> reqModels) {
        Long schoolId = getSchoolId();
        return externalCompetitionExportRuleService.addOrUpdate(reqModels, schoolId);
    }

    /**
     * 删除校外活动导出规则
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除校外活动导出规则")
    @SaCheckPermission("externalCompetitionExportRule:delete")
    public Result<Boolean> delete(@RequestParam List<Long> ids) {
        Long schoolId = getSchoolId();
        return externalCompetitionExportRuleService.delete(ids, schoolId);
    }

    /**
     * 检查规则是否存在
     */
    @PostMapping("/checkRuleExists")
    @ApiOperation("检查规则是否存在")
    @SaCheckPermission("externalCompetitionExportRule:check")
    public Result<Boolean> checkRuleExists(@RequestBody ExternalCompetitionExportRuleCheckReqModel reqModel) {
        Long schoolId = getSchoolId();
        boolean exists = externalCompetitionExportRuleService.checkRuleExists(reqModel, schoolId);
        return Result.success(exists);
    }
}


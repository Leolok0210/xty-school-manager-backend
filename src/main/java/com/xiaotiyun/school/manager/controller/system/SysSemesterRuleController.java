package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.SysSemesterRuleAddReqModel;
import com.xiaotiyun.school.manager.model.res.SysSemesterRuleResModel;
import com.xiaotiyun.school.manager.service.SysSemesterRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sysSemesterRule")
@Api(tags = "学段权重配置管理")
@RequiredArgsConstructor
public class SysSemesterRuleController extends BasicController {

    private final SysSemesterRuleService sysSemesterRuleService;

    @PutMapping("/update")
    @SaCheckPermission("sysSemesterRule:update")
    @ApiOperation("更新学段权重配置")
    public Result<String> updateRule(@Validated @RequestBody SysSemesterRuleAddReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        sysSemesterRuleService.updateRule(reqModel);
        return Result.success();
    }

    @GetMapping("/get")
    @SaCheckPermission("sysSemesterRule:get")
    @ApiOperation("获取学段权重配置")
    public Result<List<SysSemesterRuleResModel>> getRuleById(@Validated String schoolYear) {
        return Result.success(sysSemesterRuleService.getRuleById(getSchoolId(),schoolYear));
    }

}
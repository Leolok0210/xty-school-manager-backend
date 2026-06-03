package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.SubjectLevelRuleReqModel;
import com.xiaotiyun.school.manager.model.res.SubjectLevelRuleResModel;
import com.xiaotiyun.school.manager.service.SubjectLevelRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjectLevelRule")
@Api(tags = "科目评级规则管理")
@RequiredArgsConstructor
public class SubjectLevelRuleController extends BasicController {


    private final SubjectLevelRuleService subjectLevelRuleService;

    @PutMapping("/update")
    @SaCheckPermission("subjectLevelRule:update")
    @ApiOperation("更新科目评级规则")
    public Result<String> updateRule(@Validated @RequestBody SubjectLevelRuleReqModel reqModel) {
        subjectLevelRuleService.updateRule(getSchoolId(), reqModel);
        return Result.success();
    }

    @GetMapping("/get/{groupId}")
    @SaCheckPermission("subjectLevelRule:get")
    @ApiOperation("获取科目评级规则")
    public Result<List<SubjectLevelRuleResModel>> getRuleByDepartment(@PathVariable Long groupId) {
        return Result.success(subjectLevelRuleService.getRuleByDepartment(getSchoolId(),groupId));
    }

}
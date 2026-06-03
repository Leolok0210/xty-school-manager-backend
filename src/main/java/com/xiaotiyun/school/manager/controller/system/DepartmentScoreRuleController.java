package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.DepartmentScoreRuleReqModel;
import com.xiaotiyun.school.manager.model.res.DepartmentScoreRuleResModel;
import com.xiaotiyun.school.manager.service.DepartmentScoreRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departmentScoreRule") // 修改请求路径
@Api(tags = "学部成绩权重规则管理") // 修改描述
@RequiredArgsConstructor
public class DepartmentScoreRuleController extends BasicController {

    private final DepartmentScoreRuleService departmentScoreRuleService;

    @PutMapping("/update")
    @SaCheckPermission("departmentScoreRule:update")
    @ApiOperation("更新学部成绩权重规则")
    public Result<String> updateRule(@Validated @RequestBody DepartmentScoreRuleReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        departmentScoreRuleService.updateRule(reqModel);
        return Result.success();
    }

    @GetMapping("/get")
    @SaCheckPermission("departmentScoreRule:get")
    @ApiOperation("获取学部成绩权重规则")
    public Result<DepartmentScoreRuleResModel> getRuleByDepartment(@RequestParam Long groupId) {
        return Result.success(departmentScoreRuleService.getRuleByDepartment(getSchoolId(),groupId));
    }

}
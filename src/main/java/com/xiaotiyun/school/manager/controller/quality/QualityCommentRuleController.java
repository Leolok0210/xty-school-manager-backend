package com.xiaotiyun.school.manager.controller.quality;

import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.model.entity.QualityEvaluationCommentRuleEntity;
import com.xiaotiyun.school.manager.model.req.QualityCommentRuleBatchOperateReqModel;
import com.xiaotiyun.school.manager.service.QualityCommentRuleService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "素质登记评语设定")
@RestController
@RequestMapping("/api/quality/comment")
public class QualityCommentRuleController extends BasicController {
    
    @Resource
    private QualityCommentRuleService qualityCommentRuleService;
    
    @ApiOperation(value = "批量操作评语规则", notes = "支持同时进行新增、编辑和删除操作")
    @SaCheckPermission("quality:batchOperateCommentRules")
    @PostMapping("/rule/batch-operate")
    public Result<Void> batchOperateRules(HttpServletRequest request, @Validated @RequestBody QualityCommentRuleBatchOperateReqModel reqModel) {
        qualityCommentRuleService.batchOperateRules(getSchoolId(request), reqModel);
        return Result.success();
    }
    
    @ApiOperation(value = "查询评语规则列表", notes = "获取所有评语规则,按优先级升序排序")
    @SaCheckPermission("quality:query")
    @GetMapping("/rule/list")
    public Result<List<QualityEvaluationCommentRuleEntity>> listRules(HttpServletRequest request) {
        return Result.success(qualityCommentRuleService.listRules(getSchoolId(request)));
    }
} 
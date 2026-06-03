package com.xiaotiyun.school.manager.controller.quality;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.res.QualityEvaluationGradeStandardResModel;
import com.xiaotiyun.school.manager.model.req.QualityGradeStandardSaveReqModel;
import com.xiaotiyun.school.manager.model.req.QualityIndicatorSaveReqModel;
import com.xiaotiyun.school.manager.model.res.QualityIndicatorListResModel;
import com.xiaotiyun.school.manager.model.req.QualityIndicatorBatchSaveReqModel;
import com.xiaotiyun.school.manager.model.req.QualityGradeStandardBatchSaveReqModel;
import com.xiaotiyun.school.manager.service.QualityEvaluationService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "素质评价指标设定")
@RestController
@RequestMapping("/api/quality")
public class QualityEvaluationController extends BasicController {

    @Resource
    private QualityEvaluationService qualityEvaluationService;

    @ApiOperation(value = "查询评价指标列表", notes = "获取当前学校的所有素质评价指标列表")
    @SaCheckPermission("quality:indicator:query")
    @GetMapping("/indicator/list")
    public Result<List<QualityIndicatorListResModel>> listIndicators(HttpServletRequest request) {
        return Result.success(qualityEvaluationService.listIndicators(getSchoolId(request)));
    }
    

    @ApiOperation("查询评分标准列表")
    @SaCheckPermission("quality:standard:query")
    @GetMapping("/grade-standard/list")
    public Result<List<QualityEvaluationGradeStandardResModel>> listGradeStandards(HttpServletRequest request) {
        return Result.success(qualityEvaluationService.listGradeStandards(getSchoolId(request)));
    }

    @ApiOperation("批量保存评价指标(支持新增、编辑、删除)")
    @SaCheckPermission("quality:indicator:edit")
    @PostMapping("/indicator/batch")
    public Result<Void> batchSaveIndicators(HttpServletRequest request, 
            @Validated @RequestBody QualityIndicatorBatchSaveReqModel reqModel) {
        qualityEvaluationService.batchSaveIndicators(getSchoolId(request), reqModel);
        return Result.success();
    }

    @ApiOperation("批量保存评分标准(支持新增、编辑、删除)")
    @SaCheckPermission("quality:standard:edit")
    @PostMapping("/grade-standard/batch")
    public Result<Void> batchSaveGradeStandards(HttpServletRequest request, 
            @Validated @RequestBody QualityGradeStandardBatchSaveReqModel reqModel) {
        qualityEvaluationService.batchSaveGradeStandards(getSchoolId(request), reqModel);
        return Result.success();
    }
} 
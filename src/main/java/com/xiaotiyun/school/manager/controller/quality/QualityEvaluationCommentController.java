package com.xiaotiyun.school.manager.controller.quality;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.model.entity.QualityEvaluationComment;
import com.xiaotiyun.school.manager.model.req.QualityEvaluationCommentAddReqModel;
import com.xiaotiyun.school.manager.model.res.QualityEvaluationCommentDetailResModel;
import com.xiaotiyun.school.manager.service.QualityEvaluationCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@Api(tags = "素质登记评语管理")
@RequestMapping("/api/qualityevaluationcomment")
public class QualityEvaluationCommentController extends BasicController {

    @Resource
    private QualityEvaluationCommentService qualityEvaluationCommentService;

    @PostMapping("/add")
    @ApiOperation("批量新增评语")
    @SaCheckPermission("quality:comment:add")
    public Result<Void> addQualityEvaluationComments(HttpServletRequest request, @Valid @RequestBody List<QualityEvaluationCommentAddReqModel> reqModels) {
        List<QualityEvaluationComment> qualityEvaluationComments = reqModels.stream()
                .map(reqModel -> BeanConvertUtil.convert(reqModel, QualityEvaluationComment.class))
                .collect(Collectors.toList());
        qualityEvaluationCommentService.saveBatch(qualityEvaluationComments);
        return Result.success();
    }

    @PostMapping("/update")
    @ApiOperation("修改评语")
    @SaCheckPermission("quality:comment:edit")
    public Result<Void> updateQualityEvaluationComment(@Valid @RequestBody QualityEvaluationCommentAddReqModel reqModel) {
        QualityEvaluationComment qualityEvaluationComment = BeanConvertUtil.convert(reqModel, QualityEvaluationComment.class);
        qualityEvaluationComment.setUpdateTime(LocalDateTime.now());
        qualityEvaluationCommentService.updateById(qualityEvaluationComment);
        return Result.success();
    }

//    @GetMapping("/get")
//    @ApiOperation("查看评语详情")
//    public Result<QualityEvaluationCommentDetailResModel> getQualityEvaluationCommentDetail(@RequestParam Long id) {
//        QualityEvaluationComment qualityEvaluationComment = qualityEvaluationCommentService.getById(id);
//        QualityEvaluationCommentDetailResModel resModel = BeanConvertUtil.convert(qualityEvaluationComment, QualityEvaluationCommentDetailResModel.class);
//        return Result.success(resModel);
//    }
}
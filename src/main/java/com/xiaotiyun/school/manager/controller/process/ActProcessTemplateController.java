package com.xiaotiyun.school.manager.controller.process;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.ActProcessTemplateSaveReqModel;
import com.xiaotiyun.school.manager.model.res.ActProcessTemplateInfoResModel;
import com.xiaotiyun.school.manager.model.res.ActProcessTemplateListResModel;
import com.xiaotiyun.school.manager.model.res.ActProcessTemplatePageResModel;
import com.xiaotiyun.school.manager.service.ActProcessTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "审批流程模板管理")
@RequiredArgsConstructor
@RequestMapping("/api/act/process/template")
public class ActProcessTemplateController extends BasicController {
    private final ActProcessTemplateService actProcessTemplateService;

    @GetMapping("/page")
    @ApiOperation("分页列表")
    @SaCheckPermission("act:process:template:page")
    public Result<PageInfo<ActProcessTemplatePageResModel>> page(@Validated PageReqModel reqModel) {
        return Result.success(actProcessTemplateService.page(getSchoolId(), reqModel));
    }

    @PostMapping
    @ApiOperation("新增审批流程模板")
    @SaCheckPermission("act:process:template:add")
    public Result<Void> add(@Valid @RequestBody ActProcessTemplateSaveReqModel reqModel) {
        actProcessTemplateService.save(getSchoolId(), reqModel);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("更新审批流程模板")
    @SaCheckPermission("act:process:template:update")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ActProcessTemplateSaveReqModel reqModel) {
        actProcessTemplateService.update(getSchoolId(), id, reqModel);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取审批流程模板详情")
    @SaCheckPermission("act:process:template:info")
    public Result<ActProcessTemplateInfoResModel> info(@PathVariable Long id) {
        return Result.success(actProcessTemplateService.info(id));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除审批流程模板")
    @SaCheckPermission("act:process:template:delete")
    public Result<Void> delete(@PathVariable Long id) {
        actProcessTemplateService.delete(id);
        return Result.success();
    }

    @GetMapping("/list/{processType}")
    @ApiOperation("可使用流程列表")
    @SaCheckPermission("act:process:template:list")
    public Result<List<ActProcessTemplateListResModel>> list(@ApiParam("审批类型(1.教师请假；2.教师公务；3.学生奖惩)") @PathVariable Integer processType) {
        return Result.success(actProcessTemplateService.list(getSchoolId(), getUserId(), processType));
    }
}
package com.xiaotiyun.school.manager.controller.teacher;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.SubstitutePageReqModel;
import com.xiaotiyun.school.manager.model.req.SubstituteSaveReqModel;
import com.xiaotiyun.school.manager.model.req.SubstituteUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SubstitutePageResModel;
import com.xiaotiyun.school.manager.service.SubstituteRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/substitute")
@RequiredArgsConstructor
@Api(tags = "代课设定")
public class SubstituteController extends BasicController {
    private final SubstituteRecordService substituteRecordService;

    @PostMapping
    @ApiOperation("新增代课")
    @SaCheckPermission("substitute:add")
    public Result<Void> add(@Validated @RequestBody SubstituteSaveReqModel reqModel) {
        substituteRecordService.add(getSchoolId(), reqModel);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改代课")
    @SaCheckPermission("substitute:update")
    public Result<Void> update(@PathVariable Long id,
                               @Validated @RequestBody SubstituteUpdateReqModel reqModel) {
        substituteRecordService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除代课")
    @SaCheckPermission("substitute:delete")
    public Result<Void> delete(@PathVariable Long id) {
        substituteRecordService.delete(id);
        return Result.success();
    }

    @GetMapping
    @ApiOperation("代课列表")
    @SaCheckPermission("substitute:page")
    public Result<PageInfo<SubstitutePageResModel>> page(@Validated SubstitutePageReqModel reqModel) {
        return Result.success(substituteRecordService.page(getSchoolId(), reqModel));
    }

    @GetMapping("/export")
    @ApiOperation("导出Excel")
    @SaCheckPermission("substitute:export")
    public Result<String> export(@Validated SubstitutePageReqModel reqModel) {
        return Result.success(substituteRecordService.export(getSchoolId(), reqModel));
    }
}
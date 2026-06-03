package com.xiaotiyun.school.manager.controller.basic;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.ImportTaskEntity;
import com.xiaotiyun.school.manager.model.req.ImportRecordPageReqModel;
import com.xiaotiyun.school.manager.model.req.ImportTaskPageReqModel;
import com.xiaotiyun.school.manager.model.res.ImportRecordResModel;
import com.xiaotiyun.school.manager.model.res.ImportTaskPageResModel;
import com.xiaotiyun.school.manager.service.ImportRecordService;
import com.xiaotiyun.school.manager.service.ImportTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "导入任务管理")
@RestController
@RequestMapping("/api/import/task")
public class ImportTaskController extends BasicController {
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private ImportRecordService importRecordService;

    @ApiOperation("分页查询导入任务列表")
    @PostMapping("/page")
    @SaCheckPermission("importTask:page")
    public Result<PageInfo<ImportTaskPageResModel>> page(@ApiParam("查询参数") @Validated @RequestBody ImportTaskPageReqModel reqModel) {
        return Result.success(importTaskService.page(reqModel));
    }

    @ApiOperation("分页查询导出失败信息列表")
    @GetMapping("/record/page")
    @SaCheckPermission("importTask:recordPage")
    public Result<PageInfo<ImportRecordResModel>> recordPage(@ApiParam("查询参数") @Validated ImportRecordPageReqModel reqModel) {
        return Result.success(importRecordService.page(reqModel));
    }

    @ApiOperation("导出失败信息")
    @PostMapping("/record/export")
    @SaCheckPermission("importTask:recordExport")
    public Result<String> recordExport(HttpServletRequest request, @Validated @RequestBody ImportRecordPageReqModel reqModel) {
        long schoolId = getSchoolId(request);
        ImportTaskEntity importTask = importTaskService.getById(reqModel.getTaskId());
        return Result.success(importRecordService.recordExport(schoolId, importTask, reqModel));
    }
}
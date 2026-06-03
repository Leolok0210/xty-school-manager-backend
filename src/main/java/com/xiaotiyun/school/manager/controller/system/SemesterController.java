package com.xiaotiyun.school.manager.controller.system;

import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.model.req.SemesterAddReqModel;
import com.xiaotiyun.school.manager.model.req.SemesterQueryReqModel;
import com.xiaotiyun.school.manager.model.req.SemesterUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SemesterResModel;
import com.xiaotiyun.school.manager.service.SemesterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/semester")
@Api(tags = "学段管理")
public class SemesterController extends BasicController {
    @Resource
    private SemesterService semesterService;

    @PostMapping("/batch")
    @ApiOperation("批量新增或编辑学段")
    @SaCheckPermission("system:semester:add")
    public Result<Void> batchSave(HttpServletRequest request, @Valid @RequestBody List<SemesterAddReqModel> reqModels) {
        semesterService.batchSave(reqModels, getSchoolId(request));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除学段")
    @SaCheckPermission("system:semester:del")
    public Result<Void> delete(HttpServletRequest request, @PathVariable Long id) {
        semesterService.delete(id, getSchoolId(request));
        return Result.success();
    }

    @GetMapping
    @ApiOperation("查询学段列表")
    public Result<List<SemesterResModel>> list(HttpServletRequest request, @Valid SemesterQueryReqModel reqModel) {
        return Result.success(semesterService.list(reqModel, getSchoolId(request)));
    }

    @GetMapping("/list/student")
    @ApiOperation("查询学段列表-学生端(非鉴权)")
    public Result<List<SemesterResModel>> listByStudent(HttpServletRequest request, @Valid SemesterQueryReqModel reqModel) {
        return Result.success(semesterService.listByStudent(reqModel, getSchoolId(request)));
    }
}
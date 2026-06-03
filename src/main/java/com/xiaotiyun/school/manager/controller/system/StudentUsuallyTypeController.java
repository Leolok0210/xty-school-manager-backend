package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyTypeReqModel;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyTypeSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyTypeResModel;
import com.xiaotiyun.school.manager.service.StudentUsuallyTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平时成绩类型Controller
 */
@RestController
@RequestMapping("/api/studentUsuallyType")
@Api(tags = "平时成绩类型管理")
public class StudentUsuallyTypeController extends BasicController {

    @Autowired
    private StudentUsuallyTypeService studentUsuallyTypeService;

    /**
     * 分页查询平时成绩类型列表
     */
    @GetMapping("/pageList")
    @ApiOperation("分页查询平时成绩类型列表")
    @SaCheckPermission("studentUsuallyType:list")
    public Result<PageInfo<StudentUsuallyTypeResModel>> pageList(@Validated StudentUsuallyTypeReqModel reqModel) {
        Long schoolId = getSchoolId();
        PageInfo<StudentUsuallyTypeResModel> pageInfo = studentUsuallyTypeService.pageList(reqModel, schoolId);
        return Result.success(pageInfo);
    }

    /**
     * 新增平时成绩类型
     */
    @PostMapping("/addOrUpdate")
    @ApiOperation("新增或修改平时成绩类型")
    @SaCheckPermission("studentUsuallyType:add")
    public Result<Boolean> addOrUpdate(@Validated @RequestBody List<StudentUsuallyTypeSaveReqModel> reqModels) {
        Long schoolId = getSchoolId();
        return studentUsuallyTypeService.addOrUpdate(reqModels, schoolId);
    }

    /**
     * 删除平时成绩类型
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除平时成绩类型")
    @SaCheckPermission("studentUsuallyType:delete")
    public Result<Boolean> delete(@RequestParam List<Long> ids) {
        Long schoolId = getSchoolId();
        return studentUsuallyTypeService.delete(ids, schoolId);
    }

    /**
     * 检查类型名称是否存在
     */
    @GetMapping("/checkTypeNameExists")
    @ApiOperation("检查类型名称是否存在")
    public Result<Boolean> checkTypeNameExists(@RequestParam String typeName, @RequestParam(required = false) Long id) {
        Long schoolId = getSchoolId();
        boolean exists = studentUsuallyTypeService.checkTypeNameExists(typeName, schoolId, id);
        return Result.success(exists);
    }
}

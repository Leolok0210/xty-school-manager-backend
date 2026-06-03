package com.xiaotiyun.school.manager.controller.school;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.SchoolAddReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolMenuBatchUpdateReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SchoolDetailResModel;
import com.xiaotiyun.school.manager.model.res.SchoolDetailStudentResModel;
import com.xiaotiyun.school.manager.service.SchoolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/school")
@Api(tags = "学校管理")
public class SchoolController extends BasicController {
    
    @Resource
    private SchoolService schoolService;
    
    @PostMapping
    @ApiOperation("新增学校")
    @SaCheckPermission("school:add")
    @SaCheckRole("role:superAdmin")
    public Result<Void> addSchool(@Valid @RequestBody SchoolAddReqModel reqModel) {
        schoolService.addSchool(reqModel);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @ApiOperation("修改学校")
    @SaCheckPermission("school:edit")
    @SaCheckRole("role:superAdmin")
    public Result<Void> updateSchool(@PathVariable Long id, @Valid @RequestBody SchoolAddReqModel reqModel) {
        schoolService.updateSchool(id, reqModel);
        return Result.success();
    }
    
    @GetMapping("/{id}")
    @ApiOperation("查看学校详情")
    @SaCheckPermission("school:query")
    public Result<SchoolDetailResModel> getSchoolDetail(@PathVariable Long id) {
        return Result.success(schoolService.getSchoolDetail(id));
    }
    
    @GetMapping
    @ApiOperation("查询学校列表")
    @SaCheckPermission("school:query")
    public Result<PageInfo<SchoolDetailResModel>> getSchoolList(@Valid SchoolQueryReqModel reqModel) {
        return Result.success(schoolService.getSchoolList(reqModel));
    }

    @SaIgnore
    @GetMapping("/student/school/list")
    @ApiOperation("查询学校列表-学生端(非鉴权)")
    public Result<PageInfo<SchoolDetailStudentResModel>> getSchoolListByStudent(@Valid SchoolQueryReqModel reqModel) {
        return Result.success(schoolService.getSchoolListByStudent(reqModel));
    }
    
    @PostMapping("/menu/batch")
    @ApiOperation("批量开通学校菜单")
    @SaCheckPermission("school:menu:edit")
    @SaCheckRole("role:superAdmin")
    public Result<Void> batchUpdateSchoolMenu(@Valid @RequestBody SchoolMenuBatchUpdateReqModel reqModel) {
        schoolService.batchUpdateSchoolMenu(reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除学校")
    @SaCheckPermission("school:del")
    @SaCheckRole("role:superAdmin")
    public Result<Void> deleteSchool(@PathVariable Long id) {
        schoolService.deleteSchool(id);
        return Result.success();
    }
} 
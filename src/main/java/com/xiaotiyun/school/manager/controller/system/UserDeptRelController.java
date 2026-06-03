package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.UserDeptRelReqModel;
import com.xiaotiyun.school.manager.model.res.UserDeptRelResModel;
import com.xiaotiyun.school.manager.service.UserDeptRelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户部门关联表 Controller
 */
@RestController
@RequestMapping("/api/userDeptRel")
@Api(tags = "用户部门关联管理")
@SaCheckPermission("userDeptRel:view")
public class UserDeptRelController extends BasicController {

    @Autowired
    private UserDeptRelService userDeptRelService;

    /**
     * 查询所有用户部门关联
     */
    @GetMapping("/list")
    @ApiOperation("查询所有用户部门关联")
    public Result<List<UserDeptRelResModel>> list() {
//        List<UserDeptRelResModel> list = userDeptRelService.list();
//        return Result.success(list);
        return Result.success();
    }

    /**
     * 根据ID查询用户部门关联
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询用户部门关联")
    public Result<UserDeptRelResModel> getById(@PathVariable Long id) {
//        UserDeptRelResModel resModel = userDeptRelService.getById(id);
//        return Result.success(resModel);
        return Result.success();
    }

    /**
     * 新增用户部门关联
     */
    @PostMapping
    @ApiOperation("新增用户部门关联")
    @SaCheckPermission("userDeptRel:add")
    public Result<Boolean> add(@RequestBody @Validated UserDeptRelReqModel reqModel) {
        boolean success = userDeptRelService.save(reqModel.convertToEntity());
        return Result.success(success);
    }

    /**
     * 更新用户部门关联
     */
    @PutMapping
    @ApiOperation("更新用户部门关联")
    @SaCheckPermission("userDeptRel:edit")
    public Result<Boolean> update(@RequestBody @Validated UserDeptRelReqModel reqModel) {
        boolean success = userDeptRelService.updateById(reqModel.convertToEntity());
        return Result.success(success);
    }

    /**
     * 删除用户部门关联
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除用户部门关联")
    @SaCheckPermission("userDeptRel:remove")
    public Result<Boolean> remove(@PathVariable Long id) {
        boolean success = userDeptRelService.removeById(id);
        return Result.success(success);
    }
}
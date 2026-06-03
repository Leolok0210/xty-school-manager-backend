package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.UserGroupAddReqModel;
import com.xiaotiyun.school.manager.model.req.UserGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.UserGroupDetailResModel;
import com.xiaotiyun.school.manager.model.res.UserGroupResModel;
import com.xiaotiyun.school.manager.service.UserGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@Api(tags = "用户组管理")
@RequestMapping("/api/userGroup")
public class UserGroupController extends BasicController {

    @Resource
    private UserGroupService userGroupService;

    @PostMapping
    @ApiOperation("新增用户组")
    @SaCheckPermission("system:userGroup:add")
    public Result<Void> addUserGroup(HttpServletRequest request, @Valid @RequestBody UserGroupAddReqModel reqModel) {
        userGroupService.addUserGroup(reqModel, getSchoolId(request));
        return Result.success();
    }

    @PostMapping("/preset")
    @ApiOperation("新增预设组")
    @SaCheckRole("role:superAdmin")
    @SaCheckPermission("system:userGroup:preset")
    public Result<Void> addPresetUserGroup(HttpServletRequest request, @Valid @RequestBody UserGroupAddReqModel reqModel) {
        userGroupService.addUserGroup(reqModel, 0);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改用户组")
    @SaCheckPermission("system:userGroup:edit")
    public Result<Void> updateUserGroup(HttpServletRequest request, @PathVariable Long id, @Valid @RequestBody UserGroupAddReqModel reqModel) {
        userGroupService.updateUserGroup(id, reqModel, getSchoolId(request));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户组")
    @SaCheckPermission("system:userGroup:del")
    public Result<Void> deleteUserGroup(@PathVariable Long id) {
        userGroupService.deleteUserGroup(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("查看用户组详情")
    @SaCheckPermission("system:userGroup:query")
    public Result<UserGroupDetailResModel> getUserGroupDetail(HttpServletRequest request, @PathVariable Long id) {

        return Result.success(userGroupService.getUserGroupDetail(id, getSchoolId(request)));
    }

    @GetMapping
    @ApiOperation("查询用户组列表")
    @SaCheckPermission("system:userGroup:query")
    public Result<PageInfo<UserGroupDetailResModel>> getUserGroupList(HttpServletRequest request, @Valid UserGroupQueryReqModel reqModel) {
        return Result.success(userGroupService.getUserGroupList(reqModel, getSchoolId(request)));
    }

    @GetMapping("/user")
    @ApiOperation("获取角色和人员")
    @SaCheckPermission("system:userGroup:user")
    public Result<List<UserGroupResModel>> getUserGroupAndUser() {
        return Result.success(userGroupService.getUserGroupAndUser(getSchoolId()));
    }
} 
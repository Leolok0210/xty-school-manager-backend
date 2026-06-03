package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.DeptReqModel;
import com.xiaotiyun.school.manager.model.res.DeptResModel;
import com.xiaotiyun.school.manager.model.res.UserSchoolRelResModel;
import com.xiaotiyun.school.manager.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 部门控制器
 */
@RestController
@RequestMapping("/api/dept")
@Api(tags = "部门管理")
public class DeptController extends BasicController {

    @Autowired
    private DeptService deptService;

//    /**
//     * 获取部门列表
//     * @param deptReqModel 查询条件
//     * @return 部门列表
//     */
//    @GetMapping("/list")
//    @ApiOperation("获取部门列表")
//    public Result<List<DeptResModel>> listDept(DeptReqModel deptReqModel) {
//        return Result.success(deptService.listDepts(deptReqModel));
//    }

    /**
     * 获取部门详情
     * @param id 部门ID
     * @return 部门详情
     */
    @GetMapping("/{id}")
    @ApiOperation("获取部门详情")
    @SaCheckPermission("system:dept:detail")
    public Result<DeptResModel> getDeptById(@PathVariable Long id) {
        return Result.success(deptService.getDeptById(id));
    }

    /**
     * 新增部门
     * @param deptReqModel 部门信息
     * @return 操作结果
     */
    @PostMapping
    @ApiOperation("新增部门")
    @SaCheckPermission("system:dept:add")
    public Result<Boolean> saveDept(@Valid @RequestBody DeptReqModel deptReqModel) {
        deptReqModel.setSchoolId(getSchoolId());
        return deptService.saveDept(deptReqModel);
    }

    /**
     * 更新部门
     * @param deptReqModel 部门信息
     * @return 操作结果
     */
    @PutMapping
    @ApiOperation("更新部门")
    @SaCheckPermission("system:dept:update")
    public Result<Boolean> updateDept(@Valid @RequestBody DeptReqModel deptReqModel) {
        deptReqModel.setSchoolId(getSchoolId());
        return deptService.updateDept(deptReqModel);
    }

    /**
     * 删除部门
     * @param id 部门ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除部门")
    @SaCheckPermission("system:dept:delete")
    public Result<Boolean> deleteDept(@PathVariable Long id) {
        return deptService.deleteDept(id);
    }

    /**
     * 获取部门树结构
     * @return 部门树结构
     */
    @GetMapping("/tree")
    @ApiOperation("获取部门树结构")
    @SaCheckPermission("system:dept:tree")
    public Result<List<DeptResModel>> getDeptTree() {
        return Result.success(deptService.getDeptTree(getSchoolId(), false));
    }

    /**
     * 获取部门树和人员
     * @return 部门树结构
     */
    @GetMapping("/tree/user")
    @ApiOperation("获取部门树结构和人员")
    @SaCheckPermission("system:dept:treeUser")
    public Result<List<DeptResModel>> getDeptTreeAndUser() {
        return Result.success(deptService.getDeptTree(getSchoolId(), true));
    }

    /**
     * 获取部门人员列表
     */
    @GetMapping("/user/list/{deptId}")
    @ApiOperation("获取部门人员列表")
    @SaCheckPermission("system:dept:userList")
    public Result<List<UserSchoolRelResModel>> getDeptUserList(@PathVariable String deptId) {
        return Result.success(deptService.getDeptUsers(getSchoolId(), Long.valueOf(deptId)));
    }
}
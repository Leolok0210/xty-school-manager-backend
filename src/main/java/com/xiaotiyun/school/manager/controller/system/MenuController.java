package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.MenuPageReqModel;
import com.xiaotiyun.school.manager.model.req.MenuSaveReqModel;
import com.xiaotiyun.school.manager.model.res.MenuResModel;
import com.xiaotiyun.school.manager.model.res.MenuTreeResModel;
import com.xiaotiyun.school.manager.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "菜单管理")
@RestController
@RequestMapping("/api/menu")
public class MenuController extends BasicController {

    @Resource
    private MenuService menuService;

    @ApiOperation("查询菜单列表")
    @GetMapping("/list")
    @SaCheckPermission("system:menu:query")
    public Result<List<MenuResModel>> list(@ApiParam("查询参数") MenuPageReqModel reqModel) {
        return Result.success(menuService.list(reqModel));
    }

    @ApiOperation("新增菜单")
    @PostMapping("/add")
    @SaCheckPermission("system:menu:add")
    @SaCheckRole("role:superAdmin")
    public Result<Void> save(@ApiParam("菜单信息") @Validated @RequestBody MenuSaveReqModel reqModel) {
        menuService.save(reqModel);
        return Result.success();
    }

    @ApiOperation("修改菜单")
    @PutMapping("/update/{id}")
    @SaCheckPermission("system:menu:edit")
    @SaCheckRole("role:superAdmin")
    public Result<Void> update(
        @ApiParam("菜单ID") @PathVariable Long id,
        @ApiParam("菜单信息") @Validated @RequestBody MenuSaveReqModel reqModel) {
        menuService.update(id, reqModel);
        return Result.success();
    }

    @ApiOperation("删除菜单")
    @DeleteMapping("/delete/{id}")
    @SaCheckRole("role:superAdmin")
    @SaCheckPermission("system:menu:del")
    public Result<Void> delete(@ApiParam("菜单ID") @PathVariable Long id) {
        menuService.delete(id);
        return Result.success();
    }

    @ApiOperation("获取当前学校菜单列表")
    @GetMapping("/school/list")
    // @SaCheckPermission("system:menu:query")
    public Result<List<MenuResModel>> getSchoolMenuList(HttpServletRequest request) {
        Long schoolId = getSchoolId(request);
        return Result.success(menuService.getSchoolMenuList(schoolId));
    }
}
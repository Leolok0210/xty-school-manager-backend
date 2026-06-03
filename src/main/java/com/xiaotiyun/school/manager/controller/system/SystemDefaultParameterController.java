package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.SystemDefaultParameterAddReqModel;
import com.xiaotiyun.school.manager.model.req.SystemDefaultParameterQueryReqModel;
import com.xiaotiyun.school.manager.model.req.SystemDefaultParameterUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SystemDefaultParameterResModel;
import com.xiaotiyun.school.manager.service.SystemDefaultParameterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/systemDefaultParam")
@Api(tags = "系统预设参数控制器")
@RequiredArgsConstructor
public class SystemDefaultParameterController extends BasicController {

    private final SystemDefaultParameterService systemDefaultParameterService;

    @GetMapping("/page")
    @SaCheckPermission("systemDefaultParam:page")
    @ApiOperation("获取系统预设参数")
    public Result<Page<SystemDefaultParameterResModel>> listSysDictionaries(@Validated SystemDefaultParameterQueryReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        return systemDefaultParameterService.listSystemDefaultParameter(reqModel);
    }

    @PostMapping("/add")
    @SaCheckPermission("systemDefaultParam:add")
    @ApiOperation("新增系统预设参数")
    public Result<String> addSysDictionary(@Validated @RequestBody SystemDefaultParameterAddReqModel entity) {
        return systemDefaultParameterService.addSystemDefaultParameter(entity, getSchoolId());
    }

    @PostMapping("/update")
    @SaCheckPermission("systemDefaultParam:update")
    @ApiOperation("更新系统预设参数")
    public Result<String> updateSysDictionary(@Validated @RequestBody SystemDefaultParameterUpdateReqModel entity) {
        return systemDefaultParameterService.updateSystemDefaultParameter(entity);
    }

    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("systemDefaultParam:delete")
    @ApiOperation("删除系统预设参数")
    public Result<String> deleteSysDictionary(@PathVariable Long id) {
        return systemDefaultParameterService.deleteSystemDefaultParameter(id);
    }
}
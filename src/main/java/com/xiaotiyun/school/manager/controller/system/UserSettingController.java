package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.UserSettingReqModel;
import com.xiaotiyun.school.manager.model.res.UserSettingResModel;
import com.xiaotiyun.school.manager.service.UserSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户设置控制器
 */
@RestController
@RequestMapping("/api/userSetting")
@Api(tags = "用户设置管理")
public class UserSettingController extends BasicController {

    @Autowired
    private UserSettingService userSettingService;

    /**
     * 根据ID获取用户设置
     * @param id 主键ID
     * @return 用户设置信息
     */
    @GetMapping("/{id}")
//    @SaCheckPermission("userSetting:read")
    @ApiOperation("根据ID获取用户设置")
    public Result<UserSettingResModel> getUserSettingById(@PathVariable Long id) {
        return userSettingService.getUserSettingById(id);
    }

    /**
     * 创建用户设置
     * @param reqModel 用户设置实体
     * @return 操作结果
     */
    @PostMapping
//    @SaCheckPermission("userSetting:create")
    @ApiOperation("创建用户设置")
    public Result<Boolean> createUserSetting(@Valid @RequestBody UserSettingReqModel reqModel) {
        return userSettingService.createUserSetting(reqModel);
    }

    /**
     * 更新用户设置
     * @param reqModel 用户设置实体
     * @return 操作结果
     */
    @PutMapping
//    @SaCheckPermission("userSetting:update")
    @ApiOperation("更新用户设置")
    public Result<Boolean> updateUserSetting(@Valid @RequestBody UserSettingReqModel reqModel) {
        return userSettingService.updateUserSetting(reqModel);
    }

}


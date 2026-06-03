package com.xiaotiyun.school.manager.controller.auth;

import cn.dev33.satoken.annotation.SaIgnore;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.LoginReqModel;
import com.xiaotiyun.school.manager.model.req.UpdatePasswordReqModel;
import com.xiaotiyun.school.manager.model.res.LoginResModel;
import com.xiaotiyun.school.manager.model.res.MenuResModel;
import com.xiaotiyun.school.manager.service.AuthService;
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
@RequestMapping("/api/auth")
@Api(tags = "认证接口")
public class AuthController extends BasicController {

    @Resource
    private AuthService authService;

    @SaIgnore
    @PostMapping("/login")
    @ApiOperation("登录")
    public Result<LoginResModel> login(@Valid @RequestBody LoginReqModel reqModel) {
        return Result.success(authService.login(reqModel));
    }
    @SaIgnore
    @PostMapping("/logout")
    @ApiOperation("登出")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @PostMapping("/password/update")
    @ApiOperation("修改密码")
    public Result<Void> updatePassword(@Valid @RequestBody UpdatePasswordReqModel reqModel) {
        authService.updatePassword(reqModel);
        return Result.success();
    }

    @GetMapping("/menu")
    @ApiOperation("获取用户菜单")
    public Result<List<MenuResModel>> getUserMenu(HttpServletRequest request) {
        return Result.success(authService.getUserMenu(getSchoolId(request)));
    }
}
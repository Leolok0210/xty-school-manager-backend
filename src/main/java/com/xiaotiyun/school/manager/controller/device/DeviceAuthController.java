package com.xiaotiyun.school.manager.controller.device;

import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.service.DeviceTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "设备认证")
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceAuthController {
    private final DeviceTokenService deviceTokenService;

    @PostMapping("/auth")
    @ApiOperation(value = "设备认证")
    public Result<String> auth(
            @ApiParam("设备序列号") @RequestParam String deviceSn,
            @ApiParam("设备名称") @RequestParam(required = false, defaultValue = "") String deviceName) {
        String token = deviceTokenService.getOrCreateToken(deviceSn, deviceName);
        return Result.success(token);
    }

    @PostMapping("/refresh")
    @ApiOperation(value = "刷新设备令牌")
    public Result<String> refresh(
            @ApiParam("设备序列号") @RequestParam String deviceSn) {
        deviceTokenService.revokeToken(deviceSn);
        String token = deviceTokenService.getOrCreateToken(deviceSn, "");
        return Result.success(token);
    }
}

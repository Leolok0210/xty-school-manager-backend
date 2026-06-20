package com.xiaotiyun.school.manager.controller.device;

import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.model.entity.DeviceCardEntity;
import com.xiaotiyun.school.manager.model.req.CardBindReqModel;
import com.xiaotiyun.school.manager.service.DeviceCardService;
import com.xiaotiyun.school.manager.service.DeviceTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = "设备卡片管理")
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class DeviceCardController {
    private final DeviceCardService deviceCardService;
    private final DeviceTokenService deviceTokenService;

    private boolean checkAuth(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        return deviceTokenService.validateToken(token);
    }

    @PostMapping("/bind")
    @ApiOperation(value = "綁定卡片與學生")
    public Result<DeviceCardEntity> bindCard(
            HttpServletRequest request,
            @RequestBody CardBindReqModel reqModel) {
        if (!checkAuth(request)) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        DeviceCardEntity entity = deviceCardService.bindCard(
                reqModel.getCardId(), reqModel.getStudentId(),
                reqModel.getName(), reqModel.getDeviceSn());
        return Result.success(entity);
    }

    @DeleteMapping("/{cardId}")
    @ApiOperation(value = "解除卡片綁定")
    public Result<String> unbindCard(
            HttpServletRequest request,
            @ApiParam("卡片ID") @PathVariable String cardId) {
        if (!checkAuth(request)) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        boolean ok = deviceCardService.unbindCard(cardId);
        if (ok) {
            return Result.success("unbind success");
        }
        return Result.failed(ResultCode.NOT_FOUND);
    }

    @GetMapping("/students")
    @ApiOperation(value = "查詢所有卡片綁定")
    public Result<List<DeviceCardEntity>> listBindings(
            HttpServletRequest request,
            @ApiParam("设备序列号") @RequestParam(required = false) String deviceSn) {
        if (!checkAuth(request)) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        List<DeviceCardEntity> list = deviceCardService.listBindings(deviceSn);
        return Result.success(list);
    }
}

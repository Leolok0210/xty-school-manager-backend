package com.xiaotiyun.school.manager.controller.device;

import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.model.entity.DeviceFaceEntity;
import com.xiaotiyun.school.manager.model.req.FaceRegisterReqModel;
import com.xiaotiyun.school.manager.service.DeviceFaceService;
import com.xiaotiyun.school.manager.service.DeviceTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = "设备人脸管理")
@RequestMapping("/api/face")
@RequiredArgsConstructor
public class DeviceFaceController {
    private final DeviceFaceService deviceFaceService;
    private final DeviceTokenService deviceTokenService;

    private boolean checkAuth(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        return deviceTokenService.validateToken(token);
    }

    @PostMapping("/register")
    @ApiOperation(value = "通知人脸注册")
    public Result<DeviceFaceEntity> registerFace(
            HttpServletRequest request,
            @RequestBody FaceRegisterReqModel reqModel) {
        if (!checkAuth(request)) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        DeviceFaceEntity entity = deviceFaceService.registerFace(
                reqModel.getStudentId(), reqModel.getName(), reqModel.getDeviceSn());
        return Result.success(entity);
    }

    @DeleteMapping("/{studentId}")
    @ApiOperation(value = "通知人脸刪除")
    public Result<String> deleteFace(
            HttpServletRequest request,
            @ApiParam("学生学号") @PathVariable String studentId) {
        if (!checkAuth(request)) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        boolean ok = deviceFaceService.deleteFace(studentId);
        if (ok) {
            return Result.success("unregister success");
        }
        return Result.failed(ResultCode.NOT_FOUND);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查詢人臉註冊列表")
    public Result<List<DeviceFaceEntity>> listFaces(
            HttpServletRequest request,
            @ApiParam("设备序列号") @RequestParam(required = false) String deviceSn) {
        if (!checkAuth(request)) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        List<DeviceFaceEntity> list = deviceFaceService.listFaces(deviceSn);
        return Result.success(list);
    }
}

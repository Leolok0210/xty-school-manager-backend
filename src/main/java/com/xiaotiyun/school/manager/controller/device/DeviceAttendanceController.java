package com.xiaotiyun.school.manager.controller.device;

import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.model.req.DeviceAttendanceReqModel;
import com.xiaotiyun.school.manager.model.res.DeviceAttendanceResModel;
import com.xiaotiyun.school.manager.service.DeviceAttendanceService;
import com.xiaotiyun.school.manager.service.DeviceTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = "设备考勤记录")
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class DeviceAttendanceController {
    private final DeviceAttendanceService deviceAttendanceService;
    private final DeviceTokenService deviceTokenService;

    private boolean checkAuth(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        return deviceTokenService.validateToken(token);
    }

    @PostMapping("/record")
    @ApiOperation(value = "上傳單筆考勤記錄")
    public Result<DeviceAttendanceResModel> uploadRecord(
            HttpServletRequest request,
            @RequestBody DeviceAttendanceReqModel reqModel) {
        if (!checkAuth(request)) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        DeviceAttendanceResModel res = deviceAttendanceService.record(reqModel);
        return Result.success(res);
    }

    @PostMapping("/records")
    @ApiOperation(value = "批量上傳考勤記錄")
    public Result<List<String>> uploadRecords(
            HttpServletRequest request,
            @RequestBody List<DeviceAttendanceReqModel> records) {
        if (!checkAuth(request)) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        List<String> ids = deviceAttendanceService.batchRecord(records);
        return Result.success(ids);
    }

    @GetMapping("/records")
    @ApiOperation(value = "查詢考勤記錄")
    public Result<List<DeviceAttendanceResModel>> queryRecords(
            HttpServletRequest request,
            @ApiParam("日期 yyyy-MM-dd") @RequestParam(required = false) String date,
            @ApiParam("班級ID") @RequestParam(required = false) Long classId) {
        if (!checkAuth(request)) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        List<DeviceAttendanceResModel> records = deviceAttendanceService.queryRecords(date, classId);
        return Result.success(records);
    }

    @GetMapping("/stats")
    @ApiOperation(value = "考勤統計")
    public Result<Object> getStats(
            HttpServletRequest request,
            @ApiParam("類型: day/week/month") @RequestParam String type,
            @ApiParam("日期 yyyy-MM-dd") @RequestParam String date,
            @ApiParam("班級ID") @RequestParam(required = false) Long classId) {
        if (!checkAuth(request)) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        Object stats = deviceAttendanceService.stats(type, date, classId);
        return Result.success(stats);
    }
}

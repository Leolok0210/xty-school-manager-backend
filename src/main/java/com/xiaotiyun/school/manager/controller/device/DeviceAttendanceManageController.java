package com.xiaotiyun.school.manager.controller.device;

import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.service.DeviceAttendanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Api(tags = "设备考勤管理查询")
@RequestMapping("/api/manage/device-attendance")
@RequiredArgsConstructor
public class DeviceAttendanceManageController {
    private final DeviceAttendanceService deviceAttendanceService;

    @GetMapping("/records")
    @ApiOperation(value = "分页查询设备考勤记录")
    public Result<Map<String, Object>> queryRecords(
            @ApiParam("日期 yyyy-MM-dd") @RequestParam(required = false) String date,
            @ApiParam("班级ID") @RequestParam(required = false) Long classId,
            @ApiParam("人员类型 student/teacher") @RequestParam(required = false) String personType,
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Map<String, Object> result = deviceAttendanceService.queryRecordsPage(pageNum, pageSize, date, classId, personType);
            return Result.success(result);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/stats")
    @ApiOperation(value = "设备考勤统计")
    public Result<Map<String, Object>> getStats(
            @ApiParam("类型: day/week/month") @RequestParam(defaultValue = "day") String type,
            @ApiParam("日期 yyyy-MM-dd") @RequestParam String date,
            @ApiParam("班级ID") @RequestParam(required = false) Long classId,
            @ApiParam("人员类型 student/teacher") @RequestParam(required = false) String personType) {
        try {
            Map<String, Object> result = deviceAttendanceService.statsForManage(type, date, classId, personType);
            return Result.success(result);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }
}

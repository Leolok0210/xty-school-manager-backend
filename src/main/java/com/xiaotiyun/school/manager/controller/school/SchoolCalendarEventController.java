package com.xiaotiyun.school.manager.controller.school;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.SchoolCalendarEventSaveReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolCalendarEventSaveV230ReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolCalendarEventUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SchoolCalendarEventResModel;
import com.xiaotiyun.school.manager.service.SchoolCalendarEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "校历事项管理")
@RestController
@RequestMapping("/api/school/calendar/events")
@RequiredArgsConstructor
public class SchoolCalendarEventController extends BasicController {
    private final SchoolCalendarEventService eventService;

    @ApiOperation("新增事项")
    @PostMapping
    @SaCheckPermission("schoolCalendarEvent:add")
    public Result<Void> save(@ApiParam("事项信息") @Valid @RequestBody SchoolCalendarEventSaveReqModel reqModel) {
        eventService.save(reqModel);
        return Result.success();
    }

    @ApiOperation("新增/编辑事项(V2.3.0)")
    @PostMapping("/v230/addOrEdit")
    @SaCheckPermission("schoolCalendarEvent:addOrEdit")
    public Result<Void> addOrEditV230(@ApiParam("事项信息") @Valid @RequestBody SchoolCalendarEventSaveV230ReqModel reqModel) {
        eventService.addOrEditV230(reqModel);
        return Result.success();
    }

    @ApiOperation("修改事项")
    @PutMapping("/{id}")
    @SaCheckPermission("schoolCalendarEvent:update")
    public Result<Void> update(
            @ApiParam("事项ID") @PathVariable Long id,
            @ApiParam("事项信息") @Valid @RequestBody SchoolCalendarEventUpdateReqModel reqModel) {
        eventService.update(id, reqModel);
        return Result.success();
    }

    @ApiOperation("删除事项")
    @DeleteMapping("/{id}")
    @SaCheckPermission("schoolCalendarEvent:delete")
    public Result<Void> delete(@ApiParam("事项ID") @PathVariable Long id) {
        eventService.delete(id);
        return Result.success();
    }

    @ApiOperation("获取事项列表")
    @GetMapping("/{calendarId}")
    @SaCheckPermission("schoolCalendarEvent:getEvents")
    public Result<List<SchoolCalendarEventResModel>> getEvents(@ApiParam("校历ID") @PathVariable Long calendarId) {
        return Result.success(eventService.listByCalendarId(calendarId));
    }

    @ApiOperation("获取事项列表-学生端(非鉴权)")
    @GetMapping("student/{calendarId}")
    public Result<List<SchoolCalendarEventResModel>> getEventsByStudent(@ApiParam("校历ID") @PathVariable Long calendarId) {
        return Result.success(eventService.listByCalendarId(calendarId));
    }
} 
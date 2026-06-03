package com.xiaotiyun.school.manager.controller.school;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.SchoolCalendarPageReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolCalendarSaveReqModel;
import com.xiaotiyun.school.manager.model.res.SchoolCalendarPageResModel;
import com.xiaotiyun.school.manager.model.res.SchoolCalendarResModel;
import com.xiaotiyun.school.manager.model.res.SchoolCalendarV230ResModel;
import com.xiaotiyun.school.manager.service.SchoolCalendarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "校历管理")
@RestController
@RequestMapping("/api/school/calendar")
@RequiredArgsConstructor
public class SchoolCalendarController extends BasicController {
    private final SchoolCalendarService schoolCalendarService;

    @ApiOperation("校历分页查询")
    @GetMapping("/page")
    @SaCheckPermission("schoolCalendar:page")
    public Result<PageInfo<SchoolCalendarPageResModel>> page(@ApiParam("查询参数") @Validated SchoolCalendarPageReqModel reqModel) {
        return Result.success(schoolCalendarService.page(reqModel));
    }

    @ApiOperation("校历分页查询-学生端(非鉴权)")
    @GetMapping("/student/page")
    public Result<PageInfo<SchoolCalendarPageResModel>> pageByStudent(@ApiParam("查询参数") @Validated SchoolCalendarPageReqModel reqModel) {
        return Result.success(schoolCalendarService.page(reqModel));
    }

    @ApiOperation("新增校历")
    @PostMapping
    @SaCheckPermission("schoolCalendar:add")
    public Result<Long> save(
            @ApiParam("校历信息") @Validated @RequestBody SchoolCalendarSaveReqModel reqModel) {
        return Result.success(schoolCalendarService.save(reqModel));
    }

    @ApiOperation("修改校历")
    @PutMapping("/{id}")
    @SaCheckPermission("schoolCalendar:update")
    public Result<Void> update(
            @ApiParam("校历ID") @PathVariable Long id,
            @ApiParam("校历信息") @Validated @RequestBody SchoolCalendarSaveReqModel reqModel) {
        schoolCalendarService.update(id, reqModel);
        return Result.success();
    }

    @ApiOperation("获取校历信息")
    @GetMapping("/{id}")
    @SaCheckPermission("schoolCalendar:info")
    public Result<SchoolCalendarResModel> info(@ApiParam("校历ID") @PathVariable Long id) {
        return Result.success(schoolCalendarService.info(id));
    }

    @ApiOperation("获取校历信息(v2.3.0)")
    @GetMapping("/v230/{id}")
    @SaCheckPermission("schoolCalendar:info")
    public Result<SchoolCalendarV230ResModel> infoV230(@ApiParam("校历ID") @PathVariable Long id) {
        return Result.success(schoolCalendarService.infoV230(id));
    }

    @ApiOperation("删除校历")
    @DeleteMapping("/{id}")
    @SaCheckPermission("schoolCalendar:delete")
    public Result<Void> delete(@ApiParam("校历ID") @PathVariable Long id) {
        schoolCalendarService.delete(id);
        return Result.success();
    }
}
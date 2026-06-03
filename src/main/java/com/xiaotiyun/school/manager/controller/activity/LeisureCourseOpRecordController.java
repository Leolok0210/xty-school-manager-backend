package com.xiaotiyun.school.manager.controller.activity;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.LeisureCourseOpRecordQuery;
import com.xiaotiyun.school.manager.model.res.LeisureCourseOpRecordRes;
import com.xiaotiyun.school.manager.service.LeisureCourseOpRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "余暇活动课程操作记录管理")
@RestController
@RequestMapping("/api/leisure/course/operation")
@RequiredArgsConstructor
public class LeisureCourseOpRecordController {
    private final LeisureCourseOpRecordService leisureCourseOpRecordService;

    @ApiOperation("余暇课程操作记录查询-分页")
    @PostMapping("/page")
    @SaCheckPermission("leisureCourseOpRecord:page")
    public Result<PageInfo<LeisureCourseOpRecordRes>> add(@RequestBody @Validated LeisureCourseOpRecordQuery query) {
        return Result.success(leisureCourseOpRecordService.page(query));
    }
}

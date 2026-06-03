package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.CrossBorderStudentReqModel;
import com.xiaotiyun.school.manager.model.res.CrossBorderStudentResModel;
import com.xiaotiyun.school.manager.service.CrossBorderStudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 跨境学生登记控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/crossBorder")
@Api(tags = "跨境学生登记管理")
public class CrossBorderStudentController extends BasicController {
    private final CrossBorderStudentService crossBorderStudentService;

    @PostMapping
    @ApiOperation("保存跨境学生信息")
    @SaCheckPermission("student:crossBorder:save")
    public Result<Long> save(@RequestBody CrossBorderStudentReqModel reqModel) {
        return Result.success(crossBorderStudentService.save(reqModel));
    }

    @PutMapping("/{studentId}")
    @ApiOperation("修改跨境学生信息")
    @SaCheckPermission("student:crossBorder:update")
    public Result<Void> update(
            @PathVariable Long studentId,
            @Valid @RequestBody CrossBorderStudentReqModel reqModel) {
        crossBorderStudentService.update(studentId, reqModel);
        return Result.success();
    }

    @GetMapping("/info/{studentId}")
    @ApiOperation("获取跨境学生信息")
    @SaCheckPermission("student:crossBorder:info")
    public Result<CrossBorderStudentResModel> info(@PathVariable Long studentId) {
        return Result.success(crossBorderStudentService.info(studentId));
    }
}
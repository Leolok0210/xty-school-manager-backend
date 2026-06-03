package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentBusinessPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentBusinessSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentBusinessPageResModel;
import com.xiaotiyun.school.manager.service.StudentBusinessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/business")
@Api(tags = "学生公务管理")
public class StudentBusinessController extends BasicController {
    private final StudentBusinessService studentBusinessService;

    @GetMapping("/page")
    @ApiOperation("分页查询公务记录")
    @SaCheckPermission("student:business:page")
    public Result<PageInfo<StudentBusinessPageResModel>> page(@Validated StudentBusinessPageReqModel reqModel) {
        reqModel.setUserId(getUserId());
        return Result.success(studentBusinessService.page(getSchoolId(), reqModel));
    }

    @PostMapping
    @ApiOperation("新增公务记录")
    @SaCheckPermission("student:business:add")
    public Result<Void> save(@Valid @RequestBody StudentBusinessSaveReqModel reqModel) {
        studentBusinessService.save(getSchoolId(), reqModel);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改公务记录")
    @SaCheckPermission("student:business:update")
    public Result<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody StudentBusinessSaveReqModel reqModel) {
        studentBusinessService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除公务记录")
    @SaCheckPermission("student:business:delete")
    public Result<Void> delete(@PathVariable Long id) {
        studentBusinessService.delete(id);
        return Result.success();
    }

    @GetMapping("/export")
    @ApiOperation("导出公务记录")
    @SaCheckPermission("student:business:export")
    public Result<String> exportBusiness(@Validated StudentBusinessPageReqModel reqModel) {
        return Result.success(studentBusinessService.export(getSchoolId(), reqModel));
    }
} 
package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentPromotionPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentPromotionSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentPromotionUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentPromotionResModel;
import com.xiaotiyun.school.manager.service.StudentPromotionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/promotions")
@Api(tags = "升留级带科登记管理")
public class StudentPromotionController extends BasicController {
    private final StudentPromotionService studentPromotionService;

    @GetMapping("/student/list")
    @SaCheckPermission("student:promotion:student:list")
    @ApiOperation("已登记学生列表")
    public Result<List<Long>> studentList(@RequestParam(value = "schoolYear") String schoolYear, @RequestParam(value = "classId") Long classId) {
        return Result.success(studentPromotionService.studentList(getSchoolId(), schoolYear, classId));
    }

    @GetMapping("/page")
    @SaCheckPermission("student:promotion:page")
    @ApiOperation("分页查询记录")
    public Result<PageInfo<StudentPromotionResModel>> page(@Validated StudentPromotionPageReqModel reqModel) {
        reqModel.setUserId(getUserId());
        return Result.success(studentPromotionService.page(getSchoolId(), reqModel));
    }

    @PostMapping
    @SaCheckPermission("student:promotion:add")
    @ApiOperation("新增登记记录")
    public Result<Void> add(@Validated @RequestBody StudentPromotionSaveReqModel reqModel) {
        studentPromotionService.save(getSchoolId(), reqModel);
        return Result.success();
    }

    @PutMapping("/{id}")
    @SaCheckPermission("student:promotion:update")
    @ApiOperation("修改登记记录")
    public Result<Void> update(@PathVariable Long id, @RequestBody StudentPromotionUpdateReqModel reqModel) {
        studentPromotionService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("student:promotion:delete")
    @ApiOperation("删除登记记录")
    public Result<Void> delete(@PathVariable Long id) {
        studentPromotionService.delete(id);
        return Result.success();
    }

    @GetMapping("/export")
    @SaCheckPermission("student:promotion:export")
    @ApiOperation("导出Excel")
    public Result<String> export(@Validated StudentPromotionPageReqModel reqModel) {
        return Result.success(studentPromotionService.export(getSchoolId(), reqModel));
    }
} 
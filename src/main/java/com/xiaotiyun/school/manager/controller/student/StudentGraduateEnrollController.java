package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentGraduateEnrollBatcheSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateEnrollPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateEnrollSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateEnrollPageResModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateEnrollResModel;
import com.xiaotiyun.school.manager.service.StudentGraduateEnrollService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "毕业登记管理")
@RestController
@RequestMapping("/api/student/graduate/enroll")
public class StudentGraduateEnrollController extends BasicController {
    @Resource
    private StudentGraduateEnrollService studentGraduateEnrollService;

    @ApiOperation("分页查询列表")
    @GetMapping("/page")
    @SaCheckPermission("studentGraduateEnroll:page")
    public Result<PageInfo<StudentGraduateEnrollPageResModel>> page(@ApiParam("查询参数") @Validated StudentGraduateEnrollPageReqModel reqModel) {
        reqModel.setUserId(getUserId());
        return Result.success(studentGraduateEnrollService.page(reqModel));
    }

    @ApiOperation("新增")
    @PostMapping("/add")
    @SaCheckPermission("studentGraduateEnroll:add")
    public Result<Void> save(@ApiParam("毕业登记信息") @Validated @RequestBody StudentGraduateEnrollSaveReqModel reqModel) {
        studentGraduateEnrollService.save(reqModel);
        return Result.success();
    }

    @ApiOperation("批量新增")
    @PostMapping("/batch/add")
    @SaCheckPermission("studentGraduateEnroll:batch:add")
    public Result<Void> batchAdd(@ApiParam("毕业登记信息") @Validated @RequestBody StudentGraduateEnrollBatcheSaveReqModel reqModel) {
        studentGraduateEnrollService.batchSave(reqModel);
        return Result.success();
    }

    @ApiOperation("已登记的学生列表")
    @GetMapping("/student/list")
    @SaCheckPermission("studentGraduateEnroll:student:list")
    public Result<List<Long>> studentList(@ApiParam("毕业登记信息") @RequestParam(value = "classId") Long classId) {
        return Result.success(studentGraduateEnrollService.studentList(classId));
    }

    @ApiOperation("修改")
    @PutMapping("/update/{id}")
    @SaCheckPermission("studentGraduateEnroll:update")
    public Result<Void> update(
            @ApiParam("id") @PathVariable Long id,
            @ApiParam("毕业登记信息") @Validated @RequestBody StudentGraduateEnrollSaveReqModel reqModel) {
        studentGraduateEnrollService.update(id, reqModel);
        return Result.success();
    }

    @ApiOperation("获取信息")
    @GetMapping("/info/{id}")
    @SaCheckPermission("studentGraduateEnroll:info")
    public Result<StudentGraduateEnrollResModel> info(
            @ApiParam("id") @PathVariable Long id) {
        return Result.success(studentGraduateEnrollService.info(id));
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("studentGraduateEnroll:delete")
    public Result<Void> delete(@ApiParam("id") @PathVariable Long id) {
        studentGraduateEnrollService.delete(id);
        return Result.success();
    }
}
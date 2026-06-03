package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentEnrollSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentEnrollResModel;
import com.xiaotiyun.school.manager.service.StudentEnrollService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "入学记录")
@RestController
@RequestMapping("/api/student/enroll")
public class StudentEnrollController {
    @Resource
    private StudentEnrollService studentEnrollService;

    @ApiOperation("新增/编辑")
    @PostMapping("/addOrEdit")
    @SaCheckPermission("studentEnroll:addOrEdit")
    public Result<Void> addOrEdit(@ApiParam("信息") @Validated @RequestBody StudentEnrollSaveReqModel reqModel) {
        studentEnrollService.addOrEdit(reqModel);
        return Result.success();
    }

    @ApiOperation("获取")
    @GetMapping("/info/{studentId}")
    @SaCheckPermission("student:info")
    public Result<StudentEnrollResModel> info(
            @ApiParam("studentId") @PathVariable Long studentId) {
        return Result.success(studentEnrollService.info(studentId));
    }
}
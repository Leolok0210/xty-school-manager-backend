package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentDateRecordUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentDateRecordResModel;
import com.xiaotiyun.school.manager.service.StudentDateRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/studentDateRecord")
@Api(tags = "学生日期记录管理")
@RequiredArgsConstructor
public class StudentDateRecordController extends BasicController {

    private final StudentDateRecordService studentDateRecordService;

    @GetMapping("/get/{id}")
    @SaCheckPermission("studentDateRecord:get")
    @ApiOperation("根据ID获取学生日期记录")
    public Result<StudentDateRecordResModel> getStudentDateRecordById(@PathVariable Long id) {
        return Result.success(studentDateRecordService.getStudentDateRecordById(id, getSchoolId()));
    }

    @PostMapping("/update")
    @SaCheckPermission("studentDateRecord:update")
    @ApiOperation("更新学生日期记录")
    public Result<String> updateStudentDateRecord(@Valid @RequestBody StudentDateRecordUpdateReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        return studentDateRecordService.updateStudentDateRecord(reqModel);
    }
}
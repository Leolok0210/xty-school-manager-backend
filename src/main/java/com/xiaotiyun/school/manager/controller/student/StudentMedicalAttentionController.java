package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentMedicalAttentionReqModel;
import com.xiaotiyun.school.manager.model.req.StudentMedicalAttentionUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentMedicalAttentionResModel;
import com.xiaotiyun.school.manager.service.StudentMedicalAttentionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical/attention")
@Api(tags = "学生医护注意事项业务控制器")
@RequiredArgsConstructor
public class StudentMedicalAttentionController extends BasicController {

    private final StudentMedicalAttentionService studentMedicalAttentionService;

    @GetMapping("/get")
    @SaCheckPermission("medicalAttention:get")
    @ApiOperation("获取学生医护注意事项")
    public Result<StudentMedicalAttentionResModel> listStudentMedicalAttentions(@Validated StudentMedicalAttentionReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        return studentMedicalAttentionService.listStudentMedicalAttentions(reqModel);
    }

    @PostMapping("/update")
    @SaCheckPermission("medicalAttention:update")
    @ApiOperation("更新学生医护注意事项")
    public Result<String> updateStudentMedicalAttention(@Validated @RequestBody StudentMedicalAttentionUpdateReqModel entity) {
        return studentMedicalAttentionService.updateStudentMedicalAttention(entity, getSchoolId());
    }
}
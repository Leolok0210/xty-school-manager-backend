package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.MedicalRecordReqModel;
import com.xiaotiyun.school.manager.model.req.StudentMedicalRecordAddReqModel;
import com.xiaotiyun.school.manager.model.req.StudentMedicalRecordUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.MedicalRecordResModel;
import com.xiaotiyun.school.manager.service.StudentMedicalRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/student/medical")
@Api(tags = "学生医护保健记录业务控制器")
@RequiredArgsConstructor
public class StudentMedicalRecordController extends BasicController {

    private final StudentMedicalRecordService studentMedicalRecordService;

    @GetMapping("/page")
    @SaCheckPermission("studentMedical:page")
    @ApiOperation("学生医护保健记录-分页查询")
    public Result<PageInfo<MedicalRecordResModel>> listStudentMedicalRecords(@Validated MedicalRecordReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        return studentMedicalRecordService.listStudentMedicalRecords(reqModel);
    }

    @PostMapping("/add")
    @SaCheckPermission("studentMedical:add")
    @ApiOperation("新增学生医护保健记录")
    public Result<String> addStudentMedicalRecord(@Validated @RequestBody StudentMedicalRecordAddReqModel entity) {
        return studentMedicalRecordService.addStudentMedicalRecord(entity, getSchoolId());
    }

    @PostMapping("/update")
    @SaCheckPermission("studentMedical:update")
    @ApiOperation("更新学生医护保健记录")
    public Result<String> updateStudentMedicalRecord(@Validated @RequestBody StudentMedicalRecordUpdateReqModel entity) {
        return studentMedicalRecordService.updateStudentMedicalRecord(entity);
    }

    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("studentMedical:delete")
    @ApiOperation("删除学生医护保健记录")
    public Result<String> deleteStudentMedicalRecord(@PathVariable Long id) {
        return studentMedicalRecordService.deleteStudentMedicalRecord(id);
    }

    @GetMapping("/export")
    @SaCheckPermission("studentMedical:export")
    @ApiOperation("导出学生医护保健记录")
    public ResponseEntity<byte[]> exportStudentMedicalRecords(MedicalRecordReqModel reqModel) throws IOException {
        reqModel.setSchoolId(getSchoolId());
        return studentMedicalRecordService.exportMedicalRecords(reqModel);
    }

    @PostMapping("/import")
    @SaCheckPermission("studentMedical:import")
    @ApiOperation("批量导入学生医护保健记录")
    public Result<Long> importMedicalRecords(@RequestPart("file") MultipartFile file, @RequestPart("schoolYear") String schoolYear) {
        Long taskId = studentMedicalRecordService.importMedicalRecords(file, getSchoolId(), schoolYear);
        return Result.success(taskId);
    }
}
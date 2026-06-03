package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtils;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.req.ClassTopStudentsReqModel;
import com.xiaotiyun.school.manager.model.req.StudentSubjectScoresReqModel;
import com.xiaotiyun.school.manager.model.req.YearGradeCheckReqModel;
import com.xiaotiyun.school.manager.model.res.ClassTopStudentsResModel;
import com.xiaotiyun.school.manager.model.res.QualityIndicatorListResModel;
import com.xiaotiyun.school.manager.model.res.StudentSubjectScoresResModel;
import com.xiaotiyun.school.manager.model.res.YearGradeCheckResModel;
import com.xiaotiyun.school.manager.service.StudentScoreCheckService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 学生成绩检查
 */
@Api(tags = "学生成绩检查")
@RestController
@RequestMapping("/api/score/student/check")
@Slf4j
public class StudentGradeCheckController extends BasicController {

    @Resource
    private StudentScoreCheckService studentScoreCheckService;

    @ApiOperation("各班名列前茅名单")
    @SaCheckPermission("student:check:topStudents")
//    @SaIgnore
    @PostMapping("/class/topStudents")
    public Result<List<ClassTopStudentsResModel>> getClassTopStudents(
            @Validated @RequestBody ClassTopStudentsReqModel reqModel) {
        return Result.success(studentScoreCheckService.getClassTopStudents(reqModel));
    }

    @ApiOperation("查询学生各科成绩")
    @SaCheckPermission("student:check:studentSubjectScores")
//    @SaIgnore
    @PostMapping("/subject/scores")
    public Result<List<StudentSubjectScoresResModel>> getStudentSubjectScores(
            @Validated @RequestBody StudentSubjectScoresReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        return Result.success(studentScoreCheckService.getStudentSubjectScores(reqModel));
    }

    @ApiOperation("学年成绩检查查询")
//    @SaIgnore
    @SaCheckPermission("student:check:yearCheck")
    @PostMapping("/year/check")
    public Result<List<YearGradeCheckResModel>> getYearGradeCheck(
            @Validated @RequestBody YearGradeCheckReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        return Result.success(studentScoreCheckService.getYearGradeCheck(reqModel));
    }

    @ApiOperation("学年成绩检查导出")
    @SaCheckPermission("student:check:yearCheckExport")
//    @SaIgnore
    @PostMapping("/year/check/export")
    public ResponseEntity<byte[]> exportYearGradeCheck(HttpServletResponse response,
            @Validated @RequestBody YearGradeCheckReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        String fileName = "班级学生学年成绩_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ".xlsx";

        // 获取Excel数据并生成字节数组
        byte[] excelBytes = studentScoreCheckService.getExportYearGradeCheck(reqModel, fileName);
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

    @ApiOperation("导出各班名列前茅名单")
//    @SaIgnore
    @SaCheckPermission("student:check:topStudentsExport")
    @PostMapping("/class/topStudents/export")
    public ResponseEntity<byte[]> exportClassTopStudents(
            @Validated @RequestBody ClassTopStudentsReqModel reqModel) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String fileName = "各班名列前茅名单_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ".xlsx";
        headers.setContentDispositionFormData("attachment", fileName);
        byte[] excelBytes = studentScoreCheckService.exportClassTopStudents(reqModel);
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

    @ApiOperation("导出学生各科成绩")
//    @SaIgnore
    @SaCheckPermission("student:check:studentSubjectScoresExport")
    @PostMapping("/subject/scores/export")
    public ResponseEntity<byte[]> exportStudentSubjectScores(
            @Validated @RequestBody StudentSubjectScoresReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String fileName = "学生各科成绩_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ".xlsx";
        headers.setContentDispositionFormData("attachment", fileName);
        byte[] excelBytes = studentScoreCheckService.exportStudentSubjectScores(reqModel);
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
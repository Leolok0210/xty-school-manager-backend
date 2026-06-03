package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentLateReportDayReqModel;
import com.xiaotiyun.school.manager.model.req.StudentLateReportMonthReqModel;
import com.xiaotiyun.school.manager.model.res.StudentLateDayReportPageResModel;
import com.xiaotiyun.school.manager.model.res.StudentLateMonthReportPageResModel;
import com.xiaotiyun.school.manager.service.StudentReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/report")
@Api(tags = "学生相关报表业务控制器")
@RequiredArgsConstructor
public class StudentReportController  extends BasicController {

    private final StudentReportService studentReportService;

    @GetMapping("/late/day/page")
    @SaCheckPermission("studentReportLateDay:page")
    @ApiOperation("学生迟到统计-天-分页查询")
    public Result<StudentLateDayReportPageResModel> listStudentMedicalRecords(@Validated StudentLateReportDayReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        return studentReportService.listStudentLateDayReports(reqModel);
    }

    @GetMapping("/late/month/export")
    @SaCheckPermission("studentReportLateMonth:page")
    @ApiOperation("学生迟到统计-月-分页查询")
    public Result<StudentLateMonthReportPageResModel> exportStudentMedicalRecords(@Validated StudentLateReportMonthReqModel reqModel) {
        reqModel.setSchoolId(getSchoolId());
        return studentReportService.listStudentLateMonthReports(reqModel);
    }
}

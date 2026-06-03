package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentLateReportDayReqModel;
import com.xiaotiyun.school.manager.model.req.StudentLateReportMonthReqModel;
import com.xiaotiyun.school.manager.model.res.StudentLateDayReportPageResModel;
import com.xiaotiyun.school.manager.model.res.StudentLateMonthReportPageResModel;

public interface StudentReportService {
    Result<StudentLateDayReportPageResModel> listStudentLateDayReports(StudentLateReportDayReqModel reqModel);

    Result<StudentLateMonthReportPageResModel> listStudentLateMonthReports(StudentLateReportMonthReqModel reqModel);
}

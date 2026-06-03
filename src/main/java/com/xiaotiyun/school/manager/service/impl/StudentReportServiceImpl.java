package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.StudentLateReportDayReqModel;
import com.xiaotiyun.school.manager.model.req.StudentLateReportMonthReqModel;
import com.xiaotiyun.school.manager.model.res.StudentLateDayReportPageResModel;
import com.xiaotiyun.school.manager.model.res.StudentLateMonthReportPageResModel;
import com.xiaotiyun.school.manager.service.StudentReportService;
import com.xiaotiyun.school.manager.support.ReportSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class StudentReportServiceImpl implements StudentReportService {

    @Resource
    ReportSupport reportSupport;

    @Override
    public Result<StudentLateDayReportPageResModel> listStudentLateDayReports(StudentLateReportDayReqModel reqModel){
        String respStr = reportSupport.getReport(ReportSupport.STUDENT_LATE_DAY, JSONObject.parseObject(JSONObject.toJSONString(reqModel)));
        JSONObject resp = JSONObject.parseObject(respStr);
        if (resp.getInteger("error") == 10000)
            return Result.success(JSONObject.parseObject(resp.getString("data"), StudentLateDayReportPageResModel.class));
        log.error("listStudentLateDayReports error:{}", respStr);
        return Result.failed();
    }

    @Override
    public Result<StudentLateMonthReportPageResModel> listStudentLateMonthReports(StudentLateReportMonthReqModel reqModel){
        String respStr = reportSupport.getReport(ReportSupport.STUDENT_LATE_MONTH, JSONObject.parseObject(JSONObject.toJSONString(reqModel)));
        JSONObject resp = JSONObject.parseObject(respStr);
        if (resp.getInteger("error") == 10000)
            return Result.success(JSONObject.parseObject(resp.getString("data"), StudentLateMonthReportPageResModel.class));
        log.error("listStudentLateMonthReports error:{}", respStr);
        return Result.failed();
    }
}

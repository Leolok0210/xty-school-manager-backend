package com.xiaotiyun.school.manager.support;

import cn.hutool.http.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用于内容调用数据服务，公司内部python服务
 */
@Component
public class ReportSupport {

    @Value("${xtySchool.report.url}")
    private String reportUrl;

    public static final String STUDENT_LATE_DAY = "/statistic/student/attendance/day";
    public static final String STUDENT_LATE_MONTH = "/statistic/student/attendance/month";

    public static final String STUDENT_IMAGE_PDF = "/api/student/downloadImage/merged";

    public String getReport(String url, Map<String, Object> req) {
        return HttpUtil.get(reportUrl + url, req);
    }
}

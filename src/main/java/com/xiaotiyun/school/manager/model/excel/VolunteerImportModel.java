package com.xiaotiyun.school.manager.model.excel;

import lombok.Data;

@Data
public class VolunteerImportModel extends BasicImportModel {
    // 學生（必填）
    private String studentName;
    // 學生編號（必填）
    private String studentNo;
    // 活动名称（必填）
    private String activityName;
    // 机构名称（必填）
    private String organization;
    // 服务日期（必填）
    private String serviceDate;
    // 服务时数（必填）
    private String serviceHours;
}
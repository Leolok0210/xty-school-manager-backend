package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

/**
 * 活动学生报告导出DTO
 */
@Data
public class ActivityStudentReportExportDTO {
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 班级名称
     */
    private String className;
    
    /**
     * 学号
     */
    private String studentNo;
} 
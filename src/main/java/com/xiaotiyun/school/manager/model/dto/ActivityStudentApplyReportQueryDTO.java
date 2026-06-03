package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

/**
 * 活动学生报名查询结果DTO
 */
@Data
public class ActivityStudentApplyReportQueryDTO {
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生编号
     */
    private String studentNo;
    
    /**
     * 匹配课程ID
     */
    private Long matchedCourseId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 级组名称
     */
    private String gradeGroupName;
} 
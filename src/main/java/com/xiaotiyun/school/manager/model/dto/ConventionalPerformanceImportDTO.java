package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ConventionalPerformanceImportDTO {
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 学生编号
     */
    private String studentCode;
    /**
     * 事件日期
     */
    private LocalDate date;
    /**
     * 欠作业
     */
    private Integer missingHomework;
    /**
     * 欠课本
     */
    private Integer missingTextbook;
    /**
     * 上课违规
     */
    private Integer classViolation;
    /**
     * 仪表不符
     */
    private Integer uniformNonCompliance;
    /**
     * 欠回条
     */
    private Integer missingReturnSticker;
    /**
     * 欠作业備註
     */
    private String missingHomeworkRemark;
    /**
     * 欠课本備註
     */
    private String missingTextbookRemark;
    /**
     * 上课违规備註
     */
    private String classViolationRemark;
    /**
     * 仪表不符備註
     */
    private String uniformNonComplianceRemark;
    /**
     * 欠回条備註
     */
    private String missingReturnStickerRemark;
}

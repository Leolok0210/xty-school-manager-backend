package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StudentGraduateExamScoreDetailDTO {
    /**
     * id
     */
    private Long id;
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 座位号
     */
    private Integer seatNo;
    /**
     * 中文姓名
     */
    private String chineseName;
    /**
     * 外文姓名
     */
    private String englishName;
    /**
     * 成绩展示姓名类型(1:中文姓名,2:外文姓名)
     */
    private Integer displayNameType;
    /**
     * 学生编号
     */
    private String studentNo;
    /**
     * 科目id
     */
    private Long subjectId;
    /**
     * 科目名称
     */
    private String subjectName;
    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 测试时间
     */
    private Date testDate;
    /**
     * 成绩*100
     */
    private Integer score;
}

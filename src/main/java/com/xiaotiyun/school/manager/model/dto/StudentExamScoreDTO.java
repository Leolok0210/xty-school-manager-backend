package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentExamScoreDTO {
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
     * 成绩*100
     */
    private Integer score;
    /**
     * 更新人
     */
    private String updateUser;
    /**
     * 录入时间
     */
    private LocalDateTime createTime;
}

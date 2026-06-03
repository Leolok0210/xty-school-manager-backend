package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_enroll")
public class StudentEnrollEntity extends BaseEntity {
    /**
     * 学校id
     */
    private Long schoolId;
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 原校名称
     */
    private String schoolName;
    /**
     * 原校学历
     */
    private String qualification;
    /**
     * 班别
     */
    private String className;
    /**
     * 学年
     */
    private String schoolYear;
    /**
     * 学期(1.升级;2.留级;3.中途退学)
     */
    private Integer type;
    /**
     * 语文成绩
     */
    private String chineseScore;
    /**
     * 英文成绩
     */
    private String englishScore;
    /**
     * 数学成绩
     */
    private String mathsScore;
    /**
     * 平均分
     */
    private String averageScore;
    /**
     * 评语
     */
    private String comment;
    /**
     * 操行
     */
    private String behavior;
    /**
     * 奖惩
     */
    private String bonusPenalty;
    /**
     * 报考年级
     */
    private String gradeApplication;
    /**
     * 录取年级
     */
    private String admissionGrade;
    /**
     * 介绍人
     */
    private String referrer;
    /**
     * 入学语文成绩
     */
    private String enrollChineseScore;
    /**
     * 入学英文成绩
     */
    private String enrollEnglishScore;
    /**
     * 入学数学成绩
     */
    private String enrollMathsScore;
}
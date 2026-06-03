package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

/**
 * 素质登记评语设定表
 * @TableName quality_evaluation_comment
 */
@TableName(value ="quality_evaluation_comment")
@Data
public class QualityEvaluationComment extends BaseEntity{

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 老师id
     */
    private Long teacherId;

    /**
     * 评语模板
     */
    private String comment;

}
package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_usually_score")
public class StudentUsuallyScoreEntity extends BaseEntity {
    /**
     * 学生平时分登记表id
     */
    private Long taskId;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 成绩*100
     */
    private Integer score;

    /**
     * 更新人id
     */
    private Long updateId;
} 
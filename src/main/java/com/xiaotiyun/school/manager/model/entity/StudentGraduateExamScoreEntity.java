package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_graduate_exam_score")
public class StudentGraduateExamScoreEntity extends BaseEntity {
    /**
     * 学生毕业考试登记表id
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
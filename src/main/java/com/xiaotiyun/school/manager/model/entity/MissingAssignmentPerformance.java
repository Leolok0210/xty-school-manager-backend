package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("missing_assignment_performance")
public class MissingAssignmentPerformance extends BaseEntity {
    /**
     * 所属学年
     */
    private String sid;

    /**
     * 学期
     */
    private Long term;

    /**
     * 班级id
     */
    private Long classId;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 日期
     */
    private LocalDateTime date;

    /**
     * 科目
     */
    private Long subjectId;

    private Long userId;

    /**
     * 作业描述
     */
    private String assignmentDescription;
}
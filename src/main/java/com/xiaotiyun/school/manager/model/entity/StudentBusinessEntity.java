package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_business")
@ApiModel(value = "学生公务实体")
public class StudentBusinessEntity extends BaseEntity {
    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 学年
     */
    private String schoolYear;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 班级id
     */
    private Long classId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 公务事由
     */
    private String reason;
} 
package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("student_leave_course")
public class StudentLeaveCourseEntity extends BaseEntity {

    @TableField("leave_id")
    private Long leaveId;

    @TableField("course_id")
    private Long courseId;
}
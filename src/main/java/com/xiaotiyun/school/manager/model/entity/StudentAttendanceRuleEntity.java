package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

import java.time.LocalTime;

@Data
@TableName("student_attendance_rule")
public class StudentAttendanceRuleEntity extends BaseEntity {
    /**
     * 学校ID
     */
    private Long schoolId;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 适用级组(级组id集合)
     */
    private String grade;
    /**
     * 上午入校时间
     */
    private LocalTime morningInTime;
    /**
     * 下午出校时间
     */
    private LocalTime afternoonOutTime;
}
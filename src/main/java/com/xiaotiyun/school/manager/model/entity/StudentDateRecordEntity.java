package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("student_date_record")
public class StudentDateRecordEntity extends BaseEntity {
    @TableField("school_id")
    private Long schoolId;

    @TableField("student_id")
    private Long studentId;

    @TableField("in_time")
    private String inTime;

    @TableField("out_time")
    private String outTime;

    @TableField("out_reason")
    private String outReason;

    @TableField("escalation_situation")
    private String escalationSituation;

    @TableLogic
    @TableField("deleted")
    private Long deleted;
}
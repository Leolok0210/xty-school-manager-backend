package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("dress_code_violation")
@ApiModel(description = "仪表不符登记实体对象")
public class DressCodeViolationEntity extends BaseEntity {
    @TableField("school_id")
    @ApiModelProperty(value = "学校ID", example = "1")
    private Long schoolId;

    @TableField("school_year")
    @ApiModelProperty(value = "学年", example = "2023-2024")
    private String schoolYear;

    @TableField("semester_id")
    @ApiModelProperty(value = "学段ID", example = "1")
    private Long semesterId;

    @TableField("class_id")
    @ApiModelProperty(value = "班级ID", example = "1")
    private Long classId;

    @TableField("student_id")
    @ApiModelProperty(value = "学生ID", example = "12345")
    private Long studentId;

    @TableField("violation_date")
    @ApiModelProperty(value = "日期", example = "2023-10-01")
    private String violationDate;

    @TableField("remark_id")
    @ApiModelProperty(value = "备注ID", example = "系统预设表中id")
    private Long remarkId;

    @TableField("remark")
    @ApiModelProperty(value = "备注", example = "未佩戴校徽")
    private String remark;

    @TableField("registrant")
    @ApiModelProperty(value = "登记人", example = "李四")
    private String registrant;

    @TableField("registrant_id")
    @ApiModelProperty(value = "登记人", example = "李四")
    private Long registrantId;
}
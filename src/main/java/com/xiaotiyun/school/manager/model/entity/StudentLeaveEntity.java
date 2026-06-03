package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_leave")
@ApiModel(value = "学生请假缺席实体")
public class StudentLeaveEntity extends BaseEntity {

    @ApiModelProperty(value = "学校id")
    private Long schoolId;

    @ApiModelProperty(value = "学年", example = "2023-2024")
    private String schoolYear;

    @ApiModelProperty(value = "班级ID", example = "201")
    private Long classId;

    @ApiModelProperty(value = "学生ID", example = "1001")
    private Long studentId;

    @ApiModelProperty(value = "请假日期", example = "2023-10-01")
    private LocalDate leaveDate;

    @ApiModelProperty(value = "类型 1-请假 2-缺席 3-迟到", example = "1")
    private Integer leaveType;

    @ApiModelProperty(value = "节数", example = "2")
    private Integer periods;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "登记人ID，若类型为教师为用户表ID，若为学生为学生表ID")
    private Long registrantId;

    @ApiModelProperty(value = "登记人姓名")
    private String registrantName;

    @ApiModelProperty(value = "登记人类型，0-教师，1-学生")
    private Integer registrantType;
} 
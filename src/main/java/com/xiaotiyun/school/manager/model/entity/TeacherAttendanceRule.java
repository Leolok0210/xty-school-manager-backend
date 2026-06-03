package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_attendance_rule")
@ApiModel(value = "教师考勤规则实体")
public class TeacherAttendanceRule extends BaseEntity {
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;

    @ApiModelProperty(value = "规则名称", required = true)
    private String ruleName;

    @ApiModelProperty(value = "规则类型(0.默认规则;1.特殊规则)", required = true)
    private Integer type;

    @ApiModelProperty(value = "部门ID列表")
    private String depIds; // 实际存储JSON数组

    @ApiModelProperty(value = "用户ID列表")
    private String userIds; // 实际存储JSON数组

    @ApiModelProperty(value = "生效范围(1-7.周一到周日)")
    private String effectiveScope; // 实际存储JSON数组

    @ApiModelProperty(value = "上班时间", required = true)
    private LocalTime clockInTime;

    @ApiModelProperty(value = "下班时间", required = true)
    private LocalTime clockOutTime;
} 
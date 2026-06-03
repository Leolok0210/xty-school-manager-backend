package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@ApiModel("教师出勤规则响应列表")
public class TeacherAttendanceRulePageResModel {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("学校id")
    private Long schoolId;
    @ApiModelProperty("规则名称")
    private String ruleName;
    @ApiModelProperty("规则类型(0.默认规则;1.特殊规则)")
    private Integer type;
    @ApiModelProperty(value = "部门信息(选择到部门时返回)")
    private List<TeacherAttendanceRuleDepPageResModel> depInfos;
    @ApiModelProperty(value = "用户信息(选择到用户时返回)")
    private List<TeacherAttendanceRuleUserPageResModel> userInfos;
    @ApiModelProperty("生效范围(1-7.周一到周日)")
    private List<Integer> effectiveScope;
    @ApiModelProperty(value = "上班时间")
    private LocalTime clockInTime;
    @ApiModelProperty(value = "下班时间")
    private LocalTime clockOutTime;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}
package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TeacherAttendanceRuleDepPageResModel {
    @ApiModelProperty("部门id")
    private Long depId;
    @ApiModelProperty("名称")
    private String name;
}
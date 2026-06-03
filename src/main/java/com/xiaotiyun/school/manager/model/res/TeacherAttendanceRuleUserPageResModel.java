package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TeacherAttendanceRuleUserPageResModel {
    @ApiModelProperty("用户id")
    private Long userId;
    @ApiModelProperty("用户名称")
    private String userName;
}
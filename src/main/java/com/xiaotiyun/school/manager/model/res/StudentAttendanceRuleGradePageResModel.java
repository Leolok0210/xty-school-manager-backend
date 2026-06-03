package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentAttendanceRuleGradePageResModel {
    @ApiModelProperty("级组id")
    private Long gradeId;
    @ApiModelProperty("级组名称")
    private String gradeName;
}
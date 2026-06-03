package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActivityStudentApplyRegisteredVolunteerResModel {
    @ApiModelProperty("课程名称")
    private String courseName;
    @ApiModelProperty("志愿数")
    private Long volunteerType;
    @ApiModelProperty("是否录取")
    private Boolean isAdmitted;
} 
package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 活动报名志愿列表响应模型
 */
@Data
@ApiModel("活动报名志愿列表响应模型")
public class ActivityStudentApplyReportVolunteerResModel {

    @ApiModelProperty("志愿课程ID")
    private Long lensonId;

    @ApiModelProperty("课程名称")
    private String lensonName;


    @ApiModelProperty("志愿位数")
    private Long volunteerType;
} 
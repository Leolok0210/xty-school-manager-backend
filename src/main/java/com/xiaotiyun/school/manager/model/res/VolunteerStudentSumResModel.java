package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 义工服务学生汇总响应详情
 */
@Data
@ApiModel("义工服务学生汇总响应详情")
public class VolunteerStudentSumResModel {

    @ApiModelProperty("学段ID")
    private Long semesterId;

    @ApiModelProperty("学段名称")
    private String semesterName;

    @ApiModelProperty("时数")
    private Double serviceHours;
}
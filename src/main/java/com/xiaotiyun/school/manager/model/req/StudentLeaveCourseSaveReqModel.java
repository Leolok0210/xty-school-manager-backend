package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("请假缺席具体课节请求参数")
public class StudentLeaveCourseSaveReqModel {
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "请假缺席记录ID")
    private Long leaveId;

    @ApiModelProperty(value = "课节ID", required = true)
    private Long courseId;
} 
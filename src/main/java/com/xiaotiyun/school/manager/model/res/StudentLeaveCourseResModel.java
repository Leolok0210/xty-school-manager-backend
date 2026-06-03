package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("请假缺席具体课节返回参数")
public class StudentLeaveCourseResModel {
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "请假缺席记录ID")
    private Long leaveId;

    @ApiModelProperty(value = "课节ID", required = true)
    private Long courseId;

    @ApiModelProperty(value = "课节名称", required = true)
    private String courseName;
}
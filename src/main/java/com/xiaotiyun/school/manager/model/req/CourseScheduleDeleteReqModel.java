package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("课表删除请求参数")
public class CourseScheduleDeleteReqModel {

    @NotNull(message = "课表id不能为空")
    @ApiModelProperty(value = "课表id", required = true)
    private Long id;

    @ApiModelProperty("是否删除其他周次同一节同一课程")
    private Boolean isDeleteOther;
}
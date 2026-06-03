package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("学年成绩检查请求参数")
public class YearGradeCheckReqModel {

    @ApiModelProperty(value = "班级名称", required = true)
    @NotNull(message = "班级名称不能为空")
    private Long classId;

    @ApiModelProperty(hidden = true)
    private Long schoolId;
}
package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SchoolCalendarDateTypeSaveV230ReqModel {
    @ApiModelProperty(value = "类型(1:工作日,2:双休日,3:假期)", required = true)
    @NotNull(message = "类型不能为空")
    private Integer type;

    @ApiModelProperty(value = "适用类型(1:老师,2:学生)", required = true)
    @NotNull(message = "适用类型不能为空")
    private Integer applyType;
}
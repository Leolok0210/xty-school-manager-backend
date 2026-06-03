package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SubstituteUpdateReqModel {

    @NotNull(message = "代课老师id不能为空")
    @ApiModelProperty(value = "代课老师id", required = true)
    private Long substituteTeacherId;

    @ApiModelProperty(value = "备注")
    private String remark;
}
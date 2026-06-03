package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 各班名列前茅请求参数
 */
@Data
@ApiModel("各班名列前茅请求参数")
public class ClassTopStudentsReqModel {

    @ApiModelProperty(value = "学段", required = true)
    @NotNull(message = "学段不能为空")
    private Long section;

    @ApiModelProperty(value = "班级", required = true)
    @NotNull(message = "班级不能为空")
    private Long classId;
}
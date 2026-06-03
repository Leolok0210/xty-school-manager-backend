package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "系统字典返回对象")
public class SystemDefaultParameterResModel {
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "类型", example = "dress_code_violation")
    private String typeGroup;

    @ApiModelProperty(value = "代码", example = "1")
    private String code;

    @ApiModelProperty(value = "值", example = "未佩戴校徽")
    private String value;

}
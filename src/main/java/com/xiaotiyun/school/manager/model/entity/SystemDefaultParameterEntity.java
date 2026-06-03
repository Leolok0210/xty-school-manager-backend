package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("system_default_parameter")
@ApiModel(description = "系统预设参数表实体对象")
public class SystemDefaultParameterEntity extends BaseEntity {
    @TableField("school_id")
    @ApiModelProperty(value = "学校ID", example = "1")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("type_group")
    @ApiModelProperty(value = "类型", example = "REST-大小息，PERF-课堂，APPEARANCE-仪表不符，ROUNDS-巡堂登记", required = true)
    private String typeGroup;

    @TableField("code")
    @ApiModelProperty(value = "代码", example = "1")
    private String code;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("value")
    @ApiModelProperty(value = "值", example = "未佩戴校徽", required = true)
    private String value;

    @TableField("description")
    @ApiModelProperty(value = "描述", example = "学生未佩戴校徽")
    private String description;
}
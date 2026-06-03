package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户设置响应参数模型
 */
@Data
@ApiModel("用户设置响应参数")
public class UserSettingResModel {
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "配置项键名：language_time-用户语言和时间")
    private String settingKey;

    @ApiModelProperty(value = "配置项值")
    private String settingValue;

    @ApiModelProperty(value = "配置项描述")
    private String description;
}

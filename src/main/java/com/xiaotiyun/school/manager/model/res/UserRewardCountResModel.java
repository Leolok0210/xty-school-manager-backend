package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("奖励总数返回信息")
public class UserRewardCountResModel {
    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("优点总数")
    private Integer minRewardCount;

    @ApiModelProperty("中功总数")
    private Integer midRewardCount;

    @ApiModelProperty("大功总数")
    private Integer maxRewardCount;
}
package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("校外活动奖项评级响应模型")
public class ExternalCompetitionAwardsResModel {

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 奖项评级名称
     */
    @ApiModelProperty(value = "奖项评级名称")
    private String awardsName;

}

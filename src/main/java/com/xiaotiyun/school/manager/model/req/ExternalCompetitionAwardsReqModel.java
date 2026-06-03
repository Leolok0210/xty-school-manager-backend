package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 校外活动奖项评级请求模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("校外活动奖项评级请求模型")
public class ExternalCompetitionAwardsReqModel extends PageReqModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 奖项评级名称
     */
    @ApiModelProperty(value = "奖项评级名称")
    private String awardsName;
}

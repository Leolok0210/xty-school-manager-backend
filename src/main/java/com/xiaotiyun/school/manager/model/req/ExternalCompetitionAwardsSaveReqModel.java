package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 校外活动奖项评级保存和更新模型
 */
@Data
@ApiModel("校外活动奖项评级保存和更新模型")
public class ExternalCompetitionAwardsSaveReqModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID,新增时不需要,修改时必传")
    private Long id;

    /**
     * 奖项评级名称
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "奖项评级名称", required = true)
    private String awardsName;
}

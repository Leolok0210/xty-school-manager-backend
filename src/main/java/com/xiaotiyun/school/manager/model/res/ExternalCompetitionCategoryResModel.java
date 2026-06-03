package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 校外活动范畴响应模型
 */
@Data
@ApiModel("校外活动范畴响应模型")
public class ExternalCompetitionCategoryResModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;


    /**
     * 范畴名称
     */
    @ApiModelProperty(value = "范畴名称")
    private String categoryName;
}

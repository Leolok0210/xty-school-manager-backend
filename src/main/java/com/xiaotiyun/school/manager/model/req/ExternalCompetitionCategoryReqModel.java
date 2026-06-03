package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 校外活动范畴请求模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("校外活动范畴请求模型")
public class ExternalCompetitionCategoryReqModel extends PageReqModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 范畴名称
     */
    @ApiModelProperty(value = "范畴名称")
    private String categoryName;
}

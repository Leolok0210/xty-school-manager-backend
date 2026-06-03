package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 校外活动范畴保存和更新模型
 */
@Data
@ApiModel("校外活动范畴保存和更新模型")
public class ExternalCompetitionCategorySaveReqModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID,新增时不需要,修改时必传")
    private Long id;

    /**
     * 范畴名称
     */
    @NotBlank(message = "范畴名称不能为空")
    @ApiModelProperty(value = "范畴名称", required = true)
    private String categoryName;
}

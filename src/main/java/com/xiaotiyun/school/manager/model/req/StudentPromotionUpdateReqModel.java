package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("升留级登记请求参数")
public class StudentPromotionUpdateReqModel {
    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "类型（1-升级 2-留级 3-带科）", required = true)
    private Integer promotionType;
} 
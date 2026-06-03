package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "学段权重配置响应类")
public class SysSemesterRuleResModel {
    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)")
    private Long groupId;

    @ApiModelProperty(value = "学段权重详情")
    List<SysSemesterRuleAddDetailResModel> details;
}
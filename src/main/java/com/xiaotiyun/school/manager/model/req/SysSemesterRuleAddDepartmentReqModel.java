package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "学段权重配置学部详情新增类")
public class SysSemesterRuleAddDepartmentReqModel {
    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)", required = true)
    private Long groupId;

    @ApiModelProperty(value = "学段权重详情", required = true)
    List<SysSemesterRuleAddDetailReqModel> details;
}
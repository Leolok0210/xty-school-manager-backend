package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "学段权重配置详情新增类")
public class SysSemesterRuleAddDetailReqModel {
    @ApiModelProperty(value = "学段ID", required = true)
    private Long semesterId;

    @ApiModelProperty(value = "权重单位%，结果*100")
    private Integer weight;

}
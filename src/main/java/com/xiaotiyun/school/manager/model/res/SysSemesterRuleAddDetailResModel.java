package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "学段权重配置详情返回类")
public class SysSemesterRuleAddDetailResModel {
    @ApiModelProperty(value = "学段ID")
    private Long semesterId;

    @ApiModelProperty(value = "学段名称")
    private String semesterName;

    @ApiModelProperty(value = "权重单位%，结果*100")
    private Integer weight;

}
package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description = "学部成绩权重规则学部请求类") // 修改描述
public class DepartmentScoreRuleDepartmentReqModel {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "级组id", required = true)
    private Long groupId;


    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学段平均(0-直接平均 1-加权平均) 数据库保存scoreType=5", required = true)
    private Integer avgType;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学部成绩权重规则详情", required = true)
    List<DepartmentScoreRuleDetailReqModel> details;

}
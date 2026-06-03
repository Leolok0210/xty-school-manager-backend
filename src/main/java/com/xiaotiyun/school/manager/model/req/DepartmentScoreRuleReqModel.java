package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description = "学部成绩权重规则请求类") // 修改描述
public class DepartmentScoreRuleReqModel {
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @ApiModelProperty(value = "学部成绩权重规则详情,avgType=1时，必填", example = "avgType=1时，必填")
    List<DepartmentScoreRuleDepartmentReqModel> details;
}
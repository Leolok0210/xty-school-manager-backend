package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description = "科目评级规则请求类")
public class SubjectLevelRuleReqModel {
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

//    @NotNull(message = LanguageConstants.PARAM_ERROR)
//    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)", required = true)
//    private Integer department;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "成绩展示规则，枚举:0-分数，1-评级", example = "枚举:0-分数，1-评级", required = true)
    private Integer showRule;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "科目ID", required = true)
    private Long subjectId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "分组ID")
    private Long groupId;

    @ApiModelProperty(value = "规则列表，成绩展示规则为1时，必填", example = "成绩展示规则为1时，必填")
    List<SubjectLevelRuleDetailReqModel> detailList;

}
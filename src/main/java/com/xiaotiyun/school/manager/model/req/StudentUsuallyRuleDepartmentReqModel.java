package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "平时成绩权重配置请求类")
public class StudentUsuallyRuleDepartmentReqModel {

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "级组ID")
    private Long gradeGroupId;

    @ApiModelProperty(value = "科目ID")
    private Long subjectId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "平时成绩类型ID")
    private Long typeId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "权重单位%，结果*100")
    private Integer weight;

}
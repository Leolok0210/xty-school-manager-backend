package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description = "学段权重配置新增类")
public class SysSemesterRuleAddReqModel {
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学年", example = "2023-2024", required = true)
    private String schoolYear;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学部权重详情", required = true)
    List<SysSemesterRuleAddDepartmentReqModel> details;

}
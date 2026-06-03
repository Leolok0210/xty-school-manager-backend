package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("教师请假审批处理请求参数")
public class TeacherLeaveHandleReqModel{
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "请假记录ID", required = true)
    private Long id;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "处理类型（0-拒绝，1-同意，2-撤回）", required = true)
    private Integer handleType;

    @ApiModelProperty(value = "拒绝原因，handleType=0时必传")
    private String handleOpinion;
}
package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "学生端微信绑定登录请求类")
public class WXStuBindAndLoginReq {
    @ApiModelProperty(value = "渠道查询或微信登录返回的userCode")
    private String userCode;

    @ApiModelProperty(value = "wx.login的临时Code")
    private String code;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生编号", example = "1")
    private String studentNo;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "中文或英文名称", example = "1")
    private String name;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "班级ID", example = "1")
    private Long classId;
}
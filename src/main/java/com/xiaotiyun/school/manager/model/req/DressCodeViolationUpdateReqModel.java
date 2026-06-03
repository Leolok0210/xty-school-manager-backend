package com.xiaotiyun.school.manager.model.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "仪表不符登记请求对象")
public class DressCodeViolationUpdateReqModel {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "主键ID", example = "1", required = true)
    private Long id;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("violation_date")
    @ApiModelProperty(value = "日期", example = "2023-10-01", required = true)
    private String violationDate;

    @TableField("remark_id")
    @ApiModelProperty(value = "备注ID", example = "预设参数ID")
    private Long remarkId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("remark")
    @ApiModelProperty(value = "备注", example = "未佩戴校徽", required = true)
    private String remark;
}
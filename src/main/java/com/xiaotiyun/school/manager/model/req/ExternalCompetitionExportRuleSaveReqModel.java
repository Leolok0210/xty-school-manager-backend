package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 校外活动导出规则保存请求模型
 */
@Data
@ApiModel(value = "校外活动导出规则保存请求模型")
public class ExternalCompetitionExportRuleSaveReqModel {

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 范畴ID
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    private Long categoryId;

    /**
     * 范畴名称
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    private String categoryName;

    /**
     * 奖项ID
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    private Long awardsId;

    /**
     * 奖项评级名称
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    private String awardsName;

    /**
     * 类型,校内、港澳区、埠際或國際
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    private Integer type;

    /**
     * 是否具有代表性,YES、NO
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    private String representative;

    /**
     * 建议奖项
     */
    private String rules;
}

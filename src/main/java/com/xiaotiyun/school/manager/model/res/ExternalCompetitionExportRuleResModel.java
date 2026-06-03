package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 校外活动导出规则响应模型
 */
@Data
@ApiModel(value = "校外活动导出规则响应模型")
public class ExternalCompetitionExportRuleResModel {

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    /**
     * 范畴ID
     */
    @ApiModelProperty(value = "范畴ID")
    private Long categoryId;

    /**
     * 范畴名称
     */
    @ApiModelProperty(value = "范畴名称")
    private String categoryName;

    /**
     * 奖项ID
     */
    @ApiModelProperty(value = "奖项ID")
    private Long awardsId;

    /**
     * 奖项评级名称
     */
    @ApiModelProperty(value = "奖项评级名称")
    private String awardsName;

    /**
     * 类型,校内、港澳区、埠際或國際
     */
    @ApiModelProperty(value = "类型,校内、港澳区、埠際或國際")
    private Integer type;

    /**
     * 是否具有代表性,YES、NO
     */
    @ApiModelProperty(value = "是否具有代表性,YES、NO")
    private String representative;

    /**
     * 建议奖项
     */
    @ApiModelProperty(value = "建议奖项")
    private String rules;
}

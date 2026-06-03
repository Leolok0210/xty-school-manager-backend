package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 校外活动导出规则查询请求模型
 */
@Data
@ApiModel(value = "校外活动导出规则查询模型")
public class ExternalCompetitionExportRuleCheckReqModel {
    /**
     * 规则ID
     */
    @ApiModelProperty(value = "规则ID,新增时不需要")
    private Long id;

    /**
     * 范畴ID
     */
    @ApiModelProperty(value = "范畴ID")
    private Long categoryId;

    /**
     * 奖项ID
     */
    @ApiModelProperty(value = "奖项ID")
    private Long awardsId;

    /**
     * 类型,校内、港澳区、埠際或國際
     */
    @ApiModelProperty(value = "类型,校内、港澳区、埠際或國際")
    private String type;

    /**
     * 是否具有代表性,YES、NO
     */
    @ApiModelProperty(value = "是否具有代表性,YES、NO")
    private String representative;
}

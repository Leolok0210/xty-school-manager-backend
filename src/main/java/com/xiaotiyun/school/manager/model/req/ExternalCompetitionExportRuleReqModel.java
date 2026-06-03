package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 校外活动导出规则查询请求模型
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "校外活动导出规则查询请求模型")
public class ExternalCompetitionExportRuleReqModel extends PageReqModel {

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
    private Integer type;

    /**
     * 是否具有代表性,YES、NO
     */
    @ApiModelProperty(value = "是否具有代表性,YES、NO")
    private String representative;
}

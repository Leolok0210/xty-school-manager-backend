package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 校外活动导出规则表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("external_competition_export_rule")
public class ExternalCompetitionExportRuleEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 范畴ID
     */
    private Long categoryId;

    /**
     * 范畴名称
     */
    private String categoryName;

    /**
     * 奖项ID
     */
    private Long awardsId;

    /**
     * 奖项评级名称
     */
    private String awardsName;

    /**
     * 类型,校内、港澳区、埠際或國際
     */
    private Integer type;

    /**
     * 是否具有代表性,YES、NO
     */
    private String representative;

    /**
     * 建议奖项
     */
    private String rules;
}


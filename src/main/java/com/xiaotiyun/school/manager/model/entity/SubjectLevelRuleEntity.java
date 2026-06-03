package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("subject_level_rule")
@ApiModel(description = "科目评级规则实体类")
public class SubjectLevelRuleEntity extends BaseEntity {

    @ApiModelProperty(value = "学校ID")
    @TableField("school_id")
    private Long schoolId;

//    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)")
//    @TableField("department")
//    private Integer department;

    @ApiModelProperty(value = "科目ID")
    @TableField("subject_id")
    private Long subjectId;

    @ApiModelProperty(value = "规则等级")
    @TableField("rule_level")
    private String ruleLevel;

    @ApiModelProperty(value = "规则最大值")
    @TableField("rule_max")
    private Integer ruleMax;

    @ApiModelProperty(value = "规则最小值")
    @TableField("rule_min")
    private Integer ruleMin;

    @ApiModelProperty(value = "规则分组ID")
    @TableField("group_id")
    private Long groupId;

}
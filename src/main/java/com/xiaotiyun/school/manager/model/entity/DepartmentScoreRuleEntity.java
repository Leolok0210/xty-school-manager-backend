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
@TableName("department_score_rule") // 修改表名
@ApiModel(description = "学部成绩权重规则实体类") // 修改描述
public class DepartmentScoreRuleEntity extends BaseEntity {

    @ApiModelProperty(value = "学校ID", required = true)
    @TableField("school_id")
    private Long schoolId;

    @ApiModelProperty(value = "级组", required = true)
    @TableField("group_id")
    private Long groupId;

    @ApiModelProperty(value = "成绩类型,0-平时成绩,1-考试成绩,2-科目成绩 3-公共+文科/公共+理工科 4-公共+理科 6-公共+商科")
    @TableField("score_type")
    private String scoreType;

    @ApiModelProperty(value = "科目ID")
    @TableField("subject_id")
    private Long subjectId;

    @ApiModelProperty(value = "权重单位%，结果*100")
    @TableField("weight")
    private Integer weight;

    @ApiModelProperty(value = "平均分规则 0-直接平均 1-加权平均'")
    @TableField("score_rule")
    private Integer scoreRule;

}
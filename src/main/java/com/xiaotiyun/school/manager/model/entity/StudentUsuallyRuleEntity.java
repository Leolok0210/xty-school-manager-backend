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
@TableName("student_usually_rule")
@ApiModel(description = "平时成绩权重配置实体类")
public class StudentUsuallyRuleEntity extends BaseEntity {

    @ApiModelProperty(value = "学校ID")
    @TableField("school_id")
    private Long schoolId;

    @ApiModelProperty(value = "级组ID")
    @TableField("grade_group_id")
    private Long gradeGroupId;

    @ApiModelProperty(value = "科目ID")
    @TableField("subject_id")
    private Long subjectId;

    @ApiModelProperty(value = "平时成绩类型ID")
    @TableField("type_id")
    private Long typeId;

    @ApiModelProperty(value = "权重单位%，结果*100")
    @TableField("weight")
    private Integer weight;

}
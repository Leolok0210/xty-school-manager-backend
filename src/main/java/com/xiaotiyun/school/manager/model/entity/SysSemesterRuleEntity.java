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
@TableName("sys_semester_rule")
@ApiModel(description = "学段权重配置实体类")
public class SysSemesterRuleEntity extends BaseEntity {

    @ApiModelProperty(value = "学校ID", required = true)
    @TableField("school_id")
    private Long schoolId;

    @ApiModelProperty(value = "学年", example = "2023-2024")
    @TableField("school_year")
    private String schoolYear;

    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)")
    @TableField("group_id")
    private Long groupId;

    @ApiModelProperty(value = "学段ID")
    @TableField("semester_id")
    private Long semesterId;

    @ApiModelProperty(value = "权重单位%，结果*100")
    @TableField("weight")
    private Integer weight;

}
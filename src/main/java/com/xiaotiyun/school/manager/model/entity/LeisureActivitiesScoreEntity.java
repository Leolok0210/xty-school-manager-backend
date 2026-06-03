package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 余暇活动成绩信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("leisure_activities_score")
@ApiModel(value = "余暇活动成绩信息实体类")
public class LeisureActivitiesScoreEntity extends BaseEntity {

    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @ApiModelProperty(value = "学生id", required = true)
    private Long studentId;

    @ApiModelProperty(value = "活动id", required = true)
    private Long activityId;

    @ApiModelProperty(value = "课程id", required = true)
    private Long courseId;

    @ApiModelProperty(value = "出席次数")
    private Integer attendCount;

    @ApiModelProperty(value = "出席率分数(*100)")
    private Integer attendScore;

    @ApiModelProperty(value = "课节表现分数(*100)")
    private Integer lessonScore;

    @ApiModelProperty(value = "总分数(*100)")
    private Integer totalScore;

    @ApiModelProperty(value = "参考成绩等级")
    private String referenceLevel;

    @ApiModelProperty(value = "最终成绩等级")
    private String finalLevel;
} 
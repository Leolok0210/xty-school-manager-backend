package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("余暇活动成绩信息保存参数")
public class LeisureActivitiesScoreSaveReqModel {
    @NotNull(message = "活动id不能为空")
    @ApiModelProperty(value = "活动id", required = true)
    private Long activityId;
    @NotNull(message = "课程id不能为空")
    @ApiModelProperty(value = "课程id", required = true)
    private Long courseId;
    @NotNull(message = "学生id不能为空")
    @ApiModelProperty(value = "学生id", required = true)
    private Long studentId;
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
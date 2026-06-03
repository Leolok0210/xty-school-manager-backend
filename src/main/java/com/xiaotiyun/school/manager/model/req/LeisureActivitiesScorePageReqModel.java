package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("余暇活动成绩分页查询参数")
public class LeisureActivitiesScorePageReqModel extends PageReqModel {
    @NotNull(message = "活动id不能为空")
    @ApiModelProperty(value = "活动id", required = true)
    private Long activityId;
    @NotNull(message = "课程id不能为空")
    @ApiModelProperty(value = "课程id", required = true)
    private Long courseId;
    @ApiModelProperty(value = "级组id")
    private Long groupId;
    @ApiModelProperty(value = "班级id")
    private Long classId;
}
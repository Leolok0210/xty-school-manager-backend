package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("学生余暇活动匹配通知信息")
public class StudentLeisureActivitiesResultResModel {
    @ApiModelProperty("匹配成功活动id")
    private Long activityId;
    @ApiModelProperty("匹配成功课程id")
    private Long courseId;
    @ApiModelProperty("匹配成功课程名称")
    private String courseName;
    @ApiModelProperty("匹配结果(1.匹配成功;2.匹配失败)")
    private Integer matchResult;
}
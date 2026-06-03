package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 余暇活动匹配结果通知返回参数实体类
 */
@Data
@ApiModel(value = "余暇活动匹配结果通知返回参数实体类")
public class LeisureActivitiesNoticeResModel {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    @ApiModelProperty(value = "学生id")
    private Long studentId;

    @ApiModelProperty(value = "活动id")
    private Long activityId;

    @ApiModelProperty(value = "报名成功课程id")
    private Long courseId;

    @ApiModelProperty(value = "匹配结果(1.匹配成功;2.未匹配成功,等待二次报名;3.未匹配成功,进行二次报名)")
    private Integer matchResult;

    @ApiModelProperty(value = "是否查阅(0.未查阅;1.已查阅)")
    private Integer consult;
} 
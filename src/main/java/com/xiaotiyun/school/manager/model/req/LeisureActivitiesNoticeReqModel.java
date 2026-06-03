package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 余暇活动匹配结果通知请求参数实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "余暇活动匹配结果通知请求参数实体类")
public class LeisureActivitiesNoticeReqModel extends PageReqModel {

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
package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 余暇活动匹配结果通知实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("leisure_activities_notice")
@ApiModel(value = "余暇活动匹配结果通知实体类")
public class LeisureActivitiesNoticeEntity extends BaseEntity {

    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @ApiModelProperty(value = "学生id", required = true)
    private Long studentId;

    @ApiModelProperty(value = "活动id", required = true)
    private Long activityId;

    @ApiModelProperty(value = "学段id", required = true)
    private Long periodId;

    @ApiModelProperty(value = "报名成功课程id")
    private Long courseId;

    @ApiModelProperty(value = "匹配结果(1.匹配成功;2.匹配失败)", required = true)
    private Integer matchResult;

    @ApiModelProperty(value = "是否查阅(0.未查阅;1.已查阅)")
    private Integer consult;
} 
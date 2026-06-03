package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("external_competition")
@ApiModel("校外比赛实体")
public class ExternalCompetitionEntity extends BaseEntity {
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;
    
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;
    
    @ApiModelProperty(value = "比赛项目", required = true)
    private String name;
    
    @ApiModelProperty(value = "主办单位", required = true)
    private String organizer;
    
    @ApiModelProperty(value = "指导老师", required = true)
    private String advisor;

    @ApiModelProperty(value = "开始时间", required = true)
    private LocalDateTime startTime;

    @ApiModelProperty(value = "颁奖时间", required = true)
    private LocalDateTime prizeTime;

    @ApiModelProperty(value = "范畴ID", required = true)
    private Long categoryId;

    @ApiModelProperty(value = "范畴名称", required = true)
    private String categoryName;

    @ApiModelProperty(value = "是否具有代表性", required = true)
    private String representative;

    @ApiModelProperty(value = "组别数量", required = true)
    private Integer groupSum;

    @ApiModelProperty(value = "地区", required = true)
    private Integer area;

    @ApiModelProperty(value = "活动地区", required = true)
    private String activityArea;

    @ApiModelProperty(value = "创建人", required = true)
    private Long createUserId;

    @ApiModelProperty("备注一")
    private String remark1;

} 
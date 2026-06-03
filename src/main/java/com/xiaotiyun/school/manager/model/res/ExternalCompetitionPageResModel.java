package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("校外比赛分页信息")
public class ExternalCompetitionPageResModel {
    @ApiModelProperty(value = "比赛ID")
    private Long id;

    @ApiModelProperty(value = "学校id")
    private Long schoolId;

    @ApiModelProperty(value = "学年")
    private String schoolYear;

    @ApiModelProperty(value = "比赛项目")
    private String name;

    @ApiModelProperty(value = "主办单位")
    private String organizer;

    @ApiModelProperty(value = "指导老师")
    private String advisor;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "颁奖时间")
    private LocalDateTime prizeTime;

    @ApiModelProperty(value = "范畴ID")
    private Long categoryId;

    @ApiModelProperty(value = "范畴名称")
    private String categoryName;

    @ApiModelProperty(value = "是否具有代表性")
    private String representative;

    @ApiModelProperty(value = "组别数量")
    private Integer groupSum;

    @ApiModelProperty(value = "地区,1-校内、2-港澳区、3-埠際或國際")
    private Integer area;

    @ApiModelProperty(value = "活动地区")
    private String activityArea;

    @ApiModelProperty("备注一")
    private String remark1;

    @ApiModelProperty("创建人")
    private Long createUserId;

    @ApiModelProperty("组别信息")
    private List<ExternalCompetitionGroupResModel> groups;
}
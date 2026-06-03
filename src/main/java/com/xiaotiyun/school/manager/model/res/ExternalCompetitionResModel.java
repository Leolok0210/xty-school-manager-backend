package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("校外比赛信息")
public class ExternalCompetitionResModel {
    @ApiModelProperty(value = "比赛ID")
    private Long id;

    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    @ApiModelProperty(value = "学年")
    private String schoolYear;

    @ApiModelProperty(value = "比赛项目")
    private String name;

    @ApiModelProperty(value = "主办单位")
    private String organizer;

    @ApiModelProperty(value = "指导老师")
    private String advisor;

    @ApiModelProperty(value = "组别")
    private String groupType;

    @ApiModelProperty(value = "比赛类型（1-个人赛 2-团体赛）")
    private Integer competitionType;

    @ApiModelProperty("奖项")
    private String prize;

    @ApiModelProperty("级组评奖")
    private String gradeGroupAward;

    @ApiModelProperty("行政评奖")
    private String administrativeAward;

    @ApiModelProperty("备注一")
    private String remark1;

    @ApiModelProperty("备注二")
    private String remark2;

    @ApiModelProperty(value = "参与学生列表")
    private List<ExternalCompetitionRecordResModel> records;
}
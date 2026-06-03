package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("校外比赛参与学生信息")
public class ExternalCompetitionRecordResModel {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    @ApiModelProperty(value = "学生名称")
    private String studentName;

    @ApiModelProperty(value = "班级ID")
    private Long classId;

    @ApiModelProperty(value = "班级名称")
    private String className;

    @ApiModelProperty(value = "级组名称")
    private String gradeName;

    @ApiModelProperty(value = "比赛ID")
    private Long competitionId;

    @ApiModelProperty(value = "组别ID")
    private Long groupId;

    @ApiModelProperty(value = "奖项")
    private String prizeName;

    @ApiModelProperty(value = "奖项ID")
    private Long awardsId;

    @ApiModelProperty(value = "奖项评级名称")
    private String awardsName;

    @ApiModelProperty(value = "表彰建议")
    private String awardsRemark;

    @ApiModelProperty(value = "审批备注")
    private String approveRemark;

    @ApiModelProperty(value = "最终表彰")
    private String finalAwards;

    @ApiModelProperty(value = "是否单人或团队,0-个人、1-团队")
    private Integer oneOrTeam;

    @ApiModelProperty(value = "团队ID,前端区分用")
    private String teamId;
}
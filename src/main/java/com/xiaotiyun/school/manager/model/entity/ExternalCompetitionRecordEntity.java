package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("external_competition_record")
@ApiModel("校外参赛记录实体")
public class ExternalCompetitionRecordEntity extends BaseEntity {
    @ApiModelProperty(value = "比赛ID", required = true)
    private Long competitionId;

    @ApiModelProperty(value = "组别ID", required = true)
    private Long groupId;

    @ApiModelProperty(value = "奖项", required = true)
    private String prizeName;

    @ApiModelProperty(value = "奖项ID", required = true)
    private Long awardsId;

    @ApiModelProperty(value = "奖项评级名称", required = true)
    private String awardsName;

    @ApiModelProperty(value = "表彰建议", required = true)
    private String awardsRemark;

    @ApiModelProperty(value = "审批备注", required = true)
    private String approveRemark;

    @ApiModelProperty(value = "最终表彰", required = true)
    private String finalAwards;

    @ApiModelProperty(value = "最终表彰积分", required = true)
    private Integer finalAwardsPoints;
    
    @ApiModelProperty(value = "学生ID", required = true)
    private Long studentId;

    @ApiModelProperty(value = "学生姓名", required = true)
    private String studentName;
    
    @ApiModelProperty(value = "班级ID", required = true)
    private Long classId;

    @ApiModelProperty(value = "级组名称", required = true)
    private String gradeName;

    @ApiModelProperty(value = "班级名称", required = true)
    private String className;

    @ApiModelProperty(value = "是否单人或团队,0-个人、1-团队", required = true)
    private Integer oneOrTeam;

    @ApiModelProperty(value = "团队ID,前端区分用")
    private String teamId;
}
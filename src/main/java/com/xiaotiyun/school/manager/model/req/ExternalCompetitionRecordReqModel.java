package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("校外参赛记录请求")
public class ExternalCompetitionRecordReqModel {
    @ApiModelProperty(value = "ID,更新时必传")
    private Long id;

    @ApiModelProperty(value = "比赛ID,更新时必传")
    private Long competitionId;

    @ApiModelProperty(value = "组别ID,更新时必传")
    private Long groupId;

    @NotBlank(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "奖项", required = true)
    private String prizeName;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "奖项ID", required = true)
    private Long awardsId;

    @NotBlank(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "奖项评级名称", required = true)
    private String awardsName;

    @ApiModelProperty(value = "表彰建议")
    private String awardsRemark;

    @ApiModelProperty(value = "审批备注")
    private String approveRemark;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "是否单人或团队,0-个人、1-团队", required = true)
    private Integer oneOrTeam;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生ID", required = true)
    private Long studentId;

    @NotBlank(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生姓名", required = true)
    private String studentName;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "班级ID", required = true)
    private Long classId;

    @NotBlank(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "级组名称", required = true)
    private String gradeName;

    @NotBlank(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "班级名称", required = true)
    private String className;

    @ApiModelProperty(value = "团队ID,前端区分用")
    private String teamId;
}
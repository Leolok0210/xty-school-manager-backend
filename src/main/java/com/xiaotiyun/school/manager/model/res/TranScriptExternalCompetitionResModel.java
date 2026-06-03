package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TranScriptExternalCompetitionResModel {

    @ApiModelProperty("校外比赛名称")
    private String competitionName;

    @ApiModelProperty("校外比赛类型")
    private String competitionType;

    @ApiModelProperty("组别名称")
    private String competitionGroupName;

    @ApiModelProperty("奖项")
    private String  award;

    @ApiModelProperty("最终表彰")
    private String finalAward;
}

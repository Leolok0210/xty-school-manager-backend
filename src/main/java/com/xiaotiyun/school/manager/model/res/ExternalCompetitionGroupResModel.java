package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("校外组别详情响应")
public class ExternalCompetitionGroupResModel {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "比赛ID")
    private Long competitionId;

    @ApiModelProperty(value = "组别名称")
    private String groupName;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "备注")
    private List<ExternalCompetitionRecordResModel> records;
}

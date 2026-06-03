package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("校外组别分页查询请求参数")
public class ExternalCompetitionGroupQueryReqModel extends PageReqModel {

    @ApiModelProperty(value = "比赛ID")
    private Long competitionId;

    @ApiModelProperty(value = "组别名称")
    private String groupName;
}

package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel("校外组别创建/更新请求参数")
public class ExternalCompetitionGroupCreateReqModel {

    @ApiModelProperty(value = "id,更新时必传")
    private Long id;

    @ApiModelProperty(value = "比赛ID,更新时必传")
    private Long competitionId;

    @ApiModelProperty(value = "组别名称", required = true)
    @NotBlank(message = "组别名称不能为空")
    private String groupName;

    @NotEmpty(message = "参赛学生记录不能为空")
    @ApiModelProperty(value = "参赛学生记录", required = true)
    private List<ExternalCompetitionRecordReqModel> records;
}

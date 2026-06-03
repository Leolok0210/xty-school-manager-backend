package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("参赛记录批量创建参数")
public class CompetitionRecordBatchCreateReqModel {
    @ApiModelProperty(value = "比赛ID", required = true)
    @NotNull(message = "比赛ID不能为空")
    private Long competitionId;

    @ApiModelProperty(value = "参赛记录列表", required = true)
    @Valid
    @NotEmpty(message = "参赛记录不能为空")
    private List<CompetitionRecordCreateReqModel> records;
} 
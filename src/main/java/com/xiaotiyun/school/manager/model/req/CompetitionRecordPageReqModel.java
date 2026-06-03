package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("参赛记录分页查询参数")
public class CompetitionRecordPageReqModel extends PageReqModel {
    @ApiModelProperty(value = "比赛ID", required = true)
    private Long competitionId;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("班级名称")
    private String className;
} 
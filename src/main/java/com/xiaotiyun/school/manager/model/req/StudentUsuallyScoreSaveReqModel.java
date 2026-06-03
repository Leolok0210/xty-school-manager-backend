package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StudentUsuallyScoreSaveReqModel {
    @ApiModelProperty(value = "平时分登记id", required = true)
    @NotNull(message = "平时分登记id不能为空")
    private Long taskId;
    @NotNull(message = "成绩信息不能为空")
    @ApiModelProperty(value = "成绩信息", required = true)
    private List<StudentUsuallyScoreDeatilSaveReqModel> scoreList;
}
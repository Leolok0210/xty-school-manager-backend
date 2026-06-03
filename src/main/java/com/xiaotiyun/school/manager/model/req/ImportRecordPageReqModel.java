package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImportRecordPageReqModel extends PageReqModel {
    @NotNull(message = "任务id不能为空")
    @ApiModelProperty("任务id")
    private Long taskId;
}
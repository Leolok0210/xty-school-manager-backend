package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentUsuallyScorePageReqModel extends PageReqModel {
    @NotNull(message = "平时分登记id不能为空")
    @ApiModelProperty(value = "平时分登记id", required = true)
    private Long taskId;
}
package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserRewardAllListReqModel extends UserRewardPendingReqModel {
    @ApiModelProperty(value = "状态(1.待审批；2.已完成；3.已拒绝；4.已撤回")
    private Integer status;
}
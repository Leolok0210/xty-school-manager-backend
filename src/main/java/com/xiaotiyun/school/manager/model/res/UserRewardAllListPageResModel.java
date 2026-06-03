package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserRewardAllListPageResModel extends UserRewardPendingPageResModel {
    @ApiModelProperty(value = "发起人id")
    private Long startUserId;
}
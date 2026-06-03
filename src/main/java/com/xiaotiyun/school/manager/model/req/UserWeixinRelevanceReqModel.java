package com.xiaotiyun.school.manager.model.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserWeixinRelevanceReqModel {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String corpId;

    private String triUserId;

    private String openId;
}
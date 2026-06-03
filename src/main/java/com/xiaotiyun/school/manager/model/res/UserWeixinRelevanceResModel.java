package com.xiaotiyun.school.manager.model.res;

import lombok.Data;

import java.util.Date;

@Data
public class UserWeixinRelevanceResModel {
    private Long id;

    private Long userId;

    private String corpId;

    private String triUserId;

    private String openId;

    private Date createTime;

    private Date updateTime;

    private Long deleted;
}
package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user_weixin_relevance")
public class UserWeixinRelevanceEntity extends BaseEntity {

    private Long userId;//学生ID

    private String corpId;

    private String triUserId;

    private String openId;

    private Integer userType;//用户类型，0-教师(关联用户表)，1-学生(关联学生表)

    private Long schoolId;
}
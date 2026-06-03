package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@TableName("school_weixin_relevance")
@Data
public class SchoolWeixinRelevanceEntity extends BaseEntity {
    /**
     * 学校ID
     */
    @TableField("school_id")
    private Long schoolId;

    /**
     * 企业ID,内部是明文id,三方是加密id
     */
    @TableField("corp_id")
    private String corpId;

    /**
     * 企业名称
     */
    @TableField("corp_name")
    private String corpName;

    /**
     * 应用密钥
     */
    @TableField("app_secret")
    private String appSecret;

    /**
     * 授权企业ID
     */
    @TableField("auth_corp_id")
    private String authCorpId;

    /**
     * 永久授权码
     */
    @TableField("permanent_code")
    private String permanentCode;

    /**
     * 应用ID
     */
    @TableField("agent_id")
    private String agentId;

    /**
     * 应用名称
     */
    @TableField("agent_name")
    private String agentName;

    /**
     * 应用回调地址
     */
    @TableField("agent_url")
    private String agentUrl;

    /**
     * 类型,1-企微内部应用,2-企微三方应用授权
     */
    @TableField("app_type")
    private Integer appType;
}

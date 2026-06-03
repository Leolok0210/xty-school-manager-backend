package com.xiaotiyun.school.manager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMaProperties {

    /**
     * 设置微信小程序的appid
     */
    private String appid;

    /**
     * 设置微信小程序的Secret
     */
    private String secret;

    /**
     *
     */
    private String url;

    /**
     * 首页路径
     */
    private String indexPath;

    /**
     * 企微通知背景
     */
    private String wechatNoticeIllustration;
}


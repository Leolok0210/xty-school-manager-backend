package com.xiaotiyun.school.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WechatMessageMiniprogramDTO {

    /**
     * 小程序appid
     * 是否必填：是
     * 必须是关联到企业的小程序应用
     */
    private String appid;

    /**
     * 小程序消息标题
     * 是否必填：否
     * 最多64个字节，超过会自动截断（支持id转译）
     */
    private String title;

    /**
     * 小程序消息封面的mediaid
     * 是否必填：是
     * 封面图建议尺寸为520*416
     */
    private String thumb_media_id;

    /**
     * 点击消息卡片后进入的小程序页面路径
     * 是否必填：是
     */
    private String pagepath;
}

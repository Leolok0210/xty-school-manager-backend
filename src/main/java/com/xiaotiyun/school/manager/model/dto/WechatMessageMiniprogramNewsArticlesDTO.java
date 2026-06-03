package com.xiaotiyun.school.manager.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WechatMessageMiniprogramNewsArticlesDTO {
    @ApiModelProperty(value = "标题，必传，不超过128个字节，超过会自动截断 （支持id转译）")
    private String title;

    @ApiModelProperty(value = "图文消息缩略图的media_id，必传, 可以通过素材管理接口获得。此处thumb_media_id即上传接口返回的media_id")
    private String thumb_media_id;

    @ApiModelProperty(value = "图文消息的作者，不超过64个字节")
    private String author;

    @ApiModelProperty(value = "图文消息点击“阅读原文”之后的页面链接")
    private String content_source_url;

    @ApiModelProperty(value = "图文消息的内容，必传，支持html标签，不超过666 K个字节 （支持id转译）")
    private String content;

    @ApiModelProperty(value = "图文消息的描述，不超过512个字节，超过会自动截断 （支持id转译）")
    private String digest;
}

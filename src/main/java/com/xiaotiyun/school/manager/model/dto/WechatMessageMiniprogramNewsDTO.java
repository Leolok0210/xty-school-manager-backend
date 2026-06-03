package com.xiaotiyun.school.manager.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WechatMessageMiniprogramNewsDTO {

    @ApiModelProperty(value = "小程序消息标题")
    private List<WechatMessageMiniprogramNewsArticlesDTO> articles;
}

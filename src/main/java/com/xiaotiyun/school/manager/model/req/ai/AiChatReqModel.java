package com.xiaotiyun.school.manager.model.req.ai;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "AI聊天请求模型")
public class AiChatReqModel {

    @ApiModelProperty("消息列表")
    private List<ChatMessage> messages;

    @ApiModelProperty("会话ID")
    private String sessionId;

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @ApiModelProperty("是否流式返回")
    private Boolean stream = false;

    @Data
    @ApiModel(value = "聊天消息")
    public static class ChatMessage {
        @ApiModelProperty("角色: user, assistant, system")
        private String role;

        @ApiModelProperty("消息内容")
        private String content;

        @ApiModelProperty("工具调用ID")
        private String toolCallId;

        @ApiModelProperty("工具调用")
        private Map<String, Object> toolCalls;
    }
}
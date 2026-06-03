package com.xiaotiyun.school.manager.model.res.ai;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "AI聊天响应模型")
public class AiChatResModel {

    @ApiModelProperty("AI回复内容")
    private String content;

    @ApiModelProperty("会话ID")
    private String sessionId;

    @ApiModelProperty("是否需要确认")
    private Boolean requiresConfirmation;

    @ApiModelProperty("操作类型: query, execute, confirm")
    private String actionType;

    @ApiModelProperty("操作描述")
    private String actionDescription;

    @ApiModelProperty("待执行的操作")
    private Map<String, Object> pendingAction;

    @ApiModelProperty("数据卡片列表（表格/图表）")
    private List<DataCard> dataCards;

    @ApiModelProperty("快捷操作建议列表")
    private List<String> quickActions;

    @Data
    @ApiModel(value = "DataCard数据卡片")
    public static class DataCard {
        @ApiModelProperty("卡片类型: table, chart")
        private String type = "table";

        @ApiModelProperty("卡片标题")
        private String title;

        @ApiModelProperty("卡片数据")
        private Payload payload;

        @Data
        public static class Payload {
            @ApiModelProperty("列名列表")
            private List<String> columns;

            @ApiModelProperty("数据行")
            private List<List<Object>> rows;
        }
    }
}
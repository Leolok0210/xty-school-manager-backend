package com.xiaotiyun.school.manager.service.ai;

import com.xiaotiyun.school.manager.model.req.ai.AiChatReqModel;
import com.xiaotiyun.school.manager.model.res.ai.AiChatResModel;

import java.util.List;
import java.util.Map;

public interface AiChatService {

    /**
     * 处理聊天消息并返回AI响应
     */
    AiChatResModel chat(AiChatReqModel reqModel);

    /**
     * 执行确认的操作
     */
    AiChatResModel executeAction(String sessionId, String actionId, Object params);

    /**
     * 获取会话历史消息
     */
    List<Map<String, Object>> getSessionHistory(String sessionId);

    /**
     * 获取用户会话列表
     */
    List<Map<String, Object>> getUserSessions();

    /**
     * 创建新会话
     */
    String createSession();

    /**
     * 删除会话
     */
    void deleteSession(String sessionId);

    /**
     * 获取FAQ列表
     */
    List<Map<String, Object>> getFaqs();

    /**
     * 提交消息反馈
     */
    void feedback(String sessionId, String messageId, String comment);
}
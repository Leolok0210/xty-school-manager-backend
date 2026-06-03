package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.AiChatMessageEntity;
import com.xiaotiyun.school.manager.model.entity.AiChatSessionEntity;

import java.util.List;

/**
 * AI聊天会话Service
 */
public interface AiChatSessionService extends IService<AiChatSessionEntity> {

    /**
     * 保存用户消息
     */
    void saveMessage(String sessionId, String role, String content);

    /**
     * 获取会话消息历史
     */
    List<AiChatMessageEntity> getSessionMessages(String sessionId);

    /**
     * 创建新会话
     */
    String createSession(Long userId, Long schoolId);

    /**
     * 获取用户会话列表
     */
    List<AiChatSessionEntity> getUserSessions(Long userId);

    /**
     * 删除会话
     */
    void deleteSession(String sessionId);

    /**
     * 更新消息反馈
     */
    void updateMessageFeedback(String sessionId, String messageId, String comment);
}
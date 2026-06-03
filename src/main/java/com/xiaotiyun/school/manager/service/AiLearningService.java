package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.model.entity.AiConversationLearnEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface AiLearningService extends IService<AiConversationLearnEntity> {

    /**
     * 从对话中学习
     */
    void learnFromConversation(String sessionId, String userQuery, String aiResponse, Long schoolId);

    /**
     * 记录反馈
     */
    void recordFeedback(String sessionId, String messageId, boolean isPositive);

    /**
     * 获取学习统计
     */
    Map<String, Object> getStats(Long schoolId);

    /**
     * 获取建议FAQ
     */
    List<Map<String, Object>> getSuggestions(Long schoolId);

    /**
     * 批准建议并添加到知识库
     */
    void approveSuggestion(Long id);

    /**
     * 忽略建议
     */
    void ignoreSuggestion(Long id);

    /**
     * 标准化问题
     */
    String normalizeQuery(String text);
}
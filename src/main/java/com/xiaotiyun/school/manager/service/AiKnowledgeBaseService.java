package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.AiKnowledgeBaseEntity;

import java.util.List;

/**
 * AI知识库Service
 */
public interface AiKnowledgeBaseService extends IService<AiKnowledgeBaseEntity> {

    /**
     * 搜索知识库
     */
    List<AiKnowledgeBaseEntity> search(Long schoolId, String keyword);

    /**
     * 获取知识库（带上下文）
     */
    String getKnowledgeContext(Long schoolId);

    /**
     * 添加知识库条目
     */
    void addKnowledge(Long schoolId, String category, String question, String answer);

    /**
     * 获取FAQ列表
     */
    List<AiKnowledgeBaseEntity> getFaqs(Long schoolId);

    /**
     * 更新知识库条目
     */
    void updateKnowledge(Long id, String category, String question, String answer);

    /**
     * 删除知识库条目
     */
    void deleteKnowledge(Long id);
}
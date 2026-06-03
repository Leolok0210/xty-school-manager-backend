package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.AiKnowledgeBaseEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI知识库Mapper
 */
@Mapper
public interface AiKnowledgeBaseDao extends BaseMapper<AiKnowledgeBaseEntity> {
}
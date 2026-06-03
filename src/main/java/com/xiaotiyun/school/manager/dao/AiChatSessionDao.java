package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.AiChatSessionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI聊天会话Mapper
 */
@Mapper
public interface AiChatSessionDao extends BaseMapper<AiChatSessionEntity> {
}
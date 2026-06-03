package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.AiChatMessageDao;
import com.xiaotiyun.school.manager.dao.AiChatSessionDao;
import com.xiaotiyun.school.manager.model.entity.AiChatMessageEntity;
import com.xiaotiyun.school.manager.model.entity.AiChatSessionEntity;
import com.xiaotiyun.school.manager.service.AiChatSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class AiChatSessionServiceImpl extends ServiceImpl<AiChatSessionDao, AiChatSessionEntity> implements AiChatSessionService {

    @Resource
    private AiChatMessageDao aiChatMessageDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMessage(String sessionId, String role, String content) {
        // 查询当前会话消息数量
        long count = aiChatMessageDao.selectCount(
            new LambdaQueryWrapper<AiChatMessageEntity>()
                .eq(AiChatMessageEntity::getSessionId, sessionId)
        );

        AiChatMessageEntity message = new AiChatMessageEntity();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setSort((int) count + 1);
        aiChatMessageDao.insert(message);

        // 更新会话标题（取第一条用户消息）
        if (count == 0 && "user".equals(role)) {
            AiChatSessionEntity session = this.getOne(
                new LambdaQueryWrapper<AiChatSessionEntity>()
                    .eq(AiChatSessionEntity::getSessionId, sessionId)
            );
            if (session != null && session.getTitle() == null) {
                String title = content.length() > 50 ? content.substring(0, 50) + "..." : content;
                session.setTitle(title);
                this.updateById(session);
            }
        }
    }

    @Override
    public List<AiChatMessageEntity> getSessionMessages(String sessionId) {
        return aiChatMessageDao.selectList(
            new LambdaQueryWrapper<AiChatMessageEntity>()
                .eq(AiChatMessageEntity::getSessionId, sessionId)
                .orderByAsc(AiChatMessageEntity::getSort)
        );
    }

    @Override
    public String createSession(Long userId, Long schoolId) {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        AiChatSessionEntity session = new AiChatSessionEntity();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setSchoolId(schoolId);
        this.save(session);
        return sessionId;
    }

    @Override
    public List<AiChatSessionEntity> getUserSessions(Long userId) {
        return this.list(
            new LambdaQueryWrapper<AiChatSessionEntity>()
                .eq(AiChatSessionEntity::getUserId, userId)
                .orderByDesc(AiChatSessionEntity::getCreateTime)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSession(String sessionId) {
        // 删除会话
        this.remove(
            new LambdaQueryWrapper<AiChatSessionEntity>()
                .eq(AiChatSessionEntity::getSessionId, sessionId)
        );
        // 删除消息
        aiChatMessageDao.delete(
            new LambdaQueryWrapper<AiChatMessageEntity>()
                .eq(AiChatMessageEntity::getSessionId, sessionId)
        );
    }

    @Override
    public void updateMessageFeedback(String sessionId, String messageId, String comment) {
        // messageId前端传来的是字符串格式的id
        Long id = Long.parseLong(messageId);
        AiChatMessageEntity message = new AiChatMessageEntity();
        message.setId(id);
        message.setFeedback(comment);
        message.setFeedbackTime(new java.util.Date());
        aiChatMessageDao.updateById(message);
    }
}
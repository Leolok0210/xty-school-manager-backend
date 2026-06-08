package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.AiChatMessageDao;
import com.xiaotiyun.school.manager.dao.AiChatSessionDao;
import com.xiaotiyun.school.manager.model.entity.AiChatMessageEntity;
import com.xiaotiyun.school.manager.model.entity.AiChatSessionEntity;
import com.xiaotiyun.school.manager.service.AiChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AiChatSessionServiceImpl extends ServiceImpl<AiChatSessionDao, AiChatSessionEntity> implements AiChatSessionService {

    @Resource
    private AiChatMessageDao aiChatMessageDao;

    @Resource
    private DataSource dataSource;

    @PostConstruct
    public void initTables() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS `ai_chat_session` (" +
                "`id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID'," +
                "`session_id` VARCHAR(64) NOT NULL COMMENT '会话ID'," +
                "`user_id` BIGINT NOT NULL COMMENT '用户ID'," +
                "`school_id` BIGINT DEFAULT NULL COMMENT '学校ID'," +
                "`title` VARCHAR(200) DEFAULT NULL COMMENT '会话标题'," +
                "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                "`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                "`deleted` TINYINT DEFAULT 0 COMMENT '删除标记'," +
                "PRIMARY KEY (`id`)," +
                "UNIQUE KEY `uk_session_id` (`session_id`)," +
                "KEY `idx_user_id` (`user_id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI聊天会话表'");
            stmt.execute("CREATE TABLE IF NOT EXISTS `ai_chat_message` (" +
                "`id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID'," +
                "`session_id` VARCHAR(64) NOT NULL COMMENT '会话ID'," +
                "`role` VARCHAR(20) NOT NULL COMMENT '角色'," +
                "`content` TEXT COMMENT '消息内容'," +
                "`sort` INT DEFAULT 0 COMMENT '排序号'," +
                "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                "`deleted` TINYINT DEFAULT 0 COMMENT '删除标记'," +
                "`feedback` VARCHAR(500) DEFAULT NULL COMMENT '用户反馈内容'," +
                "`feedback_time` DATETIME DEFAULT NULL COMMENT '反馈时间'," +
                "PRIMARY KEY (`id`)," +
                "KEY `idx_session_id` (`session_id`)," +
                "KEY `idx_session_sort` (`session_id`, `sort`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI聊天消息表'");
            log.info("AI chat tables initialized successfully");
        } catch (Exception e) {
            log.warn("Failed to initialize AI chat tables: {}", e.getMessage());
        }
    }

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
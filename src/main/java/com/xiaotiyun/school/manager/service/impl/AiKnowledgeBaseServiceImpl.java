package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.AiKnowledgeBaseDao;
import com.xiaotiyun.school.manager.model.entity.AiKnowledgeBaseEntity;
import com.xiaotiyun.school.manager.service.AiKnowledgeBaseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiKnowledgeBaseServiceImpl extends ServiceImpl<AiKnowledgeBaseDao, AiKnowledgeBaseEntity> implements AiKnowledgeBaseService {

    @Override
    public List<AiKnowledgeBaseEntity> search(Long schoolId, String keyword) {
        LambdaQueryWrapper<AiKnowledgeBaseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
            .like(AiKnowledgeBaseEntity::getQuestion, keyword)
            .or()
            .like(AiKnowledgeBaseEntity::getAnswer, keyword)
        );
        wrapper.eq(AiKnowledgeBaseEntity::getStatus, 1);
        wrapper.and(w -> w
            .isNull(AiKnowledgeBaseEntity::getSchoolId)
            .or()
            .eq(AiKnowledgeBaseEntity::getSchoolId, schoolId)
        );
        return this.list(wrapper);
    }

    @Override
    public String getKnowledgeContext(Long schoolId) {
        StringBuilder context = new StringBuilder();
        context.append("【学校事务知识库】\n\n");

        // 获取FAQ
        List<AiKnowledgeBaseEntity> faqs = this.list(
            new LambdaQueryWrapper<AiKnowledgeBaseEntity>()
                .and(w -> w
                    .isNull(AiKnowledgeBaseEntity::getSchoolId)
                    .or()
                    .eq(AiKnowledgeBaseEntity::getSchoolId, schoolId)
                )
                .eq(AiKnowledgeBaseEntity::getCategory, "faq")
                .eq(AiKnowledgeBaseEntity::getStatus, 1)
                .orderByAsc(AiKnowledgeBaseEntity::getSort)
        );

        if (!faqs.isEmpty()) {
            context.append("【常见问题】\n");
            for (AiKnowledgeBaseEntity faq : faqs) {
                context.append("Q: ").append(faq.getQuestion()).append("\n");
                context.append("A: ").append(faq.getAnswer()).append("\n\n");
            }
        }

        // 获取流程
        List<AiKnowledgeBaseEntity> processes = this.list(
            new LambdaQueryWrapper<AiKnowledgeBaseEntity>()
                .and(w -> w
                    .isNull(AiKnowledgeBaseEntity::getSchoolId)
                    .or()
                    .eq(AiKnowledgeBaseEntity::getSchoolId, schoolId)
                )
                .eq(AiKnowledgeBaseEntity::getCategory, "process")
                .eq(AiKnowledgeBaseEntity::getStatus, 1)
                .orderByAsc(AiKnowledgeBaseEntity::getSort)
        );

        if (!processes.isEmpty()) {
            context.append("【业务流程】\n");
            for (AiKnowledgeBaseEntity process : processes) {
                context.append("【").append(process.getQuestion()).append("】\n");
                context.append(process.getAnswer()).append("\n\n");
            }
        }

        // 获取通知
        List<AiKnowledgeBaseEntity> notices = this.list(
            new LambdaQueryWrapper<AiKnowledgeBaseEntity>()
                .and(w -> w
                    .isNull(AiKnowledgeBaseEntity::getSchoolId)
                    .or()
                    .eq(AiKnowledgeBaseEntity::getSchoolId, schoolId)
                )
                .eq(AiKnowledgeBaseEntity::getCategory, "notice")
                .eq(AiKnowledgeBaseEntity::getStatus, 1)
                .orderByAsc(AiKnowledgeBaseEntity::getSort)
        );

        if (!notices.isEmpty()) {
            context.append("【系统通知】\n");
            for (AiKnowledgeBaseEntity notice : notices) {
                context.append("📢 ").append(notice.getAnswer()).append("\n");
            }
            context.append("\n");
        }

        return context.toString();
    }

    @Override
    public void addKnowledge(Long schoolId, String category, String question, String answer) {
        AiKnowledgeBaseEntity entity = new AiKnowledgeBaseEntity();
        entity.setSchoolId(schoolId);
        entity.setCategory(category);
        entity.setQuestion(question);
        entity.setAnswer(answer);
        entity.setStatus(1);
        this.save(entity);
    }

    @Override
    public List<AiKnowledgeBaseEntity> getFaqs(Long schoolId) {
        return this.list(
            new LambdaQueryWrapper<AiKnowledgeBaseEntity>()
                .and(w -> w
                    .isNull(AiKnowledgeBaseEntity::getSchoolId)
                    .or()
                    .eq(AiKnowledgeBaseEntity::getSchoolId, schoolId)
                )
                .eq(AiKnowledgeBaseEntity::getCategory, "faq")
                .eq(AiKnowledgeBaseEntity::getStatus, 1)
                .orderByAsc(AiKnowledgeBaseEntity::getSort)
        );
    }

    @Override
    public void updateKnowledge(Long id, String category, String question, String answer) {
        AiKnowledgeBaseEntity entity = this.getById(id);
        if (entity != null) {
            if (category != null) entity.setCategory(category);
            if (question != null) entity.setQuestion(question);
            if (answer != null) entity.setAnswer(answer);
            this.updateById(entity);
        }
    }

    @Override
    public void deleteKnowledge(Long id) {
        this.removeById(id);
    }
}
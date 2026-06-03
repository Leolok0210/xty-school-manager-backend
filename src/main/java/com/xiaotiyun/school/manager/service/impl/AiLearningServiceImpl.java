package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.AiConversationLearnDao;
import com.xiaotiyun.school.manager.model.entity.AiConversationLearnEntity;
import com.xiaotiyun.school.manager.model.entity.AiKnowledgeBaseEntity;
import com.xiaotiyun.school.manager.service.AiKnowledgeBaseService;
import com.xiaotiyun.school.manager.service.AiLearningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiLearningServiceImpl extends ServiceImpl<AiConversationLearnDao, AiConversationLearnEntity> implements AiLearningService {

    @Resource
    private AiKnowledgeBaseService aiKnowledgeBaseService;

    // 停用词列表
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "的", "了", "是", "在", "我", "有", "和", "就", "不", "人", "都", "一", "一個",
        "上", "也", "很", "到", "說", "要", "去", "你", "會", "著", "沒有", "看", "好",
        "自己", "這", "那", "嗎", "呢", "吧", "啊", "啦", "喔", "嗯", "唉", "咦",
        "什麼", "怎麼", "為什麼", "如何", "怎樣", "哪個", "哪里", "誰", "多少"
    ));

    // 标点符号正则
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("[\\p{Punct}\\s]+");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    @Override
    public void learnFromConversation(String sessionId, String userQuery, String aiResponse, Long schoolId) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return;
        }

        try {
            String normalized = normalizeQuery(userQuery);
            if (normalized.length() < 2) {
                return;
            }

            // 查找是否已存在相同问题
            AiConversationLearnEntity existing = baseMapper.findByNormalizedQuery(normalized, schoolId);

            if (existing != null) {
                // 更新出现次数
                baseMapper.incrementQueryCount(existing.getId());
                log.debug("Updated query count for normalized: {}", normalized);
            } else {
                // 新增学习记录
                AiConversationLearnEntity learn = new AiConversationLearnEntity();
                learn.setSchoolId(schoolId);
                learn.setUserQuery(userQuery);
                learn.setNormalizedQuery(normalized);
                learn.setAiResponse(aiResponse);
                learn.setQueryCount(1);
                learn.setPositiveCount(0);
                learn.setNegativeCount(0);
                learn.setIsLearned(false);
                baseMapper.insert(learn);
                log.debug("Created new learning record for: {}", normalized);
            }
        } catch (Exception e) {
            log.error("Failed to learn from conversation", e);
            // 失败不影响主流程
        }
    }

    @Override
    public void recordFeedback(String sessionId, String messageId, boolean isPositive) {
        try {
            Long id = Long.parseLong(messageId);
            if (isPositive) {
                baseMapper.incrementPositiveCount(id);
            } else {
                baseMapper.incrementNegativeCount(id);
            }
            log.debug("Recorded feedback for message {}: positive={}", messageId, isPositive);
        } catch (Exception e) {
            log.error("Failed to record feedback", e);
        }
    }

    @Override
    public Map<String, Object> getStats(Long schoolId) {
        Map<String, Object> stats = new HashMap<>();

        try {
            int totalQueries = baseMapper.countBySchool(schoolId);
            List<AiConversationLearnEntity> topQueries = baseMapper.findTopQueries(schoolId, 10);

            int totalPositive = 0;
            int totalNegative = 0;
            int learnedCount = 0;

            for (AiConversationLearnEntity entity : topQueries) {
                totalPositive += entity.getPositiveCount();
                totalNegative += entity.getNegativeCount();
                if (entity.getIsLearned()) {
                    learnedCount++;
                }
            }

            stats.put("totalQueries", totalQueries);
            stats.put("totalPositive", totalPositive);
            stats.put("totalNegative", totalNegative);
            stats.put("learnedCount", learnedCount);
            stats.put("topQueries", topQueries.stream().limit(5).map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("query", e.getUserQuery());
                map.put("count", e.getQueryCount());
                map.put("positive", e.getPositiveCount());
                map.put("negative", e.getNegativeCount());
                return map;
            }).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("Failed to get stats", e);
        }

        return stats;
    }

    @Override
    public List<Map<String, Object>> getSuggestions(Long schoolId) {
        List<AiConversationLearnEntity> suggestions = baseMapper.findSuggestedFaqs();

        return suggestions.stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getId());
            map.put("question", e.getUserQuery());
            map.put("answer", e.getAiResponse());
            map.put("queryCount", e.getQueryCount());
            map.put("positiveCount", e.getPositiveCount());
            map.put("negativeCount", e.getNegativeCount());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveSuggestion(Long id) {
        AiConversationLearnEntity entity = baseMapper.selectById(id);
        if (entity == null || entity.getIsLearned()) {
            return;
        }

        // 添加到知识库
        AiKnowledgeBaseEntity knowledge = new AiKnowledgeBaseEntity();
        knowledge.setSchoolId(entity.getSchoolId());
        knowledge.setCategory("faq");
        knowledge.setQuestion(entity.getUserQuery());
        knowledge.setAnswer(entity.getAiResponse());
        knowledge.setStatus(1);
        aiKnowledgeBaseService.save(knowledge);

        // 标记为已学习
        baseMapper.markAsLearned(id);

        log.info("Approved suggestion {} and added to knowledge base", id);
    }

    @Override
    public void ignoreSuggestion(Long id) {
        AiConversationLearnEntity entity = baseMapper.selectById(id);
        if (entity == null) {
            return;
        }

        entity.setIsLearned(true);
        baseMapper.updateById(entity);

        log.info("Ignored suggestion {}", id);
    }

    @Override
    public String normalizeQuery(String text) {
        if (text == null) {
            return "";
        }

        // 转小写
        String normalized = text.toLowerCase();

        // 移除标点符号和多余空格
        normalized = PUNCTUATION_PATTERN.matcher(normalized).replaceAll(" ");

        // 移除数字（学号、日期等）
        normalized = NUMBER_PATTERN.matcher(normalized).replaceAll("");

        // 分词
        String[] words = normalized.split("\\s+");

        // 移除停用词
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            word = word.trim();
            if (!word.isEmpty() && !STOP_WORDS.contains(word) && word.length() > 1) {
                result.append(word).append(" ");
            }
        }

        return result.toString().trim();
    }
}
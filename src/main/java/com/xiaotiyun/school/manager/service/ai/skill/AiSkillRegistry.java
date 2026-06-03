package com.xiaotiyun.school.manager.service.ai.skill;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI Skill 注册表
 */
@Component
public class AiSkillRegistry {

    private final Map<String, AiSkill> skills = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;

    public AiSkillRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        // 自动注册所有 AiSkill Bean
        Map<String, AiSkill> skillBeans = applicationContext.getBeansOfType(AiSkill.class);
        for (AiSkill skill : skillBeans.values()) {
            register(skill);
        }
    }

    public void register(AiSkill skill) {
        skills.put(skill.getName(), skill);
    }

    public AiSkill get(String name) {
        return skills.get(name);
    }

    public List<AiSkill> getAll() {
        return new ArrayList<>(skills.values());
    }

    public List<Map<String, Object>> getToolDefinitions(String role) {
        List<Map<String, Object>> tools = new ArrayList<>();
        for (AiSkill skill : skills.values()) {
            if (skill.isAvailableForRole(role)) {
                tools.add(skill.getToolDefinition());
            }
        }
        return tools;
    }

    public List<Map<String, Object>> getToolDefinitions() {
        return getToolDefinitions(null);
    }
}
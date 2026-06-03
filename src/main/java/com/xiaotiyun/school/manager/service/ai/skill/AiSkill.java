package com.xiaotiyun.school.manager.service.ai.skill;

import java.util.List;
import java.util.Map;

/**
 * AI Skill 接口
 * 每个技能实现独立的查询/操作逻辑
 */
public interface AiSkill {

    /**
     * 获取技能名称
     */
    String getName();

    /**
     * 获取技能描述
     */
    String getDescription();

    /**
     * 获取技能参数定义 (JSON Schema)
     */
    Map<String, Object> getParameters();

    /**
     * 角色是否可使用此技能
     * @param role 角色: admin, dept_head, teacher
     */
    boolean isAvailableForRole(String role);

    /**
     * 执行技能
     * @param params 参数
     * @param context AI上下文 (userId, schoolId, role等)
     * @return 执行结果
     */
    SkillResult execute(Map<String, Object> params, AiContext context);

    /**
     * 获取工具定义 (用于发送给AI)
     */
    default Map<String, Object> getToolDefinition() {
        Map<String, Object> tool = new java.util.HashMap<>();
        tool.put("type", "function");
        Map<String, Object> function = new java.util.HashMap<>();
        function.put("name", getName());
        function.put("description", getDescription());
        function.put("parameters", getParameters());
        tool.put("function", function);
        return tool;
    }

    /**
     * 获取需要确认的预览文字 (写操作)
     */
    default String getConfirmationPreview(Map<String, Object> params, AiContext context) {
        return null;
    }

    /**
     * 是否需要写操作确认
     */
    default boolean requiresConfirmation() {
        return false;
    }
}
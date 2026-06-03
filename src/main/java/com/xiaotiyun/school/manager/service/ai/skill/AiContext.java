package com.xiaotiyun.school.manager.service.ai.skill;

import lombok.Data;

/**
 * AI 执行上下文，携带用户信息和权限
 */
@Data
public class AiContext {
    // AI 系统管理员用户ID (需要是一个真实存在的管理员用户)
    private static final Long AI_SYSTEM_USER_ID = 1L;
    private static final Long AI_SYSTEM_SCHOOL_ID = 1L;

    private Long userId = AI_SYSTEM_USER_ID;
    private Long schoolId = AI_SYSTEM_SCHOOL_ID;
    private String role = "admin";  // AI 使用管理员权限
    private String userName;
    private Long currentPeriodId;  // 当前学期/期间
    private String schoolYear = "2025-2026";  // 默认学年

    public static AiContext of(Long userId, Long schoolId, String role) {
        AiContext ctx = new AiContext();
        if (userId != null) {
            ctx.setUserId(userId);
        }
        if (schoolId != null) {
            ctx.setSchoolId(schoolId);
        }
        if (role != null) {
            ctx.setRole(role);
        }
        return ctx;
    }
}
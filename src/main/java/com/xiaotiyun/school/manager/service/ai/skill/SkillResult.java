package com.xiaotiyun.school.manager.service.ai.skill;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * Skill 执行结果统一格式
 */
@Data
public class SkillResult {
    private boolean success;
    private String message;
    private Object data;
    private List<Map<String, Object>> dataCards;
    private boolean needsConfirmation;
    private String confirmationPreview;
    private Map<String, Object> pendingAction;

    public static SkillResult ok(String message) {
        SkillResult result = new SkillResult();
        result.setSuccess(true);
        result.setMessage(message);
        return result;
    }

    public static SkillResult ok(String message, Object data) {
        SkillResult result = new SkillResult();
        result.setSuccess(true);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static SkillResult ok(String message, Object data, List<Map<String, Object>> dataCards) {
        SkillResult result = new SkillResult();
        result.setSuccess(true);
        result.setMessage(message);
        result.setData(data);
        result.setDataCards(dataCards);
        return result;
    }

    public static SkillResult fail(String message) {
        SkillResult result = new SkillResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public static SkillResult confirm(String preview, Map<String, Object> pendingAction) {
        SkillResult result = new SkillResult();
        result.setSuccess(true);
        result.setNeedsConfirmation(true);
        result.setConfirmationPreview(preview);
        result.setPendingAction(pendingAction);
        return result;
    }
}
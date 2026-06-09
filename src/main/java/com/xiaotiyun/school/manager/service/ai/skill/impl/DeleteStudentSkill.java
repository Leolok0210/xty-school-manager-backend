package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.service.StudentService;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class DeleteStudentSkill implements AiSkill {

    @Resource
    private StudentService studentService;

    @Override
    public String getName() {
        return "delete_student";
    }

    @Override
    public String getDescription() {
        return "刪除一名學生（不可撤銷）";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new java.util.ArrayList<>();
        required.add("studentId");

        properties.put("studentId", createMap("type", "integer", "description", "要刪除的學生ID（必填）"));
        params.put("properties", properties);
        params.put("required", required);
        return params;
    }

    @Override
    public boolean isAvailableForRole(String role) {
        return true;
    }

    @Override
    public SkillResult execute(Map<String, Object> params, AiContext context) {
        try {
            StpUtil.login(1L);

            Long studentId = params.get("studentId") != null ? ((Number) params.get("studentId")).longValue() : null;
            if (studentId == null) {
                return SkillResult.fail("請提供要刪除的學生ID");
            }

            String studentName = "未知";
            try {
                Object info = studentService.info(studentId);
                if (info != null) {
                    Object name = info.getClass().getMethod("getChineseName").invoke(info);
                    if (name != null) studentName = name.toString();
                }
            } catch (Exception ignored) {}

            studentService.delete(studentId);

            return SkillResult.ok("已刪除學生「" + studentName + "」(ID: " + studentId + ")");
        } catch (Exception e) {
            log.error("delete_student error", e);
            return SkillResult.fail("刪除學生失敗：" + e.getMessage());
        }
    }

    @Override
    public String getConfirmationPreview(Map<String, Object> params, AiContext context) {
        Long studentId = params.get("studentId") != null ? ((Number) params.get("studentId")).longValue() : null;
        if (studentId != null) {
            try {
                StpUtil.login(1L);
                Object info = studentService.info(studentId);
                if (info != null) {
                    Object name = info.getClass().getMethod("getChineseName").invoke(info);
                    if (name != null) {
                        return "確認刪除學生「" + name.toString() + "」(ID: " + studentId + ")？此操作不可撤銷。";
                    }
                }
            } catch (Exception ignored) {}
        }
        return "確認刪除學生 (ID: " + studentId + ")？此操作不可撤銷。";
    }

    private Map<String, Object> createMap(Object... kvs) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < kvs.length; i += 2) {
            map.put(kvs[i].toString(), kvs[i + 1]);
        }
        return map;
    }
}

package com.xiaotiyun.school.manager.service.ai.skill.impl;

import com.xiaotiyun.school.manager.model.res.SysClassListResModel;
import com.xiaotiyun.school.manager.service.SysClassService;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class QueryClassesSkill implements AiSkill {

    @Resource
    private SysClassService sysClassService;

    @Override
    public String getName() {
        return "query_classes";
    }

    @Override
    public String getDescription() {
        return "查詢學校的班級列表";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("schoolId", createMap("type", "integer", "description", "學校 ID"));
        properties.put("schoolYear", createMap("type", "string", "description", "學年，如 2025-2026"));
        properties.put("gradeGroupId", createMap("type", "integer", "description", "級組 ID"));
        properties.put("department", createMap("type", "integer", "description", "學部：1=幼稚園，2=小學，3=中學"));
        properties.put("className", createMap("type", "string", "description", "班級名稱（可部分匹配）"));
        params.put("properties", properties);
        return params;
    }

    @Override
    public boolean isAvailableForRole(String role) {
        return true;
    }

    @Override
    public SkillResult execute(Map<String, Object> params, AiContext context) {
        try {
            Long schoolId = params.get("schoolId") != null ? ((Number) params.get("schoolId")).longValue() : context.getSchoolId();
            // Fallback: 如果 schoolId 為 0 或 null，使用預設值 1
            if (schoolId == null || schoolId == 0L) {
                schoolId = 1L;
            }
            String schoolYear = params.get("schoolYear") != null ? params.get("schoolYear").toString() : context.getSchoolYear();
            Long gradeGroupId = params.get("gradeGroupId") != null ? ((Number) params.get("gradeGroupId")).longValue() : null;
            Integer department = params.get("department") != null ? ((Number) params.get("department")).intValue() : null;
            String className = params.get("className") != null ? params.get("className").toString() : null;

            log.info("query_classes called with schoolId={}, className={}", schoolId, className);

            List<SysClassListResModel> classes;
            if (gradeGroupId != null) {
                classes = sysClassService.listClasses(schoolId, schoolYear, gradeGroupId, department, context.getUserId());
            } else if (className != null && !className.isEmpty()) {
                // 按班級名稱模糊搜尋
                classes = sysClassService.getSysClassListBySchoolIdAndClassName(schoolId, className);
                // 如果搜尋不到班級，回傳所有班級讓用戶選擇
                if (classes.isEmpty()) {
                    classes = sysClassService.getSysClassListBySchoolId(schoolId);
                }
            } else {
                // 直接用學校ID查詢班級列表
                classes = sysClassService.getSysClassListBySchoolId(schoolId);
            }
            log.info("query_classes result: {} classes found", classes.size());

            List<Map<String, Object>> cards = new ArrayList<>();
            // 構建友好摘要
            StringBuilder summary = new StringBuilder();
            summary.append("查到 ").append(classes.size()).append(" 個班級：\n");
            int count = 0;
            for (SysClassListResModel c : classes) {
                if (count < 10) {
                    summary.append("- ").append(c.getClassName()).append("\n");
                    count++;
                }
                // 只保留前10個班級用於表格展示
                if (count <= 10) {
                    Map<String, Object> card = new HashMap<>();
                    card.put("班級名稱", c.getClassName() != null ? c.getClassName() : "");
                    card.put("年級", c.getGrade() != null ? c.getGrade() : "");
                    card.put("學部", getDepartmentName(c.getDepartment()));
                    cards.add(card);
                }
            }
            if (classes.size() > 10) {
                summary.append("... 還有 ").append(classes.size() - 10).append(" 個班級");
            }

            return SkillResult.ok("找到了「" + (classes.size() > 0 ? classes.get(0).getGroupName() : "") + "」的「" + (classes.size() > 0 ? classes.get(0).getClassName() : "") + "」", cards, cards);
        } catch (Exception e) {
            log.error("query_classes error", e);
            return SkillResult.fail("查詢班級失敗：" + e.getMessage());
        }
    }

    private String getDepartmentName(Integer department) {
        if (department == null) return "";
        switch (department) {
            case 1: return "幼稚園";
            case 2: return "小學";
            case 3: return "中學";
            default: return "";
        }
    }

    private Map<String, Object> createMap(Object... kvs) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < kvs.length; i += 2) {
            map.put(kvs[i].toString(), kvs[i + 1]);
        }
        return map;
    }
}
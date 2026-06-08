package com.xiaotiyun.school.manager.service.ai.skill.impl;

import com.alibaba.fastjson.JSON;
import com.xiaotiyun.school.manager.model.entity.AiReportEntity;
import com.xiaotiyun.school.manager.service.ReportService;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class ReportGenerationSkill implements AiSkill {

    @Resource
    private ReportService reportService;

    @Override
    public String getName() {
        return "generate_report";
    }

    @Override
    public String getDescription() {
        return "根據用戶要求生成報表並提供下載連結";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("reportType", createMap("type", "string", "description", "報表類型：student_list=學生名單, grade_report=成績報表, attendance_report=考勤報表"));
        properties.put("format", createMap("type", "string", "description", "檔案格式：xlsx, csv, pdf"));
        properties.put("schoolId", createMap("type", "integer", "description", "學校 ID"));
        properties.put("className", createMap("type", "string", "description", "班級名稱"));
        properties.put("description", createMap("type", "string", "description", "用戶對報表的描述，如「中五1班的學生名單」"));
        params.put("required", Arrays.asList("reportType", "format"));
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
            String reportType = params.get("reportType") != null ? params.get("reportType").toString() : "student_list";
            String format = params.get("format") != null ? params.get("format").toString() : "xlsx";
            Long schoolId = params.get("schoolId") != null ? ((Number) params.get("schoolId")).longValue() : context.getSchoolId();
            String className = params.get("className") != null ? params.get("className").toString() : null;
            String description = params.get("description") != null ? params.get("description").toString() : "";

            // 如果 schoolId 為 0 或 null，使用預設值 1
            if (schoolId == null || schoolId == 0L) {
                schoolId = 1L;
            }

            log.info("generate_report called with reportType={}, format={}, schoolId={}, className={}",
                reportType, format, schoolId, className);

            // 構建查詢參數
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("schoolId", schoolId);
            queryParams.put("className", className);
            queryParams.put("description", description);

            // 生成報表
            AiReportEntity report = reportService.generateReport(
                context.getUserId(),
                schoolId,
                reportType,
                format,
                JSON.toJSONString(queryParams)
            );

            // 構建下載資訊
            String downloadUrl = "/api/report/download/" + report.getId();

            // 構建回覆卡片
            List<Map<String, Object>> cards = new ArrayList<>();
            Map<String, Object> card = new HashMap<>();
            card.put("報表名稱", report.getReportName());
            card.put("檔案格式", format.toUpperCase());
            card.put("狀態", "已生成");
            cards.add(card);

            String summary = String.format(
                "已成功生成「%s」報表！\n\n" +
                "📊 報表名稱：%s\n" +
                "📁 檔案格式：%s\n" +
                "✅ 狀態：已生成\n\n" +
                "點擊以下連結下載：\n%s\n\n" +
                "或者您可以說「下載報表」來獲取下載連結。",
                reportType,
                report.getReportName(),
                format.toUpperCase(),
                downloadUrl
            );

            return SkillResult.ok(summary, cards, cards);

        } catch (Exception e) {
            log.error("generate_report error", e);
            return SkillResult.fail("生成報表失敗：" + e.getMessage());
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
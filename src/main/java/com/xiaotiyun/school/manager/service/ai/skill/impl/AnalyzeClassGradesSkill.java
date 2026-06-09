package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.GradeClassAvgResModel;
import com.xiaotiyun.school.manager.model.res.GradesStatisticsExcelResModel;
import com.xiaotiyun.school.manager.service.GradeStatisticsService;
import com.xiaotiyun.school.manager.service.SysClassService;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class AnalyzeClassGradesSkill implements AiSkill {

    @Resource
    private GradeStatisticsService gradeStatisticsService;

    @Resource
    private SysClassService sysClassService;

    @Override
    public String getName() {
        return "analyze_class_grades";
    }

    @Override
    public String getDescription() {
        return "分析班級成績（平均分、不及格、高分、年級趨勢）";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new java.util.ArrayList<>();
        required.add("classId");

        properties.put("classId", createMap("type", "integer", "description", "班級ID（必填）"));
        properties.put("schoolYear", createMap("type", "string", "description", "學年，如2025-2026，默認從系統獲取"));
        properties.put("semesterId", createMap("type", "integer", "description", "學段ID"));
        properties.put("department", createMap("type", "integer", "description", "學部：1=幼稚園，2=小學，3=中學，默認3"));
        properties.put("analysisType", createMap("type", "string", "description", "分析類型：average(平均分)、flunk(不合格)、top(高分)、all(全部)，默認all"));
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

            Long classId = ((Number) params.get("classId")).longValue();
            SysClass sysClass = sysClassService.getSysClassById(classId);
            if (sysClass == null) {
                return SkillResult.fail("找不到班級 (ID: " + classId + ")");
            }

            String schoolYear = params.get("schoolYear") != null ? params.get("schoolYear").toString() : context.getSchoolYear();
            int department = params.get("department") != null ? ((Number) params.get("department")).intValue() : 3;
            Long groupId = sysClass.getGradeGroup();
            Long schoolId = context.getSchoolId() != null ? context.getSchoolId() : 1L;
            String analysisType = params.get("analysisType") != null ? params.get("analysisType").toString() : "all";

            List<Map<String, Object>> allCards = new ArrayList<>();
            StringBuilder summary = new StringBuilder();
            summary.append("班級「").append(sysClass.getClassName()).append("」成績分析：\n\n");

            // 平均分
            if ("all".equals(analysisType) || "average".equals(analysisType)) {
                try {
                    GradeClassAvgReqModel avgReq = new GradeClassAvgReqModel();
                    avgReq.setSchoolId(schoolId);
                    avgReq.setSchoolYear(schoolYear);
                    avgReq.setDepartment(department);
                    avgReq.setSemesterId(params.get("semesterId") != null ? ((Number) params.get("semesterId")).longValue() : 1L);
                    avgReq.setUserId(1L);

                    List<GradeClassAvgResModel> avgList = gradeStatisticsService.getGradeClassAvg(avgReq);
                    if (avgList != null && !avgList.isEmpty()) {
                        summary.append("**各班平均分**\n");
                        for (GradeClassAvgResModel avg : avgList) {
                            Map<String, Object> card = new HashMap<>();
                            card.put("班級", avg.getClassName() != null ? avg.getClassName() : "");
                            card.put("平均分", avg.getAverageScore() != null ? avg.getAverageScore() : "");
                            card.put("人數", avg.getClassSize() != null ? avg.getClassSize() : 0);
                            card.put("級組", avg.getClassGroupName() != null ? avg.getClassGroupName() : "");
                            allCards.add(card);
                        }
                    } else {
                        summary.append("暫無平均分數據\n");
                    }
                } catch (Exception e) {
                    log.warn("Failed to get class avg: {}", e.getMessage());
                }
            }

            // 不合格
            if ("all".equals(analysisType) || "flunk".equals(analysisType)) {
                try {
                    GradeFlunkReqModel flunkReq = new GradeFlunkReqModel();
                    flunkReq.setSchoolId(schoolId);
                    flunkReq.setSchoolYear(schoolYear);
                    flunkReq.setDepartment(department);
                    flunkReq.setGroupId(groupId);
                    flunkReq.setUserId(1L);

                    List<?> flunkList = gradeStatisticsService.getGradeFlunk(flunkReq);
                    if (flunkList != null && !flunkList.isEmpty()) {
                        summary.append("\n**不合格科目統計**：").append(flunkList.size()).append(" 項\n");
                    }
                } catch (Exception e) {
                    log.warn("Failed to get flunk: {}", e.getMessage());
                }
            }

            // 高分
            if ("all".equals(analysisType) || "top".equals(analysisType)) {
                try {
                    TopScoreReqModel topReq = new TopScoreReqModel();
                    topReq.setSchoolYear(schoolYear);
                    topReq.setDepartment(department);
                    topReq.setSemesterId(params.get("semesterId") != null ? ((Number) params.get("semesterId")).longValue() : 1L);
                    topReq.setGroupId(groupId);

                    GradesStatisticsExcelResModel topResult = gradeStatisticsService.getTopScore(topReq, schoolId);
                    if (topResult != null && topResult.getContent() != null && !topResult.getContent().isEmpty()) {
                        summary.append("\n**高分情況**：已查得數據\n");
                        appendExcelResult(allCards, topResult);
                    }
                } catch (Exception e) {
                    log.warn("Failed to get top scores: {}", e.getMessage());
                }
            }

            if (allCards.isEmpty()) {
                summary.append("暫無成績數據，請確認該班級有已錄入的考試成績");
            }

            return SkillResult.ok(summary.toString().trim(), null, allCards);

        } catch (Exception e) {
            log.error("analyze_class_grades error", e);
            return SkillResult.fail("成績分析失敗：" + e.getMessage());
        }
    }

    private void appendExcelResult(List<Map<String, Object>> cards, GradesStatisticsExcelResModel model) {
        if (model.getTitle() == null || model.getContent() == null) return;
        List<String> headers = new ArrayList<>();
        for (List<String> row : model.getTitle()) {
            headers.add(String.join("", row));
        }
        for (List<Object> row : model.getContent()) {
            Map<String, Object> card = new HashMap<>();
            for (int i = 0; i < Math.min(headers.size(), row.size()); i++) {
                card.put(headers.get(i), row.get(i) != null ? row.get(i).toString() : "");
            }
            cards.add(card);
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

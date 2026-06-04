package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.dao.StudentUsuallyScoreMapper;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyScoreReqModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyScoreResModel;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class QueryDailyGradesSkill implements AiSkill {

    @Resource
    private StudentUsuallyScoreMapper studentUsuallyScoreMapper;

    @Override
    public String getName() {
        return "query_daily_grades";
    }

    @Override
    public String getDescription() {
        return "查詢班級或學生的日常成績（作業、小測等）";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("classId", createMap("type", "integer", "description", "班級 ID"));
        properties.put("studentId", createMap("type", "integer", "description", "學生 ID"));
        properties.put("subjectId", createMap("type", "integer", "description", "科目 ID"));
        properties.put("semesterId", createMap("type", "integer", "description", "學期 ID"));
        properties.put("schoolId", createMap("type", "integer", "description", "學校 ID"));
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
            // 建立 AI 管理員登錄 session
            StpUtil.login(1L);

            Long classId = params.get("classId") != null ? ((Number) params.get("classId")).longValue() : null;
            Long studentId = params.get("studentId") != null ? ((Number) params.get("studentId")).longValue() : null;
            Long semesterId = params.get("semesterId") != null ? ((Number) params.get("semesterId")).longValue() : null;

            if (classId == null && studentId == null) {
                return SkillResult.fail("需要提供班級 ID 或學生 ID");
            }

            List<Map<String, Object>> cards = new ArrayList<>();

            if (studentId != null) {
                StudentUsuallyScoreReqModel reqModel = new StudentUsuallyScoreReqModel();
                reqModel.setStudentId(studentId);
                if (semesterId != null && semesterId > 0) reqModel.setSemesterId(semesterId);
                List<StudentUsuallyScoreResModel> list = studentUsuallyScoreMapper.getScoreListByStudent(reqModel);
                for (StudentUsuallyScoreResModel score : list) {
                    Map<String, Object> card = new HashMap<>();
                    card.put("studentId", studentId);
                    card.put("科目", score.getSubjectName() != null ? score.getSubjectName() : "");
                    card.put("分數", score.getScore() != null ? score.getScore() : "");
                    card.put("類型", score.getTestTypeName() != null ? score.getTestTypeName() : "");
                    card.put("時間", score.getTestTime() != null ? score.getTestTime().toString() : "");
                    cards.add(card);
                }
            }

            return SkillResult.ok("查到 " + cards.size() + " 筆日常成績", null, cards);
        } catch (Exception e) {
            log.error("query_daily_grades error", e);
            return SkillResult.fail("查詢日常成績失敗：" + e.getMessage());
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
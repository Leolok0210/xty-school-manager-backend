package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.dao.StudentExamScoreMapper;
import com.xiaotiyun.school.manager.model.req.StudentExamScoreReqModel;
import com.xiaotiyun.school.manager.model.res.StudentExamScoreResModel;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class CheckSemesterGradesSkill implements AiSkill {

    @Resource
    private StudentExamScoreMapper studentExamScoreMapper;

    @Override
    public String getName() {
        return "check_semester_grades";
    }

    @Override
    public String getDescription() {
        return "查詢學期考試成績";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("classId", createMap("type", "integer", "description", "班級 ID"));
        properties.put("studentId", createMap("type", "integer", "description", "學生 ID"));
        properties.put("periodId", createMap("type", "integer", "description", "學期 ID"));
        properties.put("subjectId", createMap("type", "integer", "description", "科目 ID"));
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

            Long studentId = params.get("studentId") != null ? ((Number) params.get("studentId")).longValue() : null;
            Long semesterId = params.get("periodId") != null ? ((Number) params.get("periodId")).longValue() : null;
            Long subjectId = params.get("subjectId") != null ? ((Number) params.get("subjectId")).longValue() : null;

            if (studentId == null) {
                return SkillResult.fail("需要提供學生 ID");
            }

            StudentExamScoreReqModel reqModel = new StudentExamScoreReqModel();
            if (studentId != null) reqModel.setStudentId(studentId);
            if (semesterId != null) reqModel.setSemesterId(semesterId);
            if (subjectId != null) reqModel.setSubjectId(subjectId);

            // 直接使用 mapper 查詢，繞過 session 檢查
            List<StudentExamScoreResModel> list = studentExamScoreMapper.scoreListByStudent(reqModel);

            List<Map<String, Object>> cards = new ArrayList<>();
            for (StudentExamScoreResModel score : list) {
                Map<String, Object> card = new HashMap<>();
                card.put("科目", score.getSubjectName() != null ? score.getSubjectName() : "");
                card.put("分數", score.getScore() != null ? score.getScore() : "");
                card.put("考試時間", score.getExamTime() != null ? score.getExamTime().toString() : "");
                cards.add(card);
            }

            return SkillResult.ok("查到 " + list.size() + " 筆考試成績", null, cards);
        } catch (Exception e) {
            log.error("check_semester_grades error", e);
            return SkillResult.fail("查詢學期成績失敗：" + e.getMessage());
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
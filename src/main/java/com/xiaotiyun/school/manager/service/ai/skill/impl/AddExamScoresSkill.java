package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.model.req.StudentExamScoreDeatilSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentExamScoreSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentExamTaskSaveReqModel;
import com.xiaotiyun.school.manager.service.StudentExamScoreService;
import com.xiaotiyun.school.manager.service.StudentExamTaskService;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class AddExamScoresSkill implements AiSkill {

    @Resource
    private StudentExamScoreService studentExamScoreService;

    @Resource
    private StudentExamTaskService studentExamTaskService;

    @Override
    public String getName() {
        return "add_exam_scores";
    }

    @Override
    public String getDescription() {
        return "為學生錄入考試成績（可批量）";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new java.util.ArrayList<>();
        required.add("classId");
        required.add("scores");

        properties.put("taskId", createMap("type", "integer", "description", "已有考試任務ID"));
        properties.put("taskName", createMap("type", "string", "description", "新考試名稱（無taskId時與classId+periodId+subjectId一起使用）"));
        properties.put("classId", createMap("type", "integer", "description", "班級ID（必填）"));
        properties.put("periodId", createMap("type", "integer", "description", "學段ID（新建task時必填）"));
        properties.put("subjectId", createMap("type", "integer", "description", "科目ID（新建task時必填）"));

        Map<String, Object> scoreItem = new HashMap<>();
        scoreItem.put("type", "object");
        Map<String, Object> scoreProps = new HashMap<>();
        scoreProps.put("studentId", createMap("type", "integer", "description", "學生ID"));
        scoreProps.put("score", createMap("type", "number", "description", "分數（例如85.5）"));
        scoreItem.put("properties", scoreProps);

        Map<String, Object> scoresArray = new HashMap<>();
        scoresArray.put("type", "array");
        scoresArray.put("items", scoreItem);
        scoresArray.put("description", "成績列表 [{studentId: 1, score: 85.5}, ...]");
        properties.put("scores", scoresArray);

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

            Long schoolId = context.getSchoolId() != null ? context.getSchoolId() : 1L;

            // Resolve or create task
            Long taskId = params.get("taskId") != null ? ((Number) params.get("taskId")).longValue() : null;
            if (taskId == null) {
                String taskName = params.get("taskName") != null ? params.get("taskName").toString() : "AI錄入考試";
                Long classId = ((Number) params.get("classId")).longValue();
                Long periodId = params.get("periodId") != null ? ((Number) params.get("periodId")).longValue() : null;
                Long subjectId = params.get("subjectId") != null ? ((Number) params.get("subjectId")).longValue() : null;

                if (periodId == null || subjectId == null) {
                    return SkillResult.fail("無考試任務ID時，需提供 periodId 和 subjectId 來創建新任務");
                }

                StudentExamTaskSaveReqModel taskReq = new StudentExamTaskSaveReqModel();
                taskReq.setSchoolId(schoolId);
                taskReq.setName(taskName);
                taskReq.setClassId(classId);
                taskReq.setPeriodId(periodId);
                taskReq.setSubjectId(subjectId);

                Object taskResult = studentExamTaskService.save(taskReq);
                if (taskResult != null) {
                    taskId = ((Number) taskResult.getClass().getMethod("getId").invoke(taskResult)).longValue();
                }
            }

            if (taskId == null) {
                return SkillResult.fail("無法獲取考試任務ID");
            }

            // Parse scores
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> scoreList = (List<Map<String, Object>>) params.get("scores");
            if (scoreList == null || scoreList.isEmpty()) {
                return SkillResult.fail("請提供成績列表");
            }

            List<StudentExamScoreDeatilSaveReqModel> detailList = new ArrayList<>();
            for (Map<String, Object> s : scoreList) {
                StudentExamScoreDeatilSaveReqModel detail = new StudentExamScoreDeatilSaveReqModel();
                detail.setStudentId(((Number) s.get("studentId")).longValue());
                // Score * 100 for integer storage
                double rawScore = ((Number) s.get("score")).doubleValue();
                detail.setScore((int) Math.round(rawScore * 100));
                detailList.add(detail);
            }

            StudentExamScoreSaveReqModel reqModel = new StudentExamScoreSaveReqModel();
            reqModel.setTaskId(taskId);
            reqModel.setScoreList(detailList);

            studentExamScoreService.save(reqModel);

            return SkillResult.ok("成功錄入 " + detailList.size() + " 條考試成績 (任務ID: " + taskId + ")");

        } catch (Exception e) {
            log.error("add_exam_scores error", e);
            return SkillResult.fail("錄入成績失敗：" + e.getMessage());
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

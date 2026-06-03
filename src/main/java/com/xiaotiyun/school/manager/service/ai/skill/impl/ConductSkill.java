package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.model.req.UserRewardQueryReqModel;
import com.xiaotiyun.school.manager.model.res.UserRewardDetailResModel;
import com.xiaotiyun.school.manager.service.UserRewardService;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class ConductSkill implements AiSkill {

    @Resource
    private UserRewardService userRewardService;

    @Override
    public String getName() {
        return "conduct_check";
    }

    @Override
    public String getDescription() {
        return "查詢學生獎懲記錄";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("classId", createMap("type", "integer", "description", "班級 ID"));
        properties.put("studentId", createMap("type", "integer", "description", "學生 ID"));
        properties.put("term", createMap("type", "integer", "description", "學期 ID"));
        properties.put("type", createMap("type", "integer", "description", "獎懲類型：1=獎勵，2=懲罰"));
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
            Long term = params.get("term") != null ? ((Number) params.get("term")).longValue() : null;
            Integer rewardType = params.get("type") != null ? ((Number) params.get("type")).intValue() : null;

            UserRewardQueryReqModel reqModel = new UserRewardQueryReqModel();
            Long schoolId = context.getSchoolId() != null ? context.getSchoolId() : 1L;
            reqModel.setSchoolId(schoolId);
            reqModel.setSid("2025-2026");
            if (classId != null) reqModel.setClassId(String.valueOf(classId));
            if (studentId != null) reqModel.setStudentId(studentId);
            if (term != null) reqModel.setTerm(term);
            if (rewardType != null) reqModel.setType(rewardType);
            reqModel.setPageNum(1);
            reqModel.setPageSize(100);

            PageInfo<UserRewardDetailResModel> pageInfo = userRewardService.getUserRewardList(reqModel);

            List<Map<String, Object>> cards = new ArrayList<>();
            for (UserRewardDetailResModel r : pageInfo.getList()) {
                Map<String, Object> card = new HashMap<>();
                card.put("studentName", r.getStudentName() != null ? r.getStudentName() : "");
                card.put("className", r.getClassName() != null ? r.getClassName() : "");
                card.put("type", r.getType() != null ? (r.getType() == 1 ? "獎勵" : "懲罰") : "");
                card.put("reason", r.getRewardReason() != null ? r.getRewardReason() : "");
                card.put("date", r.getDate() != null ? r.getDate().toString() : "");
                cards.add(card);
            }

            return SkillResult.ok("查到 " + pageInfo.getTotal() + " 筆記錄", null, cards);
        } catch (Exception e) {
            log.error("conduct_check error", e);
            return SkillResult.fail("查詢獎懲失敗：" + e.getMessage());
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